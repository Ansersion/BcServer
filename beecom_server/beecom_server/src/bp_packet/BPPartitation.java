/**
 * 
 */
package bp_packet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ansersion
 * 
 */
public class BPPartitation {
	private static final Logger logger = LoggerFactory.getLogger(BPPacketCONNECT.class); 
	
	static final int SYS_START_SIG_ID = 0xE000;
	static final int SYS_END_SIG_ID = 0xFFFF;
	static final int SYS_SIG_TAB_SIZE = SYS_END_SIG_ID - SYS_START_SIG_ID + 1;
	static final int SYS_SIG_TAB_NUM = 16;
	static final int SYS_SIG_TAB_UNIT_SIZE = SYS_SIG_TAB_SIZE / SYS_SIG_TAB_NUM;

	int part1;
	int part2;
	int partStartID;
	byte[] sigTable;

	protected BPPartitation(int part1, int part2) {
		setPart(part1, part2);
	}

	public boolean setPart(int part1, int part2) {

		if (part1 < 0 || part1 > 4) {
			logger.error("Error: BPPartitation: invalid part1({})", part1);
			return false;
		}
		if (part2 < 0 || part2 > 6) {
			logger.error("Error: BPPartitation: invalid part2({})", part2);
			return false;
		}

		this.part1 = part1;
		this.part2 = part2;
		int partSize = SYS_SIG_TAB_UNIT_SIZE / (0x1 << this.part2);
		partStartID = SYS_START_SIG_ID + SYS_SIG_TAB_UNIT_SIZE * this.part1;
		sigTable = new byte[partSize / 8]; // 8 bit of 1 byte

		return true;
	}

	public byte[] getSigTable() {
		return sigTable;
	}

	public int parseSymTable(byte[] buf, int offset) {
		int offsetOld = offset;
		
		for (int i = 0; i < sigTable.length; i++) {
			sigTable[i] = buf[offset++];
		}

		return offset - offsetOld;
	}

	public static BPPartitation createPartitation(int part1, int part2) {
		return new BPPartitation(part1, part2);
	}

	public static int parsePart1(byte part) {
		int ret = (part & 0xf0);
		ret = ret >>> 4;
		return ret;
	}

	public static int parsePart2(byte part) {
		int ret = (part & 0x0E);
		ret = ret >>> 1;
		return ret;
	}

	public static boolean parseEndFlag(byte part) {
		return (part & 0x01) == 0x01;
	}
}
