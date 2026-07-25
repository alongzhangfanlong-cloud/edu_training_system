<!-- 积分礼品新增/编辑弹窗 -->
<template>
  <el-dialog
    :model-value="visible"
    :title="mode === 'add' ? '新增礼品' : '编辑礼品'"
    width="600px"
    destroy-on-close
    append-to-body
    @open="onOpen"
    @update:model-value="val => emit('update:visible', val)"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" size="small">
      <el-form-item label="礼品名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入礼品名称" maxlength="50" show-word-limit />
      </el-form-item>

      <el-form-item label="礼品图片" prop="imageUrl">
        <el-upload
          ref="uploadRef"
          :http-request="handleUpload"
          :limit="1"
          accept=".jpg,.jpeg,.png"
          :before-upload="beforeUpload"
          :on-remove="onUploadRemove"
          :file-list="fileList"
          list-type="picture-card"
          :disabled="uploading"
        >
          <el-icon><Plus /></el-icon>
          <template #tip>
            <div class="el-upload__tip">支持 jpg/png 格式，单张 ≤ 2MB</div>
          </template>
        </el-upload>
        <el-progress v-if="uploading" :percentage="uploadPercent" style="width:200px" />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="兑换所需积分" prop="pointsRequired">
            <el-input-number
              v-model="form.pointsRequired"
              :min="1" :max="99999" :step="1"
              :precision="0"
              placeholder="请输入积分"
              style="width:100%"
              controls-position="right"
            />
          </el-form-item>
        </el-col>
        <el-col v-if="mode === 'add'" :span="12">
          <el-form-item label="库存数量" prop="stockTotal">
            <el-input-number
              v-model="form.stockTotal"
              :min="1" :max="9999" :step="1"
              :precision="0"
              placeholder="请输入库存"
              style="width:100%"
              controls-position="right"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="礼品描述">
        <el-input
          v-model="form.description"
          type="textarea" :rows="3"
          placeholder="请输入礼品描述（选填）"
          maxlength="500" show-word-limit
        />
      </el-form-item>

      <el-form-item label="状态">
        <el-switch
          v-model="form.status"
          :active-value="1" :inactive-value="0"
          active-text="启用" inactive-text="禁用"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="emit('update:visible', false)">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { getGift, addGift, updateGift } from '@/api/points/index';
import { uploadFile } from '@/api/common/file';

const props = defineProps({
  visible: { type: Boolean, default: false },
  mode: { type: String, default: 'add' },
  giftId: { type: [Number, String], default: null },
});
const emit = defineEmits(['update:visible', 'submit-success']);

const formRef = ref(null);
const uploadRef = ref(null);
const submitting = ref(false);
const uploading = ref(false);
const uploadPercent = ref(0);
const removedImage = ref(false); // 编辑时是否移除了已有图片

const form = ref({
  name: '',
  imageUrl: '',
  pointsRequired: 50,
  stockTotal: 100,
  description: '',
  status: 1,
});

const fileList = computed(() => {
  if (form.value.imageUrl && !removedImage.value) {
    // 兼容旧数据：/api/v1/files/ → /upload/ 用于内联图片显示
    const url = form.value.imageUrl.replace('/api/v1/files/', '/upload/');
    return [{ name: '礼品图片', url }];
  }
  return [];
});

const rules = {
  name: [
    { required: true, message: '请输入礼品名称', trigger: 'blur' },
    { max: 50, message: '礼品名称不超过50个字符', trigger: 'blur' },
  ],
  imageUrl: [
    { required: true, message: '请上传礼品图片', trigger: 'change' },
  ],
  pointsRequired: [
    { required: true, message: '请输入兑换所需积分', trigger: 'blur' },
    { type: 'number', min: 1, max: 99999, message: '积分范围为 1 ~ 99999', trigger: 'blur' },
  ],
  stockTotal: [
    { required: true, message: '请输入库存数量', trigger: 'blur' },
    { type: 'number', min: 1, max: 9999, message: '库存范围为 1 ~ 9999', trigger: 'blur' },
  ],
};

// ── 上传 ──

function beforeUpload(file) {
  const isImage = ['image/jpeg', 'image/png'].includes(file.type);
  if (!isImage) { ElMessage.error('仅支持 jpg/png 格式'); return false; }
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) { ElMessage.error('图片大小不能超过 2MB'); return false; }
  return true;
}

async function handleUpload({ file }) {
  uploading.value = true;
  uploadPercent.value = 10;
  const fd = new FormData();
  fd.append('file', file);
  try {
    const res = await uploadFile(fd);
    if (res.success && res.data) {
      uploadPercent.value = 100;
      // 后端返回 { fileName, filePath, fileSize, fileType, storedName }
      const fp = typeof res.data === 'string' ? res.data : (res.data.filePath || res.data.url || '');
      form.value.imageUrl = fp ? `/upload/${fp}` : '';
      removedImage.value = false;
      formRef.value?.validateField('imageUrl');
    } else {
      ElMessage.error(res.msg || '上传失败');
    }
  } catch {
    ElMessage.error('上传失败，请重试');
  } finally {
    uploading.value = false;
  }
}

function onUploadRemove() {
  form.value.imageUrl = '';
  removedImage.value = true;
  formRef.value?.validateField('imageUrl');
}

// ── 打开弹窗 ──

async function onOpen() {
  reset();
  if (props.mode === 'edit' && props.giftId) {
    try {
      const res = await getGift(props.giftId);
      if (res.success && res.data) {
        const d = res.data;
        form.value = {
          name: d.name || '',
          imageUrl: d.imageUrl || '',
          pointsRequired: d.pointsRequired ?? 50,
          stockTotal: d.stockTotal ?? 100,
          description: d.description || '',
          status: d.status ?? 1,
        };
        removedImage.value = false;
      } else {
        ElMessage.error(res.msg || '加载礼品详情失败');
        emit('update:visible', false);
      }
    } catch {
      ElMessage.error('加载详情失败');
      emit('update:visible', false);
    }
  }
}

function reset() {
  form.value = { name: '', imageUrl: '', pointsRequired: 50, stockTotal: 100, description: '', status: 1 };
  removedImage.value = false;
  uploading.value = false;
  uploadPercent.value = 0;
  nextTick(() => formRef.value?.clearValidate());
}

// ── 提交 ──

async function handleSubmit() {
  if (uploading.value) { ElMessage.warning('请等待图片上传完成'); return; }
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  submitting.value = true;
  try {
    const data = {
      name: form.value.name,
      imageUrl: form.value.imageUrl,
      pointsRequired: form.value.pointsRequired,
      description: form.value.description,
      status: form.value.status,
    };
    let res;
    if (props.mode === 'edit') {
      res = await updateGift(props.giftId, data);
    } else {
      data.stockTotal = form.value.stockTotal;
      res = await addGift(data);
    }
    if (res.success) {
      ElMessage.success(props.mode === 'add' ? '新增成功' : '编辑成功');
      emit('update:visible', false);
      emit('submit-success');
    } else {
      ElMessage.error(res.msg || '操作失败');
    }
  } catch {
    ElMessage.error('网络异常，请稍后重试');
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped lang="less">
.el-upload__tip { margin-top: 4px; }
</style>
