package com.shiro.config;

import com.shiro.exception.GeneralException;
import com.shiro.pojo.User;
import com.shiro.service.Impl.UserServiceImpl;
import com.shiro.util.MapUtility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author:pms
 * @createtime:2018/6/14-11:36
 * @qq 718195578
 * @since 1.0
 */

public class ShiroRealm extends AuthorizingRealm {
    private static final Log LOG = LogFactory.getLog(ShiroRealm.class);
    private static final String SALT = "ShiroPasswordSalt";
    @Autowired
    private UserServiceImpl userService;

    /**
     * 授权
     **/
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String account = principalCollection.getPrimaryPrincipal().toString();
        User user = null;
        try {
            user = userService.getUserInfo(account);
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> roles = user.getRole();
        Set<String> roleNames = new HashSet<String>();
        for (Map<String, Object> role : roles) {
            roleNames.add(MapUtility.getMapStringValue(role, "name"));
        }
        List<Map<String, Object>> userPermissions = user.getPermission();
        Set<String> permissions = new HashSet<String>();
        for (Map<String, Object> p : userPermissions) {
            permissions.add(MapUtility.getMapStringValue(p, "name"));
        }
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleNames);
        info.setStringPermissions(permissions);
        return info;
    }

    /**
     * 认证 验证用户名和密码
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws
            AuthenticationException {
        User user = null;
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        String password = String.valueOf(token.getPassword());
        LOG.info("------login username:" + username + ",password:" + password);
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new UnknownAccountException("用户名或密码为空，请重新输入！");
        }
        try {
            user = userService.getUserInfo(username);
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        if (user == null) {
            throw new IncorrectCredentialsException("用户名或密码错误，请重新输入!");
        }
        String localUserName = user.getUserName();
        String localPassword = user.getPassword();
        String md5 = new Md5Hash(password, SALT).toString();
        if (username.equals(localUserName) && localPassword.equals(md5)) {
            return new SimpleAuthenticationInfo(username, password, getName());
        } else {
            throw new AuthenticationException("用户名或密码错误，请重新输入!");
        }
    }
}
