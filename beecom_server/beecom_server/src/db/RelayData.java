package db;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.mina.core.session.IoSession;

public class RelayData {
	private Timer timer;
	private TimerTask timerTask;
	private IoSession ioSession;
	private long timeStamp;
	private Object relayData;
	
	public RelayData(Timer timer, TimerTask timerTask, IoSession ioSession, long timeStamp, Object relayData) {
		super();
		this.timer = timer;
		this.timerTask = timerTask;
		this.ioSession = ioSession;
		this.timeStamp = timeStamp;
		this.relayData = relayData;
	}
	public Timer getTimer() {
		return timer;
	}
	public TimerTask getTimerTask() {
		return timerTask;
	}
	public IoSession getIoSession() {
		return ioSession;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public Object getRelayData() {
		return relayData;
	}
	
	
	
}
