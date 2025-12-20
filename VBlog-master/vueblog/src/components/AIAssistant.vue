<template>
  <div class="ai-assistant-container" :class="{ 'standalone': isStandalone }">
    <el-card class="chat-card">
      <div slot="header" class="card-header">
        <span><img src="../assets/logo2.png" alt="DeepSeek" class="deepseek-logo"> AI助手 - DeepSeek R1</span>
      </div>
      
      <div class="chat-messages" ref="messagesContainer">
        <div 
          v-for="(msg, index) in messages" 
          :key="index" 
          :class="['message-item', msg.role === 'user' ? 'user-message' : 'ai-message']"
        >
          <div class="message-avatar">
            <i :class="msg.role === 'user' ? 'fa fa-user' : 'fa fa-robot'"></i>
          </div>
          <div class="message-content">
            <div class="message-text" v-html="formatMessage(msg.content)"></div>
            <div class="message-time">{{ msg.time }}</div>
          </div>
        </div>
        <div v-if="loading" class="message-item ai-message">
          <div class="message-avatar">
            <i class="fa fa-robot"></i>
          </div>
          <div class="message-content">
            <div class="message-text">
              <i class="el-icon-loading"></i> AI正在思考中...
            </div>
          </div>
        </div>
      </div>
      
      <div class="chat-input-area">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="请输入您的问题..."
          @keydown.ctrl.enter="sendMessage"
          :disabled="loading"
        ></el-input>
        <div class="input-actions">
          <el-button 
            type="primary" 
            @click="sendMessage" 
            :loading="loading"
            :disabled="!inputMessage.trim()"
          >
            <i class="fa fa-paper-plane"></i> 发送 (Ctrl+Enter)
          </el-button>
          <el-button @click="clearMessages" :disabled="loading">
            <i class="fa fa-trash"></i> 清空对话
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { postRequest } from '@/utils/api'

export default {
  name: 'AIAssistant',
  data() {
    return {
      messages: [],
      inputMessage: '',
      loading: false,
      isStandalone: false
    }
  },
  mounted() {
    // 判断是否为独立版本
    this.isStandalone = this.$route.path === '/aiAssistantStandalone';
    // 添加欢迎消息
    this.addWelcomeMessage();
  },
  methods: {
    addWelcomeMessage() {
      const welcomeMsg = {
        role: 'ai',
        content: '您好！我是AI助手，基于DeepSeek R1模型。我可以帮助您解答问题、提供建议或进行对话。请随时向我提问！',
        time: this.getCurrentTime()
      };
      this.messages.push(welcomeMsg);
    },
    sendMessage() {
      if (!this.inputMessage.trim() || this.loading) {
        return;
      }
      
      const userMessage = {
        role: 'user',
        content: this.inputMessage.trim(),
        time: this.getCurrentTime()
      };
      
      this.messages.push(userMessage);
      this.inputMessage = '';
      this.loading = true;
      
      // 滚动到底部
      this.$nextTick(() => {
        this.scrollToBottom();
      });
      
      // 调用后端API
      postRequest('/ai/chat', { message: userMessage.content })
        .then(response => {
          this.loading = false;
          if (response.status === 200 && response.data.status === 'success') {
            const aiMessage = {
              role: 'ai',
              content: response.data.msg,
              time: this.getCurrentTime()
            };
            this.messages.push(aiMessage);
            this.$nextTick(() => {
              this.scrollToBottom();
            });
          } else {
            this.$message.error(response.data.msg || 'AI回复失败');
          }
        })
        .catch(error => {
          this.loading = false;
          console.error('AI请求失败:', error);
          this.$message.error('AI服务连接失败，请确保Ollama服务正在运行');
        });
    },
    clearMessages() {
      this.$confirm('确定要清空所有对话记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.messages = [];
        this.addWelcomeMessage();
      }).catch(() => {});
    },
    scrollToBottom() {
      const container = this.$refs.messagesContainer;
      if (container) {
        container.scrollTop = container.scrollHeight;
      }
    },
    formatMessage(content) {
      // 简单的消息格式化，将换行符转换为<br>
      return content.replace(/\n/g, '<br>');
    },
    getCurrentTime() {
      const now = new Date();
      const hours = String(now.getHours()).padStart(2, '0');
      const minutes = String(now.getMinutes()).padStart(2, '0');
      return `${hours}:${minutes}`;
    }
  }
}
</script>

<style scoped>
.ai-assistant-container {
  padding: 20px;
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
}

/* 独立版本的样式 */
.ai-assistant-container.standalone {
  height: 100vh;
  padding: 10px;
}

.ai-assistant-container.standalone .chat-messages {
  max-height: calc(100vh - 200px);
}

.chat-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
}

.card-header i, .card-header .deepseek-logo {
  margin-right: 8px;
}

.card-header .deepseek-logo {
  height: 24px;
  width: auto;
  vertical-align: middle;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 400px;
  max-height: calc(100vh - 300px);
}

.message-item {
  display: flex;
  margin-bottom: 20px;
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-message {
  flex-direction: row-reverse;
}

.user-message .message-content {
  background-color: #409EFF;
  color: white;
  margin-right: 10px;
}

.ai-message .message-content {
  background-color: white;
  color: #333;
  margin-left: 10px;
  border: 1px solid #e4e7ed;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.user-message .message-avatar {
  background-color: #409EFF;
  color: white;
}

.ai-message .message-avatar {
  background-color: #67C23A;
  color: white;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.message-text {
  word-wrap: break-word;
  line-height: 1.6;
}

.message-time {
  font-size: 12px;
  margin-top: 5px;
  opacity: 0.7;
}

.user-message .message-time {
  text-align: right;
}

.chat-input-area {
  padding: 20px;
  border-top: 1px solid #e4e7ed;
  background-color: white;
}

.input-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #555;
}
</style>

