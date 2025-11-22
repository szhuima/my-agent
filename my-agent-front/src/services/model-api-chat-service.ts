import {API_ENDPOINTS, DEFAULT_HEADERS} from '../config/api';
import {apiRequestData} from '../utils/request';

export interface ModelApiChatRequest {
  modelApiId: number;
  userMessage: string;
  imageUrl?: string; // 图片URL（上传后的文件名）
}

export interface ChatMessageResponse {
  sessionId?: string;
  content: string;
}

/**
 * 非流式对话：调用后台 /admin/model-api/chat-non-stream
 */
export async function chatModelApiNonStream(req: ModelApiChatRequest): Promise<ChatMessageResponse> {
  const url = `${API_ENDPOINTS.MODEL_API.BASE}/chat-non-stream`;
  const data = await apiRequestData<ChatMessageResponse>(url, {
    method: 'POST',
    headers: {
      ...DEFAULT_HEADERS,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(req),
  });
  return data;
}

/**
 * 流式对话：调用后台 /admin/model-api/chat-stream (SSE)
 * 增量回调 onDelta 以便前端实时渲染
 */
export async function chatModelApiStream(
  req: ModelApiChatRequest,
  onDelta: (delta: string) => void
): Promise<ChatMessageResponse> {
  const url = `${API_ENDPOINTS.MODEL_API.BASE}/chat-stream`;

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
}