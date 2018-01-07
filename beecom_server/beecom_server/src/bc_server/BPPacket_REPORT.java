/**
 * 
 */
package bc_server;

import org.apache.mina.core.buffer.IoBuffer;

import java.util.Map;
import java.util.Vector;

/**
 * @author Ansersion
 * 
 */
public class BPPacket_REPORT extends BPPacket {

	int PackSeq;
	int DevNameLen;
	Vector<BPPartitation> Partitation;
	byte[] DevName;
	// DevSigData SigData;

	BPPacket_REPORT() {
		super();
		PackSeq = 0;
		DevNameLen = 0;
		DevName = new byte[256];
	}

	@Override
	public boolean parseVariableHeader(IoBuffer io_buf) throws Exception {
		// TODO Auto-generated method stub
		int client_id_len = 0;

		try {
			byte encoded_byte = 0;
			client_id_len = 2;

			byte[] id = new byte[client_id_len];
			for (int i = 0; i < client_id_len; i++) {
				id[i] = (byte) io_buf.get();
			}
			super.parseVrbClientId(id, client_id_len);

			encoded_byte = io_buf.get();
			super.parseVrbHeadFlags(encoded_byte);

			PackSeq = io_buf.getUnsignedShort();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return true;
	}

	@Override
	public boolean parseVariableHeader(byte[] buf) throws Exception {
		// TODO Auto-generated method stub
		try {
			int counter = 0;
			int client_id_len = 0;
			byte encoded_byte = 0;
			client_id_len = 2;

			byte[] id = new byte[client_id_len];
			for (int i = 0; i < client_id_len; i++) {
				id[i] = buf[counter++];
			}
			super.parseVrbClientId(id, client_id_len);

			encoded_byte = buf[counter++];
			super.parseVrbHeadFlags(encoded_byte);

			byte pack_seq_msb = buf[counter++];
			byte pack_seq_lsb = buf[counter++];
			PackSeq = BPPacket.assemble2ByteBigend(pack_seq_msb, pack_seq_lsb);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return true;
	}

	@Override
	public boolean parsePayload(byte[] buf) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			int counter = 0;
			
			DevNameLen = buf[counter++];
			for(int i = 0; i > DevNameLen; i++) {
				DevName[i] = buf[counter + DevNameLen - 1 - i];
			}
			
			boolean end_flag;
			int part1, part2;
			do {
				byte part = buf[counter++];
				part1 = BPPartitation.parsePart1(part);
				part2 = BPPartitation.parsePart2(part);
				end_flag = BPPartitation.parseEndFlag(part);
				BPPartitation new_part = BPPartitation.createPartitation(part1, part2);
				
				counter += new_part.parseSymTable(buf, counter);
				
				// TODO: add the custom symbol table
				
				Partitation.addElement(new_part);
				
				
			}while(!end_flag);		

		} catch (Exception e) {
			System.out.println("Error: parsePayload error");
			e.printStackTrace();
			throw e;
		}

		return true;
	}
	
	@Override
	public int parseVariableHeader() throws Exception {
		// TODO Auto-generated method stub

		try {
			// flags(1 byte) + client ID(2 byte) + sequence id(2 byte)
			byte flags = getIoBuffer().get();
			super.parseVrbHeadFlags(flags);

			int client_id = getIoBuffer().getUnsignedShort();
			getVrbHead().setClientId(client_id);

			int pack_seq = getIoBuffer().getUnsignedShort();
			getVrbHead().setPackSeq(pack_seq);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return 0;
	}

	/*
	 * @Override public boolean parseVariableHeader() throws Exception { // TODO
	 * Auto-generated method stub // return parseVariableHeader(); return false;
	 * }
	 */

	@Override
	public int parsePayload() throws Exception {
		// TODO Auto-generated method stub
		try {

			VariableHeader vb = getVrbHead();
			Payload pld = getPld();
			
			if (vb.getSigFlag()) {
				int sig_tab_num = getIoBuffer().get();
				if(null == getPld().getSigData()) {
					getPld().setSigData(new DevSigData());
				}
				getPld().getSigData().clear();
				for(int i = 0; i < sig_tab_num; i++) {
					getPld().getSigData().parseSigDataTab(getIoBuffer());
				}
				
			} else {

				if (vb.getDevNameFlag()) {
					int dev_name_len = getIoBuffer().get();
					byte[] dev_name = new byte[dev_name_len];
					getIoBuffer().get(dev_name);
					pld.setDevName(dev_name);
				}

				if (vb.getSysSigMapFlag()) {
					byte dist_and_class;
					int dist;
					int sys_sig_class;
					int map_num;

					do {
						dist_and_class = getIoBuffer().get();
						dist = (dist_and_class >> 4) & 0x0F;
						sys_sig_class = (dist_and_class >> 1) & 0x07;
						if (sys_sig_class >= 0x07) {
							throw new Exception(
									"Error: System signal class 0x7");
						}
						map_num = 0x200 / 8 / (1 << sys_sig_class);
						Byte[] sys_sig_map = new Byte[map_num];
						Map<Integer, Byte[]> sys_map = pld
								.getMapDist2SysSigMap();
						for (int i = 0; i < map_num; i++) {
							sys_sig_map[i] = getIoBuffer().get();
						}
						sys_map.put(dist, sys_sig_map);
					} while ((dist_and_class & VariableHeader.DIST_END_FLAG_MSK) != VariableHeader.DIST_END_FLAG_MSK);
				}
			}

		} catch (Exception e) {
			System.out.println("Error: parsePayload error");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}
	
	public Vector<BPPartitation> getPartitation() {
		return Partitation;
	}
}
