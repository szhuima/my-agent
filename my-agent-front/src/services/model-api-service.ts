import {API_ENDPOINTS, DEFAULT_HEADERS} from '../config/api';
import {apiRequestData, apiRequestRaw} from '../utils/request';
import {PageDTO} from '../typings/page';

// 请求和响应接口定义
export interface AiClientApiQueryRequestDTO {
  modelApiName?: string;
  pageNum?: number;
  pageSize?: number;
}

// AI客户端API请求接口
export interface AiClientApiRequestDTO {
  id?: number;
  modelApiName: string;
  modelName: string;
  modelSource: string;
  modelType: string;
  baseUrl: string;
  apiKey?: string;
  completionsPath?: string;
  embeddingsPath?: string;
  status: number;
}

export interface AiClientApiResponseDTO {
  id: number;
  apiId: string;
  modelApiName: string;
  modelName: string;
  modelType: string;
  modelSource: string;
  baseUrl: string;
  apiKey?: string;
  completionsPath?: string;
  embeddingsPath?: string;
  status: number;
  createTime: string;
  updateTime: string;
}

export interface ApiResponse<T> {
  code: string;
  info: string;
  data: T;
}

export class AiClientApiAdminService {
  private baseUrl: string;

  constructor() {
    this.baseUrl = `${API_ENDPOINTS.MODEL_API.BASE}`;
  }

  /**
   * 创建AI客户端API配置
   */
  async createAiClientApi(request: AiClientApiRequestDTO): Promise<ApiResponse<boolean>> {
    return await apiRequestRaw<ApiResponse<boolean>>(`${this.baseUrl}/create`, {
      method: 'POST',
      headers: {
        ...DEFAULT_HEADERS,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });
  }

  /**
   * 根据ID更新AI客户端API配置
   */
  async updateAiClientApiById(request: AiClientApiRequestDTO): Promise<ApiResponse<boolean>> {
    return await apiRequestRaw<ApiResponse<boolean>>(`${this.baseUrl}/update-by-id`, {
      method: 'POST',
      headers: {
        ...DEFAULT_HEADERS,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });
  }

  /**
   * 根据API ID更新AI客户端API配置
   */
  async updateAiClientApiByApiId(request: AiClientApiRequestDTO): Promise<ApiResponse<boolean>> {
    return await apiRequestRaw<ApiResponse<boolean>>(`${this.baseUrl}/update-by-api-id`, {
      method: 'POST',
      headers: {
        ...DEFAULT_HEADERS,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });
  }

  /**
   * 根据ID删除AI客户端API配置
   */
  async deleteAiClientApiById(id: number): Promise<ApiResponse<boolean>> {
    return await apiRequestRaw<ApiResponse<boolean>>(`${this.baseUrl}/delete-by-id/${id}`, {
      method: 'DELETE',
      headers: DEFAULT_HEADERS,
    });
  }

  /**
   * 根据API ID删除AI客户端API配置
   */
  async deleteAiClientApiByApiId(id: string): Promise<ApiResponse<boolean>> {
    return await apiRequestRaw<ApiResponse<boolean>>(`${this.baseUrl}/delete-by-id/${id}`, {
      method: 'DELETE',
      headers: DEFAULT_HEADERS,
    });
  }

  /**
   * 根据ID查询AI客户端API配置
   */
  async queryAiClientApiById(id: number): Promise<ApiResponse<AiClientApiResponseDTO>> {
    return await apiRequestRaw<ApiResponse<AiClientApiResponseDTO>>(`${this.baseUrl}/query-by-id/${id}`, {
      method: 'GET',
      headers: DEFAULT_HEADERS,
    });
  }

  /**
   * 根据API ID查询AI客户端API配置
   */
  async queryAiClientApiByApiId(apiId: string): Promise<ApiResponse<AiClientApiResponseDTO>> {
    return await apiRequestRaw<ApiResponse<AiClientApiResponseDTO>>(`${this.baseUrl}/query-by-api-id/${apiId}`, {
      method: 'GET',
      headers: DEFAULT_HEADERS,
    });
  }

  /**
   * 查询所有启用的AI客户端API配置
   */
  async queryEnabledAiClientApis(): Promise<ApiResponse<AiClientApiResponseDTO[]>> {
    return await apiRequestRaw<ApiResponse<AiClientApiResponseDTO[]>>(`${this.baseUrl}/query-enabled`, {
      method: 'GET',
      headers: DEFAULT_HEADERS,
    });
  }

  /**
   * 分页查询AI客户端API配置列表
   */
  async queryAiClientApiList(request: AiClientApiQueryRequestDTO): Promise<PageDTO<AiClientApiResponseDTO>> {
    return await apiRequestData<PageDTO<AiClientApiResponseDTO>>(`${this.baseUrl}/query-list`, {
      method: 'POST',
      headers: {
        ...DEFAULT_HEADERS,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });
  }

  /**
   * 查询所有AI客户端API配置
   */
  async queryAllAiClientApis(): Promise<ApiResponse<AiClientApiResponseDTO[]>> {
    return await apiRequestRaw<ApiResponse<AiClientApiResponseDTO[]>>(`${this.baseUrl}/query-all`, {
      method: 'GET',
      headers: DEFAULT_HEADERS,
    });
  }

  /**
   * 克隆模型API配置
   */
  async cloneModelApi(id: string): Promise<ApiResponse<boolean>> {
    return await apiRequestRaw<ApiResponse<boolean>>(`${this.baseUrl}/clone/${id}`, {
      method: 'POST',
      headers: DEFAULT_HEADERS,
    });
  }
}

export const aiClientApiAdminService = new AiClientApiAdminService();