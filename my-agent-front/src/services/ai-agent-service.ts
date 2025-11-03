/**
 * AI Agent 服务
 */
import { API_ENDPOINTS, DEFAULT_HEADERS } from '../config';
import { apiRequestData } from '../utils/request';

export interface ArmoryAgentRequestDTO {
  agentId: string;
}

export interface ArmoryApiRequestDTO {
  apiId: string;
}

export interface ApiResponse<T> {
  code: string;
  info: string;
  data: T;
}

export class AiAgentService {
  private static readonly BASE_URL = API_ENDPOINTS.AI_AGENT.BASE;

  /**
   * 装配智能体
   * @param agentId 智能体ID
   * @returns Promise<boolean> 装配是否成功
   */
  static async armoryAgent(agentId: string): Promise<boolean> {
    try {
      const payload: ArmoryAgentRequestDTO = { agentId };
      const data = await apiRequestData<boolean>(
        `${this.BASE_URL}${API_ENDPOINTS.AI_AGENT.ARMORY_AGENT}`,
        {
          method: 'POST',
          headers: DEFAULT_HEADERS,
          body: JSON.stringify(payload),
        }
      );
      return data || false;
    } catch (error) {
      console.error('装配智能体失败:', error);
      throw error;
    }
  }

  /**
   * 装配API
   * @param apiId API ID
   * @returns Promise<boolean> 装配是否成功
   */
  static async armoryApi(apiId: string): Promise<boolean> {
    try {
      const payload: ArmoryApiRequestDTO = { apiId };
      const data = await apiRequestData<boolean>(
        `${this.BASE_URL}${API_ENDPOINTS.AI_AGENT.ARMORY_API}`,
        {
          method: 'POST',
          headers: DEFAULT_HEADERS,
          body: JSON.stringify(payload),
        }
      );
      return data || false;
    } catch (error) {
      console.error('装配API失败:', error);
      throw error;
    }
  }
}