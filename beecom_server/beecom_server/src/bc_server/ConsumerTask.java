package bc_server;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import other.Util;

/**
 * @author Ansersion
 * 
 * @see Thanks: https://www.cnblogs.com/Ming8006/p/7243858.html
 *
 */

public class ConsumerTask extends Thread {
	private static final Logger logger = LoggerFactory.getLogger(ConsumerTask.class); 
    private static final int MAX_SIZE = 100;
    private LinkedList<Product> list = new LinkedList<>();
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
                	Util.logger(logger, Util.ERROR, e);
                }
            }

            list.add(product);            
            list.notifyAll();
        }
    }

    public void consume() {
        synchronized (list) {
            while (list.isEmpty()) {
                try {
                    list.wait();
                }
                catch (InterruptedException e) {
                	Util.logger(logger, Util.ERROR, e);
                }
            }
            
            Product product = list.remove();
            product.consume();
            list.notifyAll();
        }
    }

    public List<Product> getList()
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
		logger.info("ConsumerTask start");
		while(loop) {
			consume();
			logger.info("consumed");
		}
    	
    }
}
