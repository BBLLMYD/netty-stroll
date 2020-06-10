package com.skr.signal.front.initialize;

import com.skr.signal.front.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 将下游的阻塞控制在指定线程池内
 *
 * @author mqw
 * @create 2020-06-10-11:43
 */
@Slf4j
public class AsyncResponseManager {

    private static int PARALLELISM = Runtime.getRuntime().availableProcessors() * 2;

    private static ExecutorService pool = new ThreadPoolExecutor(PARALLELISM,
            PARALLELISM + 2,
            10L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(7),
                    new NamedThreadFactory("asyncResponse-pool-%d"));

    public static void asyncResponse(AsyncHandleUnit asyncHandleUnit) throws Exception {
        Future<Boolean> future = pool.submit(asyncHandleUnit);
        boolean success = false;
        try {
            success = future.get();
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if(cause instanceof ServiceException){
                throw (ServiceException)cause;
            }else {
                throw new Exception(cause);
            }
        }finally {
            log.info("asyncHandleUnit:{},respSuccess:{}",asyncHandleUnit,success);
        }
    }

}
