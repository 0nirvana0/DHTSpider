package com.zx.dhtspider.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.turn.ttorrent.util.SpiderUtils;

/**
 * 桶模型 Created by zx on 2015/10/4.
 */
public class Bucket {

	/** 最小值 */
	private BigDecimal min;

	/** 最大值 */
	private BigDecimal max;

	/** 桶中节点列表 */
	private List<Node> nodes;

	/** 最后修改时间 */
	private LocalDateTime last_accessed_time;

	/**
	 * 构造函数
	 * 
	 * @param min
	 *            桶中节点hash最小值
	 * @param max
	 *            桶中节点hash最大值
	 */
	public Bucket(BigDecimal min, BigDecimal max) {
		this.min = min;
		this.max = max;
		Table.getBucketsFlagList().add(min);
		Table.getBucketsFlagList().add(max);
		Table.setBucketsFlagList(SpiderUtils.removeSameInArray(Table.getBucketsFlagList()));
		Collections.sort(Table.getBucketsFlagList());
		nodes = new ArrayList<Node>();
		last_accessed_time = LocalDateTime.now();
	}

	/**
	 * 判断节点是否处于桶范围
	 * 
	 * @param id
	 *            节点id
	 * @return 节点是否处于桶范围
	 */
	public boolean nodeIdInRange(byte[] id) {
		String hexId = SpiderUtils.bytesToHexString(id);
		return SpiderUtils.toUnsignedString(min, 16).compareTo(hexId) <= 0
				&& SpiderUtils.toUnsignedString(max, 16).compareTo(hexId) >= 0;
	}

	/**
	 * 如果新插入的node的nid属性长度不等于20, 终止. 如果满了, 抛出bucket已满的错误, 终止. 通知上层代码进行拆表. 如果未满,
	 * 先看看新插入的node是否已存在, 如果存在, 就替换掉, 不存在, 就添加, 添加/替换时, 更新该bucket的"新鲜程度".
	 * 
	 * @param node
	 *            节点
	 */
	public void appendNode(Node node) {
		if (node.getId().length != 20) {
			return;
		}
		if (nodes.size() < 8) {
			for (Node myNode : nodes) {
				if (Arrays.equals(myNode.getId(), node.getId())) {
					nodes.remove(myNode);
					break;
				}
			}
			nodes.add(node);
			last_accessed_time = LocalDateTime.now();
		} else {
			throw new RuntimeException("BucketFull");
		}
	}

	/**
	 * method for get min
	 */
	public BigDecimal getMin() {
		return min;
	}

	/**
	 * method for set min
	 */
	public void setMin(BigDecimal min) {
		this.min = min;
	}

	/**
	 * method for get max
	 */
	public BigDecimal getMax() {
		return max;
	}

	/**
	 * method for set max
	 */
	public void setMax(BigDecimal max) {
		this.max = max;
	}

	/**
	 * method for get nodes
	 */
	public List<Node> getNodes() {
		return nodes;
	}

	/**
	 * method for set nodes
	 */
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	/**
	 * method for get last_accessed_time
	 */
	public LocalDateTime getLast_accessed_time() {
		return last_accessed_time;
	}

	/**
	 * method for set last_accessed_time
	 */
	public void setLast_accessed_time(LocalDateTime last_accessed_time) {
		this.last_accessed_time = last_accessed_time;
	}

	@Override
	public String toString() {
		return "Bucket [min=" + min + ", max=" + max + ", nodes=" + nodes.size() + ", last_accessed_time="
				+ last_accessed_time + "]";
	}

}
