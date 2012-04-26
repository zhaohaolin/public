
package edu.hziee.common.lang.statistic;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: JVMMemory.java 14 2012-01-10 11:54:14Z archie $
 */
public class JVMMemory {
	private final MemoryMXBean memoryMXBean;

	public JVMMemory() {
		memoryMXBean = ManagementFactory.getMemoryMXBean();
	}

	public long getHeapMemoryUsed() {
		return memoryMXBean.getHeapMemoryUsage().getUsed();
	}

	public long getHeapMemoryMax() {
		return memoryMXBean.getHeapMemoryUsage().getMax();
	}

	public long getHeapMemoryCommitted() {
		return memoryMXBean.getHeapMemoryUsage().getCommitted();
	}

	public long getHeapMemoryInit() {
		return memoryMXBean.getHeapMemoryUsage().getInit();
	}

	public double getHeapMemoryUsedInMBytes() {
		return ((double) getHeapMemoryUsed() / (1024 * 1024));
	}

	public double getHeapMemoryMaxInMBytes() {
		return ((double) getHeapMemoryMax() / (1024 * 1024));
	}

	public double getHeapMemoryCommittedInMBytes() {
		return ((double) getHeapMemoryCommitted() / (1024 * 1024));
	}

	public double getHeapMemoryInitInMBytes() {
		return ((double) getHeapMemoryInit() / (1024 * 1024));
	}
}
