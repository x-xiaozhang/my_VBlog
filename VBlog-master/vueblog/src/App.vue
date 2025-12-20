<template>
  <div id="app" :class="{ 'standalone-app': isStandalone }" style="background-color: rgba(235, 235, 235, 0.08)">
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'app',
  computed: {
    isStandalone() {
      // 判断是否为独立的AI助手页面
      return this.$route.path === '/aiAssistantStandalone';
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  overflow: hidden;
}

#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
  min-height: 100vh;
}

/* 独立AI助手页面的特殊样式 */
#app.standalone-app {
  margin-top: 0;
  height: 100vh;
  overflow: hidden;
}
</style>
