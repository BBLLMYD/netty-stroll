package com.skr.signal.front.initialize;

import java.util.Locale;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author mqw
 * @create 2020-06-10-13:47
 */
public class NamedThreadFactory implements ThreadFactory {
    private static final AtomicInteger threadPoolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private static final String NAME_PATTERN = "%s-%d-thread";
    private final String threadNamePrefix;

    public NamedThreadFactory(String threadNamePrefix) {
        SecurityManager s = System.getSecurityManager();
        this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.threadNamePrefix = String.format(Locale.ROOT, NAME_PATTERN, checkPrefix(threadNamePrefix), threadPoolNumber.getAndIncrement());
    }

    private static String checkPrefix(String prefix) {
        return prefix != null && prefix.length() != 0 ? prefix : "NamedPool";
    }

    @Override
    public Thread newThread(Runnable r) {
        String threadName = String.format(Locale.ROOT, "%s-%d", this.threadNamePrefix, this.threadNumber.getAndIncrement());
        Thread t = new Thread(this.group, r, threadName, 0L);
        t.setDaemon(false);
        t.setPriority(5);
        return t;
    }
}
