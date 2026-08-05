<template>
  <div class="login-container flx-center">
    <div class="login-box">
      <!-- <SwitchDark class="dark" /> -->
      <div class="login-left">
        <img class="login-left-img" src="@/assets/images/login_left.png" alt="login" />
      </div>
      <div class="login-form">
        <div class="login-logo">
          <img class="login-icon" src="@/assets/images/logo.svg" alt="" />
          <h2 class="logo-text">温馨家园管理系统</h2>
        </div>
        <LoginForm />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="login">
import LoginForm from "./components/LoginForm.vue";
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/modules/user";
import { handleLogin } from "@/api/modules/login";
import { initDynamicRouter } from "@/routers/modules/dynamicRouter";

const userStore = useUserStore();
const router = useRouter();
import privacy from "@/components/privacyService/privacy.vue";
import service from "@/components/privacyService/service.vue";
const privacyService = reactive({
  privacyVisable: false,
  serviceVisible: false
});
const openRules = key => {
  privacyService[key] = true;
};
const loginFormRef = ref();
const loading = ref(false);
const showPassword = ref(false);
const handleSwitch = () => {
  router.push("/agentLogin");
};

const loginForm = reactive({
  cellphone: "",
  passWord: "",
  remember: false
});

const loginRules = {
  cellphone: [{ required: true, message: "请输入用户名或邮箱", trigger: "blur" }],
  passWord: [{ required: true, message: "请输入密码", trigger: "blur" }]
};

function togglePassword() {
  showPassword.value = !showPassword.value;
}

function onLogin() {
  if (!loginFormRef.value) return;
  loginFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return;
    if (!loginForm.remember) {
      ElMessage.error("请阅读并同意平台入驻规则与协议及平台隐私政策");
      return;
    }
    loading.value = true;
    // 2.添加动态路由
    initDynamicRouter().then(res => {
      setTimeout(() => {
        ElMessage.success("登录成功");
        userStore.setToken("qwdwqwqwqwqwqwqwqwq");
        setTimeout(() => {
          loading.value = false;
          router.push("/home");
        }, 500);
      }, 500);
    });
  });
}
onMounted(() => {
  localStorage.removeItem("geeker-global");
  localStorage.removeItem("geeker-auth");
});
</script>

<style scoped lang="scss">
@import "./index.scss";

.switch {
  justify-content: center;
  color: var(--el-color-primary);
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }
}

.login-form-options {
  font-size: 14px;
  white-space: nowrap;

  span {
    color: var(--el-color-primary);
    cursor: pointer;
  }
}
</style>
