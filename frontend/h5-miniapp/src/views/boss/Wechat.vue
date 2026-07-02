<template>
  <div class="page">
    <van-nav-bar title="公众号设置" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content">
      <van-cell-group inset>
        <van-cell title="公众号名称" value="未配置" />
        <van-cell title="AppID" value="请在PC端填写" />
        <van-cell title="消息模板" label="上课提醒 / 签到通知 / 课时预警" />
        <van-cell title="推送开关" value="关闭" />
      </van-cell-group>
      <p style="color:#999;font-size:12px;text-align:center;padding:12px">配置公众号请在PC管理后台操作</p>
    </div>
  </div>
</template>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}</style>
