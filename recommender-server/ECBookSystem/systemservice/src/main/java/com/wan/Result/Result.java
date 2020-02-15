package com.wan.Result;

/**
 * @author wanfeng
 * @date 2020/1/31 13:56
 */
public class Result {
  private int code;
  private String message;
  private Object data;
  private int total;
  private int state;
  private int avgScore;
  private int myScore;

  public Result(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public Result(int code, String message, Object data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  public Result(int code, String message, Object data, int total, int state) {
    this.code = code;
    this.message = message;
    this.data = data;
    this.total = total;
    this.state = state;
  }

  public Result(int code, String message, Object data, int avgScore) {
    this.code = code;
    this.message = message;
    this.data = data;
    this.avgScore = avgScore;
  }

  public Result(
      int code, String message, Object data, int avgScore, int myScore, int total, int state) {
    this.code = code;
    this.message = message;
    this.data = data;
    this.total = total;
    this.state = state;
    this.avgScore = avgScore;
    this.myScore = myScore;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public int getState() {
    return state;
  }

  public void setState(int state) {
    this.state = state;
  }

  public int getAvgScore() {
    return avgScore;
  }

  public void setAvgScore(int avgScore) {
    this.avgScore = avgScore;
  }
}
