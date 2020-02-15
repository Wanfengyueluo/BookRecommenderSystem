package com.wan.Controller;

import com.wan.POJO.Receive;
import com.wan.POJO.User;
import com.wan.Result.Result;
import com.wan.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wanfeng
 * @date 2020/1/31 13:57
 */
@RestController
public class UserController {
  private static Logger logger = LoggerFactory.getLogger(UserController.class.getName());

  @Autowired UserService userService;

  @PostMapping("/api/login")
  /**
   * @description: 接收用户的登录请求
   * @param user 包含userName和password
   * @return com.wan.Result.Result
   */
  public Result getUser(@RequestBody User user) {
    return userService.findUser(user);
  }

  @PostMapping("/api/register")
  /**
   * @description:接受用户的注册请求
   * @param user 包含userName和password
   * @return com.wan.Result.Result
   */
  public Result registerUser(@RequestBody User user) {
    return userService.registerUser(user);
  }

  @PostMapping("/api/book/rating")
  /**
   * @description: 用户评分接口
   * @param receive 包含userId bookId score
   * @return com.wan.Result.Result
   */
  public Result bookRating(@RequestBody Receive receive) {
    System.out.println(
        receive.getUserId() + "-----" + receive.getBookId() + "------" + receive.getScore());

    // 用于评分日志埋点，用于flume获取信息
    System.out.println("============埋点===========");
    logger.info(
        "PRODUCT_RATING_PREFIX:"
            + receive.getUserId()
            + "|"
            + receive.getBookId()
            + "|"
            + receive.getScore());

    return userService.bookRating(receive);
  }

  @PostMapping("/api/book/favorite")
  /**
   * @description: 书籍收藏接口
   * @param receive userId和bookId
   * @return com.wan.Result.Result
   */
  public Result bookFavorite(@RequestBody Receive receive) {
    return userService.bookFavorite(receive);
  }

  /*
   * 浏览记录删除
   * 收藏记录删除
   * */

}
