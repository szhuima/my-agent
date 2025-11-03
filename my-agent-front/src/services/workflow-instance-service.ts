/**
 * 工作流实例服务
 */
import { API_ENDPOINTS, DEFAULT_HEADERS, TEXT_REQ_HEADERS, API_CONFIG } from '../config/api';
import { apiRequestData } from '../utils/request';
import { PageDTO } from '../typings/page';

export type { PageDTO } from '../typings/page';

export interface WorkflowInstanceDTO {
  instanceId: number;
  workflowId: number;
  workflowName: string;
  status?: string;
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

export interface WorkflowInstanceQuery {
  workflowId?: string;
  workflowName?: string;
  pageNum?: number;
  pageSize?: number;
}

export class WorkflowInstanceService {
  private static readonly BASE_URL = API_ENDPOINTS.WORKFLOW_INSTANCE.BASE;

  static async queryWorkflowInstanceList(
    payload: WorkflowInstanceQuery
  ): Promise<PageDTO<WorkflowInstanceDTO> | null> {
    try {
      const data = await apiRequestData<PageDTO<WorkflowInstanceDTO>>(
        `${this.BASE_URL}${API_ENDPOINTS.WORKFLOW_INSTANCE.QUERY_LIST}`,
        {
          method: 'POST',
          headers: DEFAULT_HEADERS,
          body: JSON.stringify(payload),
        }
      );
      return data;
    } catch (error) {
      console.error('查询工作流实例列表失败:', error);
      return null;
    }
  }

  static async deleteWorkflowInstance(instanceId: number): Promise<boolean> {
    try {
      const ok = await apiRequestData<boolean>(
        `${this.BASE_URL}${API_ENDPOINTS.WORKFLOW_INSTANCE.DELETE}/${encodeURIComponent(instanceId)}`,
        {
          method: 'DELETE',
          headers: DEFAULT_HEADERS,
        }
      );
      return ok || false;
    } catch (error) {
      console.error('删除工作流实例失败:', error);
      throw error;
    }
  }

  static async unDeployWorkflowInstance(instanceId: number): Promise<boolean> {
    try {
      const ok = await apiRequestData<boolean>(
        `${this.BASE_URL}${API_ENDPOINTS.WORKFLOW_INSTANCE.UNDEPLOY}/${encodeURIComponent(instanceId)}`,
        {
          method: 'POST',
          headers: DEFAULT_HEADERS,
        }
      );
      return ok || false;
    } catch (error) {
      console.error('卸载工作流实例失败:', error);
      throw error;
    }
  }

  static async deployWorkflowInstance(instanceId: number): Promise<boolean> {
    try {
      const ok = await apiRequestData<boolean>(
        `${this.BASE_URL}${API_ENDPOINTS.WORKFLOW_INSTANCE.DEPLOY}/${encodeURIComponent(instanceId)}`,
        {
          method: 'POST',
          headers: DEFAULT_HEADERS,
        }
      );
      return ok || false;
    } catch (error) {
      console.error('部署工作流实例失败:', error);
      throw error;
    }
  }

  /**
   * 运行工作流实例（异步执行）
   * 调用后端接口：/workflow/engine/run-async/{instanceId}
   * 上下文变量以 JSON 形式传递
   */
  static async runWorkflowInstanceAsync(
    instanceId: number,
    contextVars: Record<string, any>
  ): Promise<boolean> {
    try {
      const url = `${API_CONFIG.BASE_DOMAIN}/api/${API_CONFIG.API_VERSION}/workflow/engine/run-async/${encodeURIComponent(
        instanceId.toString()
      )}`;
      const ok = await apiRequestData<boolean>(url, {
        method: 'POST',
        headers: DEFAULT_HEADERS,
        body: JSON.stringify(contextVars || {}),
      });
      return ok ?? true;
    } catch (error) {
      console.error('触发工作流实例异步执行失败:', error);
      throw error;
    }
  }
}

