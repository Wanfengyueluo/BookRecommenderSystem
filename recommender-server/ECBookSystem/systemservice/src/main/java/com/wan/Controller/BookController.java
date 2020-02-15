package com.wan.Controller;

import com.wan.POJO.PageHelper;
import com.wan.Result.Result;
import com.wan.Service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wanfeng
 * @date 2020/1/31 13:58
 */
@RestController
public class BookController {

  @Autowired BookService bookService;

  @PostMapping("/api/books")
  /**
   * @description: 获取所有书籍列表并分页
   * @param pageHelper page size state
   * @return com.wan.Result.Result
   */
  public Result getBookList(@RequestBody PageHelper pageHelper) {
    int state = pageHelper.getState();
    int page = pageHelper.getPage();
    int size = pageHelper.getSize();
    return bookService.getBookList(state, page, size);
  }

  @GetMapping("/api/hotBooks")
  /**
   * @description: 获取热门书籍列表
   * @param
   * @return com.wan.Result.Result
   */
  public Result getHotBookList() {
    return bookService.getHotBookList();
  }

  @GetMapping("/api/highBooks")
  /**
   * @description: 获取高分书籍列表
   * @param
   * @return com.wan.Result.Result
   */
  public Result getHighBookList() {
    return bookService.getHighBookList();
  }

  @GetMapping("/api/likeBooks")
  /**
   * @description: 获取用户的书籍推荐列表
   * @param userId
   * @return com.wan.Result.Result
   */
  public Result getUserBookList(@RequestParam(value = "userId") int userId) {
    return bookService.getUserBookList(userId);
  }

  @GetMapping("/api/book/favorite")
  /**
   * @description: 获取用户收藏书籍列表
   * @param userId
   * @return com.wan.Result.Result
   */
  public Result getFavoriteBooks(@RequestParam(value = "userId") int userId) {
    return bookService.getFavoriteBooks(userId);
  }

  @GetMapping("/api/books/item")
  /**
   * @description: 获取书籍详情
   * @param bookId
   * @param userId
   * @return com.wan.Result.Result
   */
  public Result getBookByBookId(
      @RequestParam(value = "bookId") int bookId, @RequestParam(value = "userId") int userId) {
    return bookService.getBookByBookId(bookId, userId);
  }

  @GetMapping("/api/books/bookRecs")
  /**
   * @description: 获取书籍的相似推荐列表
   * @param bookId
   * @return com.wan.Result.Result
   */
  public Result getBookRecsByBookId(@RequestParam(value = "bookId") int bookId) {
    return bookService.getBookRecsByBookId(bookId);
  }

  @GetMapping("/api/books/streamRecs")
  /**
   * @description: 获取用户评价书籍后的实时推荐列表
   * @param userId
   * @return com.wan.Result.Result
   */
  public Result getStreamRecsByBookId(@RequestParam(value = "userId") int userId) {
    return bookService.getStreamRecsByBookId(userId);
  }
}
