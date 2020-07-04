package com.skr.signal.base.rpc.letter.serialize;

/**
 * @auther mqw
 * @date 2020-07-02
 **/
public interface SignalSerializable {

    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] data, Class<T> cls);

}
