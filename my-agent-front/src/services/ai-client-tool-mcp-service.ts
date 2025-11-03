/**
 * AI客户端工具MCP API服务
 */

import { API_ENDPOINTS, DEFAULT_HEADERS } from '../config';
import { apiRequestData } from '../utils/request';

// 定义响应数据类型
export interface AiClientToolMcpResponseDTO {
  id: number;
  mcpId: string;
  mcpName: string;
  description?: string;
  status: number;
  createTime: string;
  updateTime: string;
}

// 定义API响应格式
export interface ApiResponse<T> {
  code: string;
  info: string;
  data: T;
}

/**
 * AI客户端工具MCP API服务类
 */
export class AiClientToolMcpService {
  private static readonly BASE_URL = API_ENDPOINTS.AI_CLIENT_TOOL_MCP.BASE;

  /**
   * 查询所有AI客户端工具MCP配置
   */
  static async queryAllAiClientToolMcps(): Promise<AiClientToolMcpResponseDTO[]> {
    try {
      const data = await apiRequestData<AiClientToolMcpResponseDTO[]>(
        `${this.BASE_URL}${API_ENDPOINTS.AI_CLIENT_TOOL_MCP.QUERY_ALL}`,
        {
          method: 'GET',
          headers: DEFAULT_HEADERS,
        }
      );
      return data || [];
    } catch (error) {
      console.error('查询AI客户端工具MCP配置失败:', error);
      // 返回空数组作为降级处理
      return [];
    }
  }
}