package com.wan.POJO;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author wanfeng
 * @date 2020/2/12 20:21 书籍相似推荐列表
 */
@Document(collection = "BookRecs")
public class BookRec {

  /**
   * bookId : 1312983301 recs :
   * [{"bookId":1312974256,"score":0.997905942278948},{"bookId":1515134384,"score":1},{"bookId":1312980353,"score":1},{"bookId":1375759689,"score":1},{"bookId":1380785218,"score":1},{"bookId":1515136530,"score":1},{"bookId":1451203771,"score":1},{"bookId":1451210611,"score":1},{"bookId":1515121843,"score":0.999936231482803},{"bookId":1515123171,"score":1},{"bookId":1515136379,"score":1},{"bookId":1446610550,"score":1},{"bookId":1515124214,"score":1},{"bookId":1515136263,"score":1},{"bookId":1515137111,"score":1}]
   */
  private int bookId;

  private List<RecsBean> recs;

  public int getBookId() {
    return bookId;
  }

  public void setBookId(int bookId) {
    this.bookId = bookId;
  }

  public List<RecsBean> getRecs() {
    return recs;
  }

  public void setRecs(List<RecsBean> recs) {
    this.recs = recs;
  }

  public static class RecsBean {
    /** bookId : 1312974256 score : 0.997905942278948 */
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
