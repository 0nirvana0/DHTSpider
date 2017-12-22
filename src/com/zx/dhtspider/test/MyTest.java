package com.zx.dhtspider.test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MyTest {

	public static void main(String[] args) {
		try {
			String data = "d1:ad2:id20:{?cOefglij01234567896:target20:anopqrstuvwxyz123456e1:q9:find_node1:t2:aa1:y1:qe";
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
					+ "\ndata::" + new String(recvPacket.getData()));
			sender.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
