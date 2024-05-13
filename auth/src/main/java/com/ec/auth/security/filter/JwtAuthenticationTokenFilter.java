package com.ec.auth.security.filter;

import java.io.IOException;
import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.ec.common.core.domain.model.LoginUser;
import com.ec.common.utils.SecurityUtils;
import com.ec.common.utils.StringUtils;
import com.ec.auth.web.service.TokenService;

/**
 * token过滤器 验证token有效性
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService tokenService;

    /**
     * 在请求处理之前进行身份验证和授权
     * @param request
     * @param response
     * @param chain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // 从请求中获取登录用户信息
        LoginUser loginUser = tokenService.getLoginUser(request);
        // 如果登录用户不为空且当前上下文中没有认证信息 则需要进行身份验证
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNull(SecurityUtils.getAuthentication())) {
            // 获取请求头中的tenant信息
            String tenant = request.getHeader("tenant");
            // 判断tenant是否与令牌信息一致
            if (!loginUser.getTenant().equalsIgnoreCase(tenant)) {
                throw new AuthenticationException("令牌无效");
            }
            // 验证令牌有效性
            tokenService.verifyToken(loginUser);
            // 创建认证令牌对象
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            // 设置认证令牌的详细信息
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 将认证令牌设置到安全上下文中
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        // 继续执行过滤器链中的下一个过滤器或目标资源
        chain.doFilter(request, response);
    }
}
