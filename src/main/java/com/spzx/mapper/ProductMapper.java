package com.spzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    int insertBatchSomeColumn(@Param("list") Collection<Product> entityList);
}
