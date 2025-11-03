/**
 * AI客户端API服务
 */

import { API_ENDPOINTS, DEFAULT_HEADERS } from '../config';
import { apiRequestData } from '../utils/request';

// 定义响应数据类型
export interface AiClientResponseDTO {
  id: number;
  clientId: string;
  clientName: string;
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
 * AI客户端API服务类
 */
export class AiClientService {
  private static readonly BASE_URL = API_ENDPOINTS.AI_CLIENT.BASE;

  /**
   * 查询所有AI客户端配置
   */
  static async queryAllAiClients(): Promise<AiClientResponseDTO[]> {
    try {
      const data = await apiRequestData<AiClientResponseDTO[]>(
        `${this.BASE_URL}${API_ENDPOINTS.AI_CLIENT.QUERY_ALL}`,
        {
          method: 'GET',
          headers: DEFAULT_HEADERS,
        }
      );
      return data || [];
    } catch (error) {
      console.error('查询AI客户端配置失败:', error);
      // 返回空数组作为降级处理
      return [];
    }
  }

  /**
   * 查询所有启用的AI客户端配置
   */
  static async queryEnabledAiClients(): Promise<AiClientResponseDTO[]> {
    try {
      const data = await apiRequestData<AiClientResponseDTO[]>(
        `${this.BASE_URL}${API_ENDPOINTS.AI_CLIENT.QUERY_ENABLED}`,
        {
          method: 'GET',
          headers: DEFAULT_HEADERS,
        }
      );
      return data || [];
    } catch (error) {
      console.error('查询启用的AI客户端配置失败:', error);
      // 返回空数组作为降级处理
      return [];
    }
  }
}