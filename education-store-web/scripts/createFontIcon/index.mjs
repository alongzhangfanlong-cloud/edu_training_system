/**
 * ---------------------------------------------
 * 创建字体图标脚本
 * ---------------------------------------------
 */

import svgtofont from "svgtofont";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

svgtofont({
  src: path.resolve(__dirname, "./svgs"), // svg 图标目录路径
  dist: path.resolve(process.cwd(), "./src/assets/svgfont"), // 输出到指定目录中
  fontName: "svgfont",
  classNamePrefix: "ifont",
  css: true,
  // useNameAsUnicode: true,
  website: {
    title: "svg font",
    template: path.resolve(__dirname, "./template.html")
  },
  emptyDist: true // 清空输出目录
}).then(() => {
  console.log("done!");
});
