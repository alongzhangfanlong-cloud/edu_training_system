<script>
export default {
  onLaunch() {
    console.log('教育培训管理系统 - 学员端启动')
    // 检查登录状态
    const token = uni.getStorageSync('token')
    if (token) {
      // TODO: 验证token有效性
      console.log('已登录')
    }
  },
  onShow() {
    console.log('App Show')
  },
  onHide() {
    console.log('App Hide')
  }
}
</script>

<style>
/* 全局样式 */
page {
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  font-size: 28rpx;
  color: #333;
}

.container {
  padding: 20rpx;
}

.card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.06);
}

.text-primary { color: #1890ff; }
.text-success { color: #52c41a; }
.text-warning { color: #faad14; }
.text-danger { color: #ff4d4f; }
.text-muted { color: #999; }

.btn-primary {
  background: #1890ff;
  color: #fff;
  border-radius: 8rpx;
  padding: 16rpx 32rpx;
  text-align: center;
  border: none;
}

.flex-row {
  display: flex;
  flex-direction: row;
  align-items: center;
}

.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.flex-1 { flex: 1; }
</style>
