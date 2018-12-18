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

public class StateOutput implements IRedisOutput {

    private String state;

    public final static StateOutput OK = new StateOutput("OK");

    public final static StateOutput PONG = new StateOutput("PONG");

    public StateOutput(String state) {
        this.state = state;
    }

    public void encode(ByteBuf buf) {
        buf.writeByte('+');
        buf.writeBytes(state.getBytes(Charsets.UTF_8));
        buf.writeByte('\r');
        buf.writeByte('\n');
    }

    public static StateOutput of(String state) {
        return new StateOutput(state);
    }
}
