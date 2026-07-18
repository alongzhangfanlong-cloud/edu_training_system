/**
 * v-permission 指令
 * 用于控制按钮/元素的显隐，基于用户权限列表
 * 权限异步加载后自动响应，不会永久删除 DOM
 *
 * 注意：该指令操作 el.style.display，仅适用于单根元素的组件（如 el-button）。
 * 对于多根/非元素根节点的组件（如 el-dropdown-item、el-menu-item），
 * 请改用 v-if="userStore.permissions.includes('xxx:xxx')"。
 *
 * 用法:
 *   <el-button v-permission="['system:user:add']">新增</el-button>
 *   <el-button v-permission="['system:user:add', 'system:user:edit']">操作</el-button>
 */
import { watch } from 'vue';
import { useUserStore } from '@/stores/user';

export default {
  mounted(el, binding) {
    const { value } = binding;
    if (!value || value.length === 0) return;

    const userStore = useUserStore();

    const checkPermission = () => {
      const permissions = userStore.permissions;
      // 权限未加载时不隐藏（保留按钮，等权限加载后再判断）
      if (!permissions || permissions.length === 0) return;

      // 检查是否拥有任意一个权限（OR 逻辑）
      const hasPermission = value.some((perm) => permissions.includes(perm));
      if (!hasPermission) {
        // el 可能是组件实例（非单根元素组件），优先取 $el
        const target = el instanceof HTMLElement ? el : el.$el;
        if (target && target.style) {
          target.style.display = 'none';
        }
      }
    };

    checkPermission();

    // 监听权限数据变化（登录后异步加载）
    watch(() => userStore.permissions, () => checkPermission(), { deep: true });
  },
};
