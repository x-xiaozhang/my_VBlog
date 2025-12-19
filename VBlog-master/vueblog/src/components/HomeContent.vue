<template>
  <div class="home-content">
    <div class="welcome-section">
      <h2>欢迎来到V部落博客管理平台</h2>
      <p>这里是您的博客管理中心，您可以在这里发布文章、管理用户、查看数据统计等</p>
    </div>
    
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-info">
              <div class="stat-number">123</div>
              <div class="stat-label">总文章数</div>
            </div>
            <div class="stat-icon article-icon"></div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-info">
              <div class="stat-number">45</div>
              <div class="stat-label">总用户数</div>
            </div>
            <div class="stat-icon user-icon"></div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-info">
              <div class="stat-number">7890</div>
              <div class="stat-label">总访问量</div>
            </div>
            <div class="stat-icon view-icon"></div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-info">
              <div class="stat-number">12</div>
              <div class="stat-label">栏目数量</div>
            </div>
            <div class="stat-icon cate-icon"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <div class="quick-actions">
      <h3>快捷操作</h3>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-button type="primary" icon="el-icon-edit" size="large" block @click="$router.push('/postArticle')">
            发表文章
          </el-button>
        </el-col>
        <el-col :span="8">
          <el-button type="success" icon="el-icon-document" size="large" block @click="$router.push('/articleList')">
            查看文章
          </el-button>
        </el-col>
        <el-col :span="8">
          <el-button type="warning" icon="el-icon-user" size="large" block @click="$router.push('/user')">
            用户管理
          </el-button>
        </el-col>
      </el-row>
    </div>
    
    <div class="recent-articles">
      <h3>最新文章</h3>
      <el-table :data="recentArticles" stripe style="width: 100%">
        <el-table-column prop="title" label="文章标题" show-overflow-tooltip></el-table-column>
        <el-table-column prop="cateName" label="栏目"></el-table-column>
        <el-table-column prop="nickname" label="作者"></el-table-column>
        <el-table-column prop="publishDate" label="发布时间" width="150"></el-table-column>
        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button type="primary" size="small" @click="viewArticle(scope.row.id)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        recentArticles: [
          {
            id: 1,
            title: 'Vue.js入门教程',
            cateName: '技术分享',
            nickname: '管理员',
            publishDate: '2025-12-18'
          },
          {
            id: 2,
            title: 'Spring Boot实战',
            cateName: '后端开发',
            nickname: '技术专家',
            publishDate: '2025-12-17'
          },
          {
            id: 3,
            title: 'Element UI使用指南',
            cateName: '前端开发',
            nickname: '前端工程师',
            publishDate: '2025-12-16'
          }
        ]
      }
    },
    methods: {
      viewArticle(id) {
        this.$router.push({ path: '/blogDetail', query: { aid: id } })
      }
    }
  }
</script>

<style scoped>
  .home-content {
    padding: 20px;
  }
  
  .welcome-section {
    margin-bottom: 30px;
    text-align: center;
  }
  
  .welcome-section h2 {
    color: #333;
    margin-bottom: 10px;
  }
  
  .welcome-section p {
    color: #666;
    font-size: 16px;
  }
  
  .stats-section {
    margin-bottom: 30px;
  }
  
  .stat-card {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20px;
  }
  
  .stat-info {
    flex: 1;
  }
  
  .stat-number {
    font-size: 32px;
    font-weight: bold;
    color: #333;
  }
  
  .stat-label {
    font-size: 14px;
    color: #666;
    margin-top: 5px;
  }
  
  .stat-icon {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    background-size: 60%;
    background-position: center;
    background-repeat: no-repeat;
  }
  
  .article-icon {
    background-color: #e6f7ff;
    background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#1890ff"><path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V5h14v14zM12 10H9v2h3v3h2v-3h3v-2h-3V7h-2v3z"/></svg>');
  }
  
  .user-icon {
    background-color: #f6ffed;
    background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#52c41a"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>');
  }
  
  .view-icon {
    background-color: #fff7e6;
    background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#fa8c16"><path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/></svg>');
  }
  
  .cate-icon {
    background-color: #fff0f6;
    background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#eb2f96"><path d="M11 18h2v-2h-2v2zm1-16C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm0-14c-2.21 0-4 1.79-4 4h2c0-1.1.9-2 2-2s2 .9 2 2c0 2-3 1.75-3 5h2c0-2.25 3-2.5 3-5 0-2.21-1.79-4-4-4z"/></svg>');
  }
  
  .quick-actions {
    margin-bottom: 30px;
  }
  
  .quick-actions h3 {
    color: #333;
    margin-bottom: 15px;
  }
  
  .recent-articles {
    margin-bottom: 30px;
  }
  
  .recent-articles h3 {
    color: #333;
    margin-bottom: 15px;
  }
</style>