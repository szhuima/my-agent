/**
 * 管理员用户API服务
 */

import { API_ENDPOINTS, DEFAULT_HEADERS } from '../config';

// 定义登录请求数据类型
export interface AdminUserLoginRequestDTO {
  username: string;
  password: string;
}

// 定义API响应格式
export interface ApiResponse<T> {
  code: string;
  info: string;
  data: T;
}

export interface AdminUserLoginResponseDTO {

  userId: string;
  
  username: string;

  /**
   * 登录成功后返回的JWT令牌
   */
  token: string;
}

/**
 * 管理员用户API服务类
 */
export class AdminUserService {
  private static readonly BASE_URL = API_ENDPOINTS.ADMIN_USER.BASE;

  /**
   * 验证管理员用户登录
   * @param loginData 登录数据
   * @returns Promise<boolean> 登录是否成功
   */
  static async validateAdminUserLogin(loginData: AdminUserLoginRequestDTO): Promise<AdminUserLoginResponseDTO|null> {
    try {
      const response = await fetch(`${this.BASE_URL}${API_ENDPOINTS.ADMIN_USER.LOGIN}`, {
        method: 'POST',
        headers: DEFAULT_HEADERS, 
        body: JSON.stringify(loginData),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result: ApiResponse<AdminUserLoginResponseDTO> = await response.json();
      
      if (result.code === '0000') {
        return result.data;
      } else {
        console.error('登录验证失败:', result.info);
        return null;
      }
    } catch (error) {
      console.error('登录验证请求失败:', error);
      return null;
    }
  }
}