/**
 * 工作流执行记录服务
 */
import { API_ENDPOINTS, DEFAULT_HEADERS } from '../config/api';
import { apiRequestData } from '../utils/request';
import { PageDTO } from '../typings/page';

export type { PageDTO } from '../typings/page';

export interface WorkflowExecutionDTO {
  executionId: number;
  workflowInstanceId: number;
  workflowName: string;
  status?: string; 
  startTime?: string;
  endTime?: string;
  context?: string;
  errorMessage?: string;
  createAt?: string;
}

// 统一请求封装已处理 ApiResponse 与错误提示

export interface WorkflowExecutionQuery {
  instanceId?: string | number;
  workflowId?: string;
  workflowName?: string;
  pageNum?: number;
  pageSize?: number;
}

export class WorkflowExecutionService {
  private static readonly BASE_URL = API_ENDPOINTS.WORKFLOW_EXECUTION.BASE;

  static async queryWorkflowExecutionList(
    payload: WorkflowExecutionQuery
  ): Promise<PageDTO<WorkflowExecutionDTO> | null> {
    try {
      const data = await apiRequestData<PageDTO<WorkflowExecutionDTO>>(
        `${this.BASE_URL}${API_ENDPOINTS.WORKFLOW_EXECUTION.QUERY_LIST}`,
        {
          method: 'POST',
          headers: DEFAULT_HEADERS,
          body: JSON.stringify(payload),
        }
      );
      return data;
    } catch (error) {
      console.error('查询工作流执行记录列表失败:', error);
      return null;
    }
  }

  static async deleteWorkflowExecution(
    executionId: number
  ): Promise<boolean> {
    try {
      const data = await apiRequestData<boolean>(
        `${this.BASE_URL}${API_ENDPOINTS.WORKFLOW_EXECUTION.DELETE}/${encodeURIComponent(executionId.toString())}`,
        {
          method: 'DELETE',
          headers: DEFAULT_HEADERS,
        }
      );
      return data;
    } catch (error) {
      console.error('删除工作流执行记录失败:', error);
      throw error;
    }
  }
}
