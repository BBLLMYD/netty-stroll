package com.skr.signal.base.rpc.letter.serialize;/**
 * @author mqw
 * @create 2020-07-02-10:45
 */

import com.skr.signal.common.util.PropertiesUtil;

import java.io.Serializable;


/**
 * @auther mqw
 * @date 2020-07-02
 **/
public interface SignalSerializable {



    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] data, Class<T> cls);

}
