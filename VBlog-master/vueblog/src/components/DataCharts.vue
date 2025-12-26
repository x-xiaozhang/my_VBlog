<template>
  <div class="analytics-dashboard" :class="theme">
    <!-- 顶部导航和统计摘要 -->
    <div class="dashboard-header">
      <div class="header-left">
        <h1 class="dashboard-title">📈 数据洞察中心</h1>
        <p class="dashboard-subtitle">实时监控网站性能与用户行为</p>
      </div>
      <div class="header-right">
        <div class="date-range">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            size="small"
            :picker-options="pickerOptions"
          >
          </el-date-picker>
        </div>
        <el-switch
          v-model="realTimeMode"
          active-text="实时"
          inactive-text="历史"
          active-color="#7c3aed"
          @change="toggleRealTime"
        ></el-switch>
      </div>
    </div>

    <!-- 主要数据卡片网格 - 优化后的响应式布局 -->
    <div class="data-grid">
      <!-- 关键指标卡片 -->
      <div class="metric-card primary">
        <div class="card-header">
          <div class="card-title">
            <i class="el-icon-data-line"></i>
            <h3>关键指标概览</h3>
          </div>
          <div class="card-actions">
            <el-tooltip content="刷新数据" placement="top">
              <i class="el-icon-refresh" @click="refreshData"></i>
            </el-tooltip>
          </div>
        </div>

        <div class="metrics-grid">
          <div class="metric-item" v-for="metric in keyMetrics" :key="metric.id">
            <div class="metric-icon" :style="{ background: metric.gradient }">
              <i :class="metric.icon"></i>
            </div>
            <div class="metric-content">
              <div class="metric-label">{{ metric.label }}</div>
              <div class="metric-value">{{ metric.value }}</div>
              <div class="metric-trend" :class="metric.trend.type">
                <i :class="metric.trend.icon"></i>
                {{ metric.trend.value }}
                <span class="metric-period">vs 昨天</span>
              </div>
            </div>
            <div class="metric-chart">
              <svg width="80" height="30">
                <path :d="metric.sparkline" stroke="currentColor" fill="none" stroke-width="1.5" />
              </svg>
            </div>
          </div>
        </div>
      </div>

      <!-- 访问趋势图表 -->
      <div class="chart-card">
        <div class="card-header">
          <div class="card-title">
            <i class="el-icon-trend-charts"></i>
            <h3>访问趋势分析</h3>
          </div>
          <div class="chart-tabs">
            <el-radio-group v-model="trendType" size="mini">
              <el-radio-button label="pv">浏览量</el-radio-button>
              <el-radio-button label="uv">访客数</el-radio-button>
              <el-radio-button label="duration">停留时长</el-radio-button>
            </el-radio-group>
          </div>
        </div>
        <div class="chart-container">
          <chart ref="trendChart" :options="trendChartOptions" style="width: 100%; height: 240px" />
        </div>
        <div class="chart-footer">
          <div class="trend-summary">
            <div class="summary-item" v-for="item in trendSummary" :key="item.label">
              <div class="summary-icon" :style="{ background: item.color }">
                <i :class="item.icon"></i>
              </div>
              <div class="summary-info">
                <span class="summary-label">{{ item.label }}</span>
                <span class="summary-value" :style="{ color: item.color }">{{ item.value }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 实时数据流 -->
      <div class="realtime-card">
        <div class="card-header">
          <div class="card-title">
            <i class="el-icon-monitor"></i>
            <h3>实时动态</h3>
            <el-tag v-if="realTimeMode" type="danger" size="mini" effect="dark">LIVE</el-tag>
          </div>
          <div class="realtime-count">
            <span class="count-label">当前在线：</span>
            <span class="count-value">{{ onlineUsers }}</span>
            <span class="count-change" v-if="onlineChange !== 0" :class="onlineChange > 0 ? 'positive' : 'negative'">
              <i :class="onlineChange > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
              {{ Math.abs(onlineChange) }}
            </span>
          </div>
        </div>

        <div class="realtime-feed">
          <div class="feed-item" v-for="event in realtimeEvents" :key="event.id">
            <div class="feed-time">{{ event.time }}</div>
            <div class="feed-content">
              <span class="feed-icon" :style="{ background: event.color }">
                <i :class="event.icon"></i>
              </span>
              <span class="feed-text">{{ event.text }}</span>
            </div>
            <div class="feed-meta">
              <el-tag size="mini" :type="event.tag.type">{{ event.tag.text }}</el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 内容表现排行 -->
      <div class="ranking-card">
        <div class="card-header">
          <div class="card-title">
            <i class="el-icon-trophy"></i>
            <h3>热门内容排行</h3>
          </div>
          <div class="ranking-tabs">
            <el-tabs v-model="rankingTab" class="compact-tabs">
              <el-tab-pane label="浏览量" name="views"></el-tab-pane>
              <el-tab-pane label="点赞数" name="likes"></el-tab-pane>
              <el-tab-pane label="评论数" name="comments"></el-tab-pane>
            </el-tabs>
          </div>
        </div>

        <div class="ranking-list">
          <div class="ranking-item" v-for="(item, index) in rankingData" :key="item.id">
            <div class="rank-number" :class="`rank-${index + 1}`">
              {{ index + 1 }}
            </div>
            <div class="article-info">
              <div class="article-title" @click="viewArticle(item)">
                {{ item.title }}
              </div>
              <div class="article-stats">
                <div class="stat-badge views">
                  <i class="el-icon-view"></i>
                  {{ formatNumber(item.views) }}
                </div>
                <div class="stat-badge likes">
                  <i class="el-icon-star-on"></i>
                  {{ item.likes }}
                </div>
                <div class="stat-badge comments">
                  <i class="el-icon-chat-dot-round"></i>
                  {{ item.comments }}
                </div>
              </div>
            </div>
            <div class="performance-gauge">
              <div class="gauge-bar">
                <div class="gauge-fill" :style="{
                  width: `${item.performance}%`,
                  background: `linear-gradient(90deg, ${getRankColor(index + 1)} 0%, ${getRankColor(index + 1)}80 100%)`
                }"></div>
              </div>
              <span class="performance-value">{{ item.performance }}%</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 用户画像分析 -->
      <div class="user-card">
        <div class="card-header">
          <div class="card-title">
            <i class="el-icon-user"></i>
            <h3>用户画像</h3>
          </div>
        </div>

        <div class="user-profile">
          <div class="profile-chart">
            <chart :options="userProfileChart" style="width: 100%; height: 200px" />
          </div>

          <div class="profile-stats">
            <div class="profile-stat" v-for="stat in userStats" :key="stat.id">
              <div class="stat-icon" :style="{ background: stat.color }">
                <i :class="stat.icon"></i>
              </div>
              <div class="stat-content">
                <div class="stat-label">{{ stat.label }}</div>
                <div class="stat-value">{{ stat.value }}</div>
              </div>
              <div class="stat-tag" :style="{ background: stat.tagColor }">{{ stat.tag }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 设备与来源分析 -->
      <div class="source-card">
        <div class="card-header">
          <div class="card-title">
            <i class="el-icon-connection"></i>
            <h3>来源与设备</h3>
          </div>
        </div>

        <div class="source-analysis">
          <div class="source-chart">
            <chart :options="sourceChart" style="width: 100%; height: 180px" />
          </div>

          <div class="device-breakdown">
            <div class="device-item" v-for="device in deviceStats" :key="device.type">
              <div class="device-icon" :style="{ background: device.color }">
                <i :class="device.icon"></i>
              </div>
              <div class="device-info">
                <div class="device-type">{{ device.type }}</div>
                <div class="device-percent">{{ device.percent }}%</div>
              </div>
              <div class="device-bar">
                <div class="bar-fill" :style="{
                  width: `${device.percent}%`,
                  background: device.color
                }"></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 性能指标卡片 -->
      <div class="performance-card">
        <div class="card-header">
          <div class="card-title">
            <i class="el-icon-odometer"></i>
            <h3>性能指标</h3>
          </div>
        </div>

        <div class="performance-metrics">
          <div class="performance-item" v-for="metric in performanceMetrics" :key="metric.id">
            <div class="performance-info">
              <div class="performance-label">{{ metric.label }}</div>
              <div class="performance-value">{{ metric.value }}</div>
              <div class="performance-target">
                <span>目标：{{ metric.target }}</span>
                <span class="performance-status" :class="metric.status">{{ metric.statusText }}</span>
              </div>
            </div>
            <div class="performance-progress">
              <el-progress
                :percentage="metric.percentage"
                :color="metric.color"
                :stroke-width="8"
                :show-text="false"
              ></el-progress>
              <div class="progress-labels">
                <span>0</span>
                <span>{{ metric.target }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部快速洞察 -->
    <div class="insights-footer">
      <div class="insight-highlight" v-for="insight in quickInsights" :key="insight.id">
        <div class="highlight-icon" :style="{ background: insight.color }">
          <i :class="insight.icon"></i>
        </div>
        <div class="highlight-content">
          <div class="highlight-title">{{ insight.title }}</div>
          <div class="highlight-desc">{{ insight.description }}</div>
        </div>
        <div class="highlight-action">
          <el-button type="text" size="mini">查看详情 →</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ECharts from 'vue-echarts/components/ECharts.vue'
import 'echarts/lib/chart/line'
import 'echarts/lib/chart/bar'
import 'echarts/lib/chart/pie'
import 'echarts/lib/chart/radar'
import 'echarts/lib/component/tooltip'
import 'echarts/lib/component/legend'
import 'echarts/lib/component/title'
import 'echarts/lib/component/grid'

export default {
  name: 'AnalyticsDashboard',
  components: {
    'chart': ECharts
  },
  data() {
    return {
      theme: 'dark',
      dateRange: [new Date(Date.now() - 30 * 24 * 60 * 60 * 1000), new Date()],
      realTimeMode: false,
      trendType: 'pv',
      rankingTab: 'views',
      onlineUsers: 42,
      onlineChange: 0,
      prevOnlineUsers: 42,

      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            picker.$emit('pick', [start, end])
          }
        }]
      },

      keyMetrics: [
        {
          id: 1,
          icon: 'el-icon-view',
          label: '总浏览量',
          value: '12.8K',
          trend: { type: 'up', icon: 'el-icon-top', value: '+12.4%' },
          gradient: 'linear-gradient(135deg, #7c3aed 0%, #a78bfa 100%)',
          sparkline: 'M0,15 Q10,5 20,20 Q30,10 40,18 Q50,8 60,22 Q70,12 80,15'
        },
        {
          id: 2,
          icon: 'el-icon-user',
          label: '独立访客',
          value: '2.8K',
          trend: { type: 'up', icon: 'el-icon-top', value: '+8.2%' },
          gradient: 'linear-gradient(135deg, #10b981 0%, #34d399 100%)',
          sparkline: 'M0,18 Q10,12 20,22 Q30,14 40,20 Q50,10 60,16 Q70,8 80,14'
        },
        {
          id: 3,
          icon: 'el-icon-time',
          label: '平均时长',
          value: '4:32',
          trend: { type: 'up', icon: 'el-icon-top', value: '+0:18' },
          gradient: 'linear-gradient(135deg, #f59e0b 0%, #fbbf24 100%)',
          sparkline: 'M0,10 Q10,18 20,12 Q30,20 40,14 Q50,22 60,16 Q70,24 80,18'
        },
        {
          id: 4,
          icon: 'el-icon-refresh',
          label: '跳出率',
          value: '32.6%',
          trend: { type: 'down', icon: 'el-icon-bottom', value: '-2.1%' },
          gradient: 'linear-gradient(135deg, #ef4444 0%, #f87171 100%)',
          sparkline: 'M0,22 Q10,15 20,24 Q30,18 40,20 Q50,14 60,18 Q70,12 80,16'
        }
      ],

      trendSummary: [
        {
          label: '今日峰值',
          value: '1,425',
          color: '#7c3aed',
          icon: 'el-icon-top'
        },
        {
          label: '平均时长',
          value: '4分32秒',
          color: '#10b981',
          icon: 'el-icon-time'
        },
        {
          label: '最高时段',
          value: '14:30',
          color: '#f59e0b',
          icon: 'el-icon-sunrise'
        }
      ],

      realtimeEvents: [
        {
          id: 1,
          time: '14:30',
          icon: 'el-icon-user',
          text: '用户 "开发工程师" 访问了文章 "Vue3实战指南"',
          color: '#7c3aed',
          tag: { type: 'success', text: '新访客' }
        },
        {
          id: 2,
          time: '14:28',
          icon: 'el-icon-star-on',
          text: '文章 "SpringBoot微服务" 获得一个新的点赞',
          color: '#f59e0b',
          tag: { type: 'warning', text: '互动' }
        },
        {
          id: 3,
          time: '14:25',
          icon: 'el-icon-chat-dot-round',
          text: '用户 "前端爱好者" 在文章下发表了评论',
          color: '#10b981',
          tag: { type: 'info', text: '评论' }
        },
        {
          id: 4,
          time: '14:22',
          icon: 'el-icon-share',
          text: '文章被分享到技术社区 Reddit',
          color: '#3b82f6',
          tag: { type: 'primary', text: '分享' }
        }
      ],

      rankingData: [
        {
          id: 1,
          title: 'Vue 3 Composition API 深度解析',
          views: 12456,
          likes: 342,
          comments: 89,
          performance: 98
        },
        {
          id: 2,
          title: '微服务架构设计模式实战',
          views: 9876,
          likes: 267,
          comments: 73,
          performance: 87
        },
        {
          id: 3,
          title: 'TypeScript 高级类型完全指南',
          views: 8567,
          likes: 221,
          comments: 58,
          performance: 79
        },
        {
          id: 4,
          title: 'React Hooks 最佳实践总结',
          views: 7423,
          likes: 198,
          comments: 45,
          performance: 72
        },
        {
          id: 5,
          title: 'Docker 容器化部署全攻略',
          views: 6895,
          likes: 176,
          comments: 38,
          performance: 68
        }
      ],

      userStats: [
        {
          id: 1,
          label: '新用户占比',
          value: '42%',
          tag: '高活跃',
          tagColor: '#10b981',
          color: '#10b981',
          icon: 'el-icon-user-plus'
        },
        {
          id: 2,
          label: '回访率',
          value: '68%',
          tag: '优秀',
          tagColor: '#7c3aed',
          color: '#7c3aed',
          icon: 'el-icon-refresh-right'
        },
        {
          id: 3,
          label: '沉默用户',
          value: '15%',
          tag: '良好',
          tagColor: '#f59e0b',
          color: '#f59e0b',
          icon: 'el-icon-mute'
        }
      ],

      deviceStats: [
        { type: '桌面端', percent: 32, icon: 'el-icon-monitor', color: '#7c3aed' },
        { type: '移动端', percent: 62, icon: 'el-icon-mobile-phone', color: '#10b981' },
        { type: '平板', percent: 6, icon: 'el-icon-tablet', color: '#f59e0b' }
      ],

      performanceMetrics: [
        {
          id: 1,
          label: '页面加载速度',
          value: '1.2s',
          target: '1.5s',
          percentage: 80,
          color: '#10b981',
          status: 'good',
          statusText: '优秀'
        },
        {
          id: 2,
          label: '首次内容渲染',
          value: '0.8s',
          target: '1.0s',
          percentage: 85,
          color: '#7c3aed',
          status: 'good',
          statusText: '优秀'
        },
        {
          id: 3,
          label: '交互响应时间',
          value: '110ms',
          target: '100ms',
          percentage: 90,
          color: '#f59e0b',
          status: 'good',
          statusText: '良好'
        },
        {
          id: 4,
          label: '错误率',
          value: '0.08%',
          target: '0.1%',
          percentage: 92,
          color: '#ef4444',
          status: 'good',
          statusText: '优秀'
        }
      ],

      quickInsights: [
        {
          id: 1,
          icon: 'el-icon-sunrise',
          title: '访问高峰预测',
          description: '今日下午2-4点预计访问量增长40%',
          color: '#7c3aed'
        },
        {
          id: 2,
          icon: 'el-icon-magic-stick',
          title: '内容优化建议',
          description: '增加TypeScript相关内容可提升20%留存',
          color: '#10b981'
        },
        {
          id: 3,
          icon: 'el-icon-connection',
          title: '流量来源分析',
          description: '技术社区引流效果最佳，建议加强合作',
          color: '#f59e0b'
        }
      ]
    }
  },
  computed: {
    trendChartOptions() {
      const hours = Array.from({length: 24}, (_, i) => `${i}:00`)
      const data = Array.from({length: 24}, (_, i) => {
        const base = i < 8 ? 200 : i < 12 ? 600 : i < 18 ? 1200 : 800
        const random = 0.9 + Math.random() * 0.2
        return Math.round(base * random)
      })

      return {
        color: ['#7c3aed'],
        backgroundColor: 'transparent',
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          top: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: hours,
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: {
            color: '#94a3b8',
            fontSize: 11,
            interval: 2
          },
          splitLine: { show: false }
        },
        yAxis: {
          type: 'value',
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: { color: '#94a3b8', fontSize: 11 },
          splitLine: {
            lineStyle: {
              color: '#334155',
              type: 'dashed'
            }
          }
        },
        series: [{
          type: 'line',
          smooth: true,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: {
            width: 3,
            shadowBlur: 12,
            shadowColor: 'rgba(124, 58, 237, 0.3)'
          },
          itemStyle: {
            color: '#7c3aed',
            borderColor: '#fff',
            borderWidth: 1
          },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(124, 58, 237, 0.2)' },
                { offset: 1, color: 'rgba(124, 58, 237, 0.02)' }
              ]
            }
          },
          data: data
        }],
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(15, 23, 42, 0.95)',
          borderColor: '#334155',
          textStyle: { color: '#e2e8f0', fontSize: 12 },
          formatter: (params) => {
            const time = params[0].axisValue
            const value = params[0].value
            return `<div style="font-weight: 600; margin-bottom: 4px;">${time}</div>
                    <div style="display: flex; align-items: center;">
                      <span style="display: inline-block; width: 8px; height: 8px; border-radius: 50%; background: #7c3aed; margin-right: 6px;"></span>
                      <span>浏览量：</span>
                      <span style="font-weight: 600; color: #7c3aed;">${value.toLocaleString()}</span>
                    </div>`
          }
        }
      }
    },

    userProfileChart() {
      return {
        radar: {
          indicator: [
            { name: '技术深度', max: 100 },
            { name: '内容广度', max: 100 },
            { name: '互动活跃', max: 100 },
            { name: '分享意愿', max: 100 },
            { name: '回访频率', max: 100 }
          ],
          shape: 'circle',
          axisLine: { lineStyle: { color: '#334155' } },
          splitLine: { lineStyle: { color: '#334155' } },
          axisName: { color: '#94a3b8', fontSize: 11 }
        },
        series: [{
          type: 'radar',
          itemStyle: { color: '#7c3aed' },
          lineStyle: { width: 2 },
          areaStyle: { color: 'rgba(124, 58, 237, 0.1)' },
          data: [{ value: [85, 78, 92, 65, 88] }]
        }]
      }
    },

    sourceChart() {
      return {
        color: ['#7c3aed', '#10b981', '#f59e0b', '#3b82f6', '#ef4444'],
        series: [{
          type: 'pie',
          radius: ['60%', '80%'],
          center: ['50%', '50%'],
          avoidLabelOverlap: false,
          label: {
            show: true,
            position: 'outside',
            formatter: '{b}\n{d}%',
            color: '#94a3b8',
            fontSize: 11
          },
          emphasis: {
            scale: false,
            label: { show: true, fontWeight: 'bold' }
          },
          data: [
            { value: 38, name: '搜索引擎' },
            { value: 25, name: '直接访问' },
            { value: 18, name: '社交媒体' },
            { value: 12, name: '外链引用' },
            { value: 7, name: '邮件推广' }
          ]
        }],
        tooltip: {
          trigger: 'item',
          backgroundColor: 'rgba(15, 23, 42, 0.95)',
          borderColor: '#334155',
          textStyle: { color: '#e2e8f0' }
        }
      }
    }
  },
  methods: {
    refreshData() {
      this.$message.success('数据已刷新')
      // 这里可以添加数据刷新逻辑
    },

    toggleRealTime() {
      if (this.realTimeMode) {
        this.startRealTimeUpdates()
      } else {
        this.stopRealTimeUpdates()
      }
    },

    startRealTimeUpdates() {
      // 模拟实时数据更新
      this.realTimeInterval = setInterval(() => {
        this.prevOnlineUsers = this.onlineUsers
        this.onlineUsers = Math.floor(30 + Math.random() * 30)
        this.onlineChange = this.onlineUsers - this.prevOnlineUsers

        // 添加新的实时事件
        const events = [
          '访问了文章',
          '点赞了内容',
          '发表了评论',
          '分享了文章',
          '收藏了教程'
        ]
        const users = ['开发者', '设计师', '学生', '工程师', '研究员']
        const articles = ['Vue3实战', 'React Hooks', 'TypeScript指南', 'Docker部署', '微服务架构']

        const newEvent = {
          id: Date.now(),
          time: new Date().toTimeString().slice(0, 5),
          icon: 'el-icon-user',
          text: `用户 "${users[Math.floor(Math.random() * users.length)]}" ${events[Math.floor(Math.random() * events.length)]} "${articles[Math.floor(Math.random() * articles.length)]}"`,
          color: '#7c3aed',
          tag: { type: 'success', text: '新动态' }
        }

        this.realtimeEvents.unshift(newEvent)
        if (this.realtimeEvents.length > 5) {
          this.realtimeEvents.pop()
        }
      }, 5000)
    },

    stopRealTimeUpdates() {
      if (this.realTimeInterval) {
        clearInterval(this.realTimeInterval)
      }
    },

    viewArticle(article) {
      this.$message({
        message: `查看文章：${article.title}`,
        type: 'info',
        duration: 2000
      })
    },

    formatNumber(num) {
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + '万'
      }
      return num.toLocaleString()
    },

    getRankColor(rank) {
      const colors = ['#ff6b6b', '#ffa726', '#4ecdc4', '#45b7d1', '#96a6a6']
      return colors[rank - 1] || '#96a6a6'
    }
  },
  beforeDestroy() {
    this.stopRealTimeUpdates()
  }
}
</script>

