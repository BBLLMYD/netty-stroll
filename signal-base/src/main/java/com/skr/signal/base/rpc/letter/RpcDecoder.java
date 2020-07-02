package com.skr.signal.base.rpc.letter;

import com.skr.signal.base.rpc.letter.serialize.SerializableFactory;
import com.sun.org.apache.xml.internal.serialize.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @author mqw
 * @create 2020-06-22-13:16
 */
@AllArgsConstructor
public class RpcDecoder  extends ByteToMessageDecoder {

    private Class<?> genericClass;

    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Object obj = SerializableFactory.getSingleInstance().deserialize(data, genericClass);
        out.add(obj);
    }
}