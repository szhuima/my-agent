/**
 * API 配置文件
 * 统一管理所有 API 的基础 URL 和相关配置
 */

// 环境变量配置
const isDevelopment = process.env.NODE_ENV === 'development';
const isProduction = process.env.NODE_ENV === 'production';

const API_BASE_DOMAIN = isDevelopment ? 'http://127.0.0.1:8093/agent-station' : 'http://myagent.natapp1.cc/agent-station';

console.log("当前ENV:", process.env.NODE_ENV, API_BASE_DOMAIN);

// 基础配置
export const API_CONFIG = {
    // 基础域名配置
    BASE_DOMAIN: API_BASE_DOMAIN,

    // API 版本
    API_VERSION: 'v1',

    // 超时配置
    TIMEOUT: 30000, // 30秒

    // 重试配置
    RETRY_TIMES: 3,
} as const;

// API 端点配置
export const API_ENDPOINTS = {
    // AI 客户端相关接口
    AI_CLIENT: {
        BASE: `${API_CONFIG.BASE_DOMAIN}/api/${API_CONFIG.API_VERSION}/admin/ai-client`,
        QUERY_ALL: '/query-all',
        QUERY_ENABLED: '/query-enabled',
    },

    // AI 客户端顾问相关接口
    AI_CLIENT_ADVISOR: {
        BASE: `${API_CONFIG.BASE_DOMAIN}/api/${API_CONFIG.API_VERSION}/admin/ai-client-advisor`,
        QUERY_ALL: '/query-all',
    },

    // AI 客户端系统提示相关接口
    AI_CLIENT_SYSTEM_PROMPT: {
        BASE: `${API_CONFIG.BASE_DOMAIN}/api/${API_CONFIG.API_VERSION}/admin/ai-client-system-prompt`,
        CREATE: '/create',
        UPDATE_BY_ID: '/update-by-id',
        UPDATE_BY_PROMPT_ID: '/update-by-prompt-id',
        DELETE_BY_ID: '/delete-by-id',
        DELETE_BY_PROMPT_ID: '/delete-by-prompt-id',
        QUERY_BY_ID: '/query-by-id',
        QUERY_BY_PROMPT_ID: '/query-by-prompt-id',
        QUERY_ALL: '/query-all',
        QUERY_ENABLED: '/query-enabled',
        QUERY_BY_PROMPT_NAME: '/query-by-prompt-name',
        QUERY_LIST: '/query-list',
    },

    // AI 客户端工具MCP相关接口
    AI_CLIENT_TOOL_MCP: {
        BASE: `${API_CONFIG.BASE_DOMAIN}/api/${API_CONFIG.API_VERSION}/admin/ai-client-tool-mcp`,
        CREATE: '/create',
        UPDATE_BY_ID: '/update-by-id',
        UPDATE_BY_MCP_ID: '/update-by-mcp-id',
        DELETE_BY_ID: '/delete-by-id',
        DELETE_BY_MCP_ID: '/delete-by-mcp-id',
        QUERY_BY_ID: '/query-by-id',
        QUERY_BY_MCP_ID: '/query-by-mcp-id',
        QUERY_ALL: '/query-all',
        QUERY_BY_STATUS: '/query-by-status',
        QUERY_BY_TRANSPORT_TYPE: '/query-by-transport-type',
        QUERY_ENABLED: '/query-enabled',
        QUERY_LIST: '/query-list',
    },

    MODEL_API: {
        BASE: `${API_CONFIG.BASE_DOMAIN}/api/${API_CONFIG.API_VERSION}/admin/model-api`,
        CREATE: '/create',
        UPDATE_BY_ID: '/update-by-id',
        UPDATE_BY_MODEL_ID: '/update-by-model-id',
        DELETE_BY_ID: '/delete-by-id',
        DELETE_BY_MODEL_ID: '/delete-by-model-id',
        QUERY_BY_ID: '/query-by-id',
        QUERY_BY_MODEL_ID: '/query-by-model-id',
        QUERY_BY_API_ID: '/query-by-api-id',
        QUERY_BY_MODEL_TYPE: '/query-by-model-type',
        QUERY_ENABLED: '/query-enabled',
        QUERY_LIST: '/query-list',
        QUERY_ALL: '/query-all',
    },

    // 工作流相关接口
    WORKFLOW: {
        BASE: `${API_CONFIG.BASE_DOMAIN}/api/${API_CONFIG.API_VERSION}/admin/workflow`,
        QUERY_LIST: '/query-list',
        GET_DSL: '/get-dsl',
        DELETE: '/delete',
        ACTIVE: '/active',
        ARCHIVE: '/archive',
        SAVE: '/save',
    },

    // 工作流实例相关接口
    WORKFLOW_INSTANCE: {
        BASE: `${API_CONFIG.BASE_DOMAIN}/api/${API_CONFIG.API_VERSION}/admin/workflow-instance`,
        QUERY_LIST: '/query-list',
        UNDEPLOY: '/un-deploy',
        DEPLOY: '/deploy',
        DELETE: '/delete',
    },

    // 工作流执行记录相关接口
    WORKFLOW_EXECUTION: {
        BASE: `${API_CONFIG.BASE_DOMAIN}/api/${API_CONFIG.API_VERSION}/admin/workflow-execution`,
        QUERY_LIST: '/query-list',
        DELETE: '/delete',
    },

    // AI 智能体相关接口
    AI_AGENT: {
        BASE: `${API_CONFIG.BASE_DOMAIN}/api/${API_CONFIG.API_VERSION}/agent`,
        ARMORY_AGENT: '/armory_agent',
        ARMORY_API: '/armory_api',
    },

    // 管理员用户相关接口
    ADMIN_USER: {
        BASE: `${API_CONFIG.BASE_DOMAIN}/api/${API_CONFIG.API_VERSION}/admin/admin-user`,
        VALIDATE_LOGIN: '/validate-login',
        LOGIN: '/login'
    },

    CLIENT_CHAT: {
        BASE: `${API_CONFIG.BASE_DOMAIN}/api/${API_CONFIG.API_VERSION}/client-chat`,
        CHAT_NONE_STREAM: '/chat-non-stream',
        CHAT_STREAM: '/chat-stream',
        CLEAR_MEMORY: '/clear-memory/:agentId/:sessionId',
    }

    // 可以在这里添加其他模块的 API 端点
    // USER: {
    //   BASE: `${API_CONFIG.BASE_DOMAIN}/api/${API_CONFIG.API_VERSION}/user`,
    //   LOGIN: '/login',
    //   LOGOUT: '/logout',
    // },

    // WORKFLOW: {
    //   BASE: `${API_CONFIG.BASE_DOMAIN}/api/${API_CONFIG.API_VERSION}/workflow`,
    //   SAVE: '/save',
    //   EXECUTE: '/execute',
    // },
} as const;

// 请求头配置
export const DEFAULT_HEADERS = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
} as const;

export const TEXT_REQ_HEADERS = {
    'Content-Type': 'text/plain',
    'Accept': 'application/json',
}

// 导出便捷方法
export const getApiUrl = (endpoint: string): string => {
    return `${API_CONFIG.BASE_DOMAIN}${endpoint}`;
};

// 环境检查工具
export const ENV_UTILS = {
    isDevelopment,
    isProduction,
    isTest: process.env.NODE_ENV === 'test',
} as const;