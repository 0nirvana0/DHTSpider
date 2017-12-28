package com.zx.dhtspider.model;

import com.turn.ttorrent.util.SpiderUtils;
import com.zx.dhtspider.constant.SpiderConstant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 表模型 Created by zx on 2015/10/4.
 */
public class Table {

	/** 路由表的id */
	private static byte[] id;

	/** 路由表内的桶 */
	private static List<Bucket> buckets;

	/** 桶最大最小值列表 */
	private static List<BigDecimal> bucketsFlagList;

	static {
		id = SpiderUtils.buildNodeId();
		buckets = new ArrayList<Bucket>();
		bucketsFlagList = new ArrayList<BigDecimal>();
		Bucket bucket = new Bucket(new BigDecimal(SpiderConstant.NODE_ID_MIN),
				new BigDecimal(SpiderConstant.NODE_ID_MAX));
		buckets.add(bucket);
	}

	private Table() {

	}

	public static void appendNode(Node node) {
		// 如果待插入的node的ID与自身一样, 那么就忽略, 终止接下来的操作.
		if (Arrays.equals(id, node.getId())) {
			return;
		}
		// 定位出待插入的node应该属于哪个bucket.
		int index = getBucketIndex(node.getId());
		Bucket bucket = buckets.get(index);
		try {
			// #协议里说道, 插入新节点时, 如果所归属的bucket是满的, 又都是活跃节点,
			// 那么先看看自身的node ID是否在该bucket的范围里, 如果不在该范围里, 那么就把
			// 该node忽略掉, 程序终止; 如果在该范围里, 就要把该bucket拆分成两个bucket,
			// 按范围"公平平分"node.
			bucket.appendNode(node);
		} catch (Exception e) {
			// 这个步骤很重要, 不然递归循环
			if (!bucket.nodeIdInRange(node.getId())) {
				return;
			}
			spilt_bucket(index);
			appendNode(node);

		}
	}

	/**
	 * 返回与目标node ID或infohash的最近K个node
	 * 
	 * @param targetId
	 * @return
	 */
	public static List<Node> findCloseNodes(byte[] targetId) {
		/*
		 * 
		 * 定位出与目标node ID或infohash所在的bucket, 如果该bucuck有K个节点, 返回. 如果不够到K个节点的话,
		 * 把该bucket前面的bucket和该bucket后面的bucket加起来, 只返回前K个节点. 还是不到K个话, 再重复这个动作.
		 * 要注意不要超出最小和最大索引范围. 总之, 不管你用什么算法, 想尽办法找出最近的K个节点.
		 */
		List<Node> nodes = new ArrayList<Node>();
		if (buckets.size() == 0) {
			return nodes;
		}
		int index = getBucketIndex(targetId);
		nodes = buckets.get(index).getNodes();
		int smallBucketIndex = index - 1;
		int bigBucketIndex = index + 1;
		while (nodes.size() < SpiderConstant.BUCKET_NODE_SPACE
				&& (smallBucketIndex > 0 || bigBucketIndex < buckets.size())) {
			if (smallBucketIndex > 0) {
				nodes = SpiderUtils.extendArray(nodes, buckets.get(smallBucketIndex).getNodes());
			}
			if (bigBucketIndex <= buckets.size()) {
				nodes = SpiderUtils.extendArray(nodes, buckets.get(bigBucketIndex).getNodes());
			}
			smallBucketIndex--;
			bigBucketIndex++;
		}
		Collections.sort(nodes);
		return nodes;
	}

	/**
	 * 拆表
	 * 
	 * @param index
	 *            待拆分的bucket(old bucket)的所在索引值.
	 */
	private static void spilt_bucket(int index) {
		/*
		 * index是待拆分的bucket(old bucket)的所在索引值. 假设这个old bucket的min:0, max:16.
		 * 拆分该old bucket的话, 分界点是8, 然后把old bucket的max改为8, min还是0. 创建一个新的bucket,
		 * new bucket的min=8, max=16. 然后根据的old bucket中的各个node的nid,
		 * 看看是属于哪个bucket的范围里, 就装到对应的bucket里. new bucket的所在索引值就在old bucket后面,
		 * 即index+1, 把新的bucket插入到路由表里.
		 */
		Bucket oldBucket = buckets.get(index);
		BigDecimal point = oldBucket.getMax()
				.subtract((oldBucket.getMax().subtract(oldBucket.getMin())).divide(new BigDecimal(2)))
				.setScale(0, BigDecimal.ROUND_DOWN);
		Bucket newBucket = new Bucket(point.add(new BigDecimal("1")), oldBucket.getMax());
		oldBucket.setMax(point);
		for (Node node : oldBucket.getNodes()) {
			if (newBucket.nodeIdInRange(node.getId())) {
				newBucket.getNodes().add(node);
			}
		}
		for (Node node : newBucket.getNodes()) {
			oldBucket.getNodes().remove(node);
		}
		buckets.add(index, newBucket);
	}

	/**
	 * 定位bucket的所在索引.<br/>
	 * 没有找到, 那么索引值就是最大索引值+1.
	 * 
	 * @param id
	 * @return
	 */
	private static int getBucketIndex(byte[] id) {
		// 传一个node的ID, 从buckets属性里循环, 定位该nid属于哪个bucket, 找到后, 返回对应的bucket的索引;
		// 没有找到, 说明就是要创建新的bucket了, 那么索引值就是最大索引值+1.
		// 注意: 为了简单, 就采用循环方式. 这个恐怕不是最有效率的方式.
		for (Bucket bucket : buckets) {
			if (bucket.nodeIdInRange(id)) {
				return buckets.indexOf(bucket);
			}
		}
		return buckets.size() - 1;
	}

	public static byte[] getId() {
		return id;
	}

	public static void setId(byte[] id) {
		Table.id = id;
	}

	public static List<Bucket> getBuckets() {
		return buckets;
	}

	public static void setBuckets(List<Bucket> buckets) {
		Table.buckets = buckets;
	}

	public static List<BigDecimal> getBucketsFlagList() {
		return bucketsFlagList;
	}

	public static void setBucketsFlagList(List<BigDecimal> bucketsFlagList) {
		Table.bucketsFlagList = bucketsFlagList;
	}
}
