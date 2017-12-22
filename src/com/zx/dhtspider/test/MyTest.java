package com.zx.dhtspider.test;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class MyTest {
	static MyTest test = new MyTest();

	public static void main(String[] args) {

		// System.out.println(test.buildNodeId());
		test.udpTest();
	}

	private void udpTest() {
		try {
			String data = "d1:ad2:id20:111111111aaaaaaaaaaa6:target20:anopqrstuvwxyz123456e1:q9:find_node1:t2:aa1:y1:qe";
			// String data =
			// "d1:ad2:id20:S���/{��Ab���6rU�Z6:target20:֠>�89����W�6au}�l�1:q9:find_node1:t2:aa1:y1:qe";
			InetAddress inetAddress = InetAddress.getByName("67.215.246.10");
			int port = 6881;
			DatagramSocket sender = new DatagramSocket();
			sender.setSoTimeout(5000);
			DatagramPacket sendPacket = new DatagramPacket(data.getBytes(), data.getBytes().length, inetAddress, port);
			sender.send(sendPacket);

			// Thread.sleep(5000);
			//
			// ============================
			byte[] bytes = new byte[10240];
			DatagramPacket recvPacket = new DatagramPacket(bytes, bytes.length);
			sender.receive(recvPacket);
			System.out.println("ip::" + recvPacket.getAddress().getHostAddress() + "\nport::" + recvPacket.getPort()
					+ "\ndata::" + new String(recvPacket.getData(), "ISO8859-1"));
			sender.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String buildNodeId() {
		return getRandomString(20);
	}

	/**
	 * 生成随机字符串
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) { // length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytesToAscii(byte[] bytes) {

		String asciiStr = null;

		try {
			asciiStr = new String(bytes, "ISO8859-1");
		} catch (UnsupportedEncodingException e) {
		}
		return asciiStr;
	}

	private final static char[] HEX_SYMBOLS = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_SYMBOLS[v >>> 4];
			hexChars[j * 2 + 1] = HEX_SYMBOLS[v & 0x0F];
		}
		return new String(hexChars);
	}
}
