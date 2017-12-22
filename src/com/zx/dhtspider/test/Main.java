package com.zx.dhtspider.test;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.zx.dhtspider.model.Bucket;
import com.zx.dhtspider.model.Node;
import com.zx.dhtspider.model.Table;
import com.zx.dhtspider.socket.DHTClient;
import com.zx.dhtspider.thread.NodeFinder;

/**
 * 程序入口 Created by zx on 2015/10/5.
 */
public class Main {

	/**
	 * 程序入口
	 * 
	 * @param args
	 *            参数组
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		DHTClient dhtClient = new DHTClient("67.215.246.10", 6881);// 67.215.246.10
		for (int i = 0; i < 20; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			List<Node> nodeList = SpiderUtils.getNodesInfo(dhtClient.findNodeOnDHT(SpiderUtils.buildNodeId()));
			for (Node node : nodeList) {
				Table.appendNode(node);
			}
		}
		for (Bucket bucket : Table.getBuckets()) {
			for (Node node : bucket.getNodes()) {
				new Thread(new NodeFinder(node.getIp(), node.getPort(), node.getId())).start();
			}
		}
	}
}