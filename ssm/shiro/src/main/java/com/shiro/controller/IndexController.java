package com.shiro.controller;

import com.shiro.pojo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author pms
 * @time 2017/12/10 13:54
 * @since 1.0
 */

@Controller
public class IndexController {
    private static final Log log = LogFactory.getLog(IndexController.class);
    private static final String LOGIN_URL = "redirect:/login";
    private static final String INDEX_URL = "redirect:/index";

    @RequestMapping({"/", "/login"})
    public String login(ModelMap model) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return INDEX_URL;
        }
        return "login";
    }

    @RequestMapping("/checkLogin")
    public String checkLogin(User user, ModelMap model, @Param("rememberMe") String rememberMe) {
        try {
            String username = user.getUserName();
            String password = user.getPassword();
            if ("".equals(username) || "".equals(password))
                return LOGIN_URL;
            //获取当前的用户，然后通过调用login方法提交认证
            //isAuthenticated/isRemembered是互斥
            //的，如果其中一个返回true，另一个返回false。
            Subject subject = SecurityUtils.getSubject();
            if (subject.isAuthenticated() || subject.isRemembered())
                return INDEX_URL;
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            if ("true".equals(rememberMe)) {
                token.setRememberMe(true);
            }
            // 如果login方法执行完毕且没有抛出任何异常信息，那么便认为用户认证通过。
            // 之后在应用程序任意地方调用SecurityUtils.getSubject() 都可以获取到当前认证通过的用户实例，
            // 使用subject.isAuthenticated()判断用户是否已验证都将返回TRUE.;
            //执行登录验证 调用自定义realm
            subject.login(token);
        } catch (Exception e) {
            log.error(e.getMessage());
            model.addAttribute("msg", "用户名或密码错误！");
            return this.login(model);
        }

        return INDEX_URL;
    }

    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    @RequestMapping("/logout")
    public String logOut(HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return LOGIN_URL;
    }

    @RequestMapping("/error")
    public String error(ModelMap modelMap) {
        return "error";
    }

}
