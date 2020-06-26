package com.skr.signal.base.registry;/**
 * @author mqw
 * @create 2020-06-25-15:42
 */

import java.util.List;

/**
 * @auther mqw
 * @date 2020-06-25
 **/
public interface LoadPolicy {

    String filterServices(List<String> services);

}
