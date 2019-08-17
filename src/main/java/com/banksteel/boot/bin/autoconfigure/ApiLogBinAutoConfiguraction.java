package com.banksteel.boot.bin.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 应用之间网络请求代价的配置类
 *
 * @author 杨新伦
 * @date 2018-11-05
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = {"com.banksteel.bone.api.log"})
public class ApiLogBinAutoConfiguraction {
}
