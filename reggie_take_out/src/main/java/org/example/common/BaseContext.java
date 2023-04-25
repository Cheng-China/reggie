package org.example.common;

/**
 * 用于完成线程间共享数据
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrent(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrent() {
        return threadLocal.get();
    }
}
