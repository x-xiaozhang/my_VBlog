<template>
  <!-- 保留美观的全局渐变背景布局 -->
  <div class="login-page">
    <div class="art-text">欢迎来到VBlog系统！</div>

    <!-- 保留美观的卡片容器布局，融入源仓库表单逻辑 -->
    <div class="login-container">
      <!-- 左侧美观装饰区域：完整保留 -->
      <div class="login-left">
        <div class="left-logo">
          <span class="logo-text">V</span>
        </div>
        <h2 class="left-title">VBlog 博客系统</h2>
        <p class="left-desc">专注于优质内容创作与分享的博客平台</p>
        <ul class="left-features">
          <li>简洁高效的编辑体验</li>
          <li>安全可靠的用户认证</li>
          <li>丰富的内容展示形式</li>
          <li>个性化的账号设置</li>
        </ul>
        <div class="left-copyright">© 2025 VBlog 版权所有</div>
      </div>

      <!-- 右侧表单区域：保留美观样式，融入源仓库核心逻辑 -->
      <div class="login-right">
        <div class="decor-bg"></div>
        <div class="form-content">
          <!-- 保留美观的标题区域，随标签切换变化 -->
          <div class="title-area">
            <div class="main-title">{{ activeTab === 'login' ? '系统登录' : '系统注册' }}</div>
            <div class="sub-title">
              {{ activeTab === 'login' ? '欢迎回来，请输入账号密码登录' : '填写信息创建你的专属账号' }}
            </div>
          </div>

          <!-- 美观的标签切换：绑定源仓库的activeTab -->
          <div class="tab-area">
            <div class="tab-item" :class="{ active: activeTab === 'login' }" @click="switchTab('login')">
              登录
            </div>
            <div class="tab-item" :class="{ active: activeTab === 'register' }" @click="switchTab('register')">
              注册
            </div>
          </div>

          <!-- 表单容器：融入源仓库的校验规则与v-loading -->
          <el-form
            :rules="currentRules"
            class="el-form-container"
            label-position="left"
            label-width="0px"
            v-loading="loading"
          >
            <!-- 登录表单：保留美观样式，绑定源仓库的表单字段 -->
            <div class="form-area" v-show="activeTab === 'login'">
              <div class="form-item">
                <label class="form-label">账号</label>
                <el-input
                  type="text"
                  v-model="loginForm.username"
                  auto-complete="off"
                  placeholder="请输入账号"
                  class="custom-input"
                ></el-input>
              </div>
              <div class="form-item">
                <label class="form-label">密码</label>
                <el-input
                  type="password"
                  v-model="loginForm.password"
                  auto-complete="off"
                  placeholder="请输入密码"
                  class="custom-input"
                ></el-input>
                <!-- 新增：登录小字提示 -->
                <div class="login-tip">当前默认为管理员账号登录</div>
              </div>
              <el-checkbox class="login_remember" v-model="checked" label-position="left">记住密码</el-checkbox>
              <div class="form-item">
                <el-button
                  type="primary"
                  @click.native.prevent="login"
                  style="width: 100%"
                  class="custom-btn"
                >登录</el-button>
              </div>
            </div>

            <!-- 注册表单：保留美观样式，补充源仓库的nickname和email字段 -->
            <div class="form-area" v-show="activeTab === 'register'">
              <div class="form-item">
                <label class="form-label">账号</label>
                <el-input
                  type="text"
                  v-model="registerForm.username"
                  auto-complete="off"
                  placeholder="请输入账号"
                  class="custom-input"
                ></el-input>
              </div>
              <div class="form-item">
                <label class="form-label">密码</label>
                <el-input
                  type="password"
                  v-model="registerForm.password"
                  auto-complete="off"
                  placeholder="请输入密码（需包含数字和英文，且长度大于8位）"
                  class="custom-input"
                  @blur="checkPassword"
                ></el-input>
              </div>
              <!-- 源仓库必备字段：昵称 -->
              <div class="form-item">
                <label class="form-label">昵称</label>
                <el-input
                  type="text"
                  v-model="registerForm.nickname"
                  auto-complete="off"
                  placeholder="请输入昵称"
                  class="custom-input"
                ></el-input>
              </div>
              <!-- 源仓库必备字段：邮箱 -->
              <div class="form-item">
                <label class="form-label">邮箱</label>
                <el-input
                  type="email"
                  v-model="registerForm.email"
                  auto-complete="off"
                  placeholder="请输入正确格式的邮箱（需包含@和.com）"
                  class="custom-input"
                  @blur="checkEmail"
                ></el-input>
              </div>
              <div class="form-item">
                <el-button
                  type="primary"
                  @click.native.prevent="register"
                  style="width: 100%"
                  class="custom-btn"
                >注册</el-button>
              </div>
            </div>
          </el-form>

          <!-- 底部区域：保留美观样式 -->
          <div class="bottom-area">
            © 2025 VBlog 博客系统 | <a href="#" class="forget-link">忘记密码?</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// 完全复刻源仓库的导入路径
