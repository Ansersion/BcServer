/**
 * 
 */
package bc_server;

import java.util.Vector;
import java.util.Map;
import java.util.Iterator;

/**
 * @author Ansersion
 * 
 */
public class BPPacket_GETACK extends BPPacket {

	int DeviceNum;
	Vector<DevSigData> VctDevSigData;

	protected BPPacket_GETACK(FixedHeader fx_header) {
		super(fx_header);
	}

	protected BPPacket_GETACK(FixedHeader fx_header, int dev_num) {
		super(fx_header);
		DeviceNum = dev_num;
	}

	public void setDevNum(int num) {
		DeviceNum = num;
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
		// byte encoded_byte;

		return false;
	}

	@Override
	public boolean assemblePayload() throws Exception {
		// TODO Auto-generated method stub
		byte encoded_byte;

		short dev_num = (short) (VctDevSigData.size() & 0x0000FFFF);
		getIoBuffer().putShort(dev_num);

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
					Map<Short, Byte> map = sig_data_ack.get1ByteDataMap();

					Iterator it = map.entrySet().iterator();
					while (it.hasNext()) {

						Map.Entry entry = (Map.Entry) it.next();
						getIoBuffer().putShort((Short) entry.getKey());
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
					Map<Short, Short> map = sig_data_ack.get2ByteDataMap();
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
					Map<Short, Integer> map = sig_data_ack.get4ByteDataMap();
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