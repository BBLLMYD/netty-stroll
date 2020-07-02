package com.skr.signal.base.rpc.letter;

import com.skr.signal.base.rpc.letter.serialize.SerializableFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 * @author mqw
 * @create 2020-06-22-13:21
 */
@AllArgsConstructor
public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> genericClass;

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) {
        if (genericClass.isInstance(in)) {
            byte[] data = SerializableFactory.getSingleInstance().serialize(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
