package com.skr.signal.front.exception;

import lombok.Data;


/**
 * @author mqw
 * @create 2020-06-07-7:12 下午
 */
@Data
public class ServiceException extends RuntimeException {

    String msg;

    public ServiceException(String msg){
        super(msg);
    }

    public ServiceException(String msg,Throwable throwable){
        super(msg,throwable);
    }

}
