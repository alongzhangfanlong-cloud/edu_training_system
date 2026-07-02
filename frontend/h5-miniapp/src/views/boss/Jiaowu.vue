<template>
  <div class="page">
    <van-nav-bar title="教务管理" left-arrow @click-left="$router.push('/boss/dashboard')" fixed placeholder />
    <div class="content">
      <van-cell-group inset>
        <van-cell title="排课管理" icon="clock-o" is-link to="/boss/schedules" />
        <van-cell title="课程管理" icon="records" is-link to="/boss/courses" />
        <van-cell title="教师管理" icon="manager-o" is-link to="/boss/users" />
        <van-cell title="学员管理" icon="friends-o" is-link to="/boss/students" />
        <van-cell title="报名缴费" icon="cart-o" is-link to="/boss/enroll" />
        <van-cell title="考勤签到" icon="checked" is-link to="/boss/checkin" />
      </van-cell-group>
    </div>
  </div>
</template>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:0}</style>
