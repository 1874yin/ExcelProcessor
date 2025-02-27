package com.spzx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {
    @Bean(name = "importExecutor")
    public ExecutorService importExecutor() {
        return new ThreadPoolExecutor(
                10, // 核心线程数
                50, // 最大线程数
                60L, // 空闲线程存活时间
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000) // 任务队列容量
        );
    }
}
