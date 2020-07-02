package com.skr.signal.base.rpc.letter.serialize;

import com.skr.signal.base.rpc.letter.serialize.impl.ProtoStuffSerializer;
import com.skr.signal.common.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @auther mqw
 * @date 2020-07-02
 **/
public class SerializableFactory {

    static SignalSerializable serializable = null;

    public static SignalSerializable getSingleInstance(){
        if(serializable == null){
            synchronized (SerializableFactory.class){
                if(serializable == null){
                    String property = PropertiesUtil.newInstance("config-rpc.properties").getProperties().getProperty("serializer.impl");
                    if(StringUtils.isNotEmpty(property)){
                        try {
                            serializable = (SignalSerializable)Class.forName(property).newInstance();
                        } catch (Exception e) {
                            throw new RuntimeException("序列化实现加载异常",e);
                        }
                    }else {
                        serializable = new ProtoStuffSerializer();
                    }
                }
            }
        }
        return serializable;
    }

}
