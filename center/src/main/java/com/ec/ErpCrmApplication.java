package com.ec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 主程序
 * scanBasePackages属性设置为"com"表示扫描以"com"开头的包
 * exclude属性设置为{DataSourceAutoConfiguration.class} 表示排除数据源自动配置
 */
@SpringBootApplication(scanBasePackages = "com", exclude = {DataSourceAutoConfiguration.class})
public class ErpCrmApplication {
    public static void main(String[] args) {
        SpringApplication.run(ErpCrmApplication.class, args);
        System.out.println("ErpCrm启动成功");
    }
}
