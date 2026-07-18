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
import { ref, onMounted } from "vue";
import { getNickName, logout } from "@/utils/validate";

import { UserFilled, CaretBottom, SwitchButton, Edit } from "@element-plus/icons-vue";
import ModifyPsw from "./modifyPsw/index.vue";

const userName = ref("");

onMounted(() => {
  userName.value = getNickName() || "未知用户";
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
