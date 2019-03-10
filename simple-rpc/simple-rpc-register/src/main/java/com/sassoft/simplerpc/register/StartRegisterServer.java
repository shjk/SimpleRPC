package com.sassoft.simplerpc.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSON;
import com.sassoft.simplerpc.common.entity.RegisterEntity;
import com.sassoft.simplerpc.common.entity.RegisterOperator;
import com.sassoft.simplerpc.common.netty.EchoServer;
import com.sassoft.simplerpc.common.util.ConfigUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


/**
 * Created by shjk_000 on 2018/1/8.
 */
public class StartRegisterServer {
    static Logger log = Logger.getLogger(StartRegisterServer.class.getName());

    public static void main(String[] args) throws InterruptedException {
        log.info("register server start.......");

        String ip = ConfigUtil.readConfig("ip");
        int port = Integer.parseInt(ConfigUtil.readConfig("port"));
        new EchoServer(ip, port, new RegisterServerHandler()).start();
    }
}

@ChannelHandler.Sharable
class RegisterServerHandler extends ChannelInboundHandlerAdapter {
    private static Map<String, List<RegisterEntity>> registerCache =
            new HashMap<String, List<RegisterEntity>>();

    //private static Logger logger = LoggerFactory.getLogger(HelloClientIntHandler.class);
    static Logger logger = Logger.getLogger(RegisterServerHandler.class.getName());

    // 接收server端的消息，并打印出来
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("RegisterServerHandler.channelRead");
        System.out.print("server received data :" + msg);
        String receivedData = msg.toString();

        RegisterOperator registerOperator = JSON.parseObject(receivedData, RegisterOperator.class);

        if (registerOperator.getAction().equals("register")) {
            List<RegisterEntity> listRegisterEntity = registerCache.get(registerOperator.getRegisterEntity().getMethodName());
            if (listRegisterEntity == null) {
                listRegisterEntity = new ArrayList<RegisterEntity>();
                listRegisterEntity.add(registerOperator.getRegisterEntity());
                registerCache.put(registerOperator.getRegisterEntity().getMethodName(), listRegisterEntity);
            } else {
                listRegisterEntity.add(registerOperator.getRegisterEntity());
            }
        } else if (registerOperator.getAction().equals("getService")) {
            List<RegisterEntity> registerEntity = registerCache.get(registerOperator.getRegisterEntity().getMethodName());
            msg = JSON.toJSONString(registerEntity);
        }

        logger.info("refresh registerCache:" + JSON.toJSONString(registerCache));

        // 返回客户端消息
        ctx.writeAndFlush(msg);
        ctx.close();
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


