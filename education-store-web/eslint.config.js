import js from "@eslint/js";
import globals from "globals";
import pluginVue from "eslint-plugin-vue";
import { defineConfig } from "eslint/config";

export default defineConfig([
  {
    files: ["**/*.{js,mjs,cjs,vue}"],
    plugins: { js, pluginVue },
    extends: ["js/recommended"],
    languageOptions: {
      globals: {
        ...globals.browser,
        ...globals.node
      }
    }
  },
  pluginVue.configs["flat/essential"],

  // 针对js文件的特定规则配置
  {
    files: ["**/*.{js,mjs,cjs,vue}"],
    rules: {
      "no-undef": "error",
      "no-unused-vars": "warn",
      "no-const-assign": "error",
      // 	强制使用===和!==，避免类型转换错误
      eqeqeq: "error"
    }
  },

  // 针对Vue文件的特定规则配置
  {
    files: ["**/*.vue"],
    rules: {
      // 禁止声明未使用的变量
      "vue/no-unused-vars": "warn",
      // 未定义变量检查
      "vue/no-undef-components": "off",
      "vue/no-undef-properties": "warn",
      // 常规的未定义变量检查
      "no-undef": "error",
      // 	强制使用===和!==，避免类型转换错误
      eqeqeq: "error",
      // 组件名称必须为多个单词
      "vue/multi-word-component-names": "off"
    }
  }
]);
