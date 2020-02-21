package com.wan.DAO;

import com.wan.POJO.*;
import com.wan.Result.Result;
import com.wan.Result.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wanfeng
 * @date 2020/1/31 14:00
 */
@Component
public class BookDao {

  @Autowired MongoTemplate mongoTemplate;

  /**
   * @description: 获取书籍列表并分页
   * @param state 当前状态
   * @param page 页数
   * @param size 每页大小
   * @return
   */
  public Result getBookList(int state, int page, int size) {
    // initMongoDB();
    ArrayList<Book> lists = new ArrayList<>();
    List<Book> books = mongoTemplate.findAll(Book.class).subList(size * (page - 1), size * page);
    lists.addAll(books);
    if (state == 0) {
      state = 1;
      int total = mongoTemplate.findAll(Book.class).size();
      return new Result(ResultCode.SUCCESS.getCode(), "获取书籍成功", lists, total, state);
    } else {
      return new Result(ResultCode.SUCCESS.getCode(), "获取书籍成功", lists);
    }
  }

  /**
   * @description: 获取书籍列表，并初始化书籍表（过滤一些不完整的数据）
   * @param
   * @return
   */
  private ArrayList<Book> getBookList() {
    initMongoDB();
    ArrayList<Book> lists = new ArrayList<>();
    List<Book> books = mongoTemplate.findAll(Book.class);
    lists.addAll(books);
    return lists;
  }

  /**
   * @description:从RateMoreBooks表中获取评分次数最多的书籍列表
   * @param
   * @return
   */
  public Result getHotBookList() {
    List<RateMoreBooks> rateMoreBooks = mongoTemplate.findAll(RateMoreBooks.class);

    ArrayList<Book> results = new ArrayList<>();

    for (int i = 0; i < rateMoreBooks.size(); i++) {
      findBook(results, rateMoreBooks.get(i).getBookId());
      if (results.size() == 20) {
        break;
      }
    }
    return new Result(
        ResultCode.SUCCESS.getCode(),
        "获取最热书籍成功",
        results.subList(0, results.size() > 20 ? 20 : results.size()));
  }

  /**
   * @description:从AverageBooks表中获取评分高并且评分次数大于1的书籍列表
   * @param
   * @return
   */
  public Result getHighBookList() {
    List<AverageBooks> averageBooks = mongoTemplate.findAll(AverageBooks.class);

    ArrayList<Book> results = new ArrayList<>();
    for (int i = 0; i < averageBooks.size(); i++) {
      findBook(results, averageBooks.get(i).getBookId());
      if (results.size() == 20) {
        break;
      }
    }
    return new Result(
        ResultCode.SUCCESS.getCode(),
        "获取高分书籍成功",
        results.subList(0, results.size() > 20 ? 20 : results.size()));
  }

  /**
   * @description:从DataLoader将数据导入后需要过滤一些错误数据，每次取数据前调用删除错误数据
   * @param
   * @return
   */
  public void initMongoDB() {
    List<Book> books = mongoTemplate.findAll(Book.class);
    for (Book book : books) {
      if (!book.getBookImageUrl().contains("http")) {
        mongoTemplate.remove(book, "Book");
      }
    }
  }

  /**
   * @description: 根据推荐列表的bookId获取对应的书籍
   * @param results
   * @param bookId
   * @return
   */
  public void findBook(ArrayList<Book> results, int bookId) {
    ArrayList<Book> lists = getBookList();
    for (Book book : lists) {
      if (bookId == book.getBookId()) {
        results.add(book);
      }
    }
  }

  /**
   * @description:获取用户的推荐列表
   * @param userId
   * @return
   */
  public Result getUserBookList(int userId) {
    Query query = new Query();
    query.addCriteria(Criteria.where("userId").is(userId));
    List<UserRec> userRecs = mongoTemplate.find(query, UserRec.class);
    ArrayList<Book> results = new ArrayList<>();
    if (userRecs.size() > 0) {
      for (int i = 0; i < userRecs.get(0).getRecs().size(); i++) {
        findBook(results, userRecs.get(0).getRecs().get(i).getBookId());
      }
      return new Result(ResultCode.SUCCESS.getCode(), "获取推荐书籍成功！", results);
    } else {
      return new Result(ResultCode.FAIL.getCode(), "获取推荐书籍失败，当前用户尚无推荐记录！");
    }
  }

