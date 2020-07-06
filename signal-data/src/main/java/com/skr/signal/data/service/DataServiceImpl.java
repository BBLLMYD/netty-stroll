package com.skr.signal.data.service;

import com.skr.signal.common.service.DataService;

/**
 * @author : mengqingwen 
 * create at:  2020-07-06
 * */
public class DataServiceImpl implements DataService {

    @Override
    public String data(String args) {
        return "data" + System.currentTimeMillis();
    }

}