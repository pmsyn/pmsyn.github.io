package com.shiro.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author pms
 * @create 2017/12/10 14:28
 * @since 1.0
 */
@Configuration
@ComponentScan(basePackages = {"com.shiro.config"},
        excludeFilters = {@ComponentScan.Filter(type= FilterType.ANNOTATION,value= EnableWebMvc.class)})
public class RootConfig {
}
