package com.ec.auth.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.ec.common.core.domain.entity.SysUser;
import com.ec.common.core.domain.model.LoginUser;
import com.ec.common.enums.UserStatus;
import com.ec.common.exception.ServiceException;
import com.ec.common.utils.StringUtils;
import com.ec.sys.service.ISysUserService;

/**
 * 用户验证处理
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private ISysUserService userService;

    @Autowired
    private SysPermissionService permissionService;

    /**
     * 根据用户名加载用户详细信息
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                SysUser user = userService.selectUserByUserName(username);
                if (StringUtils.isNull(user)) {
                    log.info("登录用户：{} 不存在.", username);
                    throw new ServiceException("登录用户：" + username + " 不存在");
                } else if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
                    log.info("登录用户：{} 已被删除.", username);
                    throw new ServiceException("对不起，您的账号：" + username + " 已被删除");
                } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
                    log.info("登录用户：{} 已被停用.", username);
            throw new ServiceException("对不起，您的账号：" + username + " 已停用");
        }
        return createLoginUser(user);
    }

    public UserDetails createLoginUser(SysUser user) {
        return new LoginUser(user.getUserId(), user.getDeptId(), user, permissionService.getMenuPermission(user));
    }
}
