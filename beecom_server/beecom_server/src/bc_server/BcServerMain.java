package bc_server;

/**
 * @author Ansersion
 *
 */
import java.io.*;

import java.net.InetSocketAddress;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.UserInfoHbn;
import sys_sig_table.BPSysEnmLangResTable;
import sys_sig_table.BPSysSigLangResTable;
import sys_sig_table.BPSysSigTable;

public class BcServerMain {

	private static final Logger logger = LoggerFactory.getLogger(BcServerMain.class); 

	private static final int BC_SERVER_PORT = 8025;
	private static final int BC_SOCK_BUFF_SIZE = 2048;
	private static final int IDLE_READ_PROC_TIME = 3600;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		BPSysSigLangResTable sigLangResTab = BPSysSigLangResTable.getSysSigLangResTable();
		try {
			sigLangResTab.loadTab();
		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
		}
		
		BPSysEnmLangResTable enumLangResTab = BPSysEnmLangResTable.getSysEnmLangResTable();
		try {
			enumLangResTab.loadTab();
		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
		}
		
		BPSysSigTable sysSigTab = BPSysSigTable.getSysSigTableInstance();
		try {
			sysSigTab.loadTab();
		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
		}

		NioSocketAcceptor bcAcceptor = new NioSocketAcceptor();

		bcAcceptor.getFilterChain().addLast("logger", new LoggingFilter());
		bcAcceptor.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new BcCodecFactory()));
		bcAcceptor.setHandler(new BcServerHandler());

		bcAcceptor.setReuseAddress(true);

		bcAcceptor.getSessionConfig().setReadBufferSize(BC_SOCK_BUFF_SIZE);
		bcAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,
				IDLE_READ_PROC_TIME);
		
		SessionFactory sessionFactory = buildSessionFactory(); 
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			   tx = session.beginTransaction();
			   // do some work
			   UserInfoHbn userInfoHbn = session.load(UserInfoHbn.class, 1L);
			   logger.info(userInfoHbn.toString());
			   tx.commit();
			}
			catch (Exception e) {
			   if (tx!=null) tx.rollback();
	            StringWriter sw = new StringWriter();
	            e.printStackTrace(new PrintWriter(sw, true));
	            String str = sw.toString();
	            logger.error(str);
			}finally {
			   session.close();
			}
		
		try {
			bcAcceptor.bind(new InetSocketAddress(BC_SERVER_PORT));
		} catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
		}

	}
	
    private static SessionFactory buildSessionFactory() {  
        try {  
            // Create the SessionFactory from hibernate.cfg.xml  
            return new Configuration().configure().buildSessionFactory();  
        }  
        catch (Throwable ex) {  
            // Make sure you log the exception, as it might be swallowed  
            System.err.println("Initial SessionFactory creation failed." + ex);  
            throw new ExceptionInInitializerError(ex);  
        }  
    }  

}
