package com.ec.auth.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import com.ec.common.utils.ServletUtils;

/**
 * 服务相关配置
 */
@Component
public class ServerConfig {
    /**
     * 获取HTTP请求的域名
     * @param request
     * @return
     */
    public static String getDomain(HttpServletRequest request) {
        // 获取请求的url
        StringBuffer url = request.getRequestURL();
        // 获取请求的上下文路径
        String contextPath = request.getServletContext().getContextPath();
        // url.delete(url.length() - request.getRequestURI().length(), url.length()) 删除url中的请求url部分 只保留域名部分
        return url.delete(url.length() - request.getRequestURI().length(), url.length()).append(contextPath).toString();
    }

    /**
     * 获取完整的请求路径，包括：域名，端口，上下文访问路径
     * @return 服务地址
     */
    public String getUrl() {
        HttpServletRequest request = ServletUtils.getRequest();
        return getDomain(request);
    }
}
