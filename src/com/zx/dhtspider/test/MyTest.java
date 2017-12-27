package com.zx.dhtspider.test;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.turn.ttorrent.bcodec.BDecoder;
import com.turn.ttorrent.bcodec.BEValue;

public class MyTest {
	static MyTest test = new MyTest();

	public static void main(String[] args) {

		// System.out.println(test.buildNodeId());
		test.udpTest();
	}

	private void udpTest() {
		try {
			String data = "d1:ad2:id20:abcdefghij01234567896:target20:mnopqrstuvwxyz123456e1:q9:find_node1:t2:aa1:y1:qe";
			InetAddress inetAddress = InetAddress.getByName("67.215.246.10");
			int port = 6881;
			DatagramSocket sender = new DatagramSocket();
			sender.setSoTimeout(5000);
			DatagramPacket sendPacket = new DatagramPacket(data.getBytes(), data.getBytes().length, inetAddress, port);
			sender.send(sendPacket);

			// Thread.sleep(5000);
			//
			// ============================
			byte[] bytes = new byte[1024];
			DatagramPacket recvPacket = new DatagramPacket(bytes, bytes.length);
			sender.receive(recvPacket);
			byte[] nodesData = recvPacket.getData();
			// System.out.println("ip::" +
			// recvPacket.getAddress().getHostAddress() + "\nport::" +
			// recvPacket.getPort()
			// + "\ndata::" + new String(recvPacket.getData(), "ISO8859-1"));
			sender.close();
			// ============================
			BEValue resBEval = BDecoder.bdecode(new ByteArrayInputStream(nodesData));
			Map<String, BEValue> resMap = resBEval.getMap();
			Map<String, BEValue> resDataMap = resMap.get("r").getMap();
			byte[] nodesInfo = resDataMap.get("nodes").getBytes();
			byte[] id = resDataMap.get("id").getBytes();
			byte[] node = Arrays.copyOf(nodesInfo, 20);
			System.out.println(Arrays.toString(id));
			System.out.println(Arrays.toString(nodesInfo));
			System.out.println(Arrays.toString(node));
			System.out.println(new String(node, "ISO8859-1"));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String buildNodeId() {
		return getRandomString(20);
	}

	/**
	 * 生成随机字符串
	 * 
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
