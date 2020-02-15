package com.wan.POJO;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.wan.Annotation.AutoIncKey;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

/**
 * @author wanfeng
 * @date 2020/1/31 13:48 用户信息
 */
@Document(collection = "User")
public class User {

  /**
   * userId : 1 userName : admin password : 123456 hitory : [{"bookId":123456,"date":"2020-1-27
   * 18:44:12"},{"bookId":123457,"date":"2020-1-27 18:55:33"}] favorite :
   * [{"bookId":123456},{"bookId":123457}] scoreRecord :
   * [{"bookId":123456,"score":8,"date":"2020-1-27
   * 18:44:12"},{"bookId":123457,"score":6,"date":"2020-1-27 18:55:33"}]
   */
  @AutoIncKey private int userId;

  private String userName;
  private String password;
  private ArrayList<HitoryBean> hitory = new ArrayList<HitoryBean>();
  private ArrayList<FavoriteBean> favorite = new ArrayList<FavoriteBean>();
  private ArrayList<ScoreRecordBean> scoreRecord = new ArrayList<ScoreRecordBean>();

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public ArrayList<HitoryBean> getHitory() {
    return hitory;
  }

  public void setHitory(ArrayList<HitoryBean> hitory) {
    this.hitory = hitory;
  }

  public ArrayList<FavoriteBean> getFavorite() {
    return favorite;
  }

  public void setFavorite(int bookId) {
    FavoriteBean bean = new FavoriteBean();
    bean.setBookId(bookId);
    this.favorite.add(bean);
  }

  public ArrayList<ScoreRecordBean> getScoreRecord() {
    return scoreRecord;
  }

  public void setScoreRecord(int bookId, int score, String date) {
    ScoreRecordBean bean = new ScoreRecordBean();
    bean.setBookId(bookId);
    bean.setScore(score);
    bean.setDate(date);
    this.scoreRecord.add(bean);
  }

  public static class HitoryBean {
    /** bookId : 123456 date : 2020-1-27 18:44:12 */
    private int bookId;

    private String date;

    public int getBookId() {
      return bookId;
    }

    public void setBookId(int bookId) {
      this.bookId = bookId;
    }

    public String getDate() {
      return date;
    }

    public void setDate(String date) {
      this.date = date;
    }
  }

  public static class FavoriteBean {
    /** bookId : 123456 */
    private int bookId;

    public int getBookId() {
      return bookId;
    }

    public void setBookId(int bookId) {
      this.bookId = bookId;
    }
  }

  public static class ScoreRecordBean {
    /** bookId : 123456 score : 8 date : 2020-1-27 18:44:12 */
    private int bookId;

    private int score;
    private String date;

    public int getBookId() {
      return bookId;
    }

    public void setBookId(int bookId) {
      this.bookId = bookId;
    }

    public int getScore() {
      return score;
    }

    public void setScore(int score) {
      this.score = score;
    }

    public String getDate() {
      return date;
    }

    public void setDate(String date) {
      this.date = date;
    }
  }

  public String getToken(User user) {
    String token = "";
    token =
        JWT.create().withAudience(user.getUserName()).sign(Algorithm.HMAC256(user.getPassword()));
    return token;
  }
}