  /**
   * @description: 获取用户的收藏列表
   * @param userId
   * @return
   */
  public Result getFavoriteBooks(int userId) {
    Query query = new Query();
    query.addCriteria(Criteria.where("userId").is(userId));
    List<User> users = mongoTemplate.find(query, User.class);
    ArrayList<Book> results = new ArrayList<>();
    if (users.size() > 0) {
      for (int i = 0; i < users.get(0).getFavorite().size(); i++) {
        findBook(results, users.get(0).getFavorite().get(i).getBookId());
      }
      return new Result(ResultCode.SUCCESS.getCode(), "获取成功!", results);
    } else {
      return new Result(ResultCode.FAIL.getCode(), "获取失败!");
    }
  }

  /**
   * @description:根据bookId获取书籍详情
   * @param bookId
   * @param userId
   * @return
   */
  public Result getBookByBookId(int bookId, int userId) {
    Query query1 = new Query();
    query1.addCriteria(Criteria.where("bookId").is(bookId));
    List<Book> books = mongoTemplate.find(query1, Book.class);
    Book book = new Book();
    if (books.size() > 0) {
      book = books.get(0);
    }
    List<AverageBooks> avgbooks = mongoTemplate.find(query1, AverageBooks.class);

    Query query2 = new Query();
    query2.addCriteria(Criteria.where("userId").is(userId));
    List<User> users = mongoTemplate.find(query2, User.class);
    boolean isRating = false;
    int myScore = 0;
    if (users.size() > 0) {
      User user = users.get(0);
      for (int i = 0; i < user.getScoreRecord().size(); i++) {
        if ((bookId == user.getScoreRecord().get(i).getBookId())
            && (user.getScoreRecord().get(i).getScore() > 0)) {
          System.out.println(user.getScoreRecord().get(i).getBookId());
          isRating = true;
          myScore = user.getScoreRecord().get(i).getScore();
          break;
        }
      }
    }
    if (avgbooks.size() > 0) {
      if (!isRating) {
        return new Result(
            ResultCode.SUCCESS.getCode(), "获取成功!未评分", book, avgbooks.get(0).getAvgScore());
      } else {
        return new Result(
            ResultCode.SUCCESS.getCode(),
            "获取成功!已评分",
            book,
            avgbooks.get(0).getAvgScore(),
            myScore,
            0,
            0);
      }
    }
    return new Result(ResultCode.FAIL.getCode(), "获取失败!", book);
  }

  /**
   * @description: 获取该书籍的相似推荐列表
   * @param bookId
   * @return
   */
  public Result getBookRecsByBookId(int bookId) {
    Query query = new Query();
    query.addCriteria(Criteria.where("bookId").is(bookId));
    List<BookRec> lists = mongoTemplate.find(query, BookRec.class);
    ArrayList<Book> books = new ArrayList<>();
    if (lists.size() > 0) {
      for (int i = 0; i < lists.get(0).getRecs().size(); i++) {
        findBook(books, lists.get(0).getRecs().get(i).getBookId());
      }
    }
    return new Result(ResultCode.SUCCESS.getCode(), "获取成功!", books);
  }

  /**
   * @description:获取该用户的实时推荐列表
   * @param userId
   * @return
   */
  public Result getStreamRecsByBookId(int userId) {
    Query query = new Query();
    query.addCriteria(Criteria.where("userId").is(userId));
    List<StreamRec> lists = mongoTemplate.find(query, StreamRec.class);
    ArrayList<Book> books = new ArrayList<>();
    if (lists.size() > 0) {
      for (int i = 0; i < lists.get(0).getRecs().size(); i++) {
        findBook(books, lists.get(0).getRecs().get(i).getBookId());
      }
    }
    return new Result(ResultCode.SUCCESS.getCode(), "获取成功!", books);
  }
}
