import { API_CONFIG, DEFAULT_HEADERS } from '../config';
import { apiRequestData } from '../utils/request';

export async function fetchModelSourceList(): Promise<string[]> {
  const url = `${API_CONFIG.BASE_DOMAIN}/api/v1/admin/support/model-source/query-list`;
  try {
    const data = await apiRequestData<string[]>(url, {
      method: 'GET',
      headers: DEFAULT_HEADERS,
    });
    return Array.isArray(data) ? data : [];
  } catch (error) {
    console.error('获取模型来源列表失败:', error);
    return [];
  }
}

export async function fetchModelTypeList(): Promise<string[]> {
  const url = `${API_CONFIG.BASE_DOMAIN}/api/v1/admin/support/model-type/query-list`;
  try {
    const data = await apiRequestData<string[]>(url, {
      method: 'GET',
      headers: DEFAULT_HEADERS,
    });
    return Array.isArray(data) ? data : [];
  } catch (error) {
    console.error('获取模型类型列表失败:', error);
    return [];
  }
}