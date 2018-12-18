package com.mlh.resp;
import io.netty.buffer.ByteBuf;

/**
 * @author: linghan.ma
 * @DATE: 2018/12/17
 * @description:
 */

public interface IRedisOutput {

    public void encode(ByteBuf buf);

}
