package bc_server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPPacketCONNECT;

/**
 * @author Ansersion
 * 
 * @see Thanks: https://www.cnblogs.com/Ming8006/p/7243858.html
 *
 */

public class ConsumerTask extends Thread {
	

	
	private static final Logger logger = LoggerFactory.getLogger(ConsumerTask.class); 
    private final int MAX_SIZE = 100;
    private LinkedList<Product> list = new LinkedList<Product>();
    private boolean loop = true;

    public void produce(Product product) {
    	if(null == product) {
    		return;
    	}
        synchronized (list) {
            while (list.size() == MAX_SIZE) {
                try {
                    list.wait();
                }
                catch (InterruptedException e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw, true));
                    String str = sw.toString();
                    logger.error(str);
                }
            }

            list.add(product);            
            list.notifyAll();
        }
    }

    public void consume() {
        synchronized (list) {
            while (list.size()==0) {
                try {
                    list.wait();
                }
                catch (InterruptedException e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw, true));
                    String str = sw.toString();
                    logger.error(str);
                }
            }
            
            Product product = list.remove();
            product.consume();
            list.notifyAll();
        }
    }

    public LinkedList<Product> getList()
    {
        return list;
    }

    public void setList(LinkedList<Product> list)
    {
        this.list = list;
    }

    public int getMAX_SIZE()
    {
        return MAX_SIZE;
    }
    
    public boolean isLoop() {
		return loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	@Override 
    public void run() {
		while(loop) {
			consume();
			logger.debug("consumed");
		}
    	
    }
}
