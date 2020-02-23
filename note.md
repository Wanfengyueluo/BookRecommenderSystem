💯❤️😅🚗:100:

```
1.zookeeper
启动：bin/zkServer.sh start
查看状态：bin/zkServer.sh status
关闭：bin/zkServer.sh stop

2.kafka
启动：
bin/kafka-server-start.sh -daemon ./config/server.properties
关闭：bin/kafka-server-stop.sh
创建topic：bin/kafka-topics.sh --create --zookeeper linux:2181 --replication-factor 1 --partitions 1 --topic recommender
查看topic：bin/kafka-topics.sh --list --zookeeper wan:2181
producer：bin/kafka-console-producer.sh --broker-list linux:9092 --topic recommender
consumer：bin/kafka-console-consumer.sh --bootstrap-server linux:9092 --topic recommender

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
启动：./bin/flume-ng agent -c ./conf/ -f ./conf/log-kafka.properties -n agent -Dflume.root.logger=INFO,console
./bin/flume-ng agent -c ./conf/ -f ./job/ex4.conf -n a1 -Dflume.root.logger=INFO,console

7.Azkaban
启动：./bin/azkaban-start.sh

启动流程：
1.业务系统启动
2.离线调度程序azkaban
3.zookeeper -- kafka启动，创建topic ----kafkaStream启动 ---实时推荐程序启动 ---- 启动flume
```

当前存在的问题：Book数据不全，导致很多书籍没法显示

收藏书籍的删除

#### git使用：

