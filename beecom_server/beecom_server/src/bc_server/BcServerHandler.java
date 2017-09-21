package bc_server;

/**
 * @author Ansersion
 *
 */

import java.util.Map;
import java.util.HashMap;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class BcServerHandler extends IoHandlerAdapter {
	
	Map<Integer, BPSession> CliId2SsnMap = new HashMap<Integer, BPSession>();
	
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
    	BPPacket decoded_pack = (BPPacket)message;
    	
    	BPPacketType pack_type = decoded_pack.getPackTypeFxHead();
    	if(BPPacketType.CONNECT == pack_type) {
        	int client_id = decoded_pack.getClientId();
        	byte[] user_name = decoded_pack.getUserNamePld();
        	byte[] password = decoded_pack.getPasswordPld();
        	boolean user_login_flag = decoded_pack.getUsrLoginFlagVrbHead();
        	boolean dev_login_flag = decoded_pack.getDevLoginFlagVrbHead();
        	
        	
        	BPPacket pack_ack = BPPackFactory.createBPPackAck(decoded_pack);
        	/* check user/pwd valid */
        	/* check client_id valid */
        	/* update login flags */
        	BPSession newBPSession = new BPSession(user_name, password, client_id, user_login_flag, dev_login_flag);
        	CliId2SsnMap.put(client_id, newBPSession);
        	
        	session.write(pack_ack);
        	
    	}
    	

    	
    	

    	// System.out.println("mymessage>>>>>>>>>>"+x);
        // Date date = new Date();
        // session.write(date.toString() );
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