<style scoped>
.analytics-dashboard {
  min-height: 100vh;
  padding: 20px 20px 80px 20px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  color: #374151;
  overflow-x: auto;
  overflow-y: auto;
  max-height: 100vh;
}

.light-theme {
  background: linear-gradient(135deg, #ffffff 0%, #f9fafb 100%);
  color: #374151;
}

/* 头部样式 */
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e5e7eb;
}

.header-left .dashboard-title {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #7c3aed 0%, #a78bfa 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.dashboard-subtitle {
  margin: 4px 0 0;
  color: #6b7280;
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 数据网格布局 - 优化后的响应式布局 */
.data-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

@media (min-width: 1200px) {
  .data-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .metric-card {
    grid-column: span 3;
  }

  .chart-card, .realtime-card {
    grid-column: span 2;
  }

  .ranking-card {
    grid-column: span 3;
  }

  .user-card, .source-card, .performance-card {
    grid-column: span 1;
  }
}

/* 通用卡片样式 */
.metric-card, .chart-card, .realtime-card, .ranking-card, .user-card, .source-card, .performance-card {
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e5e7eb;
  transition: all 0.3s ease;
  overflow: hidden;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
}

.metric-card:hover, .chart-card:hover, .realtime-card:hover,
.ranking-card:hover, .user-card:hover, .source-card:hover, .performance-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  border-color: #d1d5db;
}

