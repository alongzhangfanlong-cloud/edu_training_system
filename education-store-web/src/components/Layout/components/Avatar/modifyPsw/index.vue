<!--
  修改密码弹窗
-->

<template>
  <el-dialog v-model="dialogVisible" title="修改密码" width="500" :close-on-click-modal="false" @close="handleClose">
    <div class="container">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px" @keyup.enter="handleConfirm">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input
            placeholder="请输入旧密码"
            v-model="form.oldPassword"
            type="password"
            show-password
            autocomplete="off"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            placeholder="请输入新密码"
            type="password"
            show-password
            v-model="form.newPassword"
            autocomplete="off"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            placeholder="请再次输入新密码"
            type="password"
            show-password
            v-model="form.confirmPassword"
            autocomplete="off"
          />
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleConfirm" :loading="confirmLoading">确认</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { ElMessage } from "element-plus";
import { updatePassword } from "@/api/auth/login";
import { getUserName } from "@/utils/validate";

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(["update:visible"]);

// 控制弹窗显示
const dialogVisible = computed({
  get: () => props.visible,
  set: value => emit("update:visible", value)
});

// 表单数据
const form = ref({
  oldPassword: "",
  newPassword: "",
  confirmPassword: ""
});

// 表单引用
const formRef = ref();

// 确认按钮加载状态
const confirmLoading = ref(false);

// 密码格式校验规则
const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error("密码不能为空"));
  } else if (value.length < 6 || value.length > 20) {
    callback(new Error("密码长度需在6-20位之间"));
  } else {
    callback();
  }
};

// 确认密码校验
const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error("请再次输入密码"));
  } else if (value !== form.value.newPassword) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

// 校验新密码不能与旧密码相同
const validateNewPassword = (rule, value, callback) => {
  if (value === form.value.oldPassword) {
    callback(new Error("新密码不能与旧密码相同"));
  } else {
    validatePassword(rule, value, callback);
  }
};

// 表单验证规则
const rules = {
  oldPassword: [{ required: true, message: "请输入旧密码", trigger: "blur" }],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { validator: validateNewPassword, trigger: "blur" }
  ],
  confirmPassword: [
    { required: true, message: "请确认新密码", trigger: "blur" },
    { validator: validateConfirmPassword, trigger: "blur" }
  ]
};

// 关闭弹窗时的处理
const handleClose = () => {
  resetForm();
  emit("update:visible", false);
};

// 确认修改
const handleConfirm = async () => {
  if (!formRef.value) return;

  // 表单验证
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  confirmLoading.value = true;

  try {
    // 这里调用修改密码的API
    const res = await updatePassword({
      username: getUserName(),
      oldPassword: form.value.oldPassword,
      newPassword: form.value.newPassword
    });

    if (res?.code === 200) {
      ElMessage.success("密码修改成功");
      handleClose();
    } else {
      ElMessage.error(res?.message || "密码修改失败");
    }
  } catch (error) {
    ElMessage.error(error);
  } finally {
    confirmLoading.value = false;
  }
};

// 重置表单
const resetForm = () => {
  form.value = {
    oldPassword: "",
    newPassword: "",
    confirmPassword: ""
  };
  formRef.value?.resetFields();
};

// 监听弹窗打开，重置表单
watch(
  () => props.visible,
  newVal => {
    if (newVal) {
      resetForm();
    }
  }
);
</script>

<style lang="less" scoped>
// 可以根据需要添加样式
.dialog-footer {
  text-align: center;
}
.container {
  padding: 16px 16px 0;
}
</style>
