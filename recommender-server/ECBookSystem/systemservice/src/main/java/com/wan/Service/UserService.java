package com.wan.Service;

import com.wan.DAO.UserDao;
import com.wan.POJO.Receive;
import com.wan.POJO.User;
import com.wan.Result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wanfeng
 * @date 2020/1/31 13:58
 */
@Component
public class UserService {

  @Autowired UserDao userDao;

  public Result findUser(User user) {
    return userDao.findUser(user);
  }

  public Result registerUser(User user) {
    return userDao.registerUser(user);
  }

  public Result bookRating(Receive receive) {
    return userDao.bookRating(receive);
  }

  public Result bookFavorite(Receive receive) {
    return userDao.bookFavorite(receive);
  }
}
