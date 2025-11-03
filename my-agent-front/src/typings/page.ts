/**
 * 分页结果通用类型
 */
export interface PageDTO<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
}