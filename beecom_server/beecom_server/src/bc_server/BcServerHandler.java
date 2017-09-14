package bc_server;

/**
 * @author Ansersion
 *
 */

import java.util.Date;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class BcServerHandler extends IoHandlerAdapter {
    //捕获异常
    @Override
    public void exceptionCaught(IoSession session, Throwable cause ) throws Exception
    {
        cause.printStackTrace();
    }
    //消息接收
    @Override
    public void messageReceived(IoSession session, Object message ) throws Exception
    {
    	// String x = (String)message;

    	// System.out.println("mymessage>>>>>>>>>>"+x);
        Date date = new Date();
        session.write(date.toString() );
    	/*
        IoBuffer buf = (IoBuffer) message;
        // Print out read buffer content.
        while (buf.hasRemaining()) {
            System.out.println("byte" + (char) buf.get());
        }
        System.out.flush();
        */
    	/*
        String str = message.toString();

        System.out.println("mymessage>>>>>>>>>>"+str);
        
        Date date = new Date();
        session.write(date.toString() );
        */
        
    	/*
        String str = message.toString();
        if(str.trim().equalsIgnoreCase("quit")) {
            session.closeNow();
            return;
        }
        System.out.println("mymessage>>>>>>>>>>"+str);
        Date date = new Date();
        session.write(date.toString() );
        */
        
    }
    //会话空闲
    @Override
    public void sessionIdle(IoSession session, IdleStatus status ) throws Exception
    {
        System.out.println("IDLE" + session.getIdleCount( status ));
    }
}
