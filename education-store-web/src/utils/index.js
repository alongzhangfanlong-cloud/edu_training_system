/**
 * ---------------------------------------------
 * 方法工具类
 * ---------------------------------------------
 */

/**
 * 将扁平菜单数组构建为树形结构
 * @param {Array} flatList - 扁平菜单列表，每项需包含 id 和 parentId
 * @param {number|string} [rootParentId=0] - 根节点的 parentId 值
 * @returns {Array} 树形菜单数组
 */
export const buildMenuTree = (flatList = [], rootParentId = 0) => {
  const map = {};
  const tree = [];

  // 先建索引
  flatList.forEach(item => {
    map[item.id] = { ...item, children: [] };
  });

  // 再挂父子关系
  flatList.forEach(item => {
    const node = map[item.id];
    const parent = map[item.parentId];
    if (parent) {
      parent.children.push(node);
    } else if (item.parentId === rootParentId) {
      tree.push(node);
    }
  });

  // 清理空 children
  const cleanEmptyChildren = (nodes) => {
    nodes.forEach(node => {
      if (node.children && node.children.length === 0) {
        delete node.children;
      } else if (node.children) {
        cleanEmptyChildren(node.children);
      }
    });
  };
  cleanEmptyChildren(tree);

  return tree;
};

// 延迟方法
export const Await = ms => {
  return new Promise(resolve => setTimeout(resolve, ms));
};

/**
 * 从数组对象中递归找到某个数据
 * @param {*} dataArray
 * @param {*} searchKey
 * @param {*} searvhValue
 * @param {*} childrenKey
 * @returns
 */
export const findTargetInArray = (dataArray = [], searchKey = "id", searchValue = "", childrenKey = "children") => {
  // 参数类型检查
  if (!Array.isArray(dataArray) && (!dataArray || typeof dataArray !== "object")) {
    return null;
  }

  function dfs(node) {
    // 处理搜索值为 undefined 或 null 的情况
    if (searchValue === null && node[searchKey] === null) {
      return node;
    }

    // 严格比较
    if (node[searchKey] === searchValue) {
      return node;
    }

    // 递归搜索子节点
    const children = node[childrenKey];

    if (Array.isArray(children) && children.length > 0) {
      for (let child of children) {
        const result = dfs(child);
        if (result) {
          return result;
        }
      }
    }

    return null;
  }

  // 处理数组和对象两种情况
  if (Array.isArray(dataArray)) {
    for (let node of dataArray) {
      if (node && typeof node === "object") {
        const result = dfs(node);

        if (result) {
          return result;
        }
      }
    }
  } else {
    return dfs(dataArray);
  }

  return null;
};
