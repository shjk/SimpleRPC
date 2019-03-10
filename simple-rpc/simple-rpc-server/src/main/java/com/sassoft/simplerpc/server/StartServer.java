package com.sassoft.simplerpc.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSON;
import com.sassoft.simplerpc.common.entity.RegisterEntity;
import com.sassoft.simplerpc.common.entity.RegisterOperator;
import com.sassoft.simplerpc.common.netty.EchoClient;
import com.sassoft.simplerpc.common.netty.EchoServer;
import com.sassoft.simplerpc.common.util.ConfigUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by shjk_000 on 2018/3/8.
 */
public class StartServer {
    static Logger log = Logger.getLogger(StartServer.class.getName());

    public static void main(String[] args) throws InterruptedException {
        log.info(" server start.......");

        registerClient();

        startService();
    }

    private static void startService() throws InterruptedException {
        String host = ConfigUtil.readConfig("server_ip");
        int port = Integer.parseInt(ConfigUtil.readConfig("server_port"));

        new EchoServer(host, port, new ServerHandler()).start();
    }


    public static void registerClient() throws InterruptedException {

        log.info(" registerClient start.......");
        String host = ConfigUtil.readConfig("register_server_ip");
        int port = Integer.parseInt(ConfigUtil.readConfig("register_server_port"));

        EchoClient echoClient = new EchoClient(host, port, new RegisterClientIntHandler());

        RegisterOperator registerOperator = new RegisterOperator();
        RegisterEntity registerEntity = new RegisterEntity();
        registerEntity.setUrl("127.0.0.1:9001");
        registerEntity.setMethodName("getRemote");
        registerEntity.setClassName("com.sassoft.simplerpc.server");
        registerEntity.setParams(new String[]{"sassoft"});

        registerOperator.setRegisterEntity(registerEntity);
        registerOperator.setAction("register");

        String msg = JSON.toJSONString(registerOperator);

        echoClient.start(msg);

        log.info(" registerClient send data:" + msg);
    }
}


@ChannelHandler.Sharable
class ServerHandler extends ChannelInboundHandlerAdapter {
    private static Map<String, List<RegisterEntity>> registerCache =
            new HashMap<String, List<RegisterEntity>>();

    //private static Logger logger = LoggerFactory.getLogger(HelloClientIntHandler.class);
    static Logger logger = Logger.getLogger(ServerHandler.class.getName());

    // 接收server端的消息，并打印出来
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("ServerHandler.channelRead");
        System.out.print("server received data :" + msg);
        String receivedData = msg.toString();

        RegisterEntity registerEntity = JSON.parseObject(receivedData, RegisterEntity.class);

        //todo 执行本地方法


        // 返回客户端消息
        ctx.writeAndFlush("received:" + registerEntity.getMethodName());
    }

    // 连接成功后，向server发送消息
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("RegisterServerHandler.channelActive");
    }

    // 处理异常，输出异常信息，然后关闭Channel
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}


class RegisterClientIntHandler extends SimpleChannelInboundHandler {
    //private static Logger logger = LoggerFactory.getLogger(HelloClientIntHandler.class);
    static Logger logger = Logger.getLogger(RegisterClientIntHandler.class.getName());

    // 接收server端的消息，并打印出来
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("RegisterClientIntHandler.channelRead");
        ByteBuf result = (ByteBuf) msg;
        byte[] result1 = new byte[result.readableBytes()];
        result.readBytes(result1);
        String received = new String(result1);

        System.out.println("Server said:" + new String(received));
        result.release();
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        logger.info("RegisterClientIntHandler.channelRead0");
    }

    // 连接成功后，向server发送消息
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("RegisterClientIntHandler.channelActive");
    }
}
 /*class HelloClientIntHandler extends ChannelInboundHandlerAdapter {
    //private static Logger logger = LoggerFactory.getLogger(HelloClientIntHandler.class);
    static Logger logger= Logger.getLogger(HelloClientIntHandler.class.getName());
    // 接收server端的消息，并打印出来
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("HelloClientIntHandler.channelRead");
        ByteBuf result = (ByteBuf) msg;
        byte[] result1 = new byte[result.readableBytes()];
        result.readBytes(result1);
        System.out.println("Server said:" + new String(result1));
        result.release();
    }

    // 连接成功后，向server发送消息
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("HelloClientIntHandler.channelActive");
        RegisterEntity registerEntity=new RegisterEntity();
        registerEntity.setUrl("127.0.0.1:9001");
        registerEntity.setMethodName("getRemote");
        registerEntity.setClassName("com.sassoft.simplerpc.server");
        registerEntity.setParams(new String[]{"sassoft"});

        String msg = JSON.toJSONString(registerEntity);

        ByteBuf encoded = ctx.alloc().buffer(4 * msg.length());
        encoded.writeBytes(msg.getBytes());
        ctx.write(encoded);
        ctx.flush();
    }
}*/
