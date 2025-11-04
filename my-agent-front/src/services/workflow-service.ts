/**
 * 工作流服务
 */
import {API_ENDPOINTS, DEFAULT_HEADERS, TEXT_REQ_HEADERS} from '../config/api';
import {apiRequestData} from '../utils/request';
import {PageDTO} from '../typings/page';

export type { PageDTO } from '../typings/page';

export interface WorkflowResponseDTO {
  workflowId: number;
  configId: string;
  name: string;
  description?: string;
  agentId: string;
  dslConfig?: string;
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

export class WorkflowService {
  private static readonly BASE_URL = API_ENDPOINTS.WORKFLOW.BASE;

  static async queryWorkflowList(
    payload: WorkflowQueryRequestDTO
  ): Promise<PageDTO<WorkflowResponseDTO> | null> {
    try {
      const data = await apiRequestData<PageDTO<WorkflowResponseDTO>>(
        `${this.BASE_URL}${API_ENDPOINTS.WORKFLOW.QUERY_LIST}`,
        {
          method: 'POST',
          headers: DEFAULT_HEADERS,
          body: JSON.stringify(payload),
        }
      );
      return data;
    } catch (error) {
      console.error('查询工作流列表失败:', error);
      return null;
    }
  }

  static async getDSL(workflowId: number): Promise<string | null> {
    try {
      const url = `${this.BASE_URL}${API_ENDPOINTS.WORKFLOW.GET_DSL}/${encodeURIComponent(workflowId)}`;
      console.log('发起 getDSL API请求:', url);
      const data = await apiRequestData<string>(url, {
        method: 'GET',
        headers: DEFAULT_HEADERS,
      });
      console.log('API调用成功，返回数据:', data);
      return data;
    } catch (error) {
      console.error('获取工作流DSL失败:', error);
      return null;
    }
  }

  static async deleteWorkflow(workflowId: number): Promise<boolean> {
    try {
      const ok = await apiRequestData<boolean>(
        `${this.BASE_URL}${API_ENDPOINTS.WORKFLOW.DELETE}/${encodeURIComponent(workflowId)}`,
        {
          method: 'DELETE',
          headers: DEFAULT_HEADERS,
        }
      );
      return ok || false;
    } catch (error) {
      console.error('删除工作流失败:', error);
      throw error;
    }
  }

  static async deployWorkflow(workflowId: number): Promise<number> {
    try {
      const data = await apiRequestData<number>(
        `${this.BASE_URL}${API_ENDPOINTS.WORKFLOW.DEPLOY}/${encodeURIComponent(workflowId.toString())}`,
        {
          method: 'POST',
          headers: DEFAULT_HEADERS,
        }
      );
      return data;
    } catch (error) {
      console.error('部署工作流失败:', error);
      throw error;
    }
  }

  static async importWorkflow(dslContent: string): Promise<number> {
    const data = await apiRequestData<number>(
        `${this.BASE_URL}${API_ENDPOINTS.WORKFLOW.IMPORT}`,
        {
          method: 'POST',
          headers: TEXT_REQ_HEADERS,
          body: dslContent,
        }
    );
    return data;
  }


}