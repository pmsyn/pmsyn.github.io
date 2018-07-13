package com.shiro.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import java.util.EnumSet;

/**
 * @author pms
 * @create 2017/12/10 12:49
 * @since 1.0
 */
public class WebInitializer implements WebApplicationInitializer {
    public void onStartup(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(WebConfig.class);
        context.setServletContext(servletContext);
        context.getEnvironment().setActiveProfiles("dev");

        ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(context));
        servlet.addMapping("/");
        servlet.setLoadOnStartup(1);
        //上传文件配置 限制文件大小，整个请求文件大小，文件写到磁盘
        servlet.setMultipartConfig(new MultipartConfigElement("/tmp/file/uploads", 2048000, 4096000, 0));
        servlet.setAsyncSupported(true);

        FilterRegistration.Dynamic filter = servletContext.addFilter("shiroFilter", new DelegatingFilterProxy());
        // 该值缺省为false,表示生命周期由SpringApplicationContext管理,设置为true则表示由ServletContainer管理
        filter.setInitParameter("targetFilterLifecycle", "true");
        filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType
                .INCLUDE), false, "/*");
    }
}

