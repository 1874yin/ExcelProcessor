package com.spzx.controller;

import com.alibaba.excel.EasyExcel;
import com.spzx.listener.ExcelListener;
import com.spzx.mapper.ProductMapper;
import com.spzx.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.assertj.core.util.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

@RestController("/excel")
@Slf4j
public class ExcelController {

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;


    @GetMapping("/read")
    public void doExcel() {
        log.info("开始解析数据");
        String fileName = Files.currentFolder().getPath() + File.separator + "data" + File.separator + "data.xlsx";
        // D:\practice\ExcelProcessor\data\data.xlsx
        EasyExcel.read(fileName, Product.class, new ExcelListener(executorService, sqlSessionFactory)).sheet().doRead();
    }
}
