package com.zx.dhtspider.test;

import java.util.Arrays;
import java.util.Comparator;

import com.turn.ttorrent.util.SpiderUtils;

/**
 * 节点模型
 * Created by zx on 2015/10/4.
 */
public class NodeTest implements Comparable<NodeTest>, Comparator<NodeTest> {
	private byte[] id;
	private byte[] nodeId;
	private String ip;
	private int port;

	public NodeTest(byte[] id, byte[] nodeId, String ip, int port) {
		this.id = id;
		this.nodeId = nodeId;
		this.ip = ip;
		this.port = port;
	}

	public byte[] getNodeId() {
		return nodeId;
	}

	public void setNodeId(byte[] nodeId) {
		this.nodeId = nodeId;
	}

	public String getIp() {
		return ip;
	}

	public NodeTest setIp(String ip) {
		this.ip = ip;
		return this;
	}

	public int getPort() {
		return port;
	}

	public NodeTest setPort(int port) {
		this.port = port;
		return this;
	}

	public byte[] getId() {
		return id;
	}

	public NodeTest setId(byte[] id) {
		this.id = id;
		return this;
	}

	@Override
	public String toString() {
		return "Node{" + "id=" + Arrays.toString(id) + ", ip='" + ip + '\'' + ", port=" + port + '}';
	}

	public int compare(NodeTest o1, NodeTest o2) {
		if (o1 == o2) {
			return 0;
		} else if (o1 != null && o2 != null) {
			if (SpiderUtils.bytesToHexString(o1.id).compareTo(SpiderUtils.bytesToHexString(o2.id)) < 0) {
				return -1;
			} else {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		NodeTest node = (NodeTest) o;

		return Arrays.equals(id, node.id);

	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Arrays.hashCode(id);
	}

	/**
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(NodeTest o) {
		if (this == o) {
			return 0;
		} else if (o != null) {
			if (SpiderUtils.bytesToHexString(id).compareTo(SpiderUtils.bytesToHexString(o.id)) < 0) {
				return -1;
			} else {
				return 1;
			}
		}
		return 0;
	}
}
