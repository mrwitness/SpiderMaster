package wuxian.me.spidermaster.agent;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.agent.biz.HeartbeatRequestProducer;
import wuxian.me.spidermaster.agent.biz.RegisterRequestProducer;
import wuxian.me.spidermaster.agent.biz.ReportStatusRequestProducer;
import wuxian.me.spidermaster.agent.connector.IConnectCallback;
import wuxian.me.spidermaster.agent.connector.SpiderConnector;
import wuxian.me.spidermaster.agent.rpccore.OnRpcRequest;
import wuxian.me.spidermaster.agent.rpccore.RequestSender;
import wuxian.me.spidermaster.rpc.IRpcCallback;
import wuxian.me.spidermaster.rpc.RpcRequest;
import wuxian.me.spidermaster.rpc.RpcResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuxian on 26/5/2017.
 * <p>
 */
public class SpiderClient implements IClient {

    private Thread connectThread;
    private boolean connected = false;
    private SocketChannel channel;
    private RequestSender sender = new RequestSender(this);

    private HeartbeatRequestProducer heartbeatProducer = new HeartbeatRequestProducer();
    //private RegisterRequestProducer registerProducer = new RegisterRequestProducer();

    private ReportStatusRequestProducer reportStatusProducer = new ReportStatusRequestProducer();

    public void init() {
        sender.init();
    }

    public SocketChannel channel() {
        return channel;
    }

    public void asyncConnect(final String serverIp, final int serverPort) {
        if (connected) {
            return;
        }

        SpiderConnector connector = new SpiderConnector(
                serverIp, serverPort, this, new IConnectCallback() {
            public void onSuccess(SocketChannel channel) {
                SpiderClient.this.channel = channel;  //save channel
                connected = true;

                LogManager.info("connect success,channel "+channel);
                //sender.init();
            }

            public void onFail() {
                LogManager.info("connect fail");

            }

            public void onException() {

            }
        });
        connectThread = new Thread(connector);
        connectThread.setName("ConnectionThread");
        connectThread.start();

    }

    public boolean isConnected() {
        return connected;
    }

    //Todo:
    public void disconnectFromServer() {

    }

    //运行在"ConnectionThread"线程 --> 业务处理时间过长会阻塞io线程
    public void onMessage(RpcRequest request) {
        LogManager.info("onMessage: " + Thread.currentThread().getName());

        if (request == null) {
            return;
        }

        if (!requestMap.containsKey(request)) {
            return;
        }
        requestMap.get(request).onRpcRequest(request);
    }

    //Todo:根据reponse.requestId找到IRpcCallback
    //这个response应该运行在@"ConnectionThread"
    public void onRpcResponse(RpcResponse response) {


    }

    //内部一定是将这个任务(RpcRequest)丢到子线程去发这个请求
    //注意这里的callback在子线程执行
    public void asyncSendMessage(RpcRequest request, IRpcCallback callback) {
        sender.put(request, callback);
    }

    public void onDisconnectByServer() {
        connected = false;
    }

    private static Map<RpcRequest, OnRpcRequest> requestMap = new HashMap<RpcRequest, OnRpcRequest>();

    //业务层只需实现OnRpcRequest接口并注册即可
    public static void registerMessageNotify(RpcRequest request, OnRpcRequest onRpcRequest) {
        if (request == null || onRpcRequest == null) {
            return;
        }

        if (requestMap.containsKey(request)) {
            return;
        }

        requestMap.put(request, onRpcRequest);
    }

}
