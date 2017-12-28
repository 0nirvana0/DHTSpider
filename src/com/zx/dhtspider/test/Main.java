package com.zx.dhtspider.test;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.turn.ttorrent.util.SpiderUtils;
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
		try {
			for (int i = 0; i < 10; i++) {
				Thread.sleep(1000);
				List<Node> nodeList = SpiderUtils.getNodesInfo(dhtClient.findNodeOnDHT(SpiderUtils.buildNodeId()));
				for (Node node : nodeList) {
					Table.appendNode(node);
				}
			}
			for (Bucket bucket : Table.getBuckets()) {
				for (Node node : bucket.getNodes()) {
					 new NodeFinder(node.getIp(), node.getPort(), node.getId()).run();
					//new Thread(new NodeFinder(node.getIp(), node.getPort(), node.getId())).start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}