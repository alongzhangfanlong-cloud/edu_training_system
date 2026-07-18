/**
 * 文件下载工具 — 从 blob 响应触发浏览器下载
 */
import { ElMessage } from 'element-plus';

/**
 * 从 axios blob 响应触发文件下载
 * @param {object} response — axios 原始响应（含 data Blob、headers）
 * @param {string} defaultFilename — 默认文件名
 * @returns {string} 实际使用的文件名
 */
export function downloadFromBlob(response, defaultFilename = 'download.xlsx') {
  const blob = response.data;
  const url = window.URL.createObjectURL(blob);
  try {
    const link = document.createElement('a');
    // 从响应头解析文件名
    let filename = defaultFilename;
    const disposition = response.headers?.['content-disposition'];
    if (disposition) {
      const match = disposition.match(/filename\*?=(?:UTF-8'')?(.+?)(?:;|$)/);
      if (match) filename = decodeURIComponent(match[1]);
    }
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    ElMessage.success('下载成功');
    return filename;
  } finally {
    window.URL.revokeObjectURL(url);
  }
}