.metric-card.primary {
  border-left: 4px solid #7c3aed;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  margin-bottom: 16px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-title i {
  font-size: 20px;
  color: #7c3aed;
}

.card-title h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

/* 关键指标网格 */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  padding: 0 20px 20px;
}

.metric-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #f9fafb;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  transition: all 0.3s ease;
}

.metric-item:hover {
  background: #f3f4f6;
  border-color: #7c3aed;
  transform: translateY(-2px);
}

.metric-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 20px;
  color: white;
  flex-shrink: 0;
}

.metric-content {
  flex: 1;
}

.metric-label {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 4px;
}

.metric-value {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 4px;
  font-family: 'Inter', sans-serif;
  color: #111827;
}

.metric-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 500;
}

.metric-trend.up {
  color: #10b981;
}

.metric-trend.down {
  color: #ef4444;
}

.metric-period {
  color: #9ca3af;
  margin-left: 4px;
}

.metric-chart {
  flex-shrink: 0;
  color: #9ca3af;
  opacity: 0.8;
}

/* 趋势图表 */
.chart-container {
  padding: 0 20px;
}

.chart-footer {
  padding: 16px 20px;
  border-top: 1px solid #e5e7eb;
}

.trend-summary {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.summary-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  flex-shrink: 0;
}

.summary-info {
  display: flex;
  flex-direction: column;
}

