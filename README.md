# 基于大数据技术的图书推荐系统📚
- 这几天看见涨了好几个star，而且也有同学问我一些问题，所以打算将项目重写一下，记录项目创建与启动过程的各种问题与注意问题，新的项目地址为：https://github.com/Wanfengyueluo/BookRecSys ，目前还在进行推荐模块的记录，有需要的同学可以关注一下:smile:（--更新于2021.3.15晚19：22）
- 第一波更新2021.3.22
## 待完成任务⏰😅：

- [x] 引入Elasticsearch模块😀
- [x] 解决当推荐数据不存在时，实时推荐任务崩溃的问题🐛😄
- [ ] 尝试引入缓存✖️
- [ ] 前端路由控制✖️
- [ ] 书籍评分的触发时机✖️

## 项目架构🍏：

![](https://github.com/Wanfengyueluo/BookRecommenderSystem/doc/项目架构.png)

## 项目功能🍎：

![](https://github.com/Wanfengyueluo/BookRecommenderSystem/doc/基于大数据技术的图书推荐系统.png)

## 项目启动流程🍐：

```
1.zookeeper
启动：bin/zkServer.sh start
查看状态：bin/zkServer.sh status
关闭：bin/zkServer.sh stop

2.kafka
启动：
bin/kafka-server-start.sh -daemon ./config/server.properties
创建两个topic:
recommender: bin/kafka-topics.sh --create --zookeeper linux:2181 --replication-factor 1 --partitions 1 --topic recommender
log: bin/kafka-topics.sh --create --zookeeper linux:2181 --replication-factor 1 --partitions 1 --topic log
查看topic：bin/kafka-topics.sh --list --zookeeper wan:2181
producer：bin/kafka-console-producer.sh --broker-list linux:9092 --topic recommender
consumer：bin/kafka-console-consumer.sh --bootstrap-server linux:9092 --topic recommender
关闭：bin/kafka-server-stop.sh

3.MongoDB
启动：bin/mongod -config ./data/mongodb.conf
访问：bin/mongo
停止：bin/mongod -shutdown -config ./data/mongodb.conf

4.Redis
启动：redis-server ./redis.conf
连接：redis-cli
停止：redis-cli shutdown

5.Spark
启动：sbin/start-all.sh
关闭：sbin/stop-all.sh

6.Flume
启动：
./bin/flume-ng agent -c ./conf/ -f ./job/ex4.conf -n a1 -Dflume.root.logger=INFO,console

7.Azkaban(调度服务)
启动：./bin/azkaban-start.sh

8.启动后台服务

9.启动实时推荐服务OnlineRecommender

10.启动KafkaStreaming服务

11.启动前端服务
npm run serve

zookeeper >> kafka启动，创建topic >> kafkaStream启动 >> 实时推荐程序启动 >> 启动flume
```



## 后端项目目录🍊：

```txt
├─src
│  ├─main
│  │  ├─java
│  │  │  └─com
│  │  │      └─wan
│  │  │          ├─Annotation
│  │  │          ├─Configuation
│  │  │          ├─Controller
│  │  │          ├─DAO
│  │  │          ├─Interceptor
│  │  │          ├─Listener
│  │  │          ├─POJO
│  │  │          ├─Result
│  │  │          └─Service
│  │  ├─log
│  │  └─resources
│  └─test
│      └─java
└─target
```

### 依赖与插件🍋：

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.4.RELEASE</version>
        <relativePath/><!-- lookup parent from repository -->
    </parent>		
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <fork>true</fork>
                </configuration>
            </plugin>
        </plugins>
    </build>
```



## 前端项目目录🍌：

```txt
├─.dist
├─public
└─src
    ├─assets
    │  └─css
    ├─components
    ├─plugins
    └─router

```

## 推荐模块目录🍉：

```txt
├─Dataloader
│  └─src
│      ├─main
│      │  ├─resources
│      │  └─scala
│      │      └─com
│      │          └─wan
│      │              └─recommender
│      └─test
│          └─java
├─KafkaStreaming
│  ├─src
│  │  ├─main
│  │  │  ├─java
│  │  │  │  └─com
│  │  │  │      └─wan
│  │  │  │          └─kafkastreaming
│  │  │  └─resources
│  │  └─test
│  │      └─java
│  └─target
│      ├─classes
│      │  └─com
│      │      └─wan
│      │          └─kafkastreaming
│      └─generated-sources
│          └─annotations
├─OfflineRecommender
│  └─src
│      ├─main
│      │  ├─resources
│      │  └─scala
│      │      └─com
│      │          └─wan
│      │              └─offline
│      └─test
│          └─java
├─OnlineRecommender
│  ├─src
│  │  ├─main
│  │  │  ├─resources
│  │  │  └─scala
│  │  │      └─com
│  │  │          └─wan
│  │  │              └─online
│  │  └─test
│  │      └─java
│  └─target
│      ├─classes
│      │  └─com
│      │      └─wan
│      │          └─online
│      └─generated-sources
│          └─annotations
└─StatisticsRecommender
    └─src
        ├─main
        │  ├─resources
        │  └─scala
        │      └─com
        │          └─wan
        │              └─statistics
        └─test
            └─java

```



## 后端

- Spring Boot
- Redis
- MongoDB

## 前端

- Vue
- Element-ui
- Axios

## 推荐服务

- Spark
- Zookeeper
- Kafka
- Flume

## 任务调度

- Azkaban
