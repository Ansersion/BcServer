/**
 * 
 */
package bp_packet;

import java.util.Vector;

import other.CrcChecksum;

import java.util.Map;
import java.util.Iterator;

/**
 * @author Ansersion
 * 
 */
public class BPPacketGETACK extends BPPacket {

	int DeviceNum;
	Vector<DevSigData> VctDevSigData;

	protected BPPacketGETACK(FixedHeader fx_header) {
		super(fx_header);
	}

	protected BPPacketGETACK(FixedHeader fx_header, int dev_num) {
		super(fx_header);
		DeviceNum = dev_num;
	}

	
	protected BPPacketGETACK() {
		super();
		FixedHeader fx_head = getFxHead();
		fx_head.setPacketType(BPPacketType.GETACK);
		fx_head.setCrcType(CrcChecksum.CRC32);
	}
	
	public void setDevNum(int num) {
		DeviceNum = num;
	}
	
	@Override
	public int parseVariableHeader() throws Exception {
		// TODO Auto-generated method stub

		try {
			// flags(1 byte) + client ID(2 byte) + sequence ID(2 byte) + return code(1 byte)
			byte flags = 0;

			flags = getIoBuffer().get();
			super.parseVrbHeadFlags(flags);

			int client_id = getIoBuffer().getUnsignedShort();
			getVrbHead().setClientId(client_id);

			int seq_id = getIoBuffer().getUnsignedShort();
			getVrbHead().setPackSeq(seq_id);
			
			byte ret_code = getIoBuffer().get();
			getVrbHead().setRetCode(ret_code);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return 0;
	}

	@Override
	public int parsePayload() throws Exception {
		// TODO Auto-generated method stub
		try {
			if(getVrbHead().getRetCode() != 0) {
				// TODO: parse the error return code
				return -1;
			}
			// int user_name_len = getIoBuffer().get();
			// user_name_len = (user_name_len << 8) + getIoBuffer().get();
			int sig_tab_num = getIoBuffer().get();
			DevSigData sig_data = new DevSigData();
			for(int i = 0; i < sig_tab_num; i++) {
				boolean ret = sig_data.parseSigDataTab(getIoBuffer());
				if(false == ret) {
					System.out.println("Error(GETACK): parsePayload error");
					return -1;
				}
				
			}
			getPld().setSigData(sig_data);

		} catch (Exception e) {
			System.out.println("Error: parsePayload error");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}

	@Override
	public boolean assembleFixedHeader() throws Exception {
		// TODO Auto-generated method stub
		int pack_type = getPackTypeIntFxHead();
		byte pack_flags = getPackFlagsByteFxHead();
		byte encoded_byte = (byte) (((pack_type & 0xf) << 4) | (pack_flags & 0xf));

		getIoBuffer().put(encoded_byte);

		// Remaininglength 1 byte reserved
		getIoBuffer().put((byte) 0);

		return false;
	}

	@Override
	public boolean assembleVariableHeader() throws Exception {
		// TODO Auto-generated method stub
		byte encoded_byte;
		
		int pack_seq = getVrbHead().getPackSeq();
		getIoBuffer().putUnsignedShort(pack_seq);
		byte ret_code = (byte)getVrbHead().getRetCode();
		getIoBuffer().put(ret_code);

		return false;
	}

	@Override
	public boolean assemblePayload() throws Exception {
		// TODO Auto-generated method stub
		byte encoded_byte;

		int dev_num = VctDevSigData.size() & 0x0000FFFF;
		getIoBuffer().putUnsignedShort(dev_num);

		for (int i = 0; i < dev_num; i++) {
			DevSigData sig_data_ack = VctDevSigData.get(i);
			int sig_num;
			sig_num = sig_data_ack.get1ByteDataMap().size();

			if (sig_num > 0) {
				if (sig_num > 0x3F) {
					System.out.println("WARNING: too many signal of 1 byte");
				} else {
					encoded_byte = (byte) (sig_num & 0x3F);
					// clear bit6, bit7
					encoded_byte &= ~0xC0;
					// set the value type of bit6, bit7 to '00b'
					encoded_byte |= 0x00;
					getIoBuffer().put(encoded_byte);
					Map<Integer, Byte> map = sig_data_ack.get1ByteDataMap();

					Iterator it = map.entrySet().iterator();
					while (it.hasNext()) {

						Map.Entry entry = (Map.Entry) it.next();
						getIoBuffer().putUnsignedShort((Integer) entry.getKey());
						getIoBuffer().put((Byte) entry.getValue());
					}

				}
			}

			sig_num = sig_data_ack.get2ByteDataMap().size();
			if (sig_num > 0) {
				if (sig_num > 0x3F) {
					System.out.println("WARNING: too many signal of 2 byte");
				} else {
					encoded_byte = (byte) (sig_num & 0x3F);
					// clear bit6, bit7
					encoded_byte &= ~0xC0;
					// set the value type of bit6, bit7 to '00b'
					encoded_byte |= 0x40;
					getIoBuffer().put(encoded_byte);
					Map<Integer, Short> map = sig_data_ack.get2ByteDataMap();
					Iterator it = map.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry entry = (Map.Entry) it.next();
						getIoBuffer().putShort((Short) entry.getKey());
						getIoBuffer().putShort((Short) entry.getValue());
					}

				}
			}

			sig_num = sig_data_ack.get4ByteDataMap().size();
			if (sig_num > 0) {
				if (sig_num > 0x3F) {
					System.out.println("WARNING: too many signal of 4 byte");
				} else {
					encoded_byte = (byte) (sig_num & 0x3F);
					// clear bit6, bit7
					encoded_byte &= ~0xC0;
					// set the value type of bit6, bit7 to '00b'
					encoded_byte |= 0x80;
					getIoBuffer().put(encoded_byte);
					Map<Integer, Integer> map = sig_data_ack.get4ByteDataMap();
					Iterator it = map.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry entry = (Map.Entry) it.next();
						getIoBuffer().putShort((Short) entry.getKey());
						getIoBuffer().putInt((Integer) entry.getValue());
					}

				}
			}
		}

		return false;
	}

}
