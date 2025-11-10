import {useCallback, useEffect, useState} from 'react';

import {FlowNodeEntity, getNodeForm, useClientContext} from '@flowgram.ai/free-layout-editor';
import {Badge, Button, Toast} from '@douyinfe/semi-ui';
import {API_ENDPOINTS, DEFAULT_HEADERS} from '../../config/api';
import {apiRequestData} from '../../utils/request';

export function Save(props: { disabled: boolean }) {
  const [errorCount, setErrorCount] = useState(0);
  const clientContext = useClientContext();

  const updateValidateData = useCallback(() => {
    const allForms = clientContext.document.getAllNodes().map((node) => getNodeForm(node));
    const count = allForms.filter((form) => form?.state.invalid).length;
    setErrorCount(count);
  }, [clientContext]);

  /**
   * 调用后端API保存流程图配置
   */
  const saveToBackend = async (configData: any) => {
    try {
      // 以 JSON 直接保存到后端
      const workflowId = await apiRequestData<number | null>(
        `${API_ENDPOINTS.WORKFLOW.BASE}${API_ENDPOINTS.WORKFLOW.SAVE}`,
        {
          method: 'POST',
          headers: DEFAULT_HEADERS,
          body: JSON.stringify(configData ?? {}),
        }
      );
      if (workflowId) {
        Toast.success(`保存成功！工作流ID: ${workflowId}`);
        return workflowId;
      }
      Toast.error('保存失败：未返回ID');
      return null;
    } catch (error) {
      console.error('保存流程图配置失败:', error);
      Toast.error('保存失败，请检查网络连接');
      throw error;
    }
  };

  /**
   * 将编辑器文档JSON转换为自定义DSL格式
   */
  const toCustomWorkflow = (docJSON: any) => {
    const typeMap: Record<string, string> = {
      http: 'HTTP_CALL',
      llm: 'LLM',
      agent: 'AGENT',
      client: 'CLIENT',
      model: 'MODEL',
      prompt: 'PROMPT',
      tool_mcp: 'TOOL_MCP',
      condition: 'CONDITION',
      loop: 'LOOP',
      end: 'END',
      start: 'START',
      task: 'TASK',
      advisor: 'ADVISOR',
      comment: 'COMMENT',
    };

    const nodes: any[] = Array.isArray(docJSON?.nodes) ? docJSON.nodes : [];
    const edges: any[] = Array.isArray(docJSON?.edges) ? docJSON.edges : [];

    const idToNode: Map<string, any> = new Map();
    nodes.forEach((n) => idToNode.set(n.id, n));

    // 顶层 name/title 推断：优先 agent 节点
    const pickString = (v: any): string | undefined => {
      if (!v) return undefined;
      if (typeof v === 'string') return v;
      if (Array.isArray(v) && v.length > 0) {
        const first = v[0];
        if (typeof first === 'string') return first;
        if (typeof first?.value === 'string') return first.value;
        if (typeof first?.value?.content === 'string') return first.value.content;
        if (typeof first?.content === 'string') return first.content;
      }
      if (typeof v?.value === 'string') return v.value;
      if (typeof v?.content === 'string') return v.content;
      return undefined;
    };

    let topName = `工作流配置_${new Date().toLocaleString()}`;
    let topTitle = '通过前端拖拽生成的流程图配置';
    const agentNode = nodes.find((n) => n?.type === 'agent');
    if (agentNode?.data?.inputsValues) {
      topName = pickString(agentNode.data.inputsValues.agentName) || topName;
      topTitle = pickString(agentNode.data.inputsValues.description) || topTitle;
    }

    // 解析 headers 数组为对象
    const headersArrToObj = (arr: any): Record<string, string> => {
      const obj: Record<string, string> = {};
      if (Array.isArray(arr)) {
        arr.forEach((item) => {
          const key = item?.key || item?.name;
          const value = item?.value ?? '';
          if (typeof key === 'string' && key) {
            obj[key] = String(value);
          }
        });
      }
      return obj;
    };

    // 从URL解析query参数
    const parseParamsFromUrl = (url: string): Record<string, string> => {
      try {
        const u = new URL(url, window.location.origin);
        const params: Record<string, string> = {};
        u.searchParams.forEach((val, key) => {
          params[key] = val;
        });
        return params;
      } catch {
        return {};
      }
    };

    // 获取全局 clientId（如果存在客户端节点）
    let globalClientId: string | number | undefined = undefined;
    const clientNode = nodes.find((n) => n?.type === 'client');
    const clientIdVal = clientNode?.data?.inputsValues?.clientId;
    const clientIdStr = typeof clientIdVal === 'string' ? clientIdVal : pickString(clientIdVal);
    if (clientIdStr) {
      const num = Number(clientIdStr);
      globalClientId = Number.isFinite(num) ? num : clientIdStr;
    }

    // 预生成 id -> name 映射，命名规则：节点类型_当前时间毫秒数
    const idToName = new Map<string, string>();
    nodes
      .filter((n) => !['start', 'comment'].includes(n?.type))
      .forEach((n) => {
        const ts = Date.now();
        const generatedName = `${n?.type ?? 'node'}_${ts}`;
        idToName.set(n.id, generatedName);
      });

    // 节点转换：跳过 start/comment 等非业务节点
    const customNodes = nodes
      .filter((n) => !['start', 'comment'].includes(n?.type))
      .map((n) => {
        // 使用新的命名规则：节点类型_当前时间毫秒数
        const name = idToName.get(n.id) ?? `${n?.type ?? 'node'}_${Date.now()}`;
        const type = typeMap[n?.type] || String(n?.type || '').toUpperCase();
        const title = n?.data?.title || '';
        const iv = n?.data?.inputsValues || {};
        let config: Record<string, any> = {};

        if (n?.type === 'http') {
          const url = typeof iv?.url === 'string' ? iv.url : pickString(iv?.url) || '';
          const method =
            typeof iv?.method === 'string' ? iv.method : pickString(iv?.method) || 'GET';
          const headers = headersArrToObj(iv?.headers);
          const paramsFromUrl = url ? parseParamsFromUrl(url) : {};
          const paramsFromEditor = headersArrToObj(iv?.params);
          const params = { ...paramsFromUrl, ...paramsFromEditor };
          const body = typeof iv?.body === 'string' ? iv.body : pickString(iv?.body) || '';
          let timeoutSeconds: number | undefined = undefined;
          const tsVal = iv?.timeoutSeconds;
          if (typeof tsVal === 'number') {
            timeoutSeconds = tsVal;
          } else {
            const tsStr = pickString(tsVal);
            if (tsStr) {
              const n = Number(tsStr);
              timeoutSeconds = Number.isFinite(n) ? n : undefined;
            }
          }
          config = {
            url,
            method,
            headers,
            params,
            body,
            ...(timeoutSeconds !== undefined ? { timeout_seconds: timeoutSeconds } : {}),
          };
        } else if (n?.type === 'llm') {
          // 优先使用LLM节点自身的客户端ID；若无则回退到全局客户端ID
          const llmClientIdVal = iv?.clientId;
          const llmClientIdStr =
            typeof llmClientIdVal === 'string' ? llmClientIdVal : pickString(llmClientIdVal);
          let clientId: string | number | undefined = undefined;
          if (llmClientIdStr) {
            const num = Number(llmClientIdStr);
            clientId = Number.isFinite(num) ? num : llmClientIdStr;
          } else if (globalClientId !== undefined) {
            clientId = globalClientId;
          }
          if (clientId !== undefined) {
            config = { client_id: clientId };
          }
        } else if (n?.type === 'agent') {
          // 智能体节点：不需要 clientName；clientId 和 userPrompt 使用下划线命名
          const clientIdVal = iv?.clientId;
          const clientIdStr = typeof clientIdVal === 'string' ? clientIdVal : pickString(clientIdVal);
          let clientId: string | number | undefined = undefined;
          if (clientIdStr) {
            const num = Number(clientIdStr);
            clientId = Number.isFinite(num) ? num : clientIdStr;
          }
          const userPromptStr = pickString(iv?.userPrompt) || '';
          // 保留必要的策略/渠道配置，移除 clientName，改用下划线字段
          config = {
            ...(clientId !== undefined ? { client_id: clientId } : {}),
            ...(userPromptStr ? { user_prompt: userPromptStr } : {}),
            ...(iv?.channel ? { channel: iv.channel } : {}),
            ...(iv?.strategy ? { strategy: iv.strategy } : {}),
          };
        } else {
          // 其他类型：直接抄 inputsValues 作为配置
          config = iv || {};
        }

        return { name, type, title, config };
      });

    // edges 使用 name（即原 id）引用
    const startIds = new Set(
      nodes.filter((n) => n?.type === 'start').map((n) => n.id)
    );
    const customEdges = edges
      .map((e) => {
        const fromId = e?.sourceNodeID ?? e?.from?.id;
        const toId = e?.targetNodeID ?? e?.to?.id;
        return { fromId, toId };
      })
      .filter(({ fromId, toId }) => fromId && toId)
      .filter(({ fromId, toId }) => !startIds.has(fromId) && !startIds.has(toId))
      // 边引用节点的 name（新规则生成的值），若未生成则回退到原始 id
      .map(({ fromId, toId }) => ({
        from: idToName.get(fromId) || fromId,
        to: idToName.get(toId) || toId,
      }));

    return {
      name: topName,
      title: topTitle,
      meta: {},
      nodes: customNodes,
      edges: customEdges,
    };
  };

  /**
   * 提取客户端信息并添加到配置数据中
   */
  const extractClientInfo = (configData: any) => {
    // 遍历所有节点，查找client类型的节点
    if (configData.nodes) {
      configData.nodes.forEach((node: any) => {
        if (node.type === 'client' && node.data && node.data.inputsValues) {
          const inputsValues = node.data.inputsValues;

          // clientName现在是字符串格式，clientId应该已经在组件中设置到inputsValues中
          if (inputsValues.clientName && typeof inputsValues.clientName === 'string') {
            // 如果clientId已经存在，保持不变；如果不存在，尝试从clientName推断
            if (!inputsValues.clientId) {
              console.warn('clientId为空，可能是选择组件没有正确设置clientId');
              // 这里可以添加降级处理逻辑，比如使用clientName作为临时ID
              inputsValues.clientId = '';
            }
            console.info('Client info:', {
              clientName: inputsValues.clientName,
              clientId: inputsValues.clientId
            });
          }
        }
      });
    }

    return configData;
  };

  /**
   * Validate all node and Save
   */
  const onSave = useCallback(async () => {
    try {
      const allForms = clientContext.document.getAllNodes().map((node) => getNodeForm(node));
      await Promise.all(allForms.map(async (form) => form?.validate()));

      const originalConfigData = clientContext.document.toJSON();
      // 提取客户端信息（仅用于调试日志），不影响保存结构
      extractClientInfo(originalConfigData);

      // 直接保存画布文档 JSON
      console.log('工作流配置（JSON，画布文档）：', originalConfigData);
      await saveToBackend(originalConfigData);
    } catch (error) {
      console.error('保存过程中发生错误:', error);
    }
  }, [clientContext]);

  /**
   * Listen single node validate
   */
  useEffect(() => {
    const listenSingleNodeValidate = (node: FlowNodeEntity) => {
      const form = getNodeForm(node);
      if (form) {
        const formValidateDispose = form.onValidate(() => updateValidateData());
        node.onDispose(() => formValidateDispose.dispose());
      }
    };
    clientContext.document.getAllNodes().map((node) => listenSingleNodeValidate(node));
    const dispose = clientContext.document.onNodeCreate(({ node }) =>
      listenSingleNodeValidate(node)
    );
    return () => dispose.dispose();
  }, [clientContext]);

  if (errorCount === 0) {
    return (
      <Button
        disabled={props.disabled}
        onClick={onSave}
        style={{ backgroundColor: 'rgba(171,181,255,0.3)', borderRadius: '8px' }}
      >
        保存
      </Button>
    );
  }
  return (
    <Badge count={errorCount} position="rightTop" type="danger">
      <Button
        type="danger"
        disabled={props.disabled}
        onClick={onSave}
        style={{ backgroundColor: 'rgba(255, 179, 171, 0.3)', borderRadius: '8px' }}
      >
        保存
      </Button>
    </Badge>
  );
}
