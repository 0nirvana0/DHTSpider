package com.zx.dhtspider.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.turn.ttorrent.bcodec.BDecoder;
import com.turn.ttorrent.bcodec.BEValue;
import com.turn.ttorrent.bcodec.InvalidBEncodingException;
import com.zx.dhtspider.constant.SpiderConstant;
import com.zx.dhtspider.model.Node;
import com.zx.dhtspider.model.Table;

/**
 * DHT爬虫工具类 Created by zx on 2015/10/4.
 */
public class SpiderUtils {

	final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	public static String toUnsignedString(BigDecimal bigDecimal, int shift) {
		BigDecimal divisor = new BigDecimal(shift);
		Deque<Character> numberDeque = new ArrayDeque<Character>();
		do {
			BigDecimal[] ba = bigDecimal.divideAndRemainder(divisor);
			bigDecimal = ba[0];
			numberDeque.addFirst(digits[ba[1].intValue()]);
		} while (bigDecimal.compareTo(BigDecimal.ZERO) > 0);
		StringBuilder builder = new StringBuilder();
		for (Character character : numberDeque) {
			builder.append(character);
		}
		return builder.toString();
	}

	public static byte[] buildNodeId() {
		byte[] bytes = new byte[20];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) digits[(int) (Math.random() * digits.length)];
		}
		return bytes;
	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	public static <T> List<T> extendArray(List<T> list1, List<T> list2) {
		List<T> result = new ArrayList<T>();
		for (T t : list1) {
			result.add(t);
		}
		for (T t : list2) {
			result.add(t);
		}
		return result;
	}

	public static List<Node> getNodesInfo(byte[] nodesData) throws InvalidBEncodingException, IOException {
		List<Node> result = new ArrayList<Node>();
		if (nodesData != null) {
			System.out.println(new String(nodesData));
			BEValue resBEval = BDecoder.bdecode(new ByteArrayInputStream(nodesData));
			Map<String, BEValue> resMap = resBEval.getMap();
			String t = resMap.get("t").getString();
			String y = resMap.get("y").getString();
			Map<String, BEValue> resDataMap = resMap.get("r").getMap();
			byte[] id = resDataMap.get("id").getBytes();
			byte[] nodesInfo = resDataMap.get("nodes").getBytes();
			int nodesInfoLength = nodesInfo.length;
			int nodeLength = SpiderConstant.NODE_INFO_LENGTH_ON_DHT;
			for (int i = 0; i < nodesInfoLength / nodeLength; i++) {
				Node node;
				byte[] nodeId = new byte[20];
				String nodeIp;
				byte[] nodeIpBytes = new byte[4];
				int nodePort;
				byte[] nodePortBytes = new byte[2];
				for (int j = i * nodeLength; j < (i + 1) * nodeLength; j++) {
					if (j % nodeLength <= SpiderConstant.NODE_INFO_ID_LAST_INDEX) {
						nodeId[j % nodeLength] = nodesInfo[j];
					}
					if (SpiderConstant.NODE_INFO_ID_LAST_INDEX < j % nodeLength
							&& j % nodeLength <= SpiderConstant.NODE_INFO_IP_LAST_INDEX) {
						nodeIpBytes[j % nodeLength - SpiderConstant.NODE_INFO_ID_LAST_INDEX - 1] = nodesInfo[j];
					}
					if (SpiderConstant.NODE_INFO_IP_LAST_INDEX < j % nodeLength && j % nodeLength <= nodeLength) {
						nodePortBytes[j % nodeLength - SpiderConstant.NODE_INFO_IP_LAST_INDEX - 1] = nodesInfo[j];
					}
				}
				long ip_temp = Long.parseLong(bytesToHexString(nodeIpBytes), 16);
				nodeIp = long2IpAdress(ip_temp);
				nodePort = Integer.parseInt(bytesToHexString(nodePortBytes), 16);
				node = new Node(nodeId, nodeIp, nodePort);
				result.add(node);
			}
		}

		return result;
	}

	public static String long2IpAdress(long src) {
		long l = 256 * 256 * 256;
		StringBuffer stringBuffer = new StringBuffer();
		while (l > 0) {
			stringBuffer.append(src / l).append(".");
			src = src % l;
			l /= 256;
		}
		stringBuffer.deleteCharAt(stringBuffer.length() - 1);
		return stringBuffer.toString();
	}

	public static <T> ArrayList<T> removeSameInArray(List<T> list) {
		return new ArrayList<T>(new LinkedHashSet<T>(list));
	}
}
