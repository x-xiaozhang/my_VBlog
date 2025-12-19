package org.sang.service;

import org.sang.bean.Article;
import org.sang.mapper.ArticleMapper;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by sang on 2017/12/20.
 */
@Service
@Transactional
public class ArticleService {
    @Autowired
    ArticleMapper articleMapper;

    public int addNewArticle(Article article) {
        //处理文章摘要
        if (article.getSummary() == null || "".equals(article.getSummary())) {
            //直接截取
            String stripHtml = stripHtml(article.getHtmlContent());
            article.setSummary(stripHtml.substring(0, stripHtml.length() > 50 ? 50 : stripHtml.length()));
        }
        if (article.getId() == -1) {
            //添加操作
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            if (article.getState() == 1) {
                //设置发表日期
                article.setPublishDate(timestamp);
            }
            article.setEditTime(timestamp);
            //设置当前用户昵称
            article.setNickname(Util.getCurrentUser().getNickname());
            int i = articleMapper.addNewArticle(article);
            return i;
        } else {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            if (article.getState() == 1) {
                //设置发表日期
                article.setPublishDate(timestamp);
            }
            //更新
            article.setEditTime(new Timestamp(System.currentTimeMillis()));
            int i = articleMapper.updateArticle(article);
            return i;
        }
    }



    public String stripHtml(String content) {
        content = content.replaceAll("<p .*?>", "");
        content = content.replaceAll("<br\\s*/?>", "");
        content = content.replaceAll("\\<.*?>", "");
        return content;
    }

    public List<Article> getArticleByState(Integer state, Integer page, Integer count,String keywords) {
        int start = (page - 1) * count;
        return articleMapper.getArticleByState(state, start, count, keywords);
    }

//    public List<Article> getArticleByStateByAdmin(Integer page, Integer count,String keywords) {
//        int start = (page - 1) * count;
//        return articleMapper.getArticleByStateByAdmin(start, count,keywords);
//    }

    public int getArticleCountByState(Integer state,String keywords) {
        return articleMapper.getArticleCountByState(state, keywords);
    }

    public int updateArticleState(Integer[] ids, Integer state) {
        if (state == 2) {
            return articleMapper.deleteArticleById(ids);
        } else {
            return articleMapper.updateArticleState(ids, 2);//放入到回收站中
        }
    }

    public int restoreArticle(Integer articleId) {
        return articleMapper.updateArticleStateById(articleId, 1); // 从回收站还原在原处
    }
    
    public int restoreArticles(Integer[] ids) {
        return articleMapper.updateArticleStateByIds(ids, 1); // 批量从回收站还原文章
    }

    public Article getArticleById(Integer id) {
        Article article = articleMapper.getArticleById(id);
        articleMapper.pvIncrement(id);
        return article;
    }

    public void pvStatisticsPerDay() {
        articleMapper.pvStatisticsPerDay();
    }

    /**
     * 获取最近七天的日期
     * @return
     */
    public List<String> getCategories() {
        return articleMapper.getCategories();
    }

    /**
     * 获取最近七天的数据
     * @return
     */
    public List<Integer> getDataStatistics() {
        return articleMapper.getDataStatistics();
    }

    /**
     * 获取总访问量
     * @return
     */
    public int getTotalPV() {
        return articleMapper.getTotalPV();
    }

    public List<Article> getLatestArticles(Integer count) {
        return articleMapper.getLatestArticles(count);
    }
}
