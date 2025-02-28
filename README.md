## Mybatis Batch模式批量插入以及多线程性能对比

数据量级：500000条

#### 普通插入
该模式下，不使用 mybatis 的批量插入。

##### 小连接池、单线程
数据库连接池最大线程数：10

用时：14.7 秒

##### 大连接池、单线程
数据库连接池最大线程数：50
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 50
```
用时：14.8 秒

##### 小连接池、多线程
数据库连接池最大线程数：10

用时：4.4 秒

##### 大连接池、多线程

数据库连接池最大线程数：50

用时：4.5 秒

**P.S.**多线程下插入任务为异步提交，主线程结束时数据库插入线程还没结束，所以计时存在误差

##### 结论

普通模式下，连接池最大线程数配置对插入速度影响不大，多线程插入显著提高插入速度。

#### Batch 插入
使用 mybatis 的 Batch 模式。

在 yml 文件中开启 batch 插入：
```yaml
spring:
  datasource:
    ## 必须在 URL 中加上 rewriteBatchedStatements=true，才能开启 Batch 插入
    url: jdbc:mysql://localhost:3306/excel?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&rewriteBatchedStatements=true
```

使用 `ExecutorType.BATCH` 参数获取 SqlSession:

```java
public void insertToDbSingleThread() {
    SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
    try {
        ProductMapper mapper = sqlSession.getMapper(ProductMapper.class);
        for (Product product : batchList) {
            mapper.insert(product);
        }
        sqlSession.commit();
    } catch (Exception e) {
        sqlSession.rollback();
    } finally {
        sqlSession.close();
    }
    batchList.clear();
}
```

##### 小连接池、单线程

数据库连接池最大线程数：10

用时：14.5 秒

##### 大连接池、单线程

数据库连接池最大线程数：50

用时：14.1 秒

##### 小连接池、多线程

数据库连接池最大线程数：10

用时：4.4 秒

##### 大连接池、多线程

数据库连接池最大线程数：50

用时：4.4 秒

##### 结论
batch 模式下，连接池最大线程数配置对插入速度的影响依然不大，多线程插入的影响明显。

#### 总结

Batch 模式以及数据库连接池最大线程数配置对插入速度的影响不明显，而无论是 Simple 模式或者 Batch 模式，多线程对插入速度的影响都是显著的。