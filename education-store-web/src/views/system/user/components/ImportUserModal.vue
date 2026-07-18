<!-- 导入用户弹窗 -->
<template>
  <el-dialog
    :model-value="visible"
    title="导入用户"
    width="520px"
    destroy-on-close
    append-to-body
    @update:model-value="(val) => emit('update:visible', val)"
    @open="onOpen"
  >
    <div class="import-container">
      <el-alert
        title="操作说明"
        type="info"
        :closable="false"
        style="margin-bottom: 16px"
      >
        <template #default>
          <ol style="margin: 4px 0 0 16px; padding: 0; font-size: 13px">
            <li>下载导入模板，按模板格式填写用户数据</li>
            <li>用户名是必填字段，密码为空时默认为 123456</li>
            <li>勾选"覆盖更新"时，已存在的用户将被更新</li>
          </ol>
        </template>
      </el-alert>

      <el-upload
        ref="uploadRef"
        drag
        :auto-upload="false"
        :limit="1"
        accept=".xlsx,.xls"
        :on-change="handleFileChange"
        :on-remove="handleFileRemove"
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">
          将 Excel 文件拖到此处，或<em>点击选择</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            仅支持 .xlsx / .xls 格式
          </div>
        </template>
      </el-upload>

      <el-checkbox v-model="updateSupport" style="margin-top: 12px">
        覆盖更新已存在的用户（根据用户名匹配）
      </el-checkbox>
    </div>

    <template #footer>
      <el-button @click="handleDownloadTemplate">
        <el-icon><Download /></el-icon>下载模板
      </el-button>
      <el-button @click="emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit" :disabled="!file">
        <el-icon><Upload /></el-icon>确认导入
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { UploadFilled, Upload, Download } from '@element-plus/icons-vue';
import { importUser, downloadTemplate } from '@/api/system/user';
import { downloadFromBlob } from '@/utils/download';

const props = defineProps({
  visible: { type: Boolean, default: false },
});
const emit = defineEmits(['update:visible', 'submit-success']);

const uploadRef = ref(null);
const file = ref(null);
const updateSupport = ref(false);
const submitting = ref(false);

function onOpen() {
  file.value = null;
  updateSupport.value = false;
  uploadRef.value?.clearFiles();
}

function handleFileChange(uploadFile) {
  file.value = uploadFile.raw;
}

function handleFileRemove() {
  file.value = null;
}

async function handleDownloadTemplate() {
  try {
    const res = await downloadTemplate();
    downloadFromBlob(res, '用户导入模板.xlsx');
  } catch {
    ElMessage.error('模板下载失败');
  }
}

async function handleSubmit() {
  if (!file.value) {
    ElMessage.warning('请先选择文件');
    return;
  }
  submitting.value = true;
  try {
    const formData = new FormData();
    formData.append('file', file.value);
    formData.append('updateSupport', updateSupport.value);

    const res = await importUser(formData);
    if (res.success) {
      const msg = res.data || '导入完成';
      ElMessage.success(msg);
      emit('update:visible', false);
      emit('submit-success');
    } else {
      ElMessage.error(res.msg || res.message || '导入失败');
    }
  } catch (e) {
    ElMessage.error('导入失败：' + (e.message || '未知错误'));
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped lang="less">
.import-container {
  padding: 0 4px;
}
</style>
