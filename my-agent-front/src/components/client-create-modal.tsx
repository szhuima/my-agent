import React, { useState } from "react";
import {
  Modal,
  Input,
  Select,
  Button,
  Toast,
  Space,
  TextArea,
  InputNumber,
} from "@douyinfe/semi-ui";
import {
  aiClientAdminService,
  AiClientRequestDTO,
} from "../services/ai-client-admin-service";

import {
  aiClientAdvisorAdminService,
  AiClientAdvisorQueryRequestDTO,
  AiClientAdvisorResponseDTO,
  AiClientAdvisorRequestDTO,
} from "../services/ai-client-advisor-admin-service";

import {
  aiClientApiAdminService,
  AiClientApiQueryRequestDTO,
  AiClientApiResponseDTO,
} from "../services/model-api-service";

import {
  aiClientToolMcpAdminService,
  AiClientToolMcpQueryRequestDTO,
  AiClientToolMcpResponseDTO,
  AiClientToolMcpRequestDTO,
} from "../services/ai-client-tool-mcp-admin-service";

import {
  aiClientRagOrderAdminService,
  AiClientRagOrderResponseDTO,
} from "../services/knowledge-admin-service";

interface ClientCreateModalProps {
  visible: boolean;
  onCancel: () => void;
  onSuccess: () => void;
  width?: number;
}

interface FormData {
  clientName: string;
  description: string;
  status: number;
  modelId?: number;
  systemPrompt?: string;
  advisorIds: number[];
  mcpToolIds: number[];
  memorySize?: number;
  knowledgeIds: number[];
}

