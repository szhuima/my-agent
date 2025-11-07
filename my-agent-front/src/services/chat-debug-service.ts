import {Toast} from '@douyinfe/semi-ui';
import {API_ENDPOINTS, DEFAULT_HEADERS} from '../config';
import {apiRequestData} from '../utils/request';

export interface ChatRequest {
  clientId: number;
  userMessage: string;
  sessionId: string;
  streaming?: boolean;
  context?: Record<string, string>;
}

export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  timestamp: number;
}

export interface ChatResponse {
  content: string;
}

// 定义API响应格式
export interface ApiResponse<T> {
  code: string;
  info: string;
  data: T;
}

/**
 * Simulate a backend chat API. Accepts userId and current message.
 * Returns a mocked assistant reply after a short delay.
 */
export async function sendChatMessage(req: ChatRequest): Promise<ChatResponse> {
  try {
    // 当开启流式对话时，走 /chat-stream SSE 接口
    if (req.streaming) {
      const url = API_ENDPOINTS.CLIENT_CHAT.BASE + API_ENDPOINTS.CLIENT_CHAT.CHAT_STREAM;
      // 注入鉴权头（与统一请求方法保持一致）
      let authHeader: Record<string, string> = {};
      try {
        const token = localStorage.getItem('token');
        if (token) authHeader['Authorization'] = `Bearer ${token}`;
      } catch {}

      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'text/event-stream',
          ...authHeader,
        },
        body: JSON.stringify(req),
      });

      if (!response.ok || !response.body) {
        throw new Error(`Stream request failed: ${response.status}`);
      }

      const reader = response.body.getReader();
      const decoder = new TextDecoder('utf-8');
      let fullText = '';
      let buffer = '';

      // 读取 SSE 数据流并拼接 data 字段
      while (true) {
        const { value, done } = await reader.read();
        if (done) break;
        const chunk = decoder.decode(value, { stream: true });
        buffer += chunk;

        // 优先解析标准 SSE 的 data: 行
        const lines = buffer.split(/\n/);
        // 如果最后一行不是完整行，暂存到 buffer，其他行处理
        buffer = lines.pop() || '';
        for (const line of lines) {
          const trimmed = line.trim();
          if (trimmed.startsWith('data:')) {
            fullText += trimmed.replace(/^data:\s?/, '');
          } else if (trimmed.length > 0) {
            // 非标准 SSE 行也做兼容拼接
            fullText += trimmed;
          }
        }
      }

      // 处理剩余的缓冲数据
      const rest = buffer.trim();
      if (rest) {
        if (rest.startsWith('data:')) {
          fullText += rest.replace(/^data:\s?/, '');
        } else {
          fullText += rest;
        }
      }

      return { content: fullText || '' };
    }

    // 非流式对话，走普通 JSON 接口
    const url = API_ENDPOINTS.CLIENT_CHAT.BASE + API_ENDPOINTS.CLIENT_CHAT.CHAT_NONE_STREAM;
    const data = await apiRequestData<ChatResponse>(url, {
      method: 'POST',
      headers: DEFAULT_HEADERS,
      body: JSON.stringify(req),
    });
    return data;

  } catch (err) {
    console.error('chat API failed:', err);
    Toast.error('发送消息失败');
    return { content: '抱歉，发送失败，请稍后重试。' };
  }
}

/**
 * 读取 SSE 流并逐步回调增量内容，用于在前端实时渲染。
 */
export async function sendChatMessageStream(
  req: ChatRequest,
  onDelta: (delta: string) => void
): Promise<ChatResponse> {
  try {
    const url = API_ENDPOINTS.CLIENT_CHAT.BASE + API_ENDPOINTS.CLIENT_CHAT.CHAT_STREAM;

    let authHeader: Record<string, string> = {};
    try {
      const token = localStorage.getItem('token');
      if (token) authHeader['Authorization'] = `Bearer ${token}`;
    } catch {}

    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'text/event-stream',
        ...authHeader,
      },
      body: JSON.stringify(req),
    });

    if (!response.ok || !response.body) {
      throw new Error(`Stream request failed: ${response.status}`);
    }

    const reader = response.body.getReader();
    const decoder = new TextDecoder('utf-8');
    let fullText = '';
    let buffer = '';

    while (true) {
      const { value, done } = await reader.read();
      if (done) break;
      const chunk = decoder.decode(value, { stream: true });
      buffer += chunk;

      const lines = buffer.split(/\n/);
      buffer = lines.pop() || '';
      for (const line of lines) {
        const trimmed = line.trim();
        if (trimmed.startsWith('data:')) {
          const payload = trimmed.replace(/^data:\s?/, '');
          fullText += payload;
          onDelta(payload);
        } else if (trimmed.length > 0) {
          fullText += trimmed;
          onDelta(trimmed);
        }
      }
    }

    const rest = buffer.trim();
    if (rest) {
      if (rest.startsWith('data:')) {
        const payload = rest.replace(/^data:\s?/, '');
        fullText += payload;
        onDelta(payload);
      } else {
        fullText += rest;
        onDelta(rest);
      }
    }

    return { content: fullText };
  } catch (err) {
    console.error('chat stream failed:', err);
    Toast.error('发送消息失败');
    return { content: '' };
  }
}

/**
 * Optional: persist chat history to localStorage by key.
 */
export function saveChatHistory(key: string, messages: ChatMessage[]) {
  try {
    localStorage.setItem(key, JSON.stringify(messages));
  } catch (err) {
    console.warn('Failed to save chat history:', err);
  }
}

export function loadChatHistory(key: string): ChatMessage[] {
  try {
    const raw = localStorage.getItem(key);
    if (!raw) return [];
    const parsed = JSON.parse(raw);
    if (Array.isArray(parsed)) return parsed as ChatMessage[];
    return [];
  } catch {
    return [];
  }
}

export function clearChatHistory(key: string) {
  try {
    localStorage.removeItem(key);
  } catch {
    // noop
  }
}

/**
 * 调用后端接口清空对话记忆
 * POST /clear-memory/{clientId}
 */
export async function clearChatServerMemory(agentId: number, sessionId: string): Promise<void> {
  try {
    const url = `${API_ENDPOINTS.CLIENT_CHAT.BASE}${API_ENDPOINTS.CLIENT_CHAT.CLEAR_MEMORY}`.replace(
      ':agentId',
      String(agentId)
    ).replace(':sessionId', sessionId);
    let authHeader: Record<string, string> = {};
    try {
      const token = localStorage.getItem('token');
      if (token) authHeader['Authorization'] = `Bearer ${token}`;
    } catch {}
    const resp = await fetch(url, {
      method: 'POST',
      headers: {
        ...DEFAULT_HEADERS,
        ...authHeader,
      },
    });
    if (!resp.ok) {
      throw new Error(`clear memory failed: ${resp.status}`);
    }
  } catch (err) {
    console.error('后端清空对话记忆失败:', err);
    Toast.error('后端清空记忆失败');
  }
}