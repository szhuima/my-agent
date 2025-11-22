import {ApiResponse} from '../utils/request';
import {API_ENDPOINTS} from '../config/api';

export interface FileUploadService {
  uploadImage(file: File): Promise<ApiResponse<string>>;
}

class FileUploadServiceImpl implements FileUploadService {
  private baseUrl = API_ENDPOINTS.FILE_UPLOAD.BASE;

  async uploadImage(file: File): Promise<ApiResponse<string>> {
    try {
      const formData = new FormData();
      formData.append('file', file);

      // 获取token用于认证
      const token = localStorage.getItem('token');
      const headers: Record<string, string> = {};
      
      // 添加认证头（如果有token）
      // 注意：不设置 Content-Type，让浏览器自动设置 multipart/form-data 的边界
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }

      const response = await fetch(`${this.baseUrl}/image`, {
        method: 'POST',
        headers: headers,
        body: formData,
      });

      if (!response.ok) {
        throw new Error(`上传失败: ${response.statusText}`);
      }

      const result = await response.json();
      return result;
    } catch (error) {
      console.error('图片上传失败:', error);
      throw error;
    }
  }
}

export const fileUploadService: FileUploadService = new FileUploadServiceImpl();