import { postRequest } from '../utils/api';

export default {
  data() {
    return {
      // 源仓库核心：标签切换状态
      activeTab: 'login',
      // 源仓库核心：登录校验规则
      loginRules: {
        account: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        checkPass: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      // 源仓库核心：注册校验规则（包含昵称、邮箱校验）
      registerRules: {
        regUsername: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        regPassword: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        regNickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
        regEmail: [{ required: true, message: '请输入邮箱', trigger: 'blur' }]
      },
      // 源仓库核心：记住密码默认值
      checked: true,
      // 源仓库核心：登录表单默认值
      loginForm: {
        username: 'admin',
        password: '123'
      },
      // 源仓库核心：注册表单完整字段
      registerForm: {
        username: '',
        password: '',
        nickname: '',
        email: ''
      },
      // 源仓库核心：全局loading状态
      loading: false
    };
  },
  // 源仓库核心：计算属性切换校验规则
  computed: {
    currentRules() {
      return this.activeTab === 'login' ? this.loginRules : this.registerRules;
    }
  },
  methods: {
    // 标签切换方法：适配美观UI的切换逻辑
    switchTab(tab) {
      this.activeTab = tab;
      // 保留源仓库切换时的状态重置逻辑
      this.$nextTick(() => {
        const inputs = document.querySelectorAll('.custom-input');
        inputs.forEach(input => {
          input.blur();
        });
      });
    },

    // 新增：密码检查方法
    checkPassword() {
      const password = this.registerForm.password;
      // 检查密码是否包含数字和英文，且长度大于8位
      const hasNumber = /\d/.test(password);
      const hasLetter = /[a-zA-Z]/.test(password);
      const isLongEnough = password.length > 8;

      if (!hasNumber || !hasLetter || !isLongEnough) {
        this.$alert('密码必须同时包含数字和英文，且长度大于8位！', '密码格式错误');
        this.registerForm.password = ''; // 清空输入
      }
    },

    // 新增：邮箱检查方法
    checkEmail() {
      const email = this.registerForm.email;
      // 检查邮箱是否包含@和.com
      if (!email.includes('@') || !email.includes('.com')) {
        this.$alert('请输入正确格式的邮箱', '邮箱格式错误');
        this.registerForm.email = ''; // 清空输入
      }
    },

    // 源仓库核心：登录方法（逻辑、接口、响应处理完全一致）
    login: function () {
      var _this = this;
      this.loading = true;
      postRequest('/login', {
        username: this.loginForm.username,
        password: this.loginForm.password
      }).then(resp => {
        _this.loading = false;
        if (resp.status == 200) {
          var json = resp.data;
          if (json.status == 'success') {
            _this.$router.replace({ path: '/home' });
          } else {
            _this.$alert('登录失败!', '失败!');
          }
        } else {
          _this.$alert('登录失败!', '失败!');
        }
      }, resp => {
        _this.loading = false;
        _this.$alert('找不到服务器⊙﹏⊙∥!', '失败!');
      });
    },

    // 源仓库核心：注册方法（逻辑、接口、响应处理完全一致）
    register: function () {
      var _this = this;
      // 注册前先检查密码和邮箱格式
      this.checkPassword();
      this.checkEmail();

      // 如果密码或邮箱为空，说明验证没通过，不提交
      if (!this.registerForm.password || !this.registerForm.email) {
        this.loading = false;
        return;
      }

      this.loading = true;
      postRequest('/reg', this.registerForm).then(resp => {
        _this.loading = false;
        if (resp.status == 200) {
          var json = resp.data;
          if (json.status == 'success') {
            // 修改：注册成功提示
            _this.$message.success('注册完成，正在跳转登录页面');
            // 延迟跳转，让用户看到提示
            setTimeout(() => {
              _this.activeTab = 'login'; // 注册成功切换到登录页
            }, 1500);
          } else {
            _this.$alert(json.msg || '注册失败!', '失败!');
          }
        } else {
          _this.$alert('注册失败!', '失败!');
        }
      }, resp => {
        _this.loading = false;
        _this.$alert('找不到服务器⊙﹏⊙∥!', '失败!');
      });
    }
  }
};
</script>

<style scoped>
/* 保留美观的全局样式 */
.login-page {
  background: linear-gradient(135deg, #F0F7FF 0%, #E8F3FF 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 40px 20px 20px;
  position: relative;
  overflow: hidden;
}

/* 新增：登录提示样式 */
.login-tip {
  font-size: 12px;
  color: #6B7280;
  margin-top: 6px;
  padding-left: 4px;
}

.login-page::before, .login-page::after {
  content: "";
  position: absolute;
  border-radius: 50%;
  background: rgba(22, 93, 255, 0.05);
  z-index: 0;
}

.login-page::before {
  width: 500px;
  height: 500px;
  top: -250px;
  right: -250px;
}

.login-page::after {
  width: 400px;
  height: 400px;
  bottom: -200px;
  left: -200px;
}

.art-text {
  position: absolute;
  top: 30px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 36px;
  font-weight: 700;
  background: linear-gradient(90deg, #165DFF 0%, #4080FF 50%, #69B1FF 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  opacity: 0.18;
  z-index: 0;
  letter-spacing: 3px;
  pointer-events: none;
  text-align: center;
  width: 100%;
}

/* 保留美观的卡片容器样式 */
.login-container {
  display: flex;
  width: 850px;
  background-color: #FFFFFF;
  border: 1px solid rgba(22, 93, 255, 0.1);
  border-radius: 20px;
  box-shadow: 0 12px 32px rgba(22, 93, 255, 0.08), 0 2px 8px rgba(22, 93, 255, 0.04);
  overflow: hidden;
  position: relative;
  z-index: 1;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.login-container:hover {
  box-shadow: 0 16px 40px rgba(22, 93, 255, 0.12), 0 4px 12px rgba(22, 93, 255, 0.06);
}

/* 保留美观的左侧装饰区域样式 */
.login-left {
  flex: 1.1;
  background: linear-gradient(135deg, #165DFF 0%, #2962FF 100%);
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  position: relative;
  overflow: hidden;
}

.login-left::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url("data:image/svg+xml,%3Csvg width='400' height='400' viewBox='0 0 400 400' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath fill='%23FFFFFF' fill-opacity='0.08' d='M130.5 321.5c-15 0-27-12-27-27v-10c0-15 12-27 27-27s27 12 27 27v10c0 15-12 27-27 27zm-70-70c-15 0-27-12-27-27v-10c0-15 12-27 27-27s27 12 27 27v10c0 15-12 27-27 27zm210 70c-15 0-27-12-27-27v-10c0-15 12-27 27-27s27 12 27 27v10c0 15-12 27-27 27zm-70-70c-15 0-27-12-27-27v-10c0-15 12-27 27-27s27 12 27 27v10c0 15-12 27-27 27zm-70-70c-15 0-27-12-27-27v-10c0-15 12-27 27-27s27 12 27 27v10c0 15-12 27-27 27zm-70-70c-15 0-27-12-27-27v-10c0-15 12-27 27-27s27 12 27 27v10c0 15-12 27-27 27z'/%3E%3C/svg%3E");
  opacity: 0.25;
}

.login-left::after {
  content: "";
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0) 70%);
  transform: rotate(30deg);
}

.left-logo {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 24px;
  position: relative;
  z-index: 1;
  backdrop-filter: blur(10px);
}

.logo-text {
  font-size: 36px;
  font-weight: bold;
  color: white;
}

.left-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 16px;
  position: relative;
  z-index: 1;
  letter-spacing: 0.5px;
}

.left-desc {
  font-size: 16px;
  line-height: 1.7;
  text-align: center;
  margin-bottom: 40px;
  position: relative;
  z-index: 1;
  opacity: 0.9;
  max-width: 300px;
}

.left-features {
  list-style: none;
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 300px;
}

.left-features li {
  margin-bottom: 16px;
  display: flex;
  align-items: flex-start;
  font-size: 15px;
}

.left-features li::before {
  content: "✓";
  display: inline-flex;
  justify-content: center;
  align-items: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  text-align: center;
  line-height: 24px;
  margin-right: 12px;
  font-size: 14px;
  flex-shrink: 0;
  margin-top: 2px;
}

.left-copyright {
  position: absolute;
  bottom: 20px;
  font-size: 12px;
  opacity: 0.7;
  z-index: 1;
}

/* 保留美观的右侧表单区域样式 */
.login-right {
  flex: 1;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  position: relative;
}

.login-right::before {
  content: "";
  position: absolute;
  top: 0;
  right: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, #165DFF 0%, #4080FF 100%);
}

.decor-bg {
  position: absolute;
  top: -40px;
  right: -40px;
  width: 180px;
  height: 180px;
  background: radial-gradient(circle, rgba(22, 93, 255, 0.04) 0%, rgba(22, 93, 255, 0) 70%);
  border-radius: 50%;
  z-index: 0;
}

.form-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.title-area {
  text-align: center;
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(22, 93, 255, 0.08);
}

.main-title {
  font-size: 24px;
  font-weight: 600;
  background: linear-gradient(90deg, #165DFF 0%, #2962FF 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  margin-bottom: 8px;
  letter-spacing: 0.3px;
}

.sub-title {
  font-size: 15px;
  color: #6B7280;
  font-weight: 400;
}

/* 保留美观的标签切换样式 */
.tab-area {
  display: flex;
  margin-bottom: 24px;
  position: relative;
  background-color: #F9FAFB;
  border-radius: 12px;
  padding: 4px;
}

.tab-item {
  flex: 1;
  text-align: center;
  height: 40px;
  line-height: 40px;
  font-size: 15px;
  color: #6B7280;
  position: relative;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 8px;
  font-weight: 500;
}

.tab-item.active {
  color: #FFFFFF;
  background: linear-gradient(90deg, #165DFF 0%, #2962FF 100%);
  box-shadow: 0 2px 8px rgba(22, 93, 255, 0.2);
}

/* 表单区域样式：美观与功能兼顾 */
.el-form-container {
  flex: 1;
  border: none;
  box-shadow: none;
  padding: 0;
  margin: 0;
}

.form-area {
  margin-bottom: 24px;
}

.form-item {
  margin-bottom: 20px;
  position: relative;
}

.form-label {
  display: block;
  font-size: 14px;
  color: #374151;
  margin-bottom: 8px;
  padding-left: 4px;
  font-weight: 500;
}

/* 美观的自定义输入框样式 */
.custom-input {
  width: 100%;
}

.custom-input .el-input__wrapper {
  border-radius: 8px;
  box-shadow: none;
  border: 1px solid #eaeaea;
  height: 48px;
  padding: 0 16px;
}

.custom-input .el-input__wrapper:focus-within {
  border-color: #165DFF;
  box-shadow: 0 0 0 4px rgba(22, 93, 255, 0.1);
}

.custom-input .el-input__inner {
  font-size: 15px;
  line-height: 46px;
  height: 46px;
}

/* 保留源仓库记住密码样式，优化美观度 */
.login_remember {
  margin: 0px 0px 35px 0px;
  text-align: left;
  font-size: 14px;
}

.login_remember .el-checkbox__label {
  color: #4B5563;
}

.login_remember .el-checkbox__input.is-checked .el-checkbox__inner {
  background-color: #165DFF;
  border-color: #165DFF;
}

/* 美观的自定义按钮样式 */
.custom-btn {
  height: 52px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(90deg, #165DFF 0%, #2962FF 100%);
  box-shadow: 0 4px 12px rgba(22, 93, 255, 0.15);
  transition: all 0.3s ease;
}

.custom-btn:hover {
  background: linear-gradient(90deg, #0e42d2 0%, #165dff 100%);
  box-shadow: 0 6px 16px rgba(22, 93, 255, 0.2);
  transform: translateY(-2px);
}

.custom-btn:active {
  transform: translateY(0);
}

/* 保留美观的底部区域样式 */
.bottom-area {
  text-align: center;
  font-size: 13px;
  color: #6B7280;
  padding-top: 16px;
  border-top: 1px solid #F3F4F6;
  margin-top: auto;
}

.forget-link {
  color: #4080FF;
  text-decoration: none;
  margin-left: 6px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.forget-link:hover {
  color: #165DFF;
  text-decoration: underline;
}

/* 移动端适配：保留美观响应式布局 */
@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
    width: 90%;
    max-width: 400px;
  }

  .login-left {
    padding: 30px 20px;
    text-align: center;
  }

  .left-logo {
    width: 60px;
    height: 60px;
  }

  .left-title {
    font-size: 24px;
  }

  .login-right {
    padding: 30px 20px;
  }

  .main-title {
    font-size: 20px;
  }
}
</style>