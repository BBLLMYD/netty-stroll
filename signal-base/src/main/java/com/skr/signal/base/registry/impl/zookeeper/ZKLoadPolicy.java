package com.skr.signal.base.registry.impl.zookeeper;/**
 * @author mqw
 * @create 2020-06-25-15:43
 */

import com.skr.signal.base.registry.LoadPolicy;

import java.util.List;

/**
 * @auther mqw
 * @date 2020-06-25
 **/
public class ZKLoadPolicy implements LoadPolicy {

    @Override
    public String filterServices(List<String> services) {
        return null;
    }
}
