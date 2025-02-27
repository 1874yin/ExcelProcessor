package com.spzx;

import com.spzx.mapper.ProductMapper;
import com.spzx.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class MapperTest {

    @Autowired
    private ProductMapper mapper;

    @Test
    public void testMapper() {
        List<Product> productList = new ArrayList<>();
        productList.add(new Product(100001, "randomName", 5000));
        mapper.insertBatchSomeColumn(productList);
    }
}
