package com.ec.auth.web.service;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.ec.common.constant.Constants;
import com.ec.common.core.domain.entity.SysUser;
import com.ec.common.core.domain.model.LoginUser;
import com.ec.common.core.redis.RedisCache;
import com.ec.common.exception.ServiceException;
import com.ec.common.exception.user.CaptchaException;
import com.ec.common.exception.user.CaptchaExpireException;
import com.ec.common.exception.user.UserPasswordNotMatchException;
import com.ec.common.utils.DateUtils;
import com.ec.common.utils.MessageUtils;
import com.ec.common.utils.ServletUtils;
import com.ec.common.utils.ip.IpUtils;
import com.ec.auth.manager.AsyncManager;
import com.ec.auth.manager.factory.AsyncFactory;
import com.ec.sys.service.ISysConfigService;
import com.ec.sys.service.ISysUserService;

/**
 * 登录校验方法
 */
@Component
public class SysLoginService {
    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    /**
     * 登录验证
     * @param tenant   租户ID
     * @param username 用户名
     * @param password 密码
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public String login(String tenant, String username, String password, String code, String uuid) {
        // 检验验证码是否正确
        validateCaptcha(username, code, uuid);
        // 用户验证
        Authentication authentication = null;
        try {
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername(根据用户名加载用户信息)
            // 进行用户认证
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(tenant, username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(tenant, username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new ServiceException(e.getMessage());
            }
        }
        // 记录登录成功的信息
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(tenant, username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        // 获取已认证的用户信息
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        // 设置租户信息
        loginUser.setTenant(tenant);
        // 记录登录信息
        recordLoginInfo(loginUser.getUserId());
        // 生成token
        return tokenService.createToken(loginUser);
    }

    /**
     * 校验验证码
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid) {
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        // 从Redis缓存中获取与该键关联的验证码值
        String captcha = redisCache.getCacheObject(verifyKey);
        // 删除Redis缓存中的该键
        redisCache.deleteObject(verifyKey);
        // 如果captcha为null 表示验证码已过期
        if (captcha == null) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
            throw new CaptchaExpireException();
        }
        // 如果code与captcha不相等 表示验证码输入错误
        if (!code.equalsIgnoreCase(captcha)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
            throw new CaptchaException();
        }
    }

    /**
     * 记录登录信息
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        // 获取当前请求的IP地址
        sysUser.setLoginIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        // 获取当前日期和时间 并将其设置为sysUser对象的登录日期
        sysUser.setLoginDate(DateUtils.getNowDate());
        // 更新用户数据
        userService.updateUserProfile(sysUser);
    }
}
