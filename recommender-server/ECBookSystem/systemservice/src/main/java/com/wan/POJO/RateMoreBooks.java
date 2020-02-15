package com.wan.POJO;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author wanfeng
 * @date 2020/2/1 14:16 评分个数最多的书籍，未考虑评分的高低
 */
@Document(collection = "RateMoreBooks")
public class RateMoreBooks {

  /** bookId : 1971880107 countNum : NumberLong(17) */
  private int bookId;

  private int countNum;

  public int getBookId() {
    return bookId;
  }

  public void setBookId(int bookId) {
    this.bookId = bookId;
  }

  public int getCountNum() {
    return countNum;
  }

  public void setCountNum(int countNum) {
    this.countNum = countNum;
  }
}
