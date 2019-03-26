package server_chain;

public class ServerNode {
	private int type;
	private String address;
	public ServerNode(int type, String address) {
		super();
		this.type = type;
		this.address = address;
	}
	public int getType() {
		return type;
	}
	public String getAddress() {
		return address;
	}
	
	
}
