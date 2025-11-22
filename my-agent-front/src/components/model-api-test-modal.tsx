import React, {useEffect, useMemo, useRef, useState} from 'react';
import {Button, Divider, Modal, Progress, Space, Switch, TextArea, Toast, Typography} from '@douyinfe/semi-ui';
import styled from 'styled-components';
import {AiClientApiResponseDTO} from '../services/model-api-service';
import {chatModelApiNonStream, chatModelApiStream} from '../services/model-api-chat-service';
import {fileUploadService} from '../services/file-upload-service';

const { Text } = Typography;

const Container = styled.div`
  display: flex;
  height: 70vh;
`;

const LeftBar = styled.div`
  width: 180px;
  border-right: 1px solid #e6e6e6;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
`;

const ChatArea = styled.div`
  flex: 1;
  display: flex;
  flex-direction: column;
`;

const History = styled.div`
  flex: 1;
  overflow-y: auto;
  padding: 12px;
  background: #fafafa;
`;

const MessageRow = styled.div<{ $role: 'user' | 'assistant' }>`
  display: flex;
  justify-content: ${p => (p.$role === 'user' ? 'flex-end' : 'flex-start')};
  margin-bottom: 8px;
`;

const Bubble = styled.div<{ $role: 'user' | 'assistant' }>`
  max-width: 70%;
  padding: 10px 12px;
  border-radius: 8px;
  background: ${p => (p.$role === 'user' ? '#e6f7ff' : '#ffffff')};
  border: 1px solid #e6e6e6;
  white-space: pre-wrap;
`;

const InputBar = styled.div<{ $isDragOver: boolean }>`
  padding: 12px;
  border-top: 1px solid #e6e6e6;
  display: flex;
  gap: 8px;
  position: relative;
  border: ${p => p.$isDragOver ? '2px dashed #1890ff' : '1px solid #e6e6e6'};
  border-radius: ${p => p.$isDragOver ? '8px' : '0'};
  background: ${p => p.$isDragOver ? '#f0f8ff' : 'transparent'};
  transition: all 0.3s;
`;

const InputWrapper = styled.div`
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
  position: relative;
`;

const ImagePreviewContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 8px;
  background: #fafafa;
  border-radius: 4px;
  border: 1px solid #e6e6e6;
`;

const ImagePreviewItem = styled.div`
  position: relative;
  display: inline-block;
`;

const ImagePreviewThumb = styled.img`
  max-width: 100px;
  max-height: 100px;
  border-radius: 4px;
  border: 1px solid #e6e6e6;
`;

const RemoveButton = styled.button`
  position: absolute;
  top: -8px;
  right: -8px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #ff4d4f;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
  
  &:hover {
    background: #ff7875;
  }
`;

const ImagePreview = styled.img`
  max-width: 200px;
  max-height: 200px;
  border-radius: 4px;
  margin: 8px 0;
