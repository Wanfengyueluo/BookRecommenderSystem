package com.wan;

/**
 * @author wanfeng
 * @date 2020/1/31 13:46
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class App {
  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
