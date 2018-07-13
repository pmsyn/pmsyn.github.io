package com.shiro.config;

/**
 * @author pms
 * @create 2017/12/10 15:48
 * @since 1.0
 */
public class WebThymeleafConfig {
    /**
     * Thymeleaf视图解析器
     * @param templateEngine
     * @return
     */
    /*@Bean
    public ViewResolver viewResolver(SpringTemplateEngine templateEngine) {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        return viewResolver;
    }

    *//**
     * 模板引擎
     * @param templateResolver
     * @return
     *//*
    @Bean
    public TemplateEngine templateEngine(SpringResourceTemplateResolver templateResolver){
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return  templateEngine;

    }

    *//**
     * 模板解析器
     * @return
     *//*
    @Bean
    public SpringResourceTemplateResolver templateResolver(){
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("/WEB-INF/tmplates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        return templateResolver;
    }*/
}
