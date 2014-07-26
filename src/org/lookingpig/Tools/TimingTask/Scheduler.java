package org.lookingpig.Tools.TimingTask;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 时间调度器
 * @author Pig
 *
 */
public class Scheduler {
	private static Scheduler scheduler;
	private Timer timer;
	private Map<Long, List<Job>> jobs;
	
	private Scheduler() {
		timer = new Timer();
		jobs = new HashMap<Long, List<Job>>();
	}
	
	/**
	 * 获得调度器实例
	 * @return 调度器
	 */
	public static Scheduler getInstance() {
		if (null == scheduler) {
			scheduler = new Scheduler();
		}
		
		return scheduler;
	}
	
	/**
	 * 添加定时任务
	 * @param time 触发时间
	 * @param job 要执行的任务
	 */
	public void addJob(String time, Job job) {
		Instant i = Instant.parse(time);
		long epoch = i.toEpochMilli();
		
		addJobToSequence(epoch, job);
	}
	
	/**
	 * 添加定时任务
	 * @param time 触发时间
	 * @param job 要执行的任务
	 */
	public void addJob(Date time, Job job) {
		long epoch = time.getTime();
		
		addJobToSequence(epoch, job);
	}
	
	/**
	 * 添加定时任务
	 * @param delay 延迟时间
	 * @param job 要执行的任务
	 */
	public void addJob(long delay, Job job) {
		Instant i = Instant.now();
		long epoch = i.toEpochMilli();
		
		addJobToSequence(epoch + delay, job);
	}
	
	/**
	 * 将工作加入序列中
	 * @param sequence 序列标识
	 * @param job 工作
	 */
	private void addJobToSequence(long sequence, Job job) {
		if (jobs.containsKey(sequence)) {
			jobs.get(sequence).add(job);
		} else {
			List<Job> jl = new ArrayList<Job>();
			jl.add(job);
			jobs.put(sequence, jl);
			
			timer.schedule(new SchedulerTask(sequence), new Date(sequence));
		}
	}
	
	/**
	 * 时间调度任务
	 * @author Pig
	 *
	 */
	private class SchedulerTask extends TimerTask {
		private long sequence;
		
		public SchedulerTask(long sequence) {
			this.sequence = sequence;
		}
		
		@Override
		public void run() {
			if (jobs.containsKey(sequence)) {
				for (Job job : jobs.get(sequence)) {
					job.execute();
				}
				
				jobs.remove(sequence);
			}
		}	
	}
}
