package com.spzx.runner;

import com.alibaba.excel.EasyExcel;
import com.spzx.listener.ExcelListener;
import com.spzx.mapper.ProductMapper;
import com.spzx.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.ExecutorService;

@Component
@Slf4j
public class MyApplicationRunner {

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private ProductMapper mapper;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("开始解析数据");
        String fileName = Files.currentFolder().getPath() + File.separator + "data" + File.separator + "data.xlsx";
        // D:\practice\ExcelProcessor\data\data.xlsx
        EasyExcel.read(fileName, Product.class, new ExcelListener(executorService, mapper)).sheet().doRead();
    }
}
