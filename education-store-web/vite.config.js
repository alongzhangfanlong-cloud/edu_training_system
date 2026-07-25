import vue from "@vitejs/plugin-vue";
import { resolve } from "path";
import { defineConfig, loadEnv } from "vite";
import { createHtmlPlugin } from "vite-plugin-html";

export default defineConfig(({ mode }) => {
  // 读取.env文件内容到process.env
  const env = loadEnv(mode, process.cwd(), "");

  return {
    base: "./",
    plugins: [
      vue(),
      createHtmlPlugin({
        inject: {
          data: {
            title: env.VITE_DOCUMENT_TITLE
          }
        }
      }),
    ],
    resolve: {
      alias: {
        "@": resolve(__dirname, "src"),
        "@assets": resolve(__dirname, "src/assets")
      },

      // 优化导入路径，自动解析的文件扩展名
      extensions: [".mjs", ".js", ".ts", ".jsx", ".tsx", ".json", ".vue"]
    },
    server: {
      host: "0.0.0.0",
      port: env.VITE_PORT,
      proxy: {
        // 代理配置：见.env文件
        [env.VITE_API_PREFIX]: {
          target: env.VITE_API_TARGET,
          changeOrigin: true,
          rewrite: path => path.replace(new RegExp(`^${env.VITE_API_PREFIX}`), "")
        },
        // 上传文件静态资源 — 直接转发到后端（路径不变）
        "/upload": {
          target: env.VITE_API_TARGET,
          changeOrigin: true
        }
      }
    },
    build: {
      rollupOptions: {
        output: {
          // 1. 配置入口JS文件
          entryFileNames: "js/[name]-[hash].js",
          // 2. 配置代码分割产生的chunk文件
          chunkFileNames: "js/[name]-[hash].js",
          // 3. 配置静态资源文件（核心：按类型分类）
          assetFileNames: assetInfo => {
            const ext = assetInfo.name ? assetInfo.name.split(".").pop() : "";
            // 处理CSS文件
            if (ext === "css") {
              return "css/[name]-[hash].[ext]";
            }
            // 处理图片文件
            const imgExts = ["png", "jpg", "jpeg", "gif", "svg", "webp"];
            if (imgExts.includes(ext)) {
              return "img/[name]-[hash].[ext]";
            }
            // 处理字体文件
            const fontExts = ["ttf", "otf", "woff", "woff2", "eot"];
            if (fontExts.includes(ext)) {
              return "fonts/[name]-[hash].[ext]";
            }
            // 其他资源默认存放位置
            return "files/[name]-[hash].[ext]";
          }
        }
      }
    }
  };
});