`;

export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  timestamp: number;
  imageUrl?: string;
}

interface Props {
  visible: boolean;
  record: AiClientApiResponseDTO | null;
  onCancel: () => void;
}

interface UploadingImage {
  file: File;
  preview: string; // base64预览
  progress: number; // 0-100
  status: 'uploading' | 'success' | 'error';
  url?: string; // 上传成功后的URL
  error?: string; // 错误信息
}

export const ModelApiTestModal: React.FC<Props> = ({ visible, record, onCancel }) => {
  const [streaming, setStreaming] = useState<boolean>(true);
  const [input, setInput] = useState<string>('');
  const [sending, setSending] = useState<boolean>(false);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [isDragOver, setIsDragOver] = useState<boolean>(false);
  const [uploadingImages, setUploadingImages] = useState<UploadingImage[]>([]);
  const historyEndRef = useRef<HTMLDivElement | null>(null);
  const inputBarRef = useRef<HTMLDivElement>(null);

  const title = useMemo(() => {
    if (!record) return '测试模型 API';
    return `测试：${record.modelApiName || record.modelName || record.apiId}`;
  }, [record]);

  useEffect(() => {
    if (!visible) {
      setMessages([]);
      setInput('');
      setSending(false);
      setUploadingImages([]);
      setIsDragOver(false);
    }
  }, [visible]);

  useEffect(() => {
    if (historyEndRef.current) {
      historyEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [messages]);

  // 将文件转换为base64预览
  const createImagePreview = (file: File): Promise<string> => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (e) => {
        const result = e.target?.result;
        if (typeof result === 'string') {
          resolve(result);
        } else {
          reject(new Error('Failed to read file'));
        }
      };
      reader.onerror = () => reject(new Error('Failed to read file'));
      reader.readAsDataURL(file);
    });
  };

  // 处理文件上传（带进度）
  const handleFileUpload = async (file: File) => {
    if (!file.type.startsWith('image/')) {
      Toast.error('请上传图片文件');
      return;
    }

    if (file.size > 10 * 1024 * 1024) {
      Toast.error('图片大小不能超过10MB');
      return;
    }

    // 创建预览
    let preview: string;
    try {
      preview = await createImagePreview(file);
    } catch (error) {
      Toast.error('图片预览失败');
      return;
    }

    // 添加到上传列表
    const uploadItem: UploadingImage = {
      file,
      preview,
      progress: 0,
      status: 'uploading'
    };
    setUploadingImages(prev => [...prev, uploadItem]);

    // 上传文件
    try {
      // 设置进度为50%，表示正在上传
      setUploadingImages(prev => prev.map(item => 
        item.file === file ? { ...item, progress: 50 } : item
      ));

      // 使用 FileUploadService 上传图片
      const response = await fileUploadService.uploadImage(file);
      
      if (response.code === '0000') {
        setUploadingImages(prev => prev.map(item => 
          item.file === file ? { ...item, status: 'success', progress: 100, url: response.data } : item
        ));
        Toast.success('图片上传成功');
      } else {
        setUploadingImages(prev => prev.map(item => 
          item.file === file ? { ...item, status: 'error', error: response.info || '上传失败' } : item
        ));
        Toast.error(response.info || '图片上传失败');
      }
    } catch (error: any) {
      console.error('图片上传失败:', error);
      setUploadingImages(prev => prev.map(item => 
        item.file === file ? { ...item, status: 'error', error: error?.message || '上传失败' } : item
      ));
      Toast.error(error?.message || '图片上传失败，请稍后重试');
    }
  };

  // 处理输入框拖拽事件
  const handleInputDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragOver(true);
  };

  const handleInputDragLeave = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragOver(false);
  };

  const handleInputDrop = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragOver(false);

    const files = Array.from(e.dataTransfer.files);
    files.forEach(file => {
      if (file.type.startsWith('image/')) {
        handleFileUpload(file);
      }
    });
  };


  // 处理粘贴事件
  const handlePaste = (e: React.ClipboardEvent) => {
    const items = e.clipboardData.items;
    for (let i = 0; i < items.length; i++) {
      const item = items[i];
      if (item.type.startsWith('image/')) {
        e.preventDefault();
        const file = item.getAsFile();
        if (file) {
          handleFileUpload(file);
        }
      }
    }
  };

  // 移除图片
  const removeImage = (file: File) => {
    setUploadingImages(prev => prev.filter(item => item.file !== file));
  };

  // 发送消息
  const sendMessage = async () => {
    if (!record || !record.id) {
      Toast.error('缺少模型API配置ID');
      return;
    }
    const text = input.trim();
    const successImages = uploadingImages.filter(img => img.status === 'success' && img.url);
    
    if (!text && successImages.length === 0) {
      Toast.warning('请输入消息或上传图片');
      return;
    }

    // 检查是否有正在上传的图片
    const uploadingCount = uploadingImages.filter(img => img.status === 'uploading').length;
    if (uploadingCount > 0) {
      Toast.warning('请等待图片上传完成');
      return;
    }

    setSending(true);
    const now = Date.now();

    // 使用第一张成功上传的图片URL（如果有）
    const imageUrl = successImages.length > 0 ? successImages[0].url : undefined;

    // 创建用户消息
    const userMsg: ChatMessage = {
      role: 'user',
      content: text,
      timestamp: now,
      imageUrl: imageUrl
    };
    setMessages(prev => [...prev, userMsg]);
    setInput('');
    setUploadingImages([]);

    try {
      if (streaming) {
        // 先插入一个空的助手消息，随后逐步填充
        const assistantMsg: ChatMessage = { role: 'assistant', content: '', timestamp: Date.now() };
        setMessages(prev => [...prev, assistantMsg]);

        let acc = '';
        await chatModelApiStream({
          modelApiId: record.id,
          userMessage: text,
          imageUrl: imageUrl
        }, (delta) => {
          acc += delta;
          setMessages(prev => {
            const arr = [...prev];
            // 更新最后一条助手消息
            for (let i = arr.length - 1; i >= 0; i--) {
              if (arr[i].role === 'assistant') {
                arr[i] = { ...arr[i], content: acc };
                break;
              }
            }
            return arr;
          });
        });
      } else {
        const resp = await chatModelApiNonStream({
          modelApiId: record.id,
          userMessage: text,
          imageUrl: imageUrl
        });
        const assistantMsg: ChatMessage = {
          role: 'assistant',
          content: resp?.content || '',
          timestamp: Date.now()
        };
        setMessages(prev => [...prev, assistantMsg]);
      }
    } catch (err) {
      console.error('发送失败', err);
      Toast.error('发送失败，请稍后重试');
    } finally {
      setSending(false);
    }
  };

  return (
    <Modal
      visible={visible}
      title={title}
      onCancel={onCancel}
      footer={null}
      width={1000}
      maskClosable={false}
    >
      <Container>
        <LeftBar>
          <Space align="center">
            <Switch checked={streaming} onChange={setStreaming} />
            <Text>流式输出</Text>
          </Space>
          <Divider margin={8} />
          <Text type="tertiary">切换使用不同后端接口</Text>
          <Text type="secondary" size="small">
            {streaming ? '/chat-stream (SSE)' : '/chat-non-stream'}
          </Text>
        </LeftBar>
        <ChatArea>
          <History>
            {messages.map((m, idx) => (
              <MessageRow key={idx} $role={m.role}>
                <Bubble $role={m.role}>
                  <Text strong={m.role === 'user'} type={m.role === 'user' ? 'primary' : 'secondary'}>
                    {m.role === 'user' ? '我' : '模型'}
                  </Text>
                  <Divider margin={8} />
                  {m.imageUrl && (
                    <>
                      <ImagePreview src={m.imageUrl} alt="上传的图片" />
                      <Divider margin={8} />
                    </>
                  )}
                  {m.content || (m.role === 'assistant' ? '...' : '')}
                </Bubble>
              </MessageRow>
            ))}
            <div ref={historyEndRef} />
          </History>

          <InputBar 
            ref={inputBarRef}
            $isDragOver={isDragOver}
            onDragOver={handleInputDragOver}
            onDragLeave={handleInputDragLeave}
            onDrop={handleInputDrop}
            onPaste={handlePaste}
          >
            <InputWrapper>
              {/* 图片预览和上传进度 */}
              {uploadingImages.length > 0 && (
                <ImagePreviewContainer>
                  {uploadingImages.map((img, idx) => (
                    <ImagePreviewItem key={idx}>
                      <ImagePreviewThumb src={img.preview} alt="预览" />
                      <RemoveButton onClick={() => removeImage(img.file)}>×</RemoveButton>
                      {img.status === 'uploading' && (
                        <div style={{ marginTop: 4 }}>
                          <Progress percent={img.progress} size="small" />
                          <Text type="tertiary" size="small">上传中 {img.progress}%</Text>
                        </div>
                      )}
                      {img.status === 'success' && (
                        <div style={{ marginTop: 4 }}>
                          <Text type="success" size="small">上传成功</Text>
                        </div>
                      )}
                      {img.status === 'error' && (
                        <div style={{ marginTop: 4 }}>
                          <Text type="danger" size="small">{img.error || '上传失败'}</Text>
                        </div>
                      )}
                    </ImagePreviewItem>
                  ))}
                </ImagePreviewContainer>
              )}
              <TextArea
                value={input}
                onChange={setInput}
                placeholder={isDragOver ? "松开鼠标以上传图片..." : "输入消息... (可拖拽图片到此处上传)"}
                disabled={sending}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    sendMessage();
                  }
                }}
                rows={3}
                style={{ resize: 'none' }}
              />
            </InputWrapper>
            <Button 
              onClick={sendMessage} 
              disabled={sending || (!input.trim() && uploadingImages.filter(img => img.status === 'success').length === 0)}
            >
              {sending ? '发送中...' : '发送'}
            </Button>
          </InputBar>
        </ChatArea>
      </Container>
    </Modal>
  );
};