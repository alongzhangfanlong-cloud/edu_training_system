# CLAUDE.md

> 本文件用于指导 Claude Code 在本项目中的行为。
> 所有规则、命令和约束在每次会话开始时自动生效。

---

## 📦 组件拆分规范

### 拆分判断标准

**以下信号表明该拆分：**

| 信号 | 阈值 | 说明 |
|------|------|------|
| **文件行数** | > 300 行 | 单文件过长，滚动吃力，定位困难 |
| **状态独立** | 子模块有自己独立的数据/分页/加载状态 | 与父级互不污染，天然适合抽离 |
| **功能内聚** | 一个模块做"一件事" | 输入少（2~3 个 props），输出明确（1~2 个事件） |
| **多处复用** | 同一功能被 ≥2 处触发 | Dialog 被多个入口调用、表格在多页面复用 |

**以下信号表明不该拆：**

| 信号 | 说明 |
|------|------|
| **Props/Events 爆炸** | 拆完后子组件需要 10+ props 或 5+ events 才能工作 |
| **功能未稳定** | 需求频繁变动时，过早拆分锁死错误的接口边界 |
| **拆完无人复用** | 仅一处使用 + 逻辑简单 + 行数 < 200 |

### 拆分优先级

```
Dialog > Drawer > 表格 > 表单 > 搜索栏
```

- Dialog/Drawer 最优先——它们是天然的自包含单元
- 表格和表单放在一起拆通常不如分开拆（各自独立演化）

### 组件接口约定

- **Props**：遵循单向数据流，禁止子组件直接修改 props
- **受控组件**：Dialog/Drawer 一律使用 `v-model`（`modelValue` + `update:modelValue`）
- **事件**：数据变更后 emit `saved`，关闭 emit `update:modelValue`，不用 `close`（v-model 已覆盖）
- **嵌套**：子组件内可以再嵌套子组件（如 Drawer 内嵌 Dialog），但最多两层

### 命名规范

```
页面目录/
├── index.vue              ← 页面主文件（只保留核心布局 + 组件编排）
├── ComponentA.vue         ← 子组件（PascalCase）
├── ComponentB.vue         ← 子组件
```

### 示例：字典管理页面拆分

```
dict/
├── index.vue              ← 字典类型管理（搜索 + 表格 + Dialog）
├── DictDataDrawer.vue     ← 字典数据 Drawer（列表 + CRUD）
└── DictDataDialog.vue     ← 字典数据编辑 Dialog（纯表单）
```

- `DictDataDrawer` 接收 3 个 props（`modelValue`, `typeCode`, `typeName`），emit `saved`
- `DictDataDialog` 接收 3 个 props（`modelValue`, `typeCode`, `data`），emit `saved`
- 拆分后 `index.vue` 从 600+ 行降到 230 行

---

## 🧩 技术约定

### Vue 规范

- **必须使用 Composition API**
- `<script setup>` 为唯一写法
- 禁止使用 Options API
- 组件名使用 **PascalCase**
- Props / Emits 必须使用 defineProps / defineEmits 声明
- 使用pnpm，禁止使用"npm / yarn"
- Vue 3（>=3.3，使用 defineModel）
- ❌ 禁止直接修改 props（使用 defineModel 或 emit）
- ❌ 禁止在子组件中修改 visible / open 等受控状态
- ❌ 禁止直接修改 props（使用 defineModel 或 emit）
- ❌ 禁止在子组件中修改 visible / open 等受控状态
- ✅ Dialog / Drawer 一律使用 v-model
- ✅ 所有接口调用必须走 `src/api`
- ✅ 新增页面必须同步更新路由


### 状态管理

- 全局状态：`Pinia`
- 本地状态：组件内使用 `ref`

### 样式

- 使用 `<style scoped lang="less">`，样式放到单独文件，通过@import引入的组件中
- 尽量少用 `!important`
- 主题变量统一放在 `styles/themes.css`

### 远程搜索规范

> 所有带远程搜索的下拉框（el-select filterable + remote）和搜索按钮触发的列表查询，必须应用以下模式。

#### 模式一：远程下拉搜索（防抖 + 竞态）

适用于 `el-select` 的 `remote-method` 回调。用户每按一个键都可能触发 API，需要防抖；快速输入时多个请求并发，需要竞态保护。

```javascript
// ✅ 正确写法
let searchTimer = null;
let searchSeq = 0;

function searchUsers(keyword) {
  clearTimeout(searchTimer);
  if (!keyword) { userOptions.value = []; return; }

  userLoading.value = true;
  searchTimer = setTimeout(() => {
    searchSeq++;
    const seq = searchSeq;
    listUser({ page: 1, pageSize: 100, keyword }).then(res => {
      if (seq !== searchSeq) return;          // ← 丢弃过期响应
      if (res.success) {
        userOptions.value = res.data?.records || [];
      }
    }).finally(() => {
      if (seq === searchSeq) userLoading.value = false;
    });
  }, 300);                                   // ← 300ms 防抖
}
```

