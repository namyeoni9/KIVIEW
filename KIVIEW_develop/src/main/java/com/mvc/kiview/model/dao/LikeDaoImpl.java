package com.mvc.kiview.model.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mvc.kiview.model.vo.LikeVo;

@Repository
public class LikeDaoImpl implements LikeDao {
   
   @Autowired
   private SqlSessionTemplate sqlSession;

   @Override
   public int selectLikeCount(LikeVo vo) {
      int resultLike = 0;
      try {
         resultLike = sqlSession.selectOne(namespace+"selectLikeCount", vo);
      }catch (Exception  e) {
         e.printStackTrace();
      }
      return resultLike; 
   }
   @Override
   public int likeInsert(LikeVo vo) {
      int res = 0;
      
      try {
         res = sqlSession.insert(namespace+"insertlike",vo);
      }catch(Exception e) {
          e.printStackTrace();
      }
      return res;
   }

   @Override
   public int likeDelete(LikeVo vo) {
      int res = 0;
      
      try {
      res = sqlSession.delete(namespace+"deletelike",vo);
      }catch(Exception e) {
          e.printStackTrace();
      }
      return res;
   }
   
   
   


   

}