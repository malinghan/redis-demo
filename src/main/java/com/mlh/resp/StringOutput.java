package com.mlh.resp;

/**
 * @author: linghan.ma
 * @DATE: 2018/12/17
 * @description: 作者：老錢
 * 链接：https://juejin.im/post/5aaf1e0af265da2381556c0e
 * 来源：掘金
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 */

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;

public class StringOutput implements IRedisOutput {

    private String content;

    public StringOutput(String content) {
        this.content = content;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeByte('$');
        if (content == null) {
            // $-1\r\n
            buf.writeByte('-');
            buf.writeByte('1');
            buf.writeByte('\r');
            buf.writeByte('\n');
            return;
        }
        byte[] bytes = content.getBytes(Charsets.UTF_8);
        buf.writeBytes(String.valueOf(bytes.length).getBytes(Charsets.UTF_8));
        buf.writeByte('\r');
        buf.writeByte('\n');
        if (content.length() > 0) {
            buf.writeBytes(bytes);
        }
        buf.writeByte('\r');
        buf.writeByte('\n');
    }

    public static StringOutput of(String content) {
        return new StringOutput(content);
    }

    public static StringOutput of(long value) {
        return new StringOutput(String.valueOf(value));
    }

    public final static StringOutput NULL = new StringOutput(null);

}