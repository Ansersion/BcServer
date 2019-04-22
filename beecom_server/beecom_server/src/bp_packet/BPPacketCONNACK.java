/**
 * 
 */
package bp_packet;

import org.apache.mina.core.buffer.IoBuffer;

import db.ServerChainHbn;
import other.CrcChecksum;
import server_chain.ServerChain;
import sys_sig_table.BPSysSigTable;

/**
 * @author Ansersion
 *
 */
public class BPPacketCONNACK extends BPPacket {
	
	public static final int RET_CODE_OK = 0x00;
	public static final int RET_CODE_LEVEL_ERR = 0x01;
	public static final int RET_CODE_SERVER_ERR = 0x02;
	public static final int RET_CODE_USER_OR_PASSWORD_INVALID = 0x03;
	public static final int RET_CODE_PWD_INVALID = 0x04;
	public static final int RET_CODE_ENCRYPT_ERR = 0x05;
	public static final int RET_CODE_CRC_UNSUPPORT = 0x06;
	public static final int RET_CODE_CRC_CHECK_ERR = 0x07;
	public static final int RET_CODE_CLNT_ID_INVALID = 0x08;
	public static final int RET_CODE_CLNT_UNKNOWN = 0x09;
	
	public static final int RET_CODE_USER_INVALID = 0x10;
	public static final int RET_CODE_REGISTER_FAILED = 0x11;
	
	public static final int RET_CODE_SERVER_CHAIN_INVALID = 0x12;
	public static final int RET_CODE_ADMIN_NAME_INVALID = 0x13;
	public static final int RET_CODE_DEVICE_ONLINE = 0x14;
	
	public static final int RET_CODE_SERVER_LOADING_FULL = 0xFE;
	public static final int RET_CODE_SERVER_UNAVAILABLE = 0xFF;

	protected BPPacketCONNACK(FixedHeader fxHeader) {
		super(fxHeader);
	}
	
	protected BPPacketCONNACK() {
		super();
		FixedHeader fxHead = getFxHead();
		fxHead.setPacketType(BPPacketType.CONNACK);
		fxHead.setCrcType(CrcChecksum.CRC32);
	}
	
	protected BPPacketCONNACK(BPPacketCONNECT packConnnect) {
		super();
		FixedHeader fxHead = getFxHead();
		fxHead.setPacketType(BPPacketType.CONNACK);
		fxHead.setCrcType(packConnnect.getFxHead().getCrcChk());
		VariableHeader vrbHead = getVrbHead();
		if(packConnnect.getVrbHead().getLevel() > BPPacket.BP_LEVEL) {
			vrbHead.setLevel(BPPacket.BP_LEVEL);
			vrbHead.setRetCode(0x01);
		} else {
			vrbHead.setLevel(packConnnect.getVrbHead().getLevel());
			if(0 == packConnnect.getVrbHead().getClientId()) {
				vrbHead.setNewCliIdFlg();
			}
			vrbHead.setRetCode(0x00);
		}
	}

	@Override
	public boolean assembleVariableHeader() throws BPAssembleVrbHeaderException {
		super.assembleVariableHeader();
		byte encodedByte;
		VariableHeader vrb = getVrbHead();
		IoBuffer ioBuffer = getIoBuffer();
		
		encodedByte = (byte)vrb.getLevel();
		ioBuffer.put(encodedByte);
		encodedByte = vrb.getFlags();
		ioBuffer.put(encodedByte);
		ioBuffer.putUnsignedShort(vrb.getAliveTime());
		ioBuffer.put((byte)vrb.getTimeout());
		encodedByte = (byte)getVrbHead().getRetCode();
		ioBuffer.put(encodedByte);
		
		return true;
	}

	@Override
	public boolean assemblePayload() {
		byte encodedByte;
		Payload payload;
		ServerChainHbn serverChainHbn;
		IoBuffer ioBuffer = getIoBuffer();
		if(RET_CODE_OK != getVrbHead().getRetCode()) {
			return true;
		}
		payload = getPld();
		ioBuffer.putUnsignedShort(BPSysSigTable.BP_SYS_SIG_SET_VERSION);
		
		serverChainHbn = payload.getServerChainHbn();
		if(null == serverChainHbn) {
			// TODO: return true when user connect
			if(!packServer(ioBuffer, ServerChain.TYPE_DEFAULT, null)) {
				return false;
			}
			if(!packServer(ioBuffer, ServerChain.TYPE_DEFAULT, null)) {
				return false;
			}
			return true;
		}
		
		encodedByte = serverChainHbn.getUpperServerType();
		if(!packServer(ioBuffer, encodedByte, serverChainHbn.getUpperServer())) {
			return false;
		}
		encodedByte = serverChainHbn.getLowerServerType();
		
		if(!packServer(ioBuffer, encodedByte, serverChainHbn.getLowerServer())) {
			return false;
		}
		
		return true;
	}
	
	private boolean packServer(IoBuffer ioBuffer, int serverType, String serverAddress) {
		boolean ret = false;
		if(null == ioBuffer) {
			return ret;
		}
		
		switch(serverType) {
		case ServerChain.TYPE_DEFAULT: {
			ioBuffer.put((byte)serverType);
			ret = true;
			break;
		}
		case ServerChain.TYPE_IPV4: {
			String[] array = serverAddress.split("\\.");
			if(null != array && 4 == array.length) {
				ioBuffer.put((byte)serverType);
				for(int i = 0; i < array.length; i++) {
					ioBuffer.put((byte)ServerChain.parseInt(array[i]));
				}
				ret = true;
			}
			break;
		}
		case ServerChain.TYPE_IPV6: {
			String[] array = serverAddress.split(":");
			if(null != array && 8 == array.length) {
				ioBuffer.put((byte)serverType);
				for(int i = 0; i < array.length; i++) {
					ioBuffer.putUnsignedShort(ServerChain.parseIntHex(array[i]));
				}
				ret = true;
			}
			break;
		}
		case ServerChain.TYPE_DOMAIN: {
			ioBuffer.put((byte)serverType);
			if(BPUtils.assembleStr(ioBuffer, serverAddress) == 0) {
				ret = true;
			}
			break;
		}
		default:
			break;
		}
		
		return ret;
	}

}
