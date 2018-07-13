package com.shiro.config;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.filter.authz.SslFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.*;

/**
 * @author:pms
 * @createtime:2018/6/13-15:55
 * @qq 718195578
 * @since 1.0
 */
@Configuration
public class SecurityConfig {

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        /**
         *URL表达式说明
         1、URL目录是基于HttpServletRequest.getContextPath()此目录设置
         2、URL可使用通配符，**代表任意子目录
         3、Shiro验证URL时，URL匹配成功便不再继续匹配查找。所以要注意配置文件中的URL顺序，尤其在使用通配符时。
         */

        shiroFilter.setLoginUrl("/login");
        shiroFilter.setSuccessUrl("/index");
        shiroFilter.setUnauthorizedUrl("/error");

        /**
         *Filter Chain定义说明
         1、一个URL可以配置多个Filter，使用逗号分隔
         2、当设置多个过滤器时，全部验证通过，才视为通过
         3、部分过滤器可指定参数，如perms，roles
         */
        Map<String, Filter> filters = new HashMap<>();
        filters.put("user", new UserFilter());

        FormAuthenticationFilter formAuth = new FormAuthenticationFilter();
        formAuth.setEnabled(true);
        formAuth.setName("formAuth");
        formAuth.setRememberMeParam("rememberMe");
        filters.put("formAuth", formAuth);

        filters.put("ssl",sslFilter());
        shiroFilter.setFilters(filters);

        Map<String, String> filterChainMap = filterChainDefinitionMap();
        shiroFilter.setFilterChainDefinitionMap(filterChainMap);
        shiroFilter.setSecurityManager(securityManager());

        return shiroFilter;
    }

    private Map<String, String> filterChainDefinitionMap() {
        Map<String, String> filterChainMap = new HashMap<>();
        //authc 需要登录校验
        // logout：注销拦截器
        //用户拦截器，用户已经身份验证
        filterChainMap.put("/login", "anon,ssl");
        filterChainMap.put("/index", "user,ssl");
        //用户身份验证通过或RememberMe登录
        //user 拦截器的名称
        filterChainMap.put("/**", "user,ssl");
        filterChainMap.put("/checkLogin", "anon");
        //匿名拦截器，即不需要登录即可访问；
        filterChainMap.put("/resource/**", "anon");
        return filterChainMap;
    }

    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //cacheManager
        securityManager.setCacheManager(ehCacheManager());
        //sessionManager
        securityManager.setSessionManager(sessionManager());
        //rememberMeManager
        securityManager.setRememberMeManager(rememberMeManager());
        //自定义Realm
        Collection<Realm> realms = new ArrayList<>();
        realms.add(tokenRealm());
        securityManager.setRealms(realms);
        return securityManager;
    }

    @Bean
    public SslFilter sslFilter(){
        SslFilter sslfilter = new SslFilter();
        sslfilter.setEnabled(true);
        sslfilter.setLoginUrl("/login");
        sslfilter.setName("ssl");
        sslfilter.setPort(8443);
        return sslfilter;
    }

    @Bean
    public EhCacheManager ehCacheManager() {
        EhCacheManager ehCacheManager = new EhCacheManager();
        ehCacheManager.setCacheManagerConfigFile("classpath:ehcache.xml");
        return ehCacheManager;
    }

    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        //session filter
        sessionManager.setSessionListeners(sessionListener());
        sessionManager.setGlobalSessionTimeout(30 * 60 * 1000); // 30分钟

        //是否定期检查Session的有效性
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        return sessionManager;
    }

    public List<SessionListener> sessionListener() {
        List<SessionListener> listeners = new ArrayList<>();
        listeners.add(new ShiroSessionListener());
        return listeners;
    }

    @Bean
    public RememberMeManager rememberMeManager() {
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        //cipherKey是加密rememberMe Cookie的密钥；默认AES算法；
        String cipherKey = "CookieCipherKey";
        rememberMeManager.setCipherKey(cipherKey.getBytes());
        rememberMeManager.setCookie(simpleCookie());
        return rememberMeManager;

    }

    @Bean
    public SimpleCookie simpleCookie() {
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setName("rememberMe");
        simpleCookie.setHttpOnly(true);
        //30天/-1浏览器关闭时Cookie失效
        simpleCookie.setMaxAge(30 * 24 * 60 * 60);
        return simpleCookie;
    }

    @Bean
    public ShiroRealm tokenRealm() {
        ShiroRealm shiroRealm = new ShiroRealm();
        shiroRealm.setCachingEnabled(true);
        shiroRealm.setAuthenticationCachingEnabled(true);
        shiroRealm.setAuthenticationCacheName("authenticationCacheName");
        shiroRealm.setAuthorizationCachingEnabled(true);
        shiroRealm.setAuthorizationCacheName("authorizationCacheName");
        return shiroRealm;
    }

}
