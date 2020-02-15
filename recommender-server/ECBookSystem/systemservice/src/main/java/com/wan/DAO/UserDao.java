package com.wan.DAO;

import com.wan.POJO.Rating;
import com.wan.POJO.Receive;
import com.wan.POJO.User;
import com.wan.Result.Result;
import com.wan.Result.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author wanfeng
 * @date 2020/1/31 13:59
 */
@Component
public class UserDao {

  @Autowired MongoTemplate mongoTemplate;

  @Autowired StringRedisTemplate redisTemplate;

  /**
   * @description: 判断当该用户是否存在
   * @param user
   * @return
   */
  public Boolean userIsexist(User user) {
    boolean flag = false;
    List<User> users = mongoTemplate.findAll(User.class);
    if (users.size() > 0) {
      for (User u : users) {
        if (user.getUserName().equals(u.getUserName())) {
          flag = true;
          break;
        }
      }
    }
    return flag;
  }

  /**
   * @description: 根据用户名和密码查询该用户，如果该用户存在推荐列表就将该用户对应推荐列表存入Redis
   * @param user
   * @return
   */
  public Result findUser(User user) {
    List<User> users = mongoTemplate.findAll(User.class);
    System.out.println(users.size());
    if (users.size() > 0) {
      for (User u : users) {
        if (user.getUserName().equals(u.getUserName())
            && user.getPassword().equals(u.getPassword())) {
          loadInRedis(u.getUserId());
          return new Result(ResultCode.SUCCESS.getCode(), "登录成功!", u);
        }
      }
    }
    return new Result(ResultCode.FAIL.getCode(), "登录失败！");
  }

  /**
   * @description: 将对应userId的推荐列表存入Redis
   * @param userId
   * @return
   */
  private void loadInRedis(int userId) {
    Query query = new Query();
    query.addCriteria(Criteria.where("userId").is(userId));
    List<Rating> lists = mongoTemplate.find(query, Rating.class);
    if (lists.size() > 0) {
      // 先将Redis中该userId对应的key删除，然后存入
      redisTemplate.delete("userId:" + lists.get(0).getUserId());
      for (Rating rating : lists) {
        redisTemplate
            .opsForList()
            .rightPush(
                "userId:" + rating.getUserId(), rating.getBookId() + ":" + rating.getScore());
      }
    }
  }

  /**
   * @description:判断用户是否注册成功
   * @param user
   * @return
   */
  public Result registerUser(User user) {
    if (userIsexist(user)) {
      return new Result(ResultCode.EXITED.getCode(), "用户已存在！!");
    }
    mongoTemplate.insert(user, "User");
    Result result = findUser(user);
    if (result.getCode() == 200) {
      return new Result(ResultCode.SUCCESS.getCode(), "注册成功!", (User) result.getData());
    } else {
      return new Result(ResultCode.EXITED.getCode(), "注册失败!");
    }
  }

  /**
   * @description: 将书籍的评分记录存入MongoDb和Redis
   * @param receive userId bookId score
   * @return
   */
  public Result bookRating(Receive receive) {
    Query query = new Query();
    query.addCriteria(Criteria.where("userId").is(receive.getUserId()));
    List<User> users = mongoTemplate.find(query, User.class);
    boolean isRating = false;
    if (users.size() > 0) {
      User user = users.get(0);
      for (int j = 0; j < user.getScoreRecord().size(); j++) {
        if (user.getScoreRecord().get(j).getBookId() == receive.getBookId()) {
          isRating = true;
          break;
        }
      }
      if (isRating) {
        return new Result(ResultCode.FAIL.getCode(), "已经评过了！");
      } else {
        SimpleDateFormat sdf = new SimpleDateFormat();
        user.setScoreRecord(receive.getBookId(), receive.getScore(), sdf.format(new Date()));
        Update update = new Update();
        update.set("scoreRecord", user.getScoreRecord());
        mongoTemplate.upsert(query, update, "User");

        mongoTemplate.insert(
            new Rating(receive.getUserId(), receive.getBookId(), receive.getScore()), "Rating");
        redisTemplate
            .opsForList()
            .rightPush(
                "userId:" + receive.getUserId(), receive.getBookId() + ":" + receive.getScore());
      }
      return new Result(ResultCode.SUCCESS.getCode(), "评分成功!");
    } else {
      return new Result(ResultCode.FAIL.getCode(), "评分失败!");
    }
  }

  /**
   * @description:收藏书籍
   * @param receive userId bookId
   * @return
   */
  public Result bookFavorite(Receive receive) {
    Query query = new Query();
    query.addCriteria(Criteria.where("userId").is(receive.getUserId()));
    List<User> users = mongoTemplate.find(query, User.class);
    if (users.size() > 0) {
      User user = users.get(0);
      user.setFavorite(receive.getBookId());
      Update update = new Update();
      update.set("favorite", user.getFavorite());
      mongoTemplate.upsert(query, update, "User");
      return new Result(ResultCode.SUCCESS.getCode(), "收藏成功!");
    } else {
      return new Result(ResultCode.FAIL.getCode(), "收藏失败!");
    }
  }
}
