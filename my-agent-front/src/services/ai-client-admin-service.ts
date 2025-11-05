import {API_CONFIG, DEFAULT_HEADERS} from '../config/api';
import {apiRequestRaw} from '../utils/request';

// 请求和响应接口定义
export interface AiClientQueryRequestDTO {
  id?: string;
  agentName?: string;
  status?: number;
  pageNum?: number;
  pageSize?: number;
}

// 新增客户端请求接口
export interface AiClientRequestDTO {
  id?: number;
  clientId?: string;
  agentName: string;
  description?: string;
  status: number;
  modelId?: number;
  systemPrompt?: string;
  advisorIds?: number[];
  mcpToolIds?: number[];
  memorySize?: number;
  knowledgeIds?: number[];
}

export interface AiClientResponseDTO {
  id: number;
  modelId?: number;
  modelName?: string;
  memorySize?: number;
  systemPrompt?: string;
  advisorIds?: number[];
  advisorNames?: string[];
  mcpToolIds?: number[];
  mcpToolNames?: string[];
  knowledgeIds?: number[];
  agentName: string;
  description?: string;
  status: number;
  createTime: string;
  updateTime: string;
}

export interface ApiResponse<T> {
  code: string;
  info: string;
  data: T;
}

export class AiClientAdminService {
  private baseUrl: string;

  constructor() {
    this.baseUrl = `${API_CONFIG.BASE_DOMAIN}/api/v1/admin/ai-client`;
  }

  /**
   * 查询客户端列表
   */
  async queryClientList(request: AiClientQueryRequestDTO): Promise<ApiResponse<AiClientResponseDTO[]>> {
    return await apiRequestRaw<ApiResponse<AiClientResponseDTO[]>>(`${this.baseUrl}/query-list`, {
      method: 'POST',
      headers: {
        ...DEFAULT_HEADERS,
      },
      body: JSON.stringify(request),
    });
  }

  /**
   * 根据ID删除客户端
   */
  async deleteClientById(id: number): Promise<ApiResponse<boolean>> {
    return await apiRequestRaw<ApiResponse<boolean>>(`${this.baseUrl}/delete-by-id/${id}`, {
      method: 'DELETE',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }

  /**
   * 根据客户端ID删除客户端
   */
  async deleteClientByClientId(clientId: string): Promise<ApiResponse<boolean>> {
    return await apiRequestRaw<ApiResponse<boolean>>(`${this.baseUrl}/delete-by-client-id/${clientId}`, {
      method: 'DELETE',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }

  /**
   * 根据ID查询客户端详情
   */
  async queryClientById(id: number): Promise<ApiResponse<AiClientResponseDTO>> {
    return await apiRequestRaw<ApiResponse<AiClientResponseDTO>>(`${this.baseUrl}/query-by-id/${id}`, {
      method: 'GET',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }

  /**
   * 查询所有客户端
   */
  async queryAllClients(): Promise<ApiResponse<AiClientResponseDTO[]>> {
    return await apiRequestRaw<ApiResponse<AiClientResponseDTO[]>>(`${this.baseUrl}/query-all`, {
      method: 'GET',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }

  /**
   * 查询启用的客户端
   */
  async queryEnabledClients(): Promise<ApiResponse<AiClientResponseDTO[]>> {
    return await apiRequestRaw<ApiResponse<AiClientResponseDTO[]>>(`${this.baseUrl}/query-enabled`, {
      method: 'GET',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }

  /**
   * 创建客户端
   */
  async createClient(request: AiClientRequestDTO): Promise<ApiResponse<boolean>> {
    return await apiRequestRaw<ApiResponse<boolean>>(`${this.baseUrl}/create`, {
      method: 'POST',
      headers: {
        ...DEFAULT_HEADERS,
      },
      body: JSON.stringify(request),
    });
  }

  /**
   * 更新客户端信息（根据ID）
   */
  async updateClientById(request: AiClientRequestDTO): Promise<ApiResponse<boolean>> {
    return await apiRequestRaw<ApiResponse<boolean>>(`${this.baseUrl}/update-by-id`, {
      method: 'PUT',
      headers: {
        ...DEFAULT_HEADERS,
      },
      body: JSON.stringify(request),
    });
  }

  /**
   * 更新客户端信息（根据客户端ID）
   */
  async updateClientByClientId(request: AiClientRequestDTO): Promise<ApiResponse<boolean>> {
    return await apiRequestRaw<ApiResponse<boolean>>(`${this.baseUrl}/update-by-client-id`, {
      method: 'PUT',
      headers: {
        ...DEFAULT_HEADERS,
      },
      body: JSON.stringify(request),
    });
  }
}

// 导出服务实例
export const aiClientAdminService = new AiClientAdminService();