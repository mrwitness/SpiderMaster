package wuxian.me.spidermaster.framework.rpc;

import com.sun.istack.internal.Nullable;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import wuxian.me.spidercommon.log.LogManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 26/5/2017.
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private List<Class<?>> classList = null;

    public RpcDecoder(final Class<?> clazz) {
        List<Class<?>> classList = null;
        if (clazz != null) {
            classList = new ArrayList<Class<?>>();
            classList.add(clazz);
        }

        init(classList);
    }

    private void init(@Nullable List<Class<?>> list) {
        if (list == null) {
            list = new ArrayList<Class<?>>();
        }

        this.classList = list;
    }

    public RpcDecoder(List<Class<?>> clazList) {
        init(clazList);
    }

    private void safePrintByteBuf(ByteBuf buf) {
        if (buf.hasArray()) {
            LogManager.info("bytebuf: " + new String(buf.array()));

        } else {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);

            LogManager.info("bytebuf: " + new String(bytes));
        }
    }

    public void decode(ChannelHandlerContext ctx,
                          ByteBuf in, List<Object> out) throws Exception {
        //LogManager.info("RpcDecoder.decode");

        if (classList == null || classList.size() == 0) {
            return;
        }

        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) {
            ctx.close();
        }
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        Object obj = null;
        boolean success = false;


        for (Class<?> clazz : classList) {  //支持多个decoder解析器
            try {
                obj = SerializationUtil.deserialize(data, clazz); //Fixme:类型检测太弱逼了

                if (obj != null) {
                    //LogManager.info("decode with " + clazz.getSimpleName() + " obj is " + obj.toString());

                    if (clazz.equals(RpcRequest.class)) {  //Fixme:为了规避SerializationUtil.deserialize的bug 先这样猥琐处理
                        String method = ((RpcRequest) obj).methodName;
                        if (method == null || method.length() == 0) {
                            continue;
                        }
                    }

                    success = true;
                    break;

                }
            } catch (IllegalStateException e) {

            } catch (Exception e) {

            }
        }

        if (success && obj != null) {
            out.add(obj);
        }

    }
}
