package com.skr.signal.base.registry.impl.load;/**
 * @author mqw
 * @create 2020-06-25-15:43
 */

import com.skr.signal.base.registry.LoadPolicy;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @auther mqw
 * @date 2020-06-25
 **/
public class RandomLoadPolicy implements LoadPolicy {

    @Override
    public String filterServices(List<String> services) {
        return services.get(ThreadLocalRandom.current().nextInt(services.size()));
    }
}
