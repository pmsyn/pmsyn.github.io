package com.shiro.service.Impl;

import com.shiro.exception.GeneralException;
import com.shiro.mapper.Mapper;
import com.shiro.pojo.User;
import com.shiro.util.MapUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author:pms
 * @createtime:2018/6/19-16:08
 * @qq 718195578
 * @since 1.0
 */
@Service
public class UserServiceImpl {
    @Autowired
    private Mapper mapper;

    public User getUserInfo(String account) throws GeneralException {
        Map<String, Object> userInfo = mapper.getSingleResult("sys_user", "account = '" + account + "'");
        if (userInfo == null)
            return null;
        String userId = MapUtility.getMapStringValue(userInfo,"ID");
        List<Map<String, Object>> userRole = mapper.findList("sys_user_roles", "user_id = '" + userId + "'");
        List<Map<String, Object>> userPermission = mapper.findList("sys_user_permission", "user_id = '" + userId + "'");
        String userName = MapUtility.getMapStringValue(userInfo,"USER_NAME");
        String password = MapUtility.getMapStringValue(userInfo,"PASSWORD");
        account = MapUtility.getMapStringValue(userInfo,"ACCOUNT");
        User user = new User();
        user.setId(userId);
        user.setUserName(userName);
        user.setAccount(account);
        user.setPassword(password);
        user.setRole(userRole);
        user.setPermission(userPermission);
        return user;
    }
}
