package com.mlh.resp;

/**
 * @author: linghan.ma
 * @DATE: 2018/12/17
 * @description:
 */
import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;

public class IntegerOutput implements IRedisOutput {

    private long value;

    public static IntegerOutput ZERO = new IntegerOutput(0);
    public static IntegerOutput ONE = new IntegerOutput(1);

    public IntegerOutput(long value) {
        this.value = value;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeByte(':');
        buf.writeBytes(String.valueOf(value).getBytes(Charsets.UTF_8));
        buf.writeByte('\r');
        buf.writeByte('\n');
    }
    public static IntegerOutput of(long value) {
        return new IntegerOutput(value);
    }
}
