<template>
  <div class="page">
    <van-nav-bar title="教务管理" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content">
      <van-cell-group inset>
        <van-cell title="排课管理" icon="clock-o" is-link to="/teacher/dashboard" />
        <van-cell title="课程管理" icon="records" is-link to="/teacher/courses" />
        <van-cell title="教师管理" icon="manager-o" to="/boss/users" />
        <van-cell title="学员管理" icon="friends-o" to="/teacher/students" />
        <van-cell title="报名缴费" icon="cart-o" is-link @click="showMsg" />
        <van-cell title="考勤签到" icon="checked" is-link to="/teacher/checkin" />
      </van-cell-group>
    </div>
  </div>
</template>
<script setup>
import { showToast } from 'vant'
function showMsg() { showToast('请在PC端操作报名缴费') }
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:0}</style>
