<template>
  <el-form :rules="currentRules" class="login-container" label-position="left"
           label-width="0px" v-loading="loading">
    <!-- 修改为图片路径 -->
    <div class="login_logo">
      <img src="../assets/logo.jpg" alt="VBlog系统logo" class="logo-img">
    </div>
    <el-tabs v-model="activeTab" class="login-tabs">
      <el-tab-pane label="登录" name="login">
        <h3 class="login_title">系统登录</h3>
        <el-form-item prop="account">
          <el-input type="text" v-model="loginForm.username" auto-complete="off" placeholder="账号"></el-input>
        </el-form-item>
        <el-form-item prop="checkPass">
          <el-input type="password" v-model="loginForm.password" auto-complete="off" placeholder="密码"></el-input>
        </el-form-item>
        <el-checkbox class="login_remember" v-model="checked" label-position="left">记住密码</el-checkbox>
        <el-form-item style="width: 100%">
          <el-button type="primary" @click.native.prevent="login" style="width: 100%">登录</el-button>
        </el-form-item>
      </el-tab-pane>
      <el-tab-pane label="注册" name="register">
        <h3 class="login_title">系统注册</h3>
        <el-form-item prop="regUsername">
          <el-input type="text" v-model="registerForm.username" auto-complete="off" placeholder="账号"></el-input>
        </el-form-item>
        <el-form-item prop="regPassword">
          <el-input type="password" v-model="registerForm.password" auto-complete="off" placeholder="密码"></el-input>
        </el-form-item>
        <el-form-item prop="regNickname">
          <el-input type="text" v-model="registerForm.nickname" auto-complete="off" placeholder="昵称"></el-input>
        </el-form-item>
        <el-form-item prop="regEmail">
          <el-input type="email" v-model="registerForm.email" auto-complete="off" placeholder="邮箱"></el-input>
        </el-form-item>
        <el-form-item style="width: 100%">
          <el-button type="primary" @click.native.prevent="register" style="width: 100%">注册</el-button>
        </el-form-item>
      </el-tab-pane>
    </el-tabs>
  </el-form>
</template>
<script>
import {postRequest} from '../utils/api'
import {putRequest} from '../utils/api'
export default{
  data(){
    return {
      activeTab: 'login',
      loginRules: {
        account: [{required: true, message: '请输入用户名', trigger: 'blur'}],
        checkPass: [{required: true, message: '请输入密码', trigger: 'blur'}]
      },
      registerRules: {
        regUsername: [{required: true, message: '请输入用户名', trigger: 'blur'}],
        regPassword: [{required: true, message: '请输入密码', trigger: 'blur'}],
        regNickname: [{required: true, message: '请输入昵称', trigger: 'blur'}],
        regEmail: [{required: true, type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur'}]
      },
      checked: true,
      loginForm: {
        username: 'sang',
        password: '123'
      },
      registerForm: {
        username: '',
        password: '',
        nickname: '',
        email: ''
      },
      loading: false
    }
  },
  computed: {
    currentRules() {
      return this.activeTab === 'login' ? this.loginRules : this.registerRules;
    }
  },
  methods: {
    login: function () {
      var _this = this;
      this.loading = true;
      postRequest('/login', {
        username: this.loginForm.username,
        password: this.loginForm.password
      }).then(resp=> {
        _this.loading = false;
        if (resp.status == 200) {
          //成功
          var json = resp.data;
          if (json.status == 'success') {
            _this.$router.replace({path: '/home'});
          } else {
            _this.$alert('登录失败!', '失败!');
          }
        } else {
          //失败
          _this.$alert('登录失败!', '失败!');
        }
      }, resp=> {
        _this.loading = false;
        _this.$alert('找不到服务器⊙﹏⊙∥!', '失败!');
      });
    },
    register: function () {
      var _this = this;
      this.loading = true;
      postRequest('/reg', this.registerForm).then(resp=> {
        _this.loading = false;
        if (resp.status == 200) {
          var json = resp.data;
          if (json.status == 'success') {
            _this.$message.success('注册成功!');
            _this.activeTab = 'login'; // 注册成功后切换到登录
          } else {
            _this.$alert(json.msg || '注册失败!', '失败!');
          }
        } else {
          _this.$alert('注册失败!', '失败!');
        }
      }, resp=> {
        _this.loading = false;
        _this.$alert('找不到服务器⊙﹏⊙∥!', '失败!');
      });
    }
  }
}
</script>
<style>
.login-container {
  border-radius: 15px;
  background-clip: padding-box;
  margin: 150px auto;
  width: 350px;
  padding: 35px 35px 15px 35px;
  background: #fff;
  border: 1px solid #eaeaea;
  box-shadow: 0 0 25px #cac6c6;
  text-align: center;
}

.login_logo {
  margin: 0 auto 20px;
  width: 80px;
  height: 80px;
}

/* 控制图片显示样式 */
.logo-img {
  width: 100%;
  height: 100%;
  object-fit: contain; /* 保持图片比例，完整显示在容器内 */
}

.login_title {
  margin: 0px auto 40px auto;
  text-align: center;
  color: #505458;
}

.login_remember {
  margin: 0px 0px 35px 0px;
  text-align: left;
}
</style>
