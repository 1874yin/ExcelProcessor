package com.spzx.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.spzx.mapper.ProductMapper;
import com.spzx.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Slf4j
public class ExcelListener implements ReadListener<Product> {

    private static final int BATCH_SIZE = 10000;             // 每1000条执行一次
    private List<Product> batchList = new ArrayList<>();    // 记录缓存

    // 该 Listener 不由 Spring 管理，因此 Bean 需要手动传入构造器中
    private final ExecutorService executorService;
    private final SqlSessionFactory sqlSessionFactory;


    public ExcelListener(ExecutorService executorService, SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.executorService = executorService;
    }

    @Override
    public void invoke(Product product, AnalysisContext analysisContext) {
//        log.info("解析到数据：{}", JSON.toJSONString(product));
        batchList.add(product);
        if (batchList.size() >= BATCH_SIZE) {
            insertToDbThreadPool();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (!batchList.isEmpty()) {
            insertToDbThreadPool();
        }
        log.info("------- 解析结束 -------");
    }

    private void insertToDbThreadPool() {
        ArrayList<Product> newList = new ArrayList<>(batchList);
        batchList.clear();
        executorService.submit(() -> {

            SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
            try {
                ProductMapper mapper = sqlSession.getMapper(ProductMapper.class);
                for (Product product : newList) {
                    mapper.insert(product);
                }
                sqlSession.commit();
            } catch (Exception e) {
                sqlSession.rollback();
            } finally {
                sqlSession.close();
            }
        });
    }


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
}
