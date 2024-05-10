package com.ec.auth.config;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.DispatcherType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ec.common.filter.RepeatableFilter;
import com.ec.common.filter.XssFilter;
import com.ec.common.utils.StringUtils;

/**
 * Filter配置  过滤器配置
 */
@Configuration
public class FilterConfig {
    // 排除链接（多个用逗号分隔）
    @Value("${xss.excludes}")
    private String excludes;

    // 匹配链接 也就是这个里面的，要防止xss攻击
    @Value("${xss.urlPatterns}")
    private String urlPatterns;

    /**
     * 注册XSS过滤器
     * @return
     */
    @Bean
    @SuppressWarnings({"rawtypes", "unchecked"})
    // 当配置文件中的"xss.enabled"属性值为true时 该方法会被执行
    @ConditionalOnProperty(value = "xss.enabled", havingValue = "true")
    public FilterRegistrationBean xssFilterRegistration() {
        // 创建过滤器的注册器
        FilterRegistrationBean registration = new FilterRegistrationBean();
        // 设置过滤器的调度类型为DispatcherType.REQUEST 表示在浏览器直接请求资源时会执行过滤
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        // 设置具体的过滤器
        registration.setFilter(new XssFilter());
        // 添加需要匹配的url模式 这些url模式将会受到XSS过滤的保护
        registration.addUrlPatterns(StringUtils.split(urlPatterns, ","));
        registration.setName("xssFilter");
        // FilterRegistrationBean.HIGHEST_PRECEDENCE 表这个过滤器在众多过滤器中级别最高，也就是过滤的时候最先执行
        // 设置优先过滤的级别，越小越优先。
        registration.setOrder(FilterRegistrationBean.HIGHEST_PRECEDENCE);
        // initParameters存储初始化参数
        Map<String, String> initParameters = new HashMap<String, String>();
        // 排除链接（多个用逗号分隔）
        // excludes 需要排除的url链接
        initParameters.put("excludes", excludes);
        registration.setInitParameters(initParameters);
        return registration;
    }

    /**
     * 通过FilterRegistrationBean实例注册 该方法能够设置过滤器之间的优先级
     * @return
     */
    @Bean
    @SuppressWarnings({"rawtypes", "unchecked"})
    public FilterRegistrationBean someFilterRegistration() {
        // registration用于注册过滤器
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new RepeatableFilter());
        registration.addUrlPatterns("/*");
        registration.setName("repeatableFilter");
        // Ordered.HIGHEST_PRECEDENCE: 这是最高优先
        // Ordered.LOWEST_PRECEDENCE: 这是最低优先
        registration.setOrder(FilterRegistrationBean.LOWEST_PRECEDENCE);
        return registration;
    }
}
