package com.wan.POJO;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author wanfeng
 * @date 2020/2/2 16:02 书籍评分表
 */
@Document(collection = "Rating")
public class Rating {

  /** userId : 278418 bookId : 1316121789 score : 0 */
  private int userId;

  private int bookId;
  private int score;

  public Rating(int userId, int bookId, int score) {
    this.userId = userId;
    this.bookId = bookId;
    this.score = score;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

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
}
