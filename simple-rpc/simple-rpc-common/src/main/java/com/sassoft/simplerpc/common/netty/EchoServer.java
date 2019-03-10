package com.sassoft.simplerpc.common.netty;

import com.alibaba.fastjson.JSON;
import com.sassoft.simplerpc.common.entity.RegisterEntity;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shjk_000 on 2018/3/8.
 */
public class EchoServer {

    private String ip;
    private int port;
    private ChannelInboundHandlerAdapter channelInboundHandler;

    public EchoServer(String ip, int port, ChannelInboundHandlerAdapter channelInboundHandler) {
        this.ip = ip;
        this.port = port;
        this.channelInboundHandler = channelInboundHandler;
    }

    /**
     * 用于分配处理业务线程的线程组个数
     */
    protected static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors() * 2;
    protected static final int BIZTHREADSIZE = 4;
    /*
     * NioEventLoopGroup实际上就是个线程池,
     * NioEventLoopGroup在后台启动了n个NioEventLoop来处理Channel事件,
     * 每一个NioEventLoop负责处理m个Channel,
     * NioEventLoopGroup从NioEventLoop数组里挨个取出NioEventLoop来处理Channel
     */
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZGROUPSIZE);
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(BIZTHREADSIZE);

    public void start() throws InterruptedException {
        ServerBootstrap b = new ServerBootstrap();// 引导辅助程序
        try {
            b.group(bossGroup, workerGroup);
            //NioDatagramChannel UDP
            b.channel(NioServerSocketChannel.class);// 设置nio类型的channel
            b.childHandler(new TestServerInitializer(channelInboundHandler));
            b.option(ChannelOption.SO_BACKLOG, 128);
            b.childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(ip, port).sync();// 配置完成，开始绑定server，通过调用sync同步方法阻塞直到绑定成功
            System.out.println(EchoServer.class.getName() + " started and listen on " + f.channel().localAddress());
            f.channel().closeFuture().sync();// 应用程序会一直等待，直到channel关闭
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            shutdown();//关闭EventLoopGroup，释放掉所有资源包括创建的线程
        }
    }


    protected void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    class TestServerInitializer extends ChannelInitializer<SocketChannel> {
        ChannelInboundHandlerAdapter channelInboundHandler;
        public TestServerInitializer(ChannelInboundHandlerAdapter channelInboundHandler){
            this.channelInboundHandler=channelInboundHandler;
        }
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();

            //当read的时候
            pipeline.addLast("decoder", new StringDecoder());
            //当send的时候
            pipeline.addLast("encoder", new StringEncoder());
            pipeline.addLast("handler", channelInboundHandler);
        }
    }
}
