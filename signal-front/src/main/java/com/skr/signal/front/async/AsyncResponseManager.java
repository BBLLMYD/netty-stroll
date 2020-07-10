package com.skr.signal.front.async;


import com.skr.signal.front.exception.ServiceException;
import com.skr.signal.front.initialize.NamedThreadFactory;
import com.sun.org.apache.regexp.internal.RE;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 将下游的阻塞控制在指定线程池内
 *
 * @author mqw
 * @create 2020-06-10-11:43
 */
@Slf4j
public class AsyncResponseManager {

    private static int PARALLELISM = Runtime.getRuntime().availableProcessors() * 2;

    private static ExecutorService pool = new ThreadPoolExecutor(PARALLELISM, PARALLELISM,
            10L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(7),
            new NamedThreadFactory("asyncResponse-pool-%d"));

    public static void asyncResponse(AsyncHandleUnit asyncHandleUnit) {
        CompletableFuture.runAsync(asyncHandleUnit,pool)
                .whenCompleteAsync((aVoid, throwable) -> log.info("req over: {}",asyncHandleUnit))
                .exceptionally(throwable -> {
                    log.error("req error: {} -> {}",asyncHandleUnit,throwable);
                    return null;
                });

//        Future<Boolean> future = pool.submit(asyncHandleUnit);
//        boolean success = false;
//        try {
//            success = future.get();
//        }catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        } catch (ExecutionException e) {
//            Throwable cause = e.getCause();
//            if(cause instanceof ServiceException){
//                throw (ServiceException)cause;
//            }else {
//                throw new Exception(cause);
//            }
//        }finally {
//            log.info("asyncHandleUnit:{},respSuccess:{}",asyncHandleUnit,success);
//        }
    }

}
