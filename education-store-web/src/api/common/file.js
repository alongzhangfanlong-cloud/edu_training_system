/**
 * 文件上传 API — 通用模块，所有业务模块共用
 */
import request from '@/utils/request';

const BASE = '/api/v1/files';

// 上传文件（multipart/form-data，字段名 file）
export function uploadFile(formData) {
  return request({
    url: `${BASE}/upload`,
    method: 'post',
    data: formData,
  });
}
