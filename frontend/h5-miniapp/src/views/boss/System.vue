<template>
  <div class="page">
    <van-nav-bar title="系统设置" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content">
      <van-cell-group inset>
        <van-cell title="用户管理" icon="manager-o" is-link to="/boss/users" />
        <van-cell title="权限管理" icon="shield-o" is-link to="/boss/permission" />
        <van-cell title="系统配置" icon="setting-o" is-link to="/boss/config" />
        <van-cell title="公众号设置" icon="chat-o" is-link to="/boss/wechat" />
        <van-cell title="操作日志" icon="records" is-link to="/boss/logs" label="查看所有操作记录" />
      </van-cell-group>
    </div>
  </div>
</template>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:0}</style>
