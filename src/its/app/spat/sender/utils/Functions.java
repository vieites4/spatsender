package its.app.spat.sender.utils;

import java.nio.ByteOrder;
public final class Functions {

	public Functions() {

	}

	public byte[] int2ByteArray(int value, ByteOrder order) {
		java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocate(4); // in
																		// java,
																		// int
																		// takes
																		// 4
																		// bytes.
		buffer.order(order);
		return buffer.putInt(value).array();
	}

	
	public byte[] String2ByteArray(int value, ByteOrder order) {
		java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocate(4); // in
																		// java,
																		// int
																		// takes
																		// 4
																		// bytes.
		buffer.order(order);
		return buffer.putInt(value).array();
	}
	
	public byte[] intArray2ByteArray(int[] intArray) {
		int srcLength = intArray.length;
		byte[] dst = new byte[srcLength << 2];

		for (int i = 0; i < srcLength; i++) {
			int x = intArray[i];
			int j = i << 2;
			dst[j++] = (byte) ((x >>> 0) & 0xff);
			dst[j++] = (byte) ((x >>> 8) & 0xff);
			dst[j++] = (byte) ((x >>> 16) & 0xff);
			dst[j++] = (byte) ((x >>> 24) & 0xff);
		}
		return dst;
	}

}
