package com.zx.dhtspider.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.turn.ttorrent.bcodec.BDecoder;
import com.turn.ttorrent.bcodec.BEValue;
import com.turn.ttorrent.util.SpiderUtils;

public class MyTest {
	static MyTest test = new MyTest();
	final static byte[] idp = getRandomString(20).getBytes(StandardCharsets.ISO_8859_1);

	public static void main(String[] args) {

		// System.out.println(test.buildNodeId());
		test.udpTest();
	}

	private void udpTest() {

		byte[] id;
		byte[] target;
		String ip;
		int port;
		int sum = 0;
		for (int i = 0; i < 20; i++) {
			System.out.println(i + 1 + "  ============================");
			System.out.println("接收 ============================");
			target = getRandomString(20).getBytes(StandardCharsets.ISO_8859_1);
			byte[] recvPacketData = findNodeOnDHT(idp, target, "67.215.246.10", 6881);

			// ============================

			List<NodeTest> nodes = getNodesInfo(recvPacketData);
			int j = 0;
			for (NodeTest node : nodes) {
				System.out.println("  " + (i + 1) + "-" + ++j + "  ============================");
				id = node.getId();
				target = node.getNodeId();
				ip = node.getIp();
				port = node.getPort();

				// System.out.println(" ip:" + ip + "/" + port);
				recvPacketData = findNodeOnDHT(idp, target, ip, port);
				if (recvPacketData != null)
					sum++;
			}
		}
		System.out.println(" sum:" + sum);

	}

	public byte[] findNodeOnDHT(byte[] id, byte[] target, String ip, int port) {
		DatagramPacket recvPacket = null;
		DatagramSocket sender;
		try {
			String data = "d1:ad2:id20:" + new String(id, StandardCharsets.ISO_8859_1) + "6:target20:"
					+ new String(target, StandardCharsets.ISO_8859_1) + "e1:q9:find_node1:t2:aa1:y1:qe";
			System.out.println(data);
			InetAddress inetAddress = InetAddress.getByName(ip);

			sender = new DatagramSocket();
			sender.setSoTimeout(500);
			DatagramPacket sendPacket = new DatagramPacket(data.getBytes(StandardCharsets.ISO_8859_1), data.length(),
					inetAddress, port);
			try {
				sender.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}

			byte[] bytes = new byte[1024];
			recvPacket = new DatagramPacket(bytes, bytes.length);
			try {
				sender.receive(recvPacket);
				return recvPacket.getData();
			} catch (SocketTimeoutException e) {
				System.err.println(inetAddress.toString() + ":" + port + " connected time out.\n");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				sender.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static String getIp(byte[] ipbs) {
		String ip = "";

		for (int i = 0; i < 4; i++) {
			String tmp = String.valueOf(ipbs[i]);
			if (ipbs[i] < 0) {
				tmp = String.valueOf(127 + Math.abs(ipbs[i]));
			}
			if (i < 3) {
				ip += tmp + ".";
			} else {
				ip += tmp;
			}
		}
		return ip;

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

	public static List<NodeTest> getNodesInfo(byte[] nodesData) {
		List<NodeTest> result = new ArrayList<NodeTest>();
		if (nodesData == null) {
			return result;
		}
		try {
			BEValue resBEval = BDecoder.bdecode(new ByteArrayInputStream(nodesData));

			Map<String, BEValue> resMap = resBEval.getMap();
			Map<String, BEValue> resDataMap = resMap.get("r").getMap();
			byte[] id = resDataMap.get("id").getBytes();
			byte[] nodesInfo = resDataMap.get("nodes").getBytes();
			int nodeNum = nodesInfo.length / 26;
			byte[] nodeId;
			byte[] address;
			String nodeIp;
			byte[] portBytes;
			int nodePort;

			for (int i = 0; i < nodeNum; i++) {
				nodeId = Arrays.copyOfRange(nodesInfo, i * 26, i * 26 + 20);
				address = Arrays.copyOfRange(nodesInfo, i * 26 + 20, i * 26 + 26);
				nodeIp = getIp(Arrays.copyOfRange(address, 0, 4));
				portBytes = Arrays.copyOfRange(address, 4, 6);
				nodePort = Integer.parseInt(SpiderUtils.bytesToHexString(portBytes), 16);
				// System.out.println(Arrays.toString(nodeId));
				// System.out.println(Arrays.toString(Arrays.copyOfRange(address,
				// 0, 4)));
				// System.out.println(Arrays.toString(Arrays.copyOfRange(address,
				// 4, 6)));
				// System.out.println("=======");
				result.add(new NodeTest(id, nodeId, nodeIp, nodePort));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;

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
