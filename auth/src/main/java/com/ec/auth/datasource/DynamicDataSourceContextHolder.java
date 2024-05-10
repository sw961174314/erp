package com.ec.auth.datasource;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据源切换处理
 */
@Slf4j
public class DynamicDataSourceContextHolder {
    // 存储当前线程的数据源键值
    private static final ThreadLocal<String> db = new ThreadLocal<>();

    // 获取当前线程的数据源键值
    public static String getDataSourceKey() {
        return db.get();
    }

    // 设置当前线程的数据源键值
    public static void setDataSourceKey(String key) {
        db.set(key);
    }

    // 清除当前线程的数据源键值
    public static void clearDataSourceKey() {
        db.remove();
    }
}
