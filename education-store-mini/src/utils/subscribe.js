/**
 * 微信订阅消息工具
 * 封装 wx.requestSubscribeMessage，统一处理授权结果
 */

// 模板 ID 常量（注册时替换为微信后台的真实模板 ID）
export const TMPL_IDS = {
  SIGN_IN: '',
  HOMEWORK: '',
  LEAVE: '',
  EVALUATION: '',
  REDEMPTION: '',
};

/**
 * 请求订阅消息授权
 * @param {string[]} tmplIds - 模板 ID 列表
 * @returns {Promise<{accepted: string[], rejected: string[]}>}
 */
export function requestSubscribe(tmplIds) {
  // 过滤掉空 ID（模板未配置时跳过）
  const ids = tmplIds.filter(Boolean);
  if (!ids.length) return Promise.resolve({ accepted: [], rejected: [] });

  return new Promise((resolve) => {
    wx.requestSubscribeMessage({
      tmplIds: ids,
      success(res) {
        // res[templateId] = 'accept' | 'reject' | 'ban'
        const accepted = [];
        const rejected = [];
        for (const [id, status] of Object.entries(res)) {
          if (status === 'accept') accepted.push(id);
          else rejected.push(id);
        }
        resolve({ accepted, rejected });
      },
      fail(err) {
        console.warn('订阅消息授权失败:', err.errMsg || err);
        resolve({ accepted: [], rejected: ids });
      },
    });
  });
}

/**
 * 请求单个模板订阅
 * @param {string} type - TMPL_IDS 中的 key
 * @returns {Promise<boolean>} 是否授权成功
 */
export async function subscribeOne(type) {
  const tmplId = TMPL_IDS[type];
  if (!tmplId) return false;
  const { accepted } = await requestSubscribe([tmplId]);
  return accepted.includes(tmplId);
}
