package com.spzx.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.fastjson.JSON;
import com.spzx.mapper.ProductMapper;
import com.spzx.model.Product;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Slf4j
public class ExcelListener implements ReadListener<Product> {

    private static final int BATCH_SIZE = 1000;             // 每1000条执行一次
    private List<Product> batchList = new ArrayList<>();    // 记录缓存

    // 该 Listener 不由 Spring 管理，因此 Bean 需要手动传入构造器中
    private final ExecutorService executorService;
    private final ProductMapper mapper;


    public ExcelListener(ExecutorService executorService, ProductMapper mapper) {
        this.executorService = executorService;
        this.mapper = mapper;
    }

    @Override
    public void invoke(Product product, AnalysisContext analysisContext) {
//        log.info("解析到数据：{}", JSON.toJSONString(product));
        batchList.add(product);
        if (batchList.size() >= BATCH_SIZE) {
            ArrayList<Product> newList = new ArrayList<>(batchList);
            submitToThreadPool(newList);
            batchList.clear();
        }


    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (!batchList.isEmpty()) {
            submitToThreadPool(batchList);
        }
        log.info("------- 解析结束 -------");
    }

    private void submitToThreadPool(List<Product> list) {
        executorService.submit(() -> {
            log.info("线程{} 正在执行", Thread.currentThread().getName());
            log.info("数据:{}", JSON.toJSONString(list));
            mapper.insertBatchSomeColumn(list);
            batchList.clear();
        });
//        mapper.insertBatchSomeColumn(list);
//        list.clear();
    }
}
