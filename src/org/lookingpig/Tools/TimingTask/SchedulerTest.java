package org.lookingpig.Tools.TimingTask;

import org.junit.Assert;
import org.junit.Test;

public class SchedulerTest {

	@Test
	public void testAddJob() {
		Scheduler scheduler = Scheduler.getInstance();
		Assert.assertNotNull(scheduler);
		
		scheduler.addJob(1000, new Job() {
			
			@Override
			public void execute() {
				System.out.println("a");
			}
		});
		
		scheduler.addJob(1000, new Job() {
			
			@Override
			public void execute() {
				System.out.println("b");
			}
		});
		
		scheduler.addJob(2000, new Job() {
			
			@Override
			public void execute() {
				System.out.println("c");
			}
		});
		

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Assert.fail("fail");
		}
	}

}