.summary-label {
  font-size: 12px;
  color: #6b7280;
}

.summary-value {
  font-size: 18px;
  font-weight: 600;
  margin-top: 2px;
}

/* 实时数据流 */
.realtime-card {
  padding-bottom: 20px;
}

.realtime-count {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.count-value {
  font-weight: 600;
  color: #7c3aed;
}

.count-change {
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
  font-weight: 500;
}

.count-change.positive {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.count-change.negative {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.realtime-feed {
  padding: 0 20px;
}

.feed-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f3f4f6;
}

.feed-item:last-child {
  border-bottom: none;
}

.feed-time {
  width: 50px;
  font-size: 12px;
  color: #6b7280;
  flex-shrink: 0;
}

.feed-content {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
}

.feed-icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: white;
  flex-shrink: 0;
}

.feed-text {
  font-size: 13px;
  line-height: 1.4;
  color: #374151;
}

/* 排名卡片 */
.ranking-list {
  padding: 0 20px;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f3f4f6;
  transition: all 0.3s ease;
}

.ranking-item:hover {
  background: #f9fafb;
  padding-left: 12px;
  padding-right: 12px;
  border-radius: 8px;
  margin: 0 -12px;
}

.rank-number {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
  color: white;
  margin-right: 16px;
  flex-shrink: 0;
}

.rank-1 { background: linear-gradient(135deg, #ff6b6b 0%, #ff8e8e 100%); }
.rank-2 { background: linear-gradient(135deg, #ffa726 0%, #ffcc80 100%); }
.rank-3 { background: linear-gradient(135deg, #4ecdc4 0%, #88d3ce 100%); }
.rank-4, .rank-5 { background: linear-gradient(135deg, #64748b 0%, #94a3b8 100%); }

.article-info {
  flex: 1;
  min-width: 0;
}

.article-title {
  font-weight: 500;
  margin-bottom: 6px;
  cursor: pointer;
  transition: color 0.3s;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #111827;
}

.article-title:hover {
  color: #7c3aed;
}

.article-stats {
  display: flex;
  gap: 12px;
}

.stat-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #6b7280;
}

.performance-gauge {
  width: 80px;
  text-align: right;
  flex-shrink: 0;
}

.gauge-bar {
  height: 6px;
  background: #e5e7eb;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 4px;
}

.gauge-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.5s ease;
}

.performance-value {
  font-size: 12px;
  font-weight: 600;
  color: #6b7280;
}

/* 用户画像分析 */
.user-profile {
  padding: 0 20px 20px;
}

.profile-chart {
  margin-bottom: 20px;
}

.profile-stats {
  display: flex;
  gap: 12px;
}

.profile-stat {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
}

.stat-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 2px;
}

.stat-value {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

.stat-tag {
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 10px;
  color: white;
  font-weight: 500;
}

/* 设备与来源分析 */
.source-analysis {
  padding: 0 20px 20px;
}

.source-chart {
  margin-bottom: 20px;
}

.device-breakdown {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.device-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.device-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  flex-shrink: 0;
}

.device-info {
  flex: 1;
  min-width: 0;
}

.device-type {
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 2px;
  color: #111827;
}

.device-percent {
  font-size: 12px;
  color: #6b7280;
}

.device-bar {
  width: 80px;
  height: 6px;
  background: #e5e7eb;
  border-radius: 3px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.5s ease;
}

/* 性能指标卡片 */
.performance-metrics {
  padding: 0 20px 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.performance-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
}

.performance-info {
  flex: 1;
}

.performance-label {
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 4px;
  color: #111827;
}

.performance-value {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 4px;
  color: #111827;
}

.performance-target {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 11px;
  color: #6b7280;
}

.performance-status {
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 10px;
  color: white;
  font-weight: 500;
}

.performance-status.good {
  background: #10b981;
}

.performance-progress {
  width: 120px;
  flex-shrink: 0;
}

.progress-labels {
  display: flex;
  justify-content: space-between;
  font-size: 10px;
  color: #6b7280;
  margin-top: 2px;
}

/* 底部快速洞察 */
.insights-footer {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
  margin-top: 24px;
}

.insight-highlight {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  transition: all 0.3s ease;
}

.insight-highlight:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  border-color: #d1d5db;
}

.highlight-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 20px;
  color: white;
  flex-shrink: 0;
}

.highlight-content {
  flex: 1;
}

.highlight-title {
  font-weight: 600;
  margin-bottom: 4px;
  color: #111827;
}

.highlight-desc {
  font-size: 12px;
  color: #6b7280;
  line-height: 1.4;
}

.highlight-action {
  flex-shrink: 0;
}
</style>