package com.skr.signal.front.dto;

import com.google.gson.Gson;
import lombok.Data;

/**
 * @author mqw
 * @create 2020-06-05-11:24
 */
@Data
public class RequestInfo {

    String traceId;
    String businessId;

    String requestKey;

    public static void main(String[] args) {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setTraceId("traceId1");
        requestInfo.setBusinessId("businessId1");
        requestInfo.setRequestKey("requestKey1");
        System.out.println(new Gson().toJson(requestInfo));
    }

}
