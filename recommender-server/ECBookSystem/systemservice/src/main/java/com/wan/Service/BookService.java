package com.wan.Service;

import com.wan.DAO.BookDao;
import com.wan.Result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wanfeng
 * @date 2020/1/31 13:58
 */
@Component
public class BookService {

    @Autowired
    BookDao bookDao;

    public Result getBookList(int state, int page, int size) {
        return bookDao.getBookList(state,page,size);
    }

    public Result getHotBookList() {
        return bookDao.getHotBookList();
    }

    public Result getHighBookList() {
        return bookDao.getHighBookList();
    }

    public Result getUserBookList(int userId) {
        return bookDao.getUserBookList(userId);
    }
    public Result getFavoriteBooks(int userId) {
        return bookDao.getFavoriteBooks(userId);
    }

    public Result getBookByBookId(int bookId, int userId) {
        return bookDao.getBookByBookId(bookId,userId);
    }

    public Result getBookRecsByBookId(int bookId) {
        return bookDao.getBookRecsByBookId(bookId);
    }

    public Result getStreamRecsByBookId(int userId) {
        return bookDao.getStreamRecsByBookId(userId);
    }
}
