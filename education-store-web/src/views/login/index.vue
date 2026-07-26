<!--
 * ---------------------------------------------
 * 登录页面
 * ---------------------------------------------
-->

<template>
  <div class="page-container">
    <div class="image-section">
      <div class="logo">
        <!-- <img src="@assets/logo/logo.svg" alt="" /> -->
      </div>
    </div>
    <div class="content-section">
      <h4>{{ systemTitle }}</h4>

      <!-- 登录表单 -->
      <form class="form" @submit.prevent="doLogin">
        <div class="input-wrap">
          <p>用户名</p>
          <el-input v-model="userName" placeholder="用户名" :prefix-icon="User" />
        </div>
        <div class="input-wrap">
          <p>密码</p>
          <el-input
            v-model="password"
            placeholder="密码"
            :prefix-icon="Lock"
            type="password"
            @keyup.enter="doLogin"
            @keyup.enter.prevent
          />
        </div>

        <el-button type="primary" class="login-btn" @click="doLogin" :loading="loging">登录</el-button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { User, Lock } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { login } from "@/api/auth/login";
import { setToken, setRefreshToken } from "@/utils/validate";
import { useUserStore } from "@/stores/user";

const systemTitle = import.meta.env.VITE_DOCUMENT_TITLE;

const router = useRouter();

const userName = ref("");
const password = ref("");

// 正在登录
const loging = ref(false);

const doLogin = async () => {
  if (loging.value) return;

  const _userName = userName.value?.trim();
  const _password = password.value?.trim();

  if (_userName === "" || _password === "") {
    ElMessage.error("用户名或密码不能为空");
    return;
  }

  loging.value = true;

  try {
    const res = await login({ username: _userName, password: _password });

    if (res?.code === 200) {
      const data = res?.data;
      // 一步式登录，直接成功
      setToken(data.accessToken);
      if (data.refreshToken) setRefreshToken(data.refreshToken);
      await useUserStore().loadUserInfo();
      router.push("/dashboard");
    } else {
      ElMessage.error(res?.message || "登录失败");
    }
  } finally {
    loging.value = false;
  }
};
</script>

<style lang="less" scoped>
@import url("./index.less");
</style>
