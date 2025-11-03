import { Toast } from '@douyinfe/semi-ui';
import { API_CONFIG, DEFAULT_HEADERS } from '../config/api';

export interface ApiResponse<T> {
    code: string;
    info: string;
    data: T;
}

function isApiResponse(obj: any): obj is ApiResponse<any> {
    return obj && typeof obj === 'object' && 'code' in obj && 'info' in obj;
}

function buildHeaders(headers?: HeadersInit): HeadersInit {
    // 自动注入 Authorization: Bearer <token>
    let authHeaders: Record<string, string> = {};
    try {
        const token = localStorage.getItem('token');
        if (token) {
            authHeaders['Authorization'] = `Bearer ${token}`;
        }
    } catch {}
    return {
        ...DEFAULT_HEADERS,
        ...authHeaders,
        ...(headers || {}),
    };
}

/**
 * Fetch JSON with unified error interception.
 * - Shows Toast error when HTTP error or when `code !== '0000'` in ApiResponse.
 * - Returns `data` when ApiResponse; otherwise returns parsed JSON directly.
 */
export async function apiRequestData<T>(url: string, init?: RequestInit): Promise<T> {
    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), API_CONFIG.TIMEOUT);
    try {
        const response = await fetch(url, {
            ...init,
            headers: buildHeaders(init?.headers),
            signal: controller.signal,
        });

        console.log('response', response);
        if (!response.ok) {
            if (response.status === 401) {
                Toast.error('登录状态已过期，请重新登录');
                try {
                    localStorage.removeItem('token');
                    localStorage.removeItem('userInfo');
                    localStorage.removeItem('isLoggedIn');
                } catch { }
                if (typeof window !== 'undefined' && window.location.pathname !== '/login') {
                    window.location.assign('/login');
                }
            } else {
                Toast.error(`HTTP错误: ${response.status}`);
            }
            const msg = `HTTP error! status: ${response.status}`;
            throw new Error(msg);
        }

        const json = await response.json();
        if (isApiResponse(json)) {
            if (String(json.code) !== '0000') {
                const msg = json.info || '服务错误';
                throw new Error(msg);
            }
            return (json.data as T);
        }
        // 非标准响应，直接返回
        return json as T;
    } catch (err: any) {
        if (err?.name === 'AbortError') {
            Toast.error('请求超时，请稍后重试');
        } else if (err instanceof Error) {
            Toast.error(err.message || '网络错误');
            // 兼容处理：若发生 CORS 预检（OPTIONS）401 导致的 "Failed to fetch"，
            // 前端拿不到 response，仍然执行统一的登录过期处理。
            if (err.message && err.message.includes('Failed to fetch')) {
                try {
                    const isApiDomain = url.startsWith(API_CONFIG.BASE_DOMAIN);
                    const isAdminApi = url.includes('/api/') && url.includes('/admin/');
                    if (isApiDomain && isAdminApi) {
                        localStorage.removeItem('token');
                        localStorage.removeItem('userInfo');
                        localStorage.removeItem('isLoggedIn');
                        if (typeof window !== 'undefined' && window.location.pathname !== '/login') {
                            Toast.error('登录状态已过期，请重新登录');
                            window.location.assign('/login');
                        }
                    }
                } catch {}
            }
        } else {
            Toast.error('未知错误');
        }
        throw err;
    } finally {
        clearTimeout(timer);
    }
}

/**
 * Fetch JSON and return raw parsed JSON, but still intercept ApiResponse error codes.
 */
export async function apiRequestRaw<T>(url: string, init?: RequestInit): Promise<T> {
    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), API_CONFIG.TIMEOUT);
    try {
        const response = await fetch(url, {
            ...init,
            headers: buildHeaders(init?.headers),
            signal: controller.signal,
        });

        if (!response.ok) {
            if (response.status === 401) {
                Toast.error('登录状态已过期，请重新登录');
                try {
                    localStorage.removeItem('token');
                    localStorage.removeItem('userInfo');
                    localStorage.removeItem('isLoggedIn');
                } catch { }
                if (typeof window !== 'undefined' && window.location.pathname !== '/login') {
                    window.location.assign('/login');
                }
            } else {
                Toast.error(`HTTP错误: ${response.status}`);
            }
            const msg = `HTTP error! status: ${response.status}`;
            throw new Error(msg);
        }

        const json = await response.json();
        if (isApiResponse(json) && String(json.code) !== '0000') {
            const msg = json.info || '服务错误';
            Toast.error(msg);
            throw new Error(msg);
        }
        return json as T;
    } catch (err: any) {
        if (err?.name === 'AbortError') {
            Toast.error('请求超时，请稍后重试');
        } else if (err instanceof Error) {
            Toast.error(err.message || '网络错误');
            if (err.message && err.message.includes('Failed to fetch')) {
                try {
                    const isApiDomain = url.startsWith(API_CONFIG.BASE_DOMAIN);
                    const isAdminApi = url.includes('/api/') && url.includes('/admin/');
                    if (isApiDomain && isAdminApi) {
                        localStorage.removeItem('token');
                        localStorage.removeItem('userInfo');
                        localStorage.removeItem('isLoggedIn');
                        if (typeof window !== 'undefined' && window.location.pathname !== '/login') {
                            Toast.error('登录状态已过期，请重新登录');
                            window.location.assign('/login');
                        }
                    }
                } catch {}
            }
        } else {
            Toast.error('未知错误');
        }
        throw err;
    } finally {
        clearTimeout(timer);
    }
}