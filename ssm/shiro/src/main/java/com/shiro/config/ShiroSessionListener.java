package com.shiro.config;

import com.shiro.mapper.Mapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.crypto.Data;
import java.util.Date;

/**
 * @author:pms
 * @createtime:2018/6/14-15:37
 * @qq 718195578
 * @since 1.0
 */
public class ShiroSessionListener implements SessionListener {
    @Autowired
    private Mapper mapper;

    @Override
    public void onStart(Session session) {//会话创建时触发
        Subject subject = SecurityUtils.getSubject();
        String username = (String) subject.getPrincipal();
        String host = session.getHost();
        Date startTime = session.getStartTimestamp();
        Date lastAccessTime = session.getLastAccessTime();
        String type = "login";

    }

    @Override
    public void onStop(Session session) {//会话过期时触发
        String type = "timeout";
    }

    @Override
    public void onExpiration(Session session) {//退出-会话过期时触发
        String type = "logout";
    }
}