export const ClientCreateModal: React.FC<ClientCreateModalProps> = ({
  visible,
  onCancel,
  onSuccess,
  width,
}) => {
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState<FormData>({
    clientName: "",
    description: "",
    systemPrompt:"",
    status: 1,
    advisorIds: [],
    mcpToolIds: [],
    memorySize: 0,
    knowledgeIds: [],
  });
  const [errors, setErrors] = useState<FormErrors>({});

  const [models, setModels] = useState<AiClientApiResponseDTO[]>([]); // 模型列表
  const [advisors, setAdvisors] = useState<AiClientAdvisorResponseDTO[]>([]); // 顾问列表
  const [mcpTools, setMcpTools] = useState<AiClientToolMcpResponseDTO[]>([]); // MCP工具列表
  const [knowledges, setKnowledges] = useState<AiClientRagOrderResponseDTO[]>([]); // 知识库列表

  interface FormErrors {
    clientName?: string;
    description?: string;
  }

  // 表单验证
  const validateForm = (): boolean => {
    const newErrors: FormErrors = {};

    if (!formData.clientName.trim()) {
      newErrors.clientName = "请输入客户端名称";
    } else if (formData.clientName.trim().length < 2) {
      newErrors.clientName = "客户端名称至少2个字符";
    } else if (formData.clientName.trim().length > 50) {
      newErrors.clientName = "客户端名称不能超过50个字符";
    }

    if (formData.description && formData.description.length > 200) {
      newErrors.description = "描述不能超过200个字符";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const fetchModelApis = async () => {
    const apiResponse =
      await aiClientApiAdminService.queryEnabledAiClientApis();
    if (apiResponse.code === "0000" && apiResponse.data) {
      setModels(apiResponse.data);
    }
  };

  const fetchAdvisors = async () => {
    const advisorResponse =
      await aiClientAdvisorAdminService.queryEnabledAdvisors();
    if (advisorResponse.code === "0000" && advisorResponse.data) {
      setAdvisors(advisorResponse.data);
    }
  };

  const fetchMcpTools = async () => {
    const toolResponse = await aiClientToolMcpAdminService.queryEnabledAiClientToolMcps();
    if (toolResponse.code === "0000" && toolResponse.data) {
      setMcpTools(toolResponse.data);
    }
  };

  const fetchKnowledges = async () => {
    const knowledgeResponse = await aiClientRagOrderAdminService.queryEnabledKnowledges();
    if (knowledgeResponse.code === "0000" && knowledgeResponse.data) {
      setKnowledges(knowledgeResponse.data);
    }
  };

  // 组件挂载时获取模型数据
  React.useEffect(() => {
    fetchModelApis();
    fetchAdvisors();
    fetchMcpTools();
    fetchKnowledges();
  }, []);

  // 处理表单提交
  const handleSubmit = async () => {
    if (!validateForm()) {
      return;
    }

    setLoading(true);
    try {
      const request: AiClientRequestDTO = {
        clientName: formData.clientName.trim(),
        description: formData.description.trim() || "",
        status: formData.status,
        modelId: formData.modelId,
        systemPrompt: formData.systemPrompt,
        advisorIds: formData.advisorIds,
        mcpToolIds: formData.mcpToolIds,
        memorySize: formData.memorySize,
        knowledgeIds: formData.knowledgeIds,
      };

      const result = await aiClientAdminService.createClient(request);

      if (result.code === "0000" && result.data) {
        Toast.success("客户端创建成功");
        handleReset();
        onSuccess();
      } else {
        throw new Error(result.info || "创建失败");
      }
    } catch (error) {
      console.error("创建客户端失败:", error);
      Toast.error("创建失败，请检查网络连接或稍后重试");
    } finally {
      setLoading(false);
    }
  };

  // 重置表单
  const handleReset = () => {
    setFormData({
      clientName: "",
      description: "",
      status: 1,
      modelId: undefined,
      systemPrompt: "",
      advisorIds: [],
      mcpToolIds: [],
      memorySize: 0,
      knowledgeIds: [],
    });
    setErrors({});
  };

  // 处理取消
  const handleCancel = () => {
    handleReset();
    onCancel();
  };

  return (
    <Modal
      title="新增客户端"
      visible={visible}
      onCancel={handleCancel}
      footer={null}
      width={width ?? 800}
      maskClosable={false}
    >
      <div style={{ padding: "20px 0" }}>
        <div style={{ marginBottom: "16px" }}>
          <div
            style={{
              marginBottom: "8px",
              display: "flex",
              alignItems: "center",
            }}
          >
            <span
              style={{
                width: "100px",
                textAlign: "right",
                marginRight: "12px",
              }}
            >
              客户端名称<span style={{ color: "red" }}>*</span>:
            </span>
            <Input
              placeholder="请输入客户端名称"
              value={formData.clientName}
              onChange={(value: string) =>
                setFormData((prev) => ({ ...prev, clientName: value }))
              }
              style={{ flex: 1 }}
            />
          </div>
          {errors.clientName && (
            <div
              style={{ marginLeft: "112px", color: "red", fontSize: "12px" }}
            >
              {errors.clientName}
            </div>
          )}
        </div>

        <div style={{ marginBottom: "16px" }}>
          <div
            style={{
              marginBottom: "8px",
              display: "flex",
              alignItems: "flex-start",
            }}
          >
            <span
              style={{
                width: "100px",
                textAlign: "right",
                marginRight: "12px",
                paddingTop: "6px",
              }}
            >
              客户端描述:
            </span>
            <TextArea
              placeholder="请输入客户端描述（可选）"
              value={formData.description}
              onChange={(value: string) =>
                setFormData((prev) => ({ ...prev, description: value }))
              }
              maxCount={120}
              rows={2}
              style={{ flex: 1 }}
            />
          </div>
          {errors.description && (
            <div
              style={{ marginLeft: "112px", color: "red", fontSize: "12px" }}
            >
              {errors.description}
            </div>
          )}
        </div>

        <div style={{ marginBottom: "16px" }}>
          <div style={{ display: "flex", alignItems: "center" }}>
            <span
              style={{
                width: "100px",
                textAlign: "right",
                marginRight: "12px",
              }}
            >
              选择模型<span style={{ color: "red" }}>*</span>:
            </span>
            <Select
              placeholder="请选择模型"
              value={formData.modelId}
              onChange={(value) =>
                setFormData((prev) => ({ ...prev, modelId: value as number }))
              }
              style={{ flex: 1 }}
            >
              {models.map((model) => (
                <Select.Option key={model.id} value={model.id}>
                  {model.modelApiName}
                </Select.Option>
              ))}
            </Select>
          </div>
        </div>

        <div style={{ marginBottom: "16px" }}>
          <div
            style={{
              marginBottom: "8px",
              display: "flex",
              alignItems: "flex-start",
            }}
          >
            <span
              style={{
                width: "100px",
                textAlign: "right",
                marginRight: "12px",
                paddingTop: "6px",
              }}
            >
              系统提示词:
            </span>
            <TextArea
              placeholder="请输入系统提示词（可选）"
              value={formData.systemPrompt}
              onChange={(value: string) =>
                setFormData((prev) => ({ ...prev, systemPrompt: value }))
              }
              // maxCount={2000}
              rows={3}
              autosize={{ minRows: 3, maxRows: 12 }}
              style={{ flex: 1, resize: "vertical" }}
            />
          </div>
          {errors.description && (
            <div
              style={{ marginLeft: "112px", color: "red", fontSize: "12px" }}
            >
              {errors.description}
            </div>
          )}
        </div>

        {/* 对话记忆大小输入框 */}
        <div style={{ marginBottom: "16px" }}>
          <div style={{ display: "flex", alignItems: "center" }}>
            <span
              style={{
                width: "100px",
                textAlign: "right",
                marginRight: "12px",
              }}
            >
              对话记忆大小:
            </span>
            <InputNumber
              placeholder="请输入非负整数"
              min={0}
              precision={0}
              value={formData.memorySize}
              onChange={(value) =>
                setFormData((prev) => ({
                  ...prev,
                  memorySize:
                    typeof value === "number" && value >= 0 ? value : 0,
                }))
              }
              style={{ flex: 1 }}
            />
          </div>
        </div>

        {/* 选择知识库（多选） */}
        <div style={{ marginBottom: "16px" }}>
          <div style={{ display: "flex", alignItems: "center" }}>
            <span
              style={{
                width: "100px",
                textAlign: "right",
                marginRight: "12px",
              }}
            >
              选择知识库:
            </span>
            <Select
              placeholder="请选择知识库（可多选）"
              multiple
              value={formData.knowledgeIds}
              onChange={(values) => {
                setFormData((prev) => ({
                  ...prev,
                  knowledgeIds: values as number[],
                }));
              }}
              style={{ flex: 1 }}
            >
              {knowledges.map((k) => (
                <Select.Option key={k.id} value={k.id}>
                  {k.ragName}
                </Select.Option>
              ))}
            </Select>
          </div>
        </div>

        <div style={{ marginBottom: "16px" }}>
          <div style={{ display: "flex", alignItems: "center" }}>
            <span
              style={{
                width: "100px",
                textAlign: "right",
                marginRight: "12px",
              }}
            >
              选择MCP工具:
            </span>
            <Select
              placeholder="请选择MCP工具"
              multiple
              value={formData.mcpToolIds}
              onChange={(values) => {
                setFormData((prev) => ({
                  ...prev,
                  mcpToolIds: values as number[],
                }));
              }}
              style={{ flex: 1 }}
            >
              {mcpTools.map((tool) => (
                <Select.Option key={tool.id} value={tool.id}>
                  {tool.mcpName}
                </Select.Option>
              ))}
            </Select>
          </div>
        </div>

        <div style={{ marginBottom: "16px" }}>
          <div
            style={{
              marginBottom: "8px",
              display: "flex",
              alignItems: "flex-start",
            }}
          >
            <span
              style={{
                width: "100px",
                textAlign: "right",
                marginRight: "12px",
                paddingTop: "6px",
              }}
            >
              状态<span style={{ color: "red" }}>*</span>:
            </span>
            <Select
              placeholder="请选择状态"
              value={formData.status}
              onChange={(value) =>
                setFormData((prev) => ({ ...prev, status: value as number }))
              }
              style={{ flex: 1 }}
            >
              <Select.Option value={1}>启用</Select.Option>
              <Select.Option value={0}>禁用</Select.Option>
            </Select>
          </div>
        </div>

        <div style={{ textAlign: "right", marginTop: "20px" }}>
          <Space>
            <Button onClick={handleCancel}>取消</Button>
            <Button type="primary" onClick={handleSubmit} loading={loading}>
              保存
            </Button>
          </Space>
        </div>
      </div>
    </Modal>
  );
};
