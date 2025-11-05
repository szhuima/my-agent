import React, {useEffect, useState} from 'react';
import {Button, Input, Modal, Select, Space, Toast} from '@douyinfe/semi-ui';
import {aiClientApiAdminService, AiClientApiRequestDTO, AiClientApiResponseDTO} from '../services/model-api-service';

interface AiClientApiFormModalProps {
  visible: boolean;
  onCancel: () => void;
  onSuccess: () => void;
  editData?: AiClientApiResponseDTO | null;
}

interface FormData {
  baseUrl: string;
  apiKey: string;
  completionsPath: string;
  embeddingsPath: string;
  status: number;
}

interface FormErrors {
  baseUrl?: string;
  apiKey?: string;
  completionsPath?: string;
  embeddingsPath?: string;
}

export const ModelApiFormModal: React.FC<AiClientApiFormModalProps> = ({
  visible,
  onCancel,
  onSuccess,
  editData
}) => {
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState<FormData>({
    baseUrl: '',
    apiKey: '',
    completionsPath: 'v1/chat/completions',
    embeddingsPath: 'v1/embeddings',
    status: 1
  });
  const [errors, setErrors] = useState<FormErrors>({});

  // 初始化表单数据
  useEffect(() => {
    if (visible) {
      if (editData) {
        // 编辑模式
        setFormData({
          baseUrl: editData.baseUrl,
          apiKey: editData.apiKey || '',
          completionsPath: editData.completionsPath || 'v1/chat/completions',
          embeddingsPath: editData.embeddingsPath || 'v1/embeddings',
          status: editData.status
        });
      } else {
        // 创建模式
        setFormData({
          baseUrl: '',
          apiKey: '',
          completionsPath: 'v1/chat/completions',
          embeddingsPath: 'v1/embeddings',
          status: 1
        });
      }
      setErrors({});
    }
  }, [visible, editData]);

  // 表单验证
  const validateForm = (): boolean => {
    const newErrors: FormErrors = {};
    
    if (!formData.baseUrl.trim()) {
      newErrors.baseUrl = '请输入基础URL';
    } else if (!/^https?:\/\/.+/.test(formData.baseUrl.trim())) {
      newErrors.baseUrl = '请输入有效的URL格式（以http://或https://开头）';
    }

    if (!formData.apiKey.trim()) {
      newErrors.apiKey = '请输入API密钥';
    } else if (formData.apiKey.trim().length < 10) {
      newErrors.apiKey = 'API密钥长度至少10个字符';
    }

    if (!formData.completionsPath.trim()) {
      newErrors.completionsPath = '请输入对话路径';
    }

    if (!formData.embeddingsPath.trim()) {
      newErrors.embeddingsPath = '请输入嵌入路径';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // 处理表单提交
  const handleSubmit = async () => {
    if (!validateForm()) {
      return;
    }

    setLoading(true);
    try {
      const request: AiClientApiRequestDTO = {
        id: editData?.id,
        modelApiName: editData?.modelApiName || '',
        modelName: editData?.modelName || '',
        modelSource: editData?.modelSource || '',
        modelType: editData?.modelType || '',
        baseUrl: formData.baseUrl.trim(),
        apiKey: formData.apiKey.trim(),
        completionsPath: formData.completionsPath.trim(),
        embeddingsPath: formData.embeddingsPath.trim(),
        status: formData.status
      };

      let result;
      if (editData) {
        // 编辑模式
        result = await aiClientApiAdminService.updateAiClientApiById(request);
      } else {
        // 创建模式
        result = await aiClientApiAdminService.createAiClientApi(request);
      }
      
      if (result.code === '0000' && result.data) {
        Toast.success(editData ? 'API配置更新成功' : 'API配置创建成功');
        handleReset();
        onSuccess();
      } else {
        throw new Error(result.info || (editData ? '更新失败' : '创建失败'));
      }
    } catch (error) {
      console.error(`${editData ? '更新' : '创建'}API配置失败:`, error);
      Toast.error(`${editData ? '更新' : '创建'}失败，请检查网络连接或稍后重试`);
    } finally {
      setLoading(false);
    }
  };

  // 重置表单
  const handleReset = () => {
    setFormData({
      baseUrl: '',
      apiKey: '',
      completionsPath: 'v1/chat/completions',
      embeddingsPath: 'v1/embeddings',
      status: 1
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
      title={editData ? '编辑API配置' : '新增API配置'}
      visible={visible}
      onCancel={handleCancel}
      footer={null}
      width={600}
      maskClosable={false}
    >
      <div style={{ padding: '20px 0' }}>
        {/* ID（仅编辑时显示） */}
        {editData && (
          <div style={{ marginBottom: '16px' }}>
            <div style={{ marginBottom: '8px', display: 'flex', alignItems: 'center' }}>
              <span style={{ width: '120px', textAlign: 'right', marginRight: '12px' }}>
                ID:
              </span>
              <Input
                value={editData.id?.toString() || ''}
                disabled
                style={{ flex: 1 }}
              />
            </div>
          </div>
        )}

        {/* 基础URL */}
        <div style={{ marginBottom: '16px' }}>
          <div style={{ marginBottom: '8px', display: 'flex', alignItems: 'center' }}>
            <span style={{ width: '120px', textAlign: 'right', marginRight: '12px' }}>
              基础URL<span style={{ color: 'red' }}>*</span>:
            </span>
            <Input
              placeholder="请输入基础URL，如：https://api.openai.com"
              value={formData.baseUrl}
              onChange={(value: string) => setFormData(prev => ({ ...prev, baseUrl: value }))}
              style={{ flex: 1 }}
            />
          </div>
          {errors.baseUrl && (
            <div style={{ marginLeft: '132px', color: 'red', fontSize: '12px' }}>
              {errors.baseUrl}
            </div>
          )}
        </div>

        {/* API密钥 */}
        <div style={{ marginBottom: '16px' }}>
          <div style={{ marginBottom: '8px', display: 'flex', alignItems: 'center' }}>
            <span style={{ width: '120px', textAlign: 'right', marginRight: '12px' }}>
              API密钥<span style={{ color: 'red' }}>*</span>:
            </span>
            <Input
              placeholder="请输入API密钥"
              value={formData.apiKey}
              onChange={(value: string) => setFormData(prev => ({ ...prev, apiKey: value }))}
              style={{ flex: 1 }}
              type="password"
            />
          </div>
          {errors.apiKey && (
            <div style={{ marginLeft: '132px', color: 'red', fontSize: '12px' }}>
              {errors.apiKey}
            </div>
          )}
        </div>

        {/* 对话路径 */}
        <div style={{ marginBottom: '16px' }}>
          <div style={{ marginBottom: '8px', display: 'flex', alignItems: 'center' }}>
            <span style={{ width: '120px', textAlign: 'right', marginRight: '12px' }}>
              对话路径<span style={{ color: 'red' }}>*</span>:
            </span>
            <Input
              placeholder="对话补全路径"
              value={formData.completionsPath}
              onChange={(value: string) => setFormData(prev => ({ ...prev, completionsPath: value }))}
              style={{ flex: 1 }}
            />
          </div>
          {errors.completionsPath && (
            <div style={{ marginLeft: '132px', color: 'red', fontSize: '12px' }}>
              {errors.completionsPath}
            </div>
          )}
        </div>

        {/* 嵌入路径 */}
        <div style={{ marginBottom: '16px' }}>
          <div style={{ marginBottom: '8px', display: 'flex', alignItems: 'center' }}>
            <span style={{ width: '120px', textAlign: 'right', marginRight: '12px' }}>
              嵌入路径<span style={{ color: 'red' }}>*</span>:
            </span>
            <Input
              placeholder="嵌入向量路径"
              value={formData.embeddingsPath}
              onChange={(value: string) => setFormData(prev => ({ ...prev, embeddingsPath: value }))}
              style={{ flex: 1 }}
            />
          </div>
          {errors.embeddingsPath && (
            <div style={{ marginLeft: '132px', color: 'red', fontSize: '12px' }}>
              {errors.embeddingsPath}
            </div>
          )}
        </div>

        <div style={{ marginBottom: '16px' }}>
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <span style={{ width: '120px', textAlign: 'right', marginRight: '12px' }}>
              状态<span style={{ color: 'red' }}>*</span>:
            </span>
            <Select
              placeholder="请选择状态"
              value={formData.status}
              onChange={(value) => setFormData(prev => ({ ...prev, status: value as number }))}
              style={{ flex: 1 }}
            >
              <Select.Option value={1}>启用</Select.Option>
              <Select.Option value={0}>禁用</Select.Option>
            </Select>
          </div>
        </div>

        <div style={{ textAlign: 'right', marginTop: '20px' }}>
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