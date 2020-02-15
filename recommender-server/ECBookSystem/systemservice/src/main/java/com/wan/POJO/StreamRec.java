package com.wan.POJO;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author wanfeng
 * @date 2020/2/12 20:23 实时推荐列表
 */
@Document(collection = "StreamRecs")
public class StreamRec {

  /**
   * userId : 4 recs :
   * [{"bookId":1316096199,"score":7.57083805012573},{"bookId":1060987561,"score":7.50056901210942},{"bookId":1452273749,"score":7.50037217738824},{"bookId":1425178900,"score":7.50037217738824},{"bookId":1882723007,"score":7.50037217738824},{"bookId":1885840055,"score":7.50037217738824},{"bookId":1671038184,"score":7.50037217738824},{"bookId":1553570870,"score":7.50037217738824},{"bookId":1671735594,"score":7.50037217520222},{"bookId":1684837986,"score":7.50037217520222},{"bookId":1439324599,"score":7.50037217520222},{"bookId":1440514428,"score":7.50037217520222},{"bookId":1201095696,"score":7.50037217520222},{"bookId":1843928395,"score":7.50037217520222},{"bookId":1688162827,"score":7.50037217520222},{"bookId":1393045900,"score":7.50037217520222},{"bookId":1515131083,"score":7.50037216642882},{"bookId":1451163524,"score":7.50037216642882},{"bookId":1684814994,"score":7.50037216642882},{"bookId":1452283221,"score":7.50037216642882}]
   */
  private int userId;

  private List<RecsBean> recs;

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public List<RecsBean> getRecs() {
    return recs;
  }

  public void setRecs(List<RecsBean> recs) {
    this.recs = recs;
  }

  public static class RecsBean {
    /** bookId : 1316096199 score : 7.57083805012573 */
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
