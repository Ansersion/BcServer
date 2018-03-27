/**
 * 
 */
package bp_packet;

/**
 * @author Ansersion
 * 
 */
public class BPPartitation {
	static final int SYS_START_SIG_ID = 0xE000;
	static final int SYS_END_SIG_ID = 0xFFFF;
	static final int SYS_SIG_TAB_SIZE = SYS_END_SIG_ID - SYS_START_SIG_ID + 1;
	static final int SYS_SIG_TAB_NUM = 16;
	static final int SYS_SIG_TAB_UNIT_SIZE = SYS_SIG_TAB_SIZE / SYS_SIG_TAB_NUM;

	int Part1;
	int Part2;
	int PartStartID;
	byte[] SigTable;

	protected BPPartitation(int part1, int part2) {
		setPart(part1, part2);
	}

	public boolean setPart(int part1, int part2) {

		if (part1 < 0 || part1 > 4) {
			System.out.println("Error: BPPartitation: invalid part1(" + part1
					+ ")");
			return false;
		}
		if (part2 < 0 || part2 > 6) {
			System.out.println("Error: BPPartitation: invalid part2(" + part2
					+ ")");
			return false;
		}

		Part1 = part1;
		Part2 = part2;
		int part_size = SYS_SIG_TAB_UNIT_SIZE / (0x1 << Part2);
		PartStartID = SYS_START_SIG_ID + SYS_SIG_TAB_UNIT_SIZE * Part1;
		SigTable = new byte[part_size / 8]; // 8 bit of 1 byte

		return true;
	}

	public byte[] getSigTable() {
		return SigTable;
	}

	public int parseSymTable(byte[] buf, int offset) {
		int offset_old = offset;
		
		for (int i = 0; i < SigTable.length; i++) {
			SigTable[i] = buf[offset++];
		}

		return offset - offset_old;
	}

	static public BPPartitation createPartitation(int part1, int part2) {
		return new BPPartitation(part1, part2);
	}

	static public int parsePart1(byte part) {
		int ret = (part & 0xf0);
		ret = ret >>> 4;
		return ret;
	}

	static public int parsePart2(byte part) {
		int ret = (part & 0x0E);
		ret = ret >>> 1;
		return ret;
	}

	static public boolean parseEndFlag(byte part) {
		return (part & 0x01) == 0x01;
	}
}
