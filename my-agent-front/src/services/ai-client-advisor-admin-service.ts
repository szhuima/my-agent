import { API_CONFIG, DEFAULT_HEADERS } from '../config/api';
import { apiRequestRaw } from '../utils/request';

// 请求和响应接口定义
export interface AiClientAdvisorRequestDTO {
  id?: number;
  advisorId?: string;
  advisorName?: string;
  advisorType?: string;
  advisorDesc?: string;
  advisorPrompt?: string;
  advisorModel?: string;
  advisorConfig?: string;
  orderNum?: number;
  extParam?: string;
  status?: number;
}

export interface AiClientAdvisorQueryRequestDTO {
  advisorId?: string;
  advisorName?: string;
  advisorType?: string;
  status?: number;
  pageNum?: number;
  pageSize?: number;
}

export interface AiClientAdvisorResponseDTO {
  id: number;
  advisorId: string;
  advisorName: string;
  advisorType: string;
  advisorDesc?: string;
  advisorPrompt?: string;
  advisorModel?: string;
  advisorConfig?: string;
  orderNum?: number;
  extParam?: string;
  status: number;
  createTime: string;
  updateTime: string;
}

export interface ApiResponse<T> {
  code: string;
  info: string;
  data: T;
}

export class AiClientAdvisorAdminService {
  private baseUrl: string;

  constructor() {
    this.baseUrl = `${API_CONFIG.BASE_DOMAIN}/api/v1/admin/ai-client-advisor`;
  }

  /**
   * 创建顾问配置
   */
  async createAdvisor(request: AiClientAdvisorRequestDTO): Promise<ApiResponse<boolean>> {
    return await apiRequestRaw<ApiResponse<boolean>>(`${this.baseUrl}/create`, {
      method: 'POST',
      headers: {
        ...DEFAULT_HEADERS,
      },
      body: JSON.stringify(request),
    });
  }

  /**
   * 根据ID更新顾问配置
   */
  async updateAdvisorById(request: AiClientAdvisorRequestDTO): Promise<ApiResponse<boolean>> {
    return await apiRequestRaw<ApiResponse<boolean>>(`${this.baseUrl}/update-by-id`, {
      method: 'PUT',
      headers: {
        ...DEFAULT_HEADERS,
      },
      body: JSON.stringify(request),
    });
  }

  /**
   * 根据顾问ID更新顾问配置
   */
  async updateAdvisorByAdvisorId(request: AiClientAdvisorRequestDTO): Promise<ApiResponse<boolean>> {
    return await apiRequestRaw<ApiResponse<boolean>>(`${this.baseUrl}/update-by-advisor-id`, {
      method: 'PUT',
      headers: {
        ...DEFAULT_HEADERS,
      },
      body: JSON.stringify(request),
    });
  }

  /**
   * 根据ID删除顾问配置
   */
  async deleteAdvisorById(id: number): Promise<ApiResponse<boolean>> {
    return await apiRequestRaw<ApiResponse<boolean>>(`${this.baseUrl}/delete-by-id/${id}`, {
      method: 'DELETE',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }

  /**
   * 根据顾问ID删除顾问配置
   */
  async deleteAdvisorByAdvisorId(advisorId: string): Promise<ApiResponse<boolean>> {
    return await apiRequestRaw<ApiResponse<boolean>>(`${this.baseUrl}/delete-by-advisor-id/${advisorId}`, {
      method: 'DELETE',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }

  /**
   * 根据ID查询顾问配置
   */
  async queryAdvisorById(id: number): Promise<ApiResponse<AiClientAdvisorResponseDTO>> {
    return await apiRequestRaw<ApiResponse<AiClientAdvisorResponseDTO>>(`${this.baseUrl}/query-by-id/${id}`, {
      method: 'GET',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }

  /**
   * 根据顾问ID查询顾问配置
   */
  async queryAdvisorByAdvisorId(advisorId: string): Promise<ApiResponse<AiClientAdvisorResponseDTO>> {
    return await apiRequestRaw<ApiResponse<AiClientAdvisorResponseDTO>>(`${this.baseUrl}/query-by-advisor-id/${advisorId}`, {
      method: 'GET',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }

  /**
   * 查询启用的顾问配置
   */
  async queryEnabledAdvisors(): Promise<ApiResponse<AiClientAdvisorResponseDTO[]>> {
    return await apiRequestRaw<ApiResponse<AiClientAdvisorResponseDTO[]>>(`${this.baseUrl}/query-enabled`, {
      method: 'GET',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }

  /**
   * 根据状态查询顾问配置
   */
  async queryAdvisorsByStatus(status: number): Promise<ApiResponse<AiClientAdvisorResponseDTO[]>> {
    return await apiRequestRaw<ApiResponse<AiClientAdvisorResponseDTO[]>>(`${this.baseUrl}/query-by-status/${status}`, {
      method: 'GET',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }

  /**
   * 根据顾问类型查询顾问配置
   */
  async queryAdvisorsByType(advisorType: string): Promise<ApiResponse<AiClientAdvisorResponseDTO[]>> {
    return await apiRequestRaw<ApiResponse<AiClientAdvisorResponseDTO[]>>(`${this.baseUrl}/query-by-type/${advisorType}`, {
      method: 'GET',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }

  /**
   * 根据条件查询顾问配置列表
   */
  async queryAdvisorList(request: AiClientAdvisorQueryRequestDTO): Promise<ApiResponse<AiClientAdvisorResponseDTO[]>> {
    return await apiRequestRaw<ApiResponse<AiClientAdvisorResponseDTO[]>>(`${this.baseUrl}/query-list`, {
      method: 'POST',
      headers: {
        ...DEFAULT_HEADERS,
      },
      body: JSON.stringify(request),
    });
  }

  /**
   * 查询所有顾问配置
   */
  async queryAllAdvisors(): Promise<ApiResponse<AiClientAdvisorResponseDTO[]>> {
    return await apiRequestRaw<ApiResponse<AiClientAdvisorResponseDTO[]>>(`${this.baseUrl}/query-all`, {
      method: 'GET',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }
}

// 导出服务实例
export const aiClientAdvisorAdminService = new AiClientAdvisorAdminService();