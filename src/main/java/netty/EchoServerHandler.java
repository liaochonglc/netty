package netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable //@Sharable 标识这类的实例之间可以在 channel 里面共享
/***
 * 通过 ChannelHandler 来实现服务器的逻辑
 * Echo Server 将会将接受到的数据的拷贝发送给客户端。
 * 因此，我们需要实现 ChannelInboundHandler 接口，用来定义处理入站事件的方法。
 * 由于我们的应用很简单，只需要继承 ChannelInboundHandlerAdapter 就行了。
 * 这个类 提供了默认 ChannelInboundHandler 的实现，所以只需要覆盖下面的方法：
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //将客户端传入的消息转换为Netty的ByteBuf类型
        ByteBuf in = (ByteBuf) msg;

        // 在控制台打印传入的消息
        System.out.println(
                "Server received: " + in.toString(CharsetUtil.UTF_8)
        );
        //将接收到的消息写给发送者，而不冲刷出站消息
        ctx.write(in);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将未处决消息冲刷到远程节点， 并且关闭该Channel
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //打印异常栈跟踪
        cause.printStackTrace();

        // 关闭该Channel
        ctx.close();
    }
}
