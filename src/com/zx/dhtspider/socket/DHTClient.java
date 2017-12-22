package com.zx.dhtspider.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;

import com.zx.dhtspider.model.Table;

/**
 * DHT爬虫客户端 Created by zx on 2015/10/5.
 */
public class DHTClient {

	/** udp发送器 */
	private DatagramSocket sender = null;

	/** ip地址 */
	InetAddress inetAddress = null;
	int port = -1;
	private DatagramPacket sendPacket;

	public DHTClient(String ipAdress, int port) {
		try {
			sender = new DatagramSocket();
			sender.setSoTimeout(3000);
			this.inetAddress = InetAddress.getByName(ipAdress);
			this.port = port;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public byte[] sendData(String data) {
		if (checkFieldStatus()) {
			sendPacket = new DatagramPacket(data.getBytes(), data.getBytes().length, inetAddress, port);
			System.out.println(data);
			try {
				sender.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			byte[] bytes = new byte[10240];
			DatagramPacket recvPacket = new DatagramPacket(bytes, bytes.length);
			try {
				sender.receive(recvPacket);
				return recvPacket.getData();
			} catch (SocketTimeoutException e) {
				System.err.println(inetAddress.toString() + ":" + port + " connected time out.\n" + data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public byte[] findNodeOnDHT(byte[] nodeId) {
		String data = "d1:ad2:id20:" + new String(Table.getId()) + "6:target20:" + new String(nodeId)
				+ "e1:q9:find_node1:t2:aa1:y1:qe";
		System.out.println("nodeId:" + Arrays.toString(nodeId));
		return sendData(data);
	}

	/**
	 * 检查客户端状态
	 * 
	 * @return
	 */
	private boolean checkFieldStatus() {
		if (sender != null && inetAddress != null && port != -1) {
			return true;
		}
		return false;
	}

	public InetAddress getInetAddress() {
		return inetAddress;
	}

	public DHTClient setInetAddress(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
		return this;
	}

	public int getPort() {
		return port;
	}

	public DHTClient setPort(int port) {
		this.port = port;
		return this;
	}

	public DatagramSocket getSender() {
		return sender;
	}

	public DHTClient setSender(DatagramSocket sender) {
		this.sender = sender;
		return this;
	}

	public DatagramPacket getSendPacket() {
		return sendPacket;
	}

	public DHTClient setSendPacket(DatagramPacket sendPacket) {
		this.sendPacket = sendPacket;
		return this;
	}
}
