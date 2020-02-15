package com.wan.POJO;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

/**
 * @author wanfeng
 * @date 2020/2/6 19:37 用户推荐书籍列表
 */
@Document(collection = "UserRecs")
public class UserRec {

  /** userId : 277589 recs : [{"bookId":1812579402,"score":9.03321601817516}] */
  private int userId;

  private ArrayList<RecsBean> recs;

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public ArrayList<RecsBean> getRecs() {
    return recs;
  }

  public void setRecs(ArrayList<RecsBean> recs) {
    this.recs = recs;
  }

  public static class RecsBean {
    /** bookId : 1812579402 score : 9.03321601817516 */
    private int bookId;

    private double score;

    public int getBookId() {
      return bookId;
    }

    public void setBookId(int bookId) {
      this.bookId = bookId;
    }

    public double getScore() {
      return score;
    }

    public void setScore(double score) {
      this.score = score;
    }
  }
}
