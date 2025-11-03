/**
 * AI Agent Draw 配置服务
 */
import { API_ENDPOINTS, DEFAULT_HEADERS } from '../config';
import { apiRequestData } from '../utils/request';
import { PageDTO } from '../typings/page';

export type { PageDTO } from '../typings/page';

export interface AiAgentDrawConfigResponseDTO {
  id?: number;
  configId: string;
  configName: string;
  description?: string;
  agentId: string;
  configData?: string;
  version?: number;
  status?: number;
  createBy?: string;
  updateBy?: string;
  createTime?: string;
  updateTime?: string;
}

export interface ApiResponse<T> {
  code: string;
  info: string;
  data: T;
}

export interface WorkflowQueryRequestDTO {
  workflowId?: string;
  workflowName?: string;
  agentId?: string;
  status?: number;
  pageNum?: number;
  pageSize?: number;
}

export class AiAgentDrawService {
  private static readonly BASE_URL = API_ENDPOINTS.WORKFLOW.BASE;

  static async queryDrawConfigList(
    payload: WorkflowQueryRequestDTO
  ): Promise<PageDTO<AiAgentDrawConfigResponseDTO> | null> {
    try {
      const data = await apiRequestData<PageDTO<AiAgentDrawConfigResponseDTO>>(
        `${this.BASE_URL}${API_ENDPOINTS.WORKFLOW.QUERY_LIST}`,
        {
          method: 'POST',
          headers: DEFAULT_HEADERS,
          body: JSON.stringify(payload),
        }
      );
      return data || null;
    } catch (error) {
      console.error('查询Agent Draw配置列表失败:', error);
      return null;
    }
  }

  static async getDrawConfig(configId: string): Promise<AiAgentDrawConfigResponseDTO | null> {
    try {
      const url = `${this.BASE_URL}${API_ENDPOINTS.WORKFLOW.GET_DSL}/${encodeURIComponent(configId)}`;
      const data = await apiRequestData<AiAgentDrawConfigResponseDTO>(url, {
        method: 'GET',
        headers: DEFAULT_HEADERS,
      });
      return data || null;
    } catch (error) {
      console.error('获取Agent Draw配置失败:', error);
      return null;
    }
  }

  static async deleteDrawConfig(configId: string): Promise<boolean> {
    try {
      const ok = await apiRequestData<boolean>(
        `${this.BASE_URL}${API_ENDPOINTS.WORKFLOW.DELETE}/${encodeURIComponent(configId)}`,
        {
          method: 'DELETE',
          headers: DEFAULT_HEADERS,
        }
      );
      return ok || false;
    } catch (error) {
      console.error('删除Agent Draw配置失败:', error);
      throw error;
    }
  }
}