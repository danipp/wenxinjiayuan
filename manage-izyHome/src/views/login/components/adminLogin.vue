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
                    <h2 class="login-title">管理员中心登录</h2>
                    <p class="login-subtitle">派单·接单·爆单——尽在高教传媒</p>
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
                            <el-checkbox v-model="loginForm.remember">记住登录状态</el-checkbox>
                            <a href="#" class="login-forgot">忘记密码?</a>
                        </div>
                        <el-form-item>
                            <div style="width: 100%;">
                                <el-button type="primary" class="login-submit-btn" :loading="loading"
                                    style="width: 100%" @click="onLogin">
                                    登录
                                </el-button>
                            </div>
                        </el-form-item>
                    </el-form>
                </div>
                <div class="login-card-right">
                    <h3 class="login-right-title">高教Media后台管理系统</h3>
                    <p class="login-right-desc">数据驱动的媒介平台，让每一次传播都有迹可循</p>
                    <div class="login-right-features">
                        <div class="login-feature-item">
                            <span class="login-feature-icon">🎯</span>
                            <span>风控护航</span>
                        </div>
                        <div class="login-feature-item">
                            <span class="login-feature-icon">📊</span>
                            <span>智能运维</span>
                        </div>
                        <div class="login-feature-item">
                            <span class="login-feature-icon">💰</span>
                            <span>审计可溯</span>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</template>

<script setup name="adminLogin">
import { ref, reactive } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/modules/user";
import { adminLogin } from '@/api/modules/admin'
const userStore = useUserStore();
const router = useRouter();
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
        loading.value = true;
        // TODO: 这里对接你的登录接口
        adminLogin({
            cellphone: loginForm.cellphone,
            passWord: userStore.encryptPassword(loginForm.passWord)
        }).then(async res => {
            console.log(res);
            ElMessage.success("登录成功");
            userStore.setToken(res.data);
            userStore.setAdmin(true)
            userStore.setAgent(false)
            await userStore.getUserInfo()
            setTimeout(() => {
                loading.value = false;
                window.location.href = "/#/admin/home";
                window.location.reload();
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
</style>