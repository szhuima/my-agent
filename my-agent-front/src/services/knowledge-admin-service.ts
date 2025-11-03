import { API_CONFIG, DEFAULT_HEADERS } from '../config/api';
import { apiRequestRaw } from '../utils/request';

// 请求和响应接口定义
export interface AiClientRagOrderRequestDTO {
  id?: number;
  ragId?: string;
  ragName?: string;
  knowledgeTag?: string;
  status?: number;
}

export interface AiClientRagOrderQueryRequestDTO {
  ragId?: string;
  ragName?: string;
  knowledgeTag?: string;
  status?: number;
  pageNum?: number;
  pageSize?: number;
}

export interface AiClientRagOrderResponseDTO {
  id: number; // 知识库ID
  ragName: string; // 知识库名称
  knowledgeTag: string; // 知识标签
  status: number;
  createTime: string;
  updateTime: string;
}

export interface ApiResponse<T> {
  code: string;
  info: string;
  data: T;
}

export class AiClientRagOrderAdminService {
  private baseUrl: string;

  constructor() {
    this.baseUrl = `${API_CONFIG.BASE_DOMAIN}/api/v1/admin/knowledge`;
  }

  /**
   * 创建知识库配置
   */
  async createRagOrder(request: AiClientRagOrderRequestDTO): Promise<ApiResponse<boolean>> {
    return await apiRequestRaw<ApiResponse<boolean>>(`${this.baseUrl}/create`, {
      method: 'POST',
      headers: {
        ...DEFAULT_HEADERS,
      },
      body: JSON.stringify(request),
    });
  }

  /**
   * 根据ID更新知识库配置
   */
  async updateRagOrderById(request: AiClientRagOrderRequestDTO): Promise<ApiResponse<boolean>> {
    return await apiRequestRaw<ApiResponse<boolean>>(`${this.baseUrl}/update-by-id`, {
      method: 'PUT',
      headers: {
        ...DEFAULT_HEADERS,
      },
      body: JSON.stringify(request),
    });
  }

  /**
   * 根据ID删除知识库配置
   */
  async deleteRagOrderById(id: number): Promise<ApiResponse<boolean>> {
    return await apiRequestRaw<ApiResponse<boolean>>(`${this.baseUrl}/delete-by-id/${id}`, {
      method: 'DELETE',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }

  /**
   * 根据知识库ID删除知识库配置
   */
  async deleteRagOrderByRagId(ragId: string): Promise<ApiResponse<boolean>> {
    return await apiRequestRaw<ApiResponse<boolean>>(`${this.baseUrl}/delete-by-rag-id/${ragId}`, {
      method: 'DELETE',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }

  /**
   * 根据ID查询知识库配置
   */
  async queryRagOrderById(id: number): Promise<ApiResponse<AiClientRagOrderResponseDTO>> {
    return await apiRequestRaw<ApiResponse<AiClientRagOrderResponseDTO>>(`${this.baseUrl}/query-by-id/${id}`, {
      method: 'GET',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }

  /**
   * 根据知识库ID查询知识库配置
   */
  async queryRagOrderByRagId(ragId: string): Promise<ApiResponse<AiClientRagOrderResponseDTO>> {
    return await apiRequestRaw<ApiResponse<AiClientRagOrderResponseDTO>>(`${this.baseUrl}/query-by-rag-id/${ragId}`, {
      method: 'GET',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }

  /**
   * 查询启用的知识库配置
   */
  async queryEnabledKnowledges(): Promise<ApiResponse<AiClientRagOrderResponseDTO[]>> {
    return await apiRequestRaw<ApiResponse<AiClientRagOrderResponseDTO[]>>(`${this.baseUrl}/query-enabled`, {
      method: 'GET',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }

  /**
   * 根据知识标签查询知识库配置
   */
  async queryRagOrdersByKnowledgeTag(knowledgeTag: string): Promise<ApiResponse<AiClientRagOrderResponseDTO[]>> {
    return await apiRequestRaw<ApiResponse<AiClientRagOrderResponseDTO[]>>(`${this.baseUrl}/query-by-knowledge-tag/${knowledgeTag}`, {
      method: 'GET',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }

  /**
   * 根据状态查询知识库配置
   */
  async queryRagOrdersByStatus(status: number): Promise<ApiResponse<AiClientRagOrderResponseDTO[]>> {
    return await apiRequestRaw<ApiResponse<AiClientRagOrderResponseDTO[]>>(`${this.baseUrl}/query-by-status/${status}`, {
      method: 'GET',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }

  /**
   * 分页查询知识库配置列表
   */
  async queryRagOrderList(request: AiClientRagOrderQueryRequestDTO): Promise<ApiResponse<AiClientRagOrderResponseDTO[]>> {
    return await apiRequestRaw<ApiResponse<AiClientRagOrderResponseDTO[]>>(`${this.baseUrl}/query-list`, {
      method: 'POST',
      headers: {
        ...DEFAULT_HEADERS,
      },
      body: JSON.stringify(request),
    });
  }

  /**
   * 查询所有知识库配置
   */
  async queryAllRagOrders(): Promise<ApiResponse<AiClientRagOrderResponseDTO[]>> {
    return await apiRequestRaw<ApiResponse<AiClientRagOrderResponseDTO[]>>(`${this.baseUrl}/query-all`, {
      method: 'GET',
      headers: {
        ...DEFAULT_HEADERS,
      },
    });
  }

  /**
   * 上传知识库文件
   */
  async uploadRagFile(name: string, tag: string, files: File[]): Promise<ApiResponse<boolean>> {
    const formData = new FormData();
    formData.append('name', name);
    formData.append('tag', tag);
    files.forEach(file => {
      formData.append('files', file);
    });

    // 获取token用于认证
    const token = localStorage.getItem('token');
    const headers: Record<string, string> = {};
    
    // 添加认证头（如果有token）
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    // 直接使用 fetch 以保持 multipart/form-data 自动设置，统一拦截不适用于此处
    const response = await fetch(`${this.baseUrl}/file/upload`, {
      method: 'POST',
      headers: headers,
      body: formData,
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
  }
}

export const aiClientRagOrderAdminService = new AiClientRagOrderAdminService();