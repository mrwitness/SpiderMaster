package wuxian.me.spidermaster.framework.rpc;

import com.sun.istack.internal.Nullable;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.ArrayList;
import java.util.List;

public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> genericClass;

    public RpcEncoder(Class<?> clazz) {
        List<Class<?>> classList = null;
        if (clazz != null) {
            classList = new ArrayList<Class<?>>();
            classList.add(clazz);
        }

        init(classList);
    }

    public RpcEncoder(List<Class<?>> classList) {
        init(classList);
    }

    private List<Class<?>> classList = null;


    private void init(@Nullable List<Class<?>> list) {
        if (list == null) {
            list = new ArrayList<Class<?>>();
        }

        this.classList = list;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {

        for (Class<?> claz : classList) { //支持多种encoder
            if (claz.isInstance(in)) {
                byte[] data = SerializationUtil.serialize(in);
                out.writeInt(data.length);
                out.writeBytes(data);
                break;
            }
        }

    }
}
