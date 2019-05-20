/**
 * 
 */
package db;

import server_chain.ServerChain;

/**
 * @author Ansersion
 *
 */
public class DevServerChainHbn {
    private Long id;
    private Long clientId;
    private String upperServer;
    private byte upperServerType;
    private String lowerServer;
    private byte lowerServerType;
    
	public DevServerChainHbn() {
		super();
		upperServer = "";
		upperServerType = ServerChain.TYPE_DEFAULT;
		lowerServer = "";
		lowerServerType = ServerChain.TYPE_DEFAULT;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getClientId() {
		return clientId;
	}
	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}
	public String getUpperServer() {
		return upperServer;
	}
	public void setUpperServer(String upperServer) {
		this.upperServer = upperServer;
	}
	public byte getUpperServerType() {
		return upperServerType;
	}
	public void setUpperServerType(byte upperServerType) {
		this.upperServerType = upperServerType;
	}
	public String getLowerServer() {
		return lowerServer;
	}
	public void setLowerServer(String lowerServer) {
		this.lowerServer = lowerServer;
	}
	public byte getLowerServerType() {
		return lowerServerType;
	}
	public void setLowerServerType(byte lowerServerType) {
		this.lowerServerType = lowerServerType;
	}
	@Override
	public String toString() {
		return "DevServerChainHbn [id=" + id + ", clientId=" + clientId + ", upperServer=" + upperServer
				+ ", upperServerType=" + upperServerType + ", lowerServer=" + lowerServer + ", lowerServerType="
				+ lowerServerType + "]";
	}

}
