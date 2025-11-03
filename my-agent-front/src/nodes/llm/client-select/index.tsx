import { useEffect, useState } from 'react';

import { Field } from '@flowgram.ai/free-layout-editor';
import { Select } from '@douyinfe/semi-ui';

import { aiClientAdminService } from '../../../services/ai-client-admin-service';
import type { AiClientResponseDTO } from '../../../services/ai-client-admin-service';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { FormItem } from '../../../form-components';

/**
 * LLM节点的“模型客户端”下拉选择
 * - 选项来源：后端接口（已启用或全部客户端，根据实现选择）
 * - 选中后：同时写入 inputsValues.clientId 和 inputsValues.clientName
 */
export function LLMClientSelect() {
  const readonly = !useIsSidebar();
  const nodeRender = useNodeRenderContext();
  const [clientOptions, setClientOptions] = useState<{ label: string; value: string }[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(false);

  useEffect(() => {
    const fetchClients = async () => {
      setLoading(true);
      setError(false);
      try {
        // 使用 admin 接口：查询启用的客户端
        const result = await aiClientAdminService.queryEnabledClients();
        const clients: AiClientResponseDTO[] = result?.data || [];
        const options = clients.map(client => ({
          label: client.clientName,
          value: String(client.id),
        }));
        setClientOptions(options);
      } catch (error) {
        console.error('获取模型客户端数据失败:', error);
        setError(true);
        setClientOptions([]);
      } finally {
        setLoading(false);
      }
    };

    fetchClients();
  }, []);

  return (
    <>
      <Field<string> name="inputsValues.clientName">
        {({ field: clientNameField }) => (
          <Field<string> name="inputsValues.clientId">
            {({ field: clientIdField }) => {
              const nodeMeta = nodeRender.node.getNodeMeta();
              const nodeInputsValues = nodeMeta?.inputsValues;
              let displayValue = '';
              let displayLabel = '';

              // 初始化显示值/标签
              if (clientIdField.value && typeof clientIdField.value === 'string') {
                displayValue = clientIdField.value;
                const matched = clientOptions.find(opt => opt.value === displayValue);
                if (matched) {
                  displayLabel = matched.label;
                  if (clientNameField.value !== displayLabel) clientNameField.onChange(displayLabel);
                }
              } else if (clientNameField.value && typeof clientNameField.value === 'string') {
                displayLabel = clientNameField.value;
                const matched = clientOptions.find(opt => opt.label === displayLabel);
                if (matched) {
                  displayValue = matched.value;
                  clientIdField.onChange(displayValue);
                }
              } else if (nodeInputsValues) {
                if (typeof nodeInputsValues.clientId === 'string' && nodeInputsValues.clientId) {
                  displayValue = nodeInputsValues.clientId;
                  const matched = clientOptions.find(opt => opt.value === displayValue);
                  if (matched) {
                    displayLabel = matched.label;
                    if (!clientIdField.value) clientIdField.onChange(displayValue);
                    if (!clientNameField.value) clientNameField.onChange(displayLabel);
                  }
                } else if (typeof nodeInputsValues.clientName === 'string' && nodeInputsValues.clientName) {
                  displayLabel = nodeInputsValues.clientName;
                  const matched = clientOptions.find(opt => opt.label === displayLabel);
                  if (matched) {
                    displayValue = matched.value;
                    if (!clientNameField.value) clientNameField.onChange(displayLabel);
                    if (!clientIdField.value) clientIdField.onChange(displayValue);
                  }
                }
              }

              return (
                <FormItem name="模型客户端" type="string" required={true} labelWidth={80}>
                  <Select
                    placeholder={loading ? '加载中...' : '请选择模型客户端'}
                    style={{ width: '100%' }}
                    value={displayValue}
                    onChange={(value) => {
                      const selected = clientOptions.find(opt => opt.value === value);
                      const clientName = selected?.label || '';
                      const clientId = selected?.value || '';
                      clientNameField.onChange(clientName);
                      clientIdField.onChange(clientId);
                    }}
                    disabled={readonly || loading}
                    optionList={clientOptions}
                    loading={loading}
                  />
                  {readonly && (displayLabel || displayValue) && (
                    <div style={{ marginTop: '4px', fontSize: '12px', color: '#666' }}>
                      {displayLabel && <div>客户端名称: {displayLabel}</div>}
                      {displayValue && <div>客户端ID: {displayValue}</div>}
                    </div>
                  )}
                </FormItem>
              );
            }}
          </Field>
        )}
      </Field>
    </>
  );
}