package com.sassoft.simplerpc.client;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sassoft.simplerpc.common.entity.RegisterEntity;
import com.sassoft.simplerpc.common.entity.RegisterOperator;
import com.sassoft.simplerpc.common.netty.EchoClient;
import com.sassoft.simplerpc.common.util.ConfigUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by shjk_000 on 2018/3/9.
 */
public class StartClient {
    static Logger log = Logger.getLogger(StartClient.class.getName());
    static RegisterOperator registerOperator;

    public static void main(String[] args) throws InterruptedException {
        log.info(" client start.......");
        registerOperator=new RegisterOperator();
        getService();

        invokeRemote();
    }

    private static void invokeRemote() throws InterruptedException {
        log.info(" invokeRemote start.......");
        //todo loadbalence
        RegisterEntity registerEntity=registerOperator.getRegistedEntities().get(0);
        String[] ipAndPort=registerEntity.getUrl().split(":");

        String host = ipAndPort[0];
        int port = Integer.parseInt(ipAndPort[1]);

        EchoClient echoClient = new EchoClient(host, port, new RequestServerHandler());

        RegisterEntity requestRegisterEntity = new RegisterEntity();
        requestRegisterEntity.setUrl("127.0.0.1:9001");
        requestRegisterEntity.setMethodName("getRemote");
        requestRegisterEntity.setClassName("com.sassoft.simplerpc.server");
        requestRegisterEntity.setParams(new String[]{"sassoft"});


        String msg = JSON.toJSONString(requestRegisterEntity);

        echoClient.start(msg);

        log.info(" getService send data:" + msg);
    }

    public static void getService() throws InterruptedException {
        log.info(" getService start.......");
        String host = ConfigUtil.readConfig("register_server_ip");
        int port = Integer.parseInt(ConfigUtil.readConfig("register_server_port"));

        List<RegisterEntity> listRegisterEntity=new ArrayList<RegisterEntity>();

        EchoClient echoClient = new EchoClient(host, port, new GetServerHandler(registerOperator));

        RegisterOperator registerOperator = new RegisterOperator();
        RegisterEntity registerEntity = new RegisterEntity();
        registerEntity.setUrl("127.0.0.1:9001");
        registerEntity.setMethodName("getRemote");
        registerEntity.setClassName("com.sassoft.simplerpc.server");
        registerEntity.setParams(new String[]{"sassoft"});

        registerOperator.setRegisterEntity(registerEntity);
        registerOperator.setAction("getService");

        String msg = JSON.toJSONString(registerOperator);

        echoClient.start(msg);

        log.info(" getService send data:" + msg);
    }
}

class GetServerHandler extends SimpleChannelInboundHandler {
    //private static Logger logger = LoggerFactory.getLogger(HelloClientIntHandler.class);
    static Logger logger = Logger.getLogger(GetServerHandler.class.getName());

    RegisterOperator registerOperator;
    public GetServerHandler(RegisterOperator registerOperator){
        this.registerOperator=registerOperator;
    }

    // 接收server端的消息，并打印出来
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("GetServerHandler.channelRead");
        ByteBuf result = (ByteBuf) msg;
        byte[] result1 = new byte[result.readableBytes()];
        result.readBytes(result1);
        String received = new String(result1);

        System.out.println("GetServerHandler Server said:" + received);


        List<RegisterEntity> list=new ArrayList<RegisterEntity>(JSONArray.parseArray(received,RegisterEntity.class));

        registerOperator.setRegistedEntities(list);

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

class RequestServerHandler extends SimpleChannelInboundHandler {
    //private static Logger logger = LoggerFactory.getLogger(HelloClientIntHandler.class);
    static Logger logger = Logger.getLogger(RequestServerHandler.class.getName());

    //List<RegisterEntity> list;
    //public GetServerHandler(){
        //this.list=listRegisterEntity;
    //}

    // 接收server端的消息，并打印出来
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("RequestServerHandler.channelRead");
        ByteBuf result = (ByteBuf) msg;
        byte[] result1 = new byte[result.readableBytes()];
        result.readBytes(result1);
        String received = new String(result1);

        System.out.println("RequestServerHandler Server said:" + received);

        //list=new ArrayList<RegisterEntity>(JSONArray.parseArray(received,RegisterEntity.class));

        result.release();
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        logger.info("RequestServerHandler.channelRead0");
    }

    // 连接成功后，向server发送消息
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("RequestServerHandler.channelActive");
    }
}


