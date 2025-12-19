package org.sang.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.sang.bean.Article;

import java.util.List;

/**
 * Created by sang on 2017/12/20.
 */
@Mapper
public interface ArticleMapper {
    int addNewArticle(Article article);

    int updateArticle(Article article);

    List<Article> getArticleByState(@Param("state") Integer state, @Param("start") Integer start, @Param("count") Integer count, @Param("keywords") String keywords);

//    List<Article> getArticleByStateByAdmin(@Param("start") int start, @Param("count") Integer count, @Param("keywords") String keywords);

    int getArticleCountByState(@Param("state") Integer state, @Param("keywords") String keywords);

    int updateArticleState(@Param("ids") Integer ids[], @Param("state") Integer state);

    int updateArticleStateById(@Param("articleId") Integer articleId, @Param("state") Integer state);
    
    int updateArticleStateByIds(@Param("ids") Integer[] ids, @Param("state") Integer state);

    int deleteArticleById(@Param("ids") Integer[] ids);

    Article getArticleById(Integer id);

    void pvIncrement(Integer id);

    //INSERT INTO pv(countDate,pv,username) SELECT NOW(),SUM(pageView),username FROM article JOIN user ON article.nickname = user.nickname GROUP BY username
    void pvStatisticsPerDay();

    List<String> getCategories();

    List<Integer> getDataStatistics();

    int getTotalPV();

    List<Article> getLatestArticles(@Param("count") Integer count);
}
