## 实测结果

数据量级：500000条

### 单条插入
影响最大因素：数据库连接池线程数量配置
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
```
当线程池最大线程数量配置较高时，多线程插入与单线程插入速度差异不大。
当线程池最大线程数量配置较低时，单线程插入所需时间提高了2倍。

### Batch 插入
数据库线程池最大线程配置为 10。

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
单线程下插入时间约为 16秒，多线程下插入时间约5秒。
