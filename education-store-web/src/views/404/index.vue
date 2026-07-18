<!--
  * ---------------------------------------------
  * 404 页面
  * ---------------------------------------------
 -->

<template>
  <div class="page-404">
    <img src="@assets/images/404.svg" alt="" />

    <router-link to="/system/user"> user </router-link>
  </div>
</template>

<style lang="less" scoped>
.page-404 {
  display: flex;
  align-items: center;
  justify-content: center;
  padding-top: 15vh;
  flex-direction: column;

  img {
    margin-bottom: 24px;
    user-select: none;
    pointer-events: none;
  }
}
</style>
