package bc_server;

/**
 * @author Ansersion
 *
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class BcServerMain {
	
	private static final int BC_SERVER_PORT= 8025;
	private static final int BC_SOCK_BUFF_SIZE = 2048;
	private static final int IDLE_READ_PROC_TIME = 3600;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        //socket接收器
    	NioSocketAcceptor BcAcceptor = new NioSocketAcceptor();
 
        //添加日志记录
        BcAcceptor.getFilterChain().addLast("logger",new LoggingFilter());
        //添加编码解码器
        // BcAcceptor.getFilterChain().addLast("codec",new ProtocolCodecFilter(new TextLineCodecFactory()));
        // BcAcceptor.getFilterChain().addLast("mycoder", new ProtocolCodecFilter(new ByteArrayCodecFactory()));
        //添加处理器(用于接收数据后处理处理数据逻辑)
        BcAcceptor.setHandler(new BcServerHandler());
        
        // 端口复用
        BcAcceptor.setReuseAddress(true);
        
        //设置读取数据缓存单位byte 
        BcAcceptor.getSessionConfig().setReadBufferSize(BC_SOCK_BUFF_SIZE);
        //设置多长时间后接收器开始空闲
        BcAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, IDLE_READ_PROC_TIME);
        try {
            //绑定某个端口，作为数据入口 
            BcAcceptor.bind(new InetSocketAddress(BC_SERVER_PORT));
        }catch (IOException e) {
            e.printStackTrace();
        }

	}

}
