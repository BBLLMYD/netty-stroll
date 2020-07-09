package com.skr.signal.front.test;


import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author mqw
 * @create 2020-05-30-11:18 上午
 */
public class Main {

    public static void main(String[] args) throws UnknownHostException {
        String ip = InetAddress.getLocalHost().getHostAddress();
        System.out.println(ip);

    }

}
