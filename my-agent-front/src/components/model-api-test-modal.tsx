import React, {useEffect, useMemo, useRef, useState} from 'react';
import {Button, Divider, Input, Modal, Space, Switch, Toast, Typography} from '@douyinfe/semi-ui';
import styled from 'styled-components';
import {AiClientApiResponseDTO} from '../services/model-api-service';
import {chatModelApiNonStream, chatModelApiStream} from '../services/model-api-chat-service';

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

const InputBar = styled.div`
  padding: 12px;
  border-top: 1px solid #e6e6e6;
  display: flex;
  gap: 8px;
`;

export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  timestamp: number;
}

interface Props {
  visible: boolean;
  record: AiClientApiResponseDTO | null;
  onCancel: () => void;
}

export const ModelApiTestModal: React.FC<Props> = ({ visible, record, onCancel }) => {
  const [streaming, setStreaming] = useState<boolean>(true);
  const [input, setInput] = useState<string>('');
  const [sending, setSending] = useState<boolean>(false);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const historyEndRef = useRef<HTMLDivElement | null>(null);

  const title = useMemo(() => {
    if (!record) return '测试模型 API';
    return `测试：${record.modelApiName || record.modelName || record.apiId}`;
  }, [record]);

  useEffect(() => {
    if (!visible) {
      setMessages([]);
      setInput('');
      setSending(false);
    }
  }, [visible]);

  useEffect(() => {
    if (historyEndRef.current) {
      historyEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [messages]);

  const sendMessage = async () => {
    if (!record || !record.id) {
      Toast.error('缺少模型API配置ID');
      return;
    }
    const text = input.trim();
    if (!text) return;
    setSending(true);
    const now = Date.now();
    const userMsg: ChatMessage = { role: 'user', content: text, timestamp: now };
    setMessages(prev => [...prev, userMsg]);
    setInput('');

    try {
      if (streaming) {
        // 先插入一个空的助手消息，随后逐步填充
        const assistantMsg: ChatMessage = { role: 'assistant', content: '', timestamp: Date.now() };
        setMessages(prev => [...prev, assistantMsg]);

        let acc = '';
        await chatModelApiStream({ modelApiId: record.id, userMessage: text }, (delta) => {
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
        const resp = await chatModelApiNonStream({ modelApiId: record.id, userMessage: text });
        const assistantMsg: ChatMessage = { role: 'assistant', content: resp?.content || '', timestamp: Date.now() };
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
                  {m.content || (m.role === 'assistant' ? '...' : '')}
                </Bubble>
              </MessageRow>
            ))}
            <div ref={historyEndRef} />
          </History>
          <InputBar>
            <Input
              value={input}
              placeholder="在此输入测试消息..."
              onChange={setInput}
              onEnterPress={() => !sending && sendMessage()}
            />
            <Button type="primary" onClick={sendMessage} loading={sending} disabled={!input.trim()}>
              发送
            </Button>
            <Button onClick={onCancel}>关闭</Button>
          </InputBar>
        </ChatArea>
      </Container>
    </Modal>
  );
};