| 参数 | 值 | 说明 |
|------|----|------|
| 防抖延迟 | 300ms | 输入停止 300ms 后才发请求 |
| pageSize | 100 | 部门选择场景足够，特殊场景可调整 |
| 竞态序号 | `searchSeq` | 只处理最新请求的结果，旧响应丢弃 |

```javascript
// ❌ 错误写法 — 无防抖，无竞态保护
function searchUsers(keyword) {
  listUser({ page: 1, pageSize: 50, keyword }).then(res => {
    userOptions.value = res.data?.records || [];
  });
}
```

#### 模式二：按钮触发搜索（仅竞态保护）

适用于用户点击"搜索"按钮或按 Enter 触发的列表查询。不需要防抖（用户主动触发），但快速重复点击会导致请求竞态。

```javascript
// ✅ 正确写法
let querySeq = 0;

function getList() {
  loading.value = true;
  querySeq++;
  const seq = querySeq;
  listUser(params).then(res => {
    if (seq !== querySeq) return;              // ← 丢弃过期响应
    if (res.success) { /* 更新列表 */ }
  }).finally(() => {
    if (seq === querySeq) loading.value = false;
  });
}

// ❌ 错误写法 — 快速双击搜索按钮时，先点的结果可能后到，显示错误数据
function getList() {
  loading.value = true;
  listUser(params).then(res => { /* ... */ })
    .finally(() => { loading.value = false; });
}
```

#### 判断使用哪种模式

```
el-select + remote-method → 模式一（防抖 + 竞态）
el-button + @click + API → 模式二（仅竞态）
@keyup.enter + API        → 模式二（仅竞态）
```

#### 原理说明

**防抖 (Debounce)**：连续触发时只执行最后一次。快速输入 "zhang" → 4次按键 → 只发1次请求。

**竞态保护 (Race Condition)**：多次 API 调用按序号标记，只处理最新序号的响应。请求 A 网络慢、请求 B 网络快 → B 先返回并渲染 → A 后到达但被丢弃。

```
时间线：
  按键 z → 按键 h → 按键 a  → ...停止300ms...→ 发请求(seq=1)
  如果请求未返回时用户又输入：
    按键 s → ... → 发请求(seq=2) 
    seq=1 的响应到达 → 丢弃（seq !== searchSeq）
    seq=2 的响应到达 → 处理 ✅
```

### Pinia Store 解构规范

> 2026-07-08 新增：修复了全站字典下拉框不渲染的问题。根因是 `useDictsStore()` 返回的是 Store 实例，模板中 `dicts.sys_normal_disable` 访问的是 `store.sys_normal_disable`（不存在→undefined）。

```javascript
// ❌ 错误 — dicts 是 Store 实例，dicts.sys_normal_disable ≡ undefined
const dicts = useDictsStore();
// 模板: v-for="d in dicts.sys_normal_disable"  → 永远为空

// ✅ 正确 — 解构获取 ComputedRef
const { dicts, loadDict } = useDictsStore();
// 模板: v-for="d in dicts.sys_normal_disable"  → Proxy getter → 正确追踪响应式
```

| Store 返回值 | 用法 |
|-------------|------|
| ComputedRef (`dicts`) | 解构后在模板中 `dicts.xxx` 直接使用 |
| 方法 (`loadDict`, `loadDicts`) | 解构后直接调用，不再通过 `dicts.xxx()` |

### 服务端搜索模式

> 列表/树类页面的搜索应触发后端接口调用，而非客户端计算属性过滤。

```javascript
// ✅ 正确 — 搜索按钮触发 API 调用
function handleQuery() { getList(); }

function getList() {
  loading.value = true;
  const params = {};
  if (queryParams.value.name?.trim()) params.name = queryParams.value.name.trim();
  if (queryParams.value.status != null && queryParams.value.status !== '') params.status = queryParams.value.status;
  listApi(params).then(res => {
    if (res.success) dataList.value = res.data || [];
  }).finally(() => { loading.value = false; });
}

// ❌ 错误 — 一次加载全量数据，前端 computed 过滤
// 问题: 搜索按钮点击无 API 调用，用户困惑；数据量大时首次加载慢
```

```
判断何时用服务端搜索:
  数据量 > 200 条 → 必须服务端搜索
  数据量 < 200 条 → 可客户端过滤，但搜索按钮仍需有明确行为（触发表格刷新）
```

## 编码规范

- 缩进 2 空格，单引号，分号必须
