import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    uni(),
  ],
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@import "uview-plus/theme.scss";`,
        silenceDeprecations: [
          'import',          // uView Plus 使用 @import，等待其升级到 @use/@forward
          'legacy-js-api',   // Vite 内置 sass 插件的旧 API，等待 Vite 升级
        ],
      },
    },
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
})
