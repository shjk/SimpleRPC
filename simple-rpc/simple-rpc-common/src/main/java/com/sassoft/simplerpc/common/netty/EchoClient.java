package com.sassoft.simplerpc.common.netty;

import com.alibaba.fastjson.JSON;
import com.sassoft.simplerpc.common.entity.RegisterEntity;
import com.sassoft.simplerpc.common.util.ConfigUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.logging.Logger;

/**
 * Created by shjk_000 on 2019/3/9.
 */
public class EchoClient {
    static Logger log = Logger.getLogger(EchoClient.class.getName());
    private String ip;
    private int port;
    private SimpleChannelInboundHandler clientChannelInboundHandler;

    public EchoClient(String ip, int port, SimpleChannelInboundHandler clientChannelInboundHandler) {
        this.ip = ip;
        this.port = port;
        this.clientChannelInboundHandler = clientChannelInboundHandler;
    }

    public void start(String data) throws InterruptedException {
        log.info(" EchoClient start.......");

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            //b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(clientChannelInboundHandler);
                }
            });

            // Start the client.
            //String host = ConfigUtil.readConfig("register_server_ip");
            //int port = Integer.parseInt(ConfigUtil.readConfig("register_server_port"));
            ChannelFuture f = b.connect(ip, port).sync();
            Channel channel = f.sync().channel();

            ByteBuf encoded = channel.alloc().buffer(4 * data.length());
            encoded.writeBytes(data.getBytes());
            channel.writeAndFlush(encoded);

            log.info(" client send data.......");

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
