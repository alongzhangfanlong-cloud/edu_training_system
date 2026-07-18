<!--
 * ---------------------------------------------
 * 路由包装组件，用来解决路由无name问题
 * ---------------------------------------------
-->

<script>
import { h } from "vue";
export default {
  name: "RouterViewWrapper",
  inheritAttrs: false,

  render() {
    if (!this.component) return null;

    // 复制组件选项，确保有 name 属性
    const componentOptions = {
      ...this.component,
      name: this.component.name || this.componentName || "AnonymousComponent"
    };
    const a = h(componentOptions, this.$attrs);

    console.debug("a", a);

    return a;
  },

  props: {
    component: {
      type: [Object, Function],
      required: true
    },
    componentName: {
      type: String,
      default: ""
    }
  }
};
</script>
