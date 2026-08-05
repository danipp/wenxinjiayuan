<template>
    <div class="login-bg">
        <header class="login-header">
            <div class="login-header-content">
                <div class="login-logo-area">
                    <!-- <div class="login-logo-icon">F</div>
                    <span class="login-logo-text">SDMediaCenter</span> -->
                    <img src="https://mediac.oss-cn-guangzhou.aliyuncs.com/home/logo.png" style="height: 30px;"
                        alt="高教MEDIA Logo" />

                </div>
                <a href="/" class="login-back-btn">返回首页</a>
            </div>
        </header>
        <main class="login-main">
            <div class="login-card">
                <div class="login-card-left">
                    <h2 class="login-title">代理中心</h2>
                    <p class="login-subtitle">登录您的高教Media代理商账户，开始您的赚钱之旅</p>
                    <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form" size="large"
                        @submit.prevent="onLogin">
                        <el-form-item prop="cellphone">
                            <el-input v-model="loginForm.cellphone" placeholder="请输入用户名或邮箱" prefix-icon="User"
                                clearable />
                        </el-form-item>
                        <el-form-item prop="passWord">
                            <el-input v-model="loginForm.passWord" :type="showPassword ? 'text' : 'password'"
                                placeholder="请输入密码" @suffix-icon-click="togglePassword" show-password
                                autocomplete="new-password" clearable />
                        </el-form-item>
                        <div class="login-form-options">
                            <!-- <el-checkbox v-model="loginForm.remember">记住登录状态</el-checkbox>
                            <a href="#" class="login-forgot">忘记密码?</a> -->
                            <el-checkbox v-model="loginForm.remember" size="large" style="margin-right: 10px;" />
                            请阅读并同意<span @click.stop="openRules('privacyVisable')">《平台入驻规则与协议》</span>及<span
                                @click.stop="openRules('serviceVisible')">《平台隐私政策》</span>
                        </div>
                        <el-form-item>
                            <div style="width: 100%;">
                                <el-button type="primary" class="login-submit-btn" :loading="loading"
                                    style="width: 100%" @click="onLogin">
                                    登录
                                </el-button>
                                <div class="flex switch" @click="handleSwitch">切换到广告主/账户主登录</div>
                            </div>
                        </el-form-item>
                        <div class="login-register">
                            <span>还没有账户？</span>
                            <a href="/#/agentRegister">立即注册</a>
                        </div>
                    </el-form>
                </div>
                <div class="login-card-right">
                    <h3 class="login-right-title">连接优质媒体资源</h3>
                    <p class="login-right-desc">帮高教Media发展媒介资源，躺着赚钱！</p>
                    <div class="login-right-features">
                        <div class="login-feature-item">
                            <span class="login-feature-icon">🎯</span>
                            <span>分享致富</span>
                        </div>
                        <div class="login-feature-item">
                            <span class="login-feature-icon">📊</span>
                            <span>管道收益</span>
                        </div>
                        <div class="login-feature-item">
                            <span class="login-feature-icon">💰</span>
                            <span>分润可控</span>
                        </div>
                    </div>
                </div>
            </div>
        </main>
        <privacy v-if="privacyService.privacyVisable" v-model:modelValue="privacyService.privacyVisable" />
    <service v-if="privacyService.serviceVisible" v-model:modelValue="privacyService.serviceVisible" />
    </div>
</template>

<script setup name="agentLogin">
import { ref, reactive } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/modules/user";
import { handleAgentLogin } from "@/api/modules/login";
const userStore = useUserStore();
const router = useRouter();
import privacy from "@/components/privacyServiceAgent/privacy.vue";
import service from "@/components/privacyServiceAgent/service.vue";
const privacyService = reactive({
  privacyVisable: false,
  serviceVisible: false
});
const openRules = (key) => {
  privacyService[key] = true;
}
const loginFormRef = ref();
const loading = ref(false);
const showPassword = ref(false);
const handleSwitch = () => {
    router.push('/login')
}
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
    loginFormRef.value.validate(async (valid) => {
        if (!valid) return;
        if (!loginForm.remember) {
            ElMessage.error("请阅读并同意平台入驻规则与协议及平台隐私政策")
            return
        }
        loading.value = true;
        // TODO: 这里对接你的登录接口
        handleAgentLogin({
            cellphone: loginForm.cellphone,
            passWord: userStore.encryptPassword(loginForm.passWord)
        }).then(async res => {
            console.log(res);
            ElMessage.success("登录成功");
            userStore.setToken(res.data);
            userStore.setAgent(true)
            await userStore.getUserInfo()
            setTimeout(() => {
                loading.value = false;
                window.location.href = "/";
            }, 500);
        }).catch(err => {
            setTimeout(() => {
                loading.value = false;
            }, 500);
        })

    });
}
</script>

<style scoped lang="scss">
@import "../index.scss";

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