package com.wan.Listener;

import com.wan.Annotation.AutoIncKey;
import com.wan.POJO.IncInfo;
import com.wan.POJO.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * @author wanfeng
 * @date 2020/1/31 13:50 用于实现自定义Id自增
 */
@Component
public class SaveEventListener extends AbstractMongoEventListener<Object> {
  private static final Logger logger = LoggerFactory.getLogger(SaveEventListener.class);
  @Autowired private MongoTemplate mongo;

  @Override
  public void onBeforeConvert(BeforeConvertEvent<Object> event) {
    logger.info(event.getSource().toString());
    try {
      User user = (User) event.getSource();

      if (user != null) {
        ReflectionUtils.doWithFields(
            user.getClass(),
            new ReflectionUtils.FieldCallback() {
              @Override
              public void doWith(Field field)
                  throws IllegalArgumentException, IllegalAccessException {
                ReflectionUtils.makeAccessible(field);
                // 如果字段添加了我们自定义的AutoIncKey注解
                if (field.isAnnotationPresent(AutoIncKey.class)) {
                  // 设置自增ID
                  field.set(user, getNextId(user.getClass().getSimpleName()));
                }
              }
            });
      }
    } catch (Exception e) {

    }
  }

  private int getNextId(String collName) {
    Query query = new Query(Criteria.where("collName").is(collName));
    Update update = new Update();
    update.inc("incId", 1);
    FindAndModifyOptions options = new FindAndModifyOptions();
    options.upsert(true);
    options.returnNew(true);
    IncInfo inc = mongo.findAndModify(query, update, options, IncInfo.class);
    return inc.getIncId();
  }
}