参考[廖雪峰的git教程](https://www.liaoxuefeng.com/wiki/896043488029600)

前提：git安装，相关配置

1.本地建库：

```
git init 初始化仓库
git add .创建README.md后添加文件
git commit -m "xxx"添加修改备注
```

2.github新建repository

```
git remote add origin git@github.com:Wanfengyueluo/xxx.git
git push -u origin master
```

JVM

离线数据

Java注解

```java
user
userId int  自增字段（https://www.cnblogs.com/yzykkpl/p/10356258.html）
userName String
password String


JWT认证（https://www.jianshu.com/p/e88d3f8151db）
```

```
2020.1.31
规划：
1.暂缓实现前端页面，重新学习前端基础，学习（https://how2j.cn/k/tmall-front/tmall-front-790/790.html#nowhere）
2.继续完成后端推荐系统的相关实现，完善API接口，独立运行后端


```

```
已完成：
2020.1.31
基本完成推荐算法实现部分，还需完善后端接口实现各组件联通

```

```
2020.2.1
完成API

登录接口：("/api/login")
方法：POST
body：
{
	"userName":"admin",
	"password":"123456"
}

注册接口：("/api/register")
方法：POST
body：
{
	"userName":"admin",
	"password":"123456"
}

书籍列表接口:("/api/books")
方法:GET

热门书籍列表接口:("/api/hotBooks")
方法:GET

高分书籍列表接口:("/api/highBooks")
方法:GET

书籍评分接口




```



```
Java ArrayList按对象的一个属性排序(https://www.cnblogs.com/yumiaomiao/p/7717676.html)
```

```
用户管理包括注册功能和登录功能。
图书推荐功能是系统的核心功能，系统随机推荐一些图书给新用户，用户根据自己个人喜好对图书进行评分，然后系统根据用户评分通过大数据分析算法计算出用户可能最感兴趣的图书推荐给用户，此后随着用户查阅的图书评分实时更新推荐模型。
图书收藏功能包括图书管理、分类搜索等功能，图书排行榜根据用户收藏图书次数动态呈现收藏次数最多的前五十本图书。

用户管理模块，完成

离线推荐模块，使用调度程序每隔一段时间运行一次，将离线推荐的结果写入书籍推荐列表BookRecs和用户推荐列表UserRecs中，用户登录后可以通过userId查询到该用户的推荐列表并进行展示（会出现冷启动问题，使用热门推荐列表填充）

实时推荐模块，...进入书籍详情页面可根据BookRecs获取该书籍的推荐列表

用户图书管理模块，书籍评分记录，书籍收藏，书籍浏览记录



全局检索模块，ElasticSearch
```

首页展示：

1.最热书籍

2.评分最高书籍

3.收藏排行榜(暂无)

书籍详情页展示：

1.实时推荐结果（书籍推荐列表）（用户评分后实时刷新该列表）

2.猜你喜欢（用户推荐列表）



```
待完成
1.接入redis
2.log文件的监控
```

虚拟机创建共享文件夹（https://blog.csdn.net/qq648483997/article/details/88640936）

共享文件夹消失：vmhgfs-fuse .host:/ /mnt/hgfs



```
a1.sources = r1
a1.sinks = k1
a1.channels = c1

a1.sources.r1.type = netcat
a1.sources.r1.bind = localhost
a1.sources.r1.port = 44444

a1.sinks.k1.type = logger

a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100

a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1 
./bin/flume-ng agent --conf ./conf/ -f ./conf/log-kafka.properties -n a1 -Dflume.root.logger=INFO,console



b1.sources = r1
b1.sinks = k1
b1.channels = c1

b1.sources.r1.type = exec
b1.sources.r1.command = tail -F /home/wan/1.log

b1.sinks.k1.type = logger

b1.channels.c1.type = memory
b1.channels.c1.capacity = 1000
b1.channels.c1.transactionCapacity = 100

b1.sources.r1.channels = c1
b1.sinks.k1.channel = c1


//标准配置
# example.conf: A single-node Flume configuration

# Name the components on this agent
a1.sources = r1
a1.sinks = k1
a1.channels = c1

# Describe/configure the source
a1.sources.r1.type = exec
a1.sources.r1.command = tail -F /var/log/secure

# Describe the sink
a1.sinks.k1.type = org.apache.flume.sink.kafka.KafkaSink
a1.sinks.k1.kafka.topic = test
a1.sinks.k1.kafka.bootstrap.servers = wan:9092
a1.sinks.k1.kafka.flumeBatchSize = 20
a1.sinks.k1.kafka.producer.acks = 1
a1.sinks.k1.kafka.producer.linger.ms = 1
a1.sinks.k1.kafka.producer.compression.type = snappy

# Use a channel which buffers events in memory
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100

# Bind the source and sink to the channel
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1





```

```
2020-2-3
redis基础
java redis api
集群shell脚本
```

```
实时推荐的逻辑：
1.用户登录后：
	若首次登录用户，无评分记录，则通过该书籍的id获取商品推荐列表
	若已有评分记录，则登录之后将该用户对应的评分记录加载到redis，当有新的评分出现时，将新的评分记录
	
	
	
案例：
	用户id：9
	评分记录：......
	商品推荐列表
	
```

```
目前存在的问题：虚拟机挂载宿主机中的日志文件无法监听，tail: cannot determine location of ...
解决方案，将项目打包到虚拟机运行
跑通了
现在就是redis了
redis问题：
1.修改bind 0.0.0.0 不然外部机器无法连接
2.protected-mode no 保护模式

```



- [ ] 后端默认登录拦截器
- [ ] yarn
- [x] vue dev-tool

```
1.elasticsearch
2.echarts
3.docker
```

```
1.将所有记录加载进redis
2.先不管平均评分记录，不管多次评分，写好接口跑通再说
3.用户登录成功后将其对应的评分记录加载进redis
4.有的书籍没有推荐列表，会报错
```

```
数据的处理
azkaban的使用(OK)
```

### [IDEA自定义方法注释](https://blog.csdn.net/liqing0013/article/details/84104419)

### [IDEA设置格式化代码插件（google-java-format，save actions）](https://blog.csdn.net/qq_36191137/article/details/92550747)

Alibaba代码规范...

[IDEA快捷键一](https://www.cnblogs.com/zdj-/p/8515360.html)

[IDEA快捷键二](https://blog.csdn.net/qq_38963960/article/details/89552704)