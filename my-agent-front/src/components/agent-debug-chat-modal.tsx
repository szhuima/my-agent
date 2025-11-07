import React, {useEffect, useMemo, useRef, useState} from "react";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import remarkBreaks from "remark-breaks";
import hljs from "highlight.js";
import "highlight.js/styles/github.css";
import {Button, Divider, Input, Modal, Space, Switch, TextArea, Toast, Typography,} from "@douyinfe/semi-ui";
import {API_ENDPOINTS, DEFAULT_HEADERS} from "../config";
import {AiClientResponseDTO} from "../services/ai-client-admin-service";
import {
    ChatMessage,
    clearChatHistory,
    clearChatServerMemory,
    loadChatHistory,
    saveChatHistory,
    sendChatMessage,
    sendChatMessageStream,
} from "../services/chat-debug-service";

interface ClientDebugChatModalProps {
  visible: boolean;
  onCancel: () => void;
  client: AiClientResponseDTO;
  userId: string;
  width?: number;
}

export const AgentDebugChatModal: React.FC<ClientDebugChatModalProps> = ({
  visible,
  onCancel,
  client,
  userId,
  width,
}) => {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [input, setInput] = useState<string>("");
  const [sending, setSending] = useState<boolean>(false);
  const [streaming, setStreaming] = useState<boolean>(true);
  const [contextVars, setContextVars] = useState<{ key: string; value: string }[]>([
    { key: "", value: "" },
  ]);
  // 本地存储键前缀与工具函数
  const VARS_STORAGE_KEY_PREFIX = "client_debug_ctx_vars";
  const getVarsStorageKey = (
    clientId: string | number | undefined,
    user: string | number | undefined
  ) => {
    const cid = clientId !== undefined && clientId !== null ? String(clientId) : "";
    const uid = user !== undefined && user !== null ? String(user) : "";
    return `${VARS_STORAGE_KEY_PREFIX}:${cid}@${uid}`;
  };
  const saveContextVarsToStorage = () => {
    try {
      const key = getVarsStorageKey(client?.id, userId);
      const data = contextVars.map((kv) => ({ key: kv.key.trim(), value: kv.value }))
        .filter((kv) => kv.key.length > 0);
      localStorage.setItem(key, JSON.stringify(data));
      Toast.success({ content: "上下文变量已保存" });
    } catch (e) {
      Toast.error({ content: "保存失败" });
    }
  };
  const loadContextVarsFromStorage = (): { key: string; value: string }[] => {
    try {
      const key = getVarsStorageKey(client?.id, userId);
      const raw = localStorage.getItem(key);
      if (!raw) return [{ key: "", value: "" }];
      const arr = JSON.parse(raw);
      if (Array.isArray(arr) && arr.length > 0) {
        return arr;
      }
      return [{ key: "", value: "" }];
    } catch {
      return [{ key: "", value: "" }];
    }
  };
  const clearContextVarsInStorage = () => {
    try {
      const key = getVarsStorageKey(client?.id, userId);
      localStorage.removeItem(key);
      setContextVars([{ key: "", value: "" }]);
      Toast.success({ content: "上下文变量已清空" });
    } catch {
      Toast.error({ content: "清空失败" });
    }
  };
  const listRef = useRef<HTMLDivElement | null>(null);
  const inputContainerRef = useRef<HTMLDivElement | null>(null);
  const [varPickerOpen, setVarPickerOpen] = useState<boolean>(false);
  const [varPickerPos, setVarPickerPos] = useState<{ left: number; top: number }>({ left: 0, top: 0 });
  const [varPickerMaxHeight, setVarPickerMaxHeight] = useState<number>(220);
  const [highlightIndex, setHighlightIndex] = useState<number>(0);
  const [varTriggerIndex, setVarTriggerIndex] = useState<number | null>(null);

  const variableKeys = useMemo(
    () =>
      contextVars
        .map((kv) => kv.key.trim())
        .filter((k) => k.length > 0),
    [contextVars]
  );

  // 打开调试弹窗时加载之前保存的上下文变量
  useEffect(() => {
    if (visible && client?.id && userId) {
      const loaded = loadContextVarsFromStorage();
      setContextVars(loaded.length > 0 ? loaded : [{ key: "", value: "" }]);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [visible, client?.id, userId]);

  // 计算 textarea 光标的屏幕位置（使用镜像元素法），并在底部溢出时向上翻转弹层
  const computeCaretPosition = (ta: HTMLTextAreaElement) => {
    const container = inputContainerRef.current;
    if (!container) return;
    const style = window.getComputedStyle(ta);
    const mirror = document.createElement("div");
    const span = document.createElement("span");
    mirror.style.position = "absolute";
    mirror.style.visibility = "hidden";
    mirror.style.whiteSpace = "pre-wrap";
    mirror.style.wordWrap = "break-word";
    mirror.style.boxSizing = "border-box";
    mirror.style.width = ta.clientWidth + "px";
    mirror.style.fontFamily = style.fontFamily;
    mirror.style.fontSize = style.fontSize;
    mirror.style.lineHeight = style.lineHeight;
    mirror.style.letterSpacing = style.letterSpacing as string;
    mirror.style.padding = style.padding;
    mirror.style.border = style.border;
    mirror.style.left = ta.offsetLeft + "px";
    mirror.style.top = ta.offsetTop + "px";
    const caretIndex = ta.selectionEnd ?? ta.value.length;
    const valBeforeCaret = ta.value.substring(0, caretIndex).replace(/\n$/, "\n ");
    mirror.textContent = ""; // reset
    const textNode = document.createTextNode(valBeforeCaret);
    mirror.appendChild(textNode);
    mirror.appendChild(span);
    container.appendChild(mirror);
    // 将弹层定位到光标附近（考虑滚动偏移）
    const containerRect = container.getBoundingClientRect();
    const spanRect = span.getBoundingClientRect();
    const left = spanRect.left - containerRect.left - ta.scrollLeft + 8; // 微调偏移
    const lineHeightNum = parseFloat(style.lineHeight || "16");
    // 默认在光标下方，留更大安全距离
    let topCandidate = spanRect.top - containerRect.top - ta.scrollTop + lineHeightNum + 12;
    // 估算弹层高度（有标题 + 若干项，最大 220）
    const headerH = 32; // 标题区域大致高度
    const itemH = 34; // 每项高度估算
    const estimatedHeight = Math.min(220, headerH + itemH * Math.max(1, variableKeys.length));
    // 计算绝对底部位置（相对视口）
    const absoluteBottom = containerRect.top + topCandidate + estimatedHeight;
    const viewportBottom = window.innerHeight - 32; // 留更大 32px 安全边距
    // 如果会溢出视口底部，则向上翻转
    if (absoluteBottom > viewportBottom) {
      const upwardExtra = 28; // 进一步提高向上弹出位置
      topCandidate = spanRect.top - containerRect.top - ta.scrollTop - estimatedHeight - upwardExtra; // 向上额外提升
      // 防止过高导致不可见，做下限钳制
      const minTop = 4;
      const maxTop = Math.max(minTop, container.clientHeight - estimatedHeight - 8);
      topCandidate = Math.max(minTop, Math.min(topCandidate, maxTop));
    }
    // 根据容器内可用空间动态限制弹层最大高度，避免底部被裁切
    const availableDown = container.clientHeight - topCandidate - 8;
    const availableUp = topCandidate - 8;
    const isUpward = (containerRect.top + topCandidate + estimatedHeight) > viewportBottom;
    const availableSpace = isUpward ? availableUp : availableDown;
    const maxHeightForPopup = Math.max(120, Math.min(220, availableSpace));
    setVarPickerMaxHeight(maxHeightForPopup);
    setVarPickerPos({ left, top: topCandidate });
    container.removeChild(mirror);
  };

  const insertVariableAtTrigger = (key: string) => {
    const ta = document.getElementById("chat-debug-input") as HTMLTextAreaElement | null;
    const current = input ?? "";
    let start = varTriggerIndex ?? Math.max(0, (ta?.selectionStart ?? current.length) - 2);
    const end = ta?.selectionStart ?? current.length;
    // 防御：确保从 '{{' 开始替换
    if (current.slice(start, end) !== "{{") {
      // 兜底：查找最近的 '{{'
      const fallback = current.lastIndexOf("{{", end);
      start = fallback >= 0 ? fallback : end;
    }
    const next = current.slice(0, start) + `{{${key}}}` + current.slice(end);
    setInput(next);
    setVarPickerOpen(false);
    setVarTriggerIndex(null);
  };

  const handleInputChange = (val: string) => {
    setInput(val ?? "");
    const ta = document.getElementById("chat-debug-input") as HTMLTextAreaElement | null;
    const shouldOpen = /\{\{$/.test(val);
    if (shouldOpen && variableKeys.length > 0 && ta) {
      setVarPickerOpen(true);
      setHighlightIndex(0);
      // 记录触发位置（'{{' 的起始索引），用于后续替换
      const caret = ta.selectionStart ?? val.length;
      setVarTriggerIndex(Math.max(0, caret - 2));
      // 计算弹层位置
      computeCaretPosition(ta);
    } else {
      setVarPickerOpen(false);
      setVarTriggerIndex(null);
    }
  };

  const handleInputKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    // 当变量选择弹层打开时，处理导航与选择
    if (varPickerOpen) {
      if (variableKeys.length === 0) return;
      if (e.key === "ArrowDown") {
        e.preventDefault();
        setHighlightIndex((idx) => (idx + 1) % variableKeys.length);
      } else if (e.key === "ArrowUp") {
        e.preventDefault();
        setHighlightIndex((idx) => (idx - 1 + variableKeys.length) % variableKeys.length);
      } else if (e.key === "Enter") {
        e.preventDefault();
        e.stopPropagation();
        insertVariableAtTrigger(variableKeys[highlightIndex]);
      } else if (e.key === "Escape") {
        e.preventDefault();
        setVarPickerOpen(false);
        setVarTriggerIndex(null);
      }
      return;
    }

    // 常规输入：Enter 发送，Shift+Enter 换行
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      e.stopPropagation();
      handleSend();
    }
  };

  // 右侧接口信息预览（根据当前设置和输入实时计算）
  const apiPreview = useMemo(() => {
    // 计算 URL 与请求头
    const url = `${API_ENDPOINTS.CLIENT_CHAT.BASE}${
      streaming ? API_ENDPOINTS.CLIENT_CHAT.CHAT_STREAM : API_ENDPOINTS.CLIENT_CHAT.CHAT_NONE_STREAM
    }`;
    let authHeader: Record<string, string> = {};
    try {
      const token = localStorage.getItem("token");
      if (token) authHeader["Authorization"] = `Bearer ${token}`;
    } catch {}

    const headers = streaming
      ? {
          "Content-Type": "application/json",
          Accept: "text/event-stream",
          ...authHeader,
        }
      : {
          ...DEFAULT_HEADERS,
          ...authHeader,
        };

    // 计算请求体（与实际发送保持一致）
    const ctxObj = Object.fromEntries(
      contextVars
        .filter((kv) => kv.key.trim() !== "")
        .map((kv) => [kv.key.trim(), kv.value])
    );
    const body = {
      clientId: client.id,
      userMessage: input.trim(),
      sessionId: client.id + "@" + userId,
      context: ctxObj,
    };

    // 解析查询参数
    let query: Record<string, string> = {};
    try {
      const u = new URL(url);
      u.searchParams.forEach((v, k) => {
        query[k] = v;
      });
    } catch {}

    return {
      url,
      method: "POST",
      headers,
      query,
      body,
    } as const;
  }, [streaming, input, client.id, userId, contextVars]);

  // 组装 cURL 命令文本
  const buildCurlFromPreview = (preview: {
    url: string;
    method: string;
    headers: Record<string, string>;
    body?: unknown;
  }) => {
    const headerLines = Object.entries(preview.headers || {})
      .map(([k, v]) => `  -H '${k}: ${v}' \\\n`)
      .join("");
    const bodyJson = preview.body !== undefined ? JSON.stringify(preview.body) : "";
    const bodyLine = bodyJson
      ? `  --data-raw '${bodyJson.replace(/'/g, "'\\''")}'`
      : "";
    const curl = `curl -X ${preview.method} '${preview.url}' \\\n${headerLines}${bodyLine}`.trim();
    return curl;
  };

  const handleCopyCurl = async () => {
    try {
      const curlText = buildCurlFromPreview(apiPreview);
      if (navigator.clipboard && navigator.clipboard.writeText) {
        await navigator.clipboard.writeText(curlText);
      } else {
        const textarea = document.createElement('textarea');
        textarea.value = curlText;
        document.body.appendChild(textarea);
        textarea.select();
        document.execCommand('copy');
        document.body.removeChild(textarea);
      }
      Toast.success('cURL 已复制');
    } catch (err) {
      console.error('复制 cURL 失败:', err);
      Toast.error('复制失败，请稍后重试');
    }
  };

  const storageKey = useMemo(() => {
    const cid = client.id;
    const uid = userId;
    return `chat_debug_${uid}_${cid}`;
  }, [client, userId]);

  // 对话ID
  const sessionId = useMemo(() => {
    const sid = client.id + "@" + userId;
    console.log("sessionId", sid);
    return sid;
  }, [client, userId]);
  
  
  useEffect(() => {
    if (visible) {
      const cached = loadChatHistory(storageKey);
      setMessages(cached);
    }
  }, [visible, storageKey]);

  useEffect(() => {
    // Auto scroll to bottom when messages change
    if (listRef.current) {
      listRef.current.scrollTop = listRef.current.scrollHeight;
    }
  }, [messages]);

  const handleSend = async () => {
    // 当变量选择弹层打开时，Enter 不触发发送
    if (varPickerOpen) {
      setVarPickerOpen(false);
      return;
    }
    const text = input.trim();
    if (!text) {
      Toast.warning("请输入聊天消息");
      return;
    }
    setSending(true);
    const now = Date.now();
    const basePayload = {
      clientId: client.id,
      userMessage: text,
      sessionId: sessionId,
      context: Object.fromEntries(
        contextVars
          .filter((kv) => kv.key.trim() !== "")
          .map((kv) => [kv.key.trim(), kv.value])
      ),
    } as const;

    // 根据开关选择流式或普通发送
    if (streaming) {
      const startMessages: ChatMessage[] = [
        ...messages,
        { role: "user", content: text, timestamp: now },
      ];
      const placeholder: ChatMessage = {
        role: "assistant",
        content: "",
        timestamp: Date.now(),
      };
      const withAssistant = [...startMessages, placeholder];
      setMessages(withAssistant);
      setInput("");
      // 初次保存，包含占位的 assistant
      saveChatHistory(storageKey, withAssistant);

      try {
        const resp = await sendChatMessageStream(basePayload, (delta: string) => {
          // 累加最新一条 assistant 的内容
          setMessages((prev) => {
            const next = [...prev];
            const lastIdx = next.length - 1;
            if (lastIdx >= 0 && next[lastIdx].role === "assistant") {
              next[lastIdx] = {
                ...next[lastIdx],
                content: next[lastIdx].content + delta,
              };
            }
            return next;
          });
        });
        // 流式结束后，保存最终消息内容
        setMessages((prev) => {
          const next = [...prev];
          // 确保最后一条 assistant 已有最终内容
          saveChatHistory(storageKey, next);
          return next;
        });
      } catch (err) {
        console.error(err);
        Toast.error("消息发送失败");
      } finally {
        setSending(false);
      }
    } else {
      const newMessages: ChatMessage[] = [
        ...messages,
        { role: "user", content: text, timestamp: now },
      ];
      setMessages(newMessages);
      setInput("");
      saveChatHistory(storageKey, newMessages);

      try {
        const resp = await sendChatMessage(basePayload);
        const assistantMsg: ChatMessage = {
          role: "assistant",
          content: resp.content,
          timestamp: Date.now(),
        };
        const final = [...newMessages, assistantMsg];
        setMessages(final);
        saveChatHistory(storageKey, final);
      } catch (err) {
        console.error(err);
        Toast.error("消息发送失败");
      } finally {
        setSending(false);
      }
    }
  };

  const handleClear = async () => {
    setMessages([]);
    clearChatHistory(storageKey);
    try {
      await clearChatServerMemory(client?.id as number, sessionId);
      Toast.success("对话记忆已清空");
    } catch (e) {
      // 错误提示在服务内已处理，这里做兜底
      Toast.error("后端清空记忆失败");
    }
  };

  const handleCancel = () => {
    onCancel();
  };

  const title = `调试 - ${client?.agentName ?? ""}`;

  // 从系统提示词中解析 {{var}} 变量名，作为上下文变量的 key 预填
  const extractContextKeysFromPrompt = (prompt?: string): string[] => {
    if (!prompt) return [];
    const regex = /\{\{\s*([^}]+?)\s*\}\}/g;
    const keys = new Set<string>();
    let match: RegExpExecArray | null;
    while ((match = regex.exec(prompt)) !== null) {
      const key = match[1].trim();
      if (key) keys.add(key);
    }
    return Array.from(keys);
  };

  // 弹窗打开或 systemPrompt 变化时，自动预填上下文变量的 key
  useEffect(() => {
    if (!visible) return;
    const keys = extractContextKeysFromPrompt(client?.systemPrompt);
    if (keys.length === 0) return;
    setContextVars((prev: { key: string; value: string }[]) => {
      const prevMap = new Map(prev.map((kv) => [kv.key.trim(), kv.value]));
      const next = keys.map((k) => ({ key: k, value: prevMap.get(k) ?? "" }));
      return next.length > 0 ? next : prev;
    });
  }, [client?.systemPrompt, visible]);

  // 上下文变量操作（移入组件内部并补充类型）
  const addContextVar = () => {
    setContextVars((prev: { key: string; value: string }[]) => [
      ...prev,
      { key: "", value: "" },
    ]);
  };

  const removeContextVar = (index: number) => {
    setContextVars((prev: { key: string; value: string }[]) => {
      const next = [...prev];
      next.splice(index, 1);
      return next.length === 0 ? [{ key: "", value: "" }] : next;
    });
  };

  const updateContextVar = (
    index: number,
    field: "key" | "value",
    value: string
  ) => {
    setContextVars((prev: { key: string; value: string }[]) => {
      const next = [...prev];
      next[index] = { ...next[index], [field]: value };
      return next;
    });
  };

  return (
    <Modal
      title={title}
      visible={visible}
      onCancel={handleCancel}
      footer={null}
      width={width ?? 1580}
      style={{ height: "95vh" }}
      maskClosable={false}
      bodyStyle={{
        maxHeight: "92vh",
        minHeight: "82vh",
        overflow: "hidden",
        display: "flex",
        flexDirection: "column",
        paddingBottom: 0,
      }}
    >
      <div
        style={{
          display: "flex",
          flexDirection: "row",
          flex: 1,
          minHeight: 0,
          overflow: "hidden",
          gap: 12,
        }}
      >
        {/* 左侧：对话调试设置 */}
        <div
          style={{
            width: 260,
            flexShrink: 0,
            padding: 12,
            borderRadius: 8,
            background: "var(--bg-tertiary)",
            overflowY: "auto",
            minHeight: 0,
          }}
        >
          <div
            style={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
            }}
          >
            <Typography.Text strong>对话调试设置</Typography.Text>
            <Space>
              <Button type="secondary" onClick={saveContextVarsToStorage}>
                保存
              </Button>
              <Button type="tertiary" onClick={clearContextVarsInStorage}>
                清空
              </Button>
            </Space>
          </div>
          <Divider margin={12} />

          <div style={{ display: "flex", alignItems: "left", gap: 8 }}>
            <span style={{ width: 100, textAlign: "right" }}>
              开启流式对话:
            </span>
            <Switch checked={streaming} onChange={setStreaming} />
          </div>

          <Divider margin={12} />
          <Typography.Text type="secondary">上下文变量</Typography.Text>
          <div style={{ marginTop: 8 }}>
            {contextVars.map((kv, idx) => (
              <div
                key={idx}
                style={{ display: "flex", gap: 8, marginBottom: 8 }}
              >
                <Input
                  placeholder="变量名 key"
                  value={kv.key}
                  onChange={(val: string) => updateContextVar(idx, "key", val)}
                  style={{ width: 100 }}
                />
                <Input
                  placeholder="变量值 value"
                  value={kv.value}
                  onChange={(val: string) =>
                    updateContextVar(idx, "value", val)
                  }
                  style={{ flex: 1 }}
                />
                <Button type="tertiary" onClick={() => removeContextVar(idx)}>
                  -
                </Button>
              </div>
            ))}
            <Button type="secondary" onClick={addContextVar} block>
              新增变量
            </Button>
          </div>
        </div>

        {/* 右侧：聊天区域 */}
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            flex: 1,
            minHeight: 0,
            overflow: "hidden",
          }}
        >
          <div
            style={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
            }}
          >
            <Typography.Text type="secondary">
              用户: {userId || "anonymous"}，客户端ID: {client?.id || "-"}
            </Typography.Text>
            <Space>
              <Button onClick={handleClear} type="tertiary">
                清空对话历史
              </Button>
            </Space>
          </div>
          <Divider margin={12} />

          {/* Chat history area */}
          <div
            ref={listRef}
            style={{
              flex: 1,
              minHeight: 0,
              overflowY: "auto",
              padding: 12,
              borderRadius: 6,
              background: "var(--bg-tertiary)",
            }}
          >
            {messages.length === 0 ? (
              <Typography.Text type="tertiary">
                暂无聊天记录，输入消息开始调试
              </Typography.Text>
            ) : (
              messages.map((msg, idx) => (
                <div
                  key={idx}
                  style={{
                    display: "flex",
                    marginBottom: 8,
                    justifyContent:
                      msg.role === "user" ? "flex-end" : "flex-start",
                  }}
                >
                  <div
                    style={{
                      maxWidth: "70%",
                      padding: "8px 12px",
                      borderRadius: 8,
                      background:
                        msg.role === "user" ? "var(--bg-secondary)" : "#fff",
                      color:
                        msg.role === "user" ? "var(--text-primary)" : "#000",
                      boxShadow: "0 1px 2px rgba(0,0,0,0.06)",
                    }}
                  >
                    <div style={{ wordBreak: "break-word", whiteSpace: "pre-wrap" }}>
                      <ReactMarkdown
                        remarkPlugins={[remarkGfm, remarkBreaks]}
                        components={markdownComponents}
                      >
                        {msg.content}
                      </ReactMarkdown>
                    </div>
                    {/* <Typography.Text type="tertiary" style={{ fontSize: 11, color: 'var(--text-tertiary)' }}>
                      {new Date(msg.timestamp).toLocaleString()}
                    </Typography.Text> */}
                  </div>
                </div>
              ))
            )}
          </div>

          {/* Input area */}
          <div
            style={{
              display: "flex",
              alignItems: "flex-end",
              gap: 8,
              marginTop: 12,
              flexShrink: 0,
            }}
          >
            <div
              style={{ position: "relative", flex: 1 }}
              ref={inputContainerRef}
            >
              <TextArea
                id="chat-debug-input"
                value={input}
                onChange={handleInputChange}
                onKeyDown={handleInputKeyDown}
                placeholder="请输入用户消息, 按 Enter 发送 , 按 Shift+Enter 换行 , 输入 {{变量名}} 引用上下文变量"
                rows={5}
                style={{ resize: "none", minHeight: 120 }}
              />
              {varPickerOpen && (
                <div
                  style={{
                    position: "absolute",
                    left: varPickerPos.left,
                    top: varPickerPos.top,
                    zIndex: 1000,
                    background: "#fff",
                    border: "1px solid var(--border-primary)",
                    boxShadow: "0 4px 12px rgba(0,0,0,0.12)",
                    borderRadius: 8,
                    minWidth: 160,
                    maxHeight: varPickerMaxHeight,
                    overflowY: "auto",
                  }}
                  onMouseDown={(e) => e.preventDefault()}
                >
                  <div
                    style={{
                      padding: "6px 8px",
                      borderBottom: "1px solid var(--border-primary)",
                    }}
                  >
                    <Typography.Text type="secondary">
                      选择上下文变量
                    </Typography.Text>
                  </div>
                  {variableKeys.map((k, i) => (
                    <div
                      key={k}
                      onMouseEnter={() => setHighlightIndex(i)}
                      onClick={() => insertVariableAtTrigger(k)}
                      style={{
                        padding: "8px 10px",
                        cursor: "pointer",
                        background:
                          i === highlightIndex
                            ? "var(--bg-tertiary)"
                            : "transparent",
                      }}
                    >
                      {k}
                    </div>
                  ))}
                </div>
              )}
            </div>
            <Button type="primary" onClick={handleSend} loading={sending}>
              发送
            </Button>
          </div>
          <div style={{ height: 40 }}></div>
        </div>

        {/* 最右侧：后端接口信息 */}
        <div
          style={{
            width: 420,
            flexShrink: 0,
            padding: 12,
            borderRadius: 8,
            background: "var(--bg-tertiary)",
            overflow: "auto",
          }}
        >
          <div
            style={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
            }}
          >
            <Typography.Text strong>接口信息</Typography.Text>
            <Button type="tertiary" onClick={handleCopyCurl}>
              复制 cURL
            </Button>
          </div>
          <Divider margin={12} />
          <div style={{ marginBottom: 8 }}>
            <div style={{ wordBreak: "break-word" }}>
              <pre>
                {apiPreview.method} {apiPreview.url}
              </pre>
            </div>
          </div>

          <div style={{ marginBottom: 8 }}>
            <Typography.Text type="secondary">
              请求头:
              <pre style={{ margin: 0 }}>
                <code>{JSON.stringify(apiPreview.headers, null, 2)}</code>
              </pre>
            </Typography.Text>
          </div>
          <div style={{ marginBottom: 8 }}>
            <Typography.Text type="secondary">
              请求参数:
              <pre style={{ margin: 0 }}>
                <code>{JSON.stringify(apiPreview.query, null, 2)}</code>
              </pre>
            </Typography.Text>
          </div>
          <div>
            <Typography.Text type="secondary">
              请求体:
              <pre style={{ margin: 0 }}>
                <code>{JSON.stringify(apiPreview.body, null, 2)}</code>
              </pre>
            </Typography.Text>
          </div>

          <Divider margin={12} />
          <Typography.Text strong>参数说明</Typography.Text>
          <div style={{ fontSize: 12, lineHeight: "20px", marginTop: 8 }}>
            <div>
              <Typography.Text type="secondary">Authorization</Typography.Text>
              ：请求头，格式为 <code>Bearer &lt;token&gt;</code>。
            </div>
            <div>
              <Typography.Text type="secondary">clientId</Typography.Text>
              :客户端ID。必填字段。
            </div>
            <div>
              <Typography.Text type="secondary">userMessage</Typography.Text>
              ：用户输入的消息文本,可选，可以引用上下文变量。例如:
              <code>{"{{city}}"}</code> 引用名为 <code>city</code> 的上下文变量
            </div>
            <div>
              <Typography.Text type="secondary">sessionId</Typography.Text>
              ：会话标识，可选,用于获取之前历史对话，实现对话记忆功能。
            </div>
            <div>
              <Typography.Text type="secondary">context</Typography.Text>
              ：上下文变量键值对，来源于左侧“上下文变量”设置，空键已自动过滤。
            </div>
          </div>
        </div>
      </div>
    </Modal>
  );
};
  // Markdown 渲染组件映射：链接与代码块
  const markdownComponents = {
    a: (props: any) => (
      <a {...props} target="_blank" rel="noopener noreferrer" />
    ),
    code: ({ inline, className, children, ...props }: any) => {
      const language = ((className || "").match(/language-(\w+)/)?.[1]) ?? "";
      const content = String(children).replace(/\n$/, "");
      if (!inline) {
        let html = "";
        try {
          html = language
            ? hljs.highlight(content, { language }).value
            : hljs.highlightAuto(content).value;
        } catch {
          html = content;
        }
        return (
          <pre style={{ margin: 0 }}>
            <code
              className={`hljs ${className || ""}`}
              dangerouslySetInnerHTML={{ __html: html }}
            />
          </pre>
        );
      }
      return (
        <code className={className} {...props}>
          {children}
        </code>
      );
    },
  } as const;
