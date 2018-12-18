package com.mlh.resp;

/**
 * @author: linghan.ma
 * @DATE: 2018/12/17
 * @description:作者：老錢 链接：https://juejin.im/post/5aaf1e0af265da2381556c0e
 * 来源：掘金
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 */

import java.util.List;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

@Sharable
public class RedisOutputEncoder extends MessageToMessageEncoder<IRedisOutput> {
    /**
     * 因为解码器对象是无状态的，所以它可以被channel共享。解码器的实现非常简单，
     * 就是分配一个ByteBuf，然后将将消息输出对象序列化的字节数组塞到ByteBuf中输出就可以了。
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, IRedisOutput msg, List<Object> out) throws Exception {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer();
        msg.encode(buf);
        out.add(buf);
    }

}

