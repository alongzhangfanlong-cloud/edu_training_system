<!--
  公共头部
-->

<template>
  <el-dropdown placement="bottom-end">
    <div class="user-info">
      <div class="photo">
        <UserFilled />
      </div>
      <span class="username">{{ userName }}</span>
      <i><CaretBottom /></i>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item @click="modifyPassword">
          <div class="item-wrap">
            <Edit />
            <span>修改密码</span>
          </div>
        </el-dropdown-item>
        <el-dropdown-item @click="logout" divided>
          <div class="item-wrap">
            <SwitchButton />
            <span>退出登录</span>
          </div>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>

  <ModifyPsw v-model:visible="modifyVisible" />
</template>

<script setup>
import { computed, ref } from "vue";
import { logout } from "@/utils/validate";
import { useUserStore } from "@/stores/user";

import { UserFilled, CaretBottom, SwitchButton, Edit } from "@element-plus/icons-vue";
import ModifyPsw from "./modifyPsw/index.vue";

const userStore = useUserStore();

// 优先显示 realName，其次 username，都没有用 ID 兜底
const userName = computed(() => {
  return userStore.realName || userStore.username || `用户#${userStore.id}` || "未知用户";
});

// ## 修改密码
// #region -------------------------------------------------------
const modifyVisible = ref(false);

const modifyPassword = () => {
  modifyVisible.value = true;
};
// #endregion  --------------------------
</script>

<style lang="less" scoped>
@import url("./index.less");
</style>
