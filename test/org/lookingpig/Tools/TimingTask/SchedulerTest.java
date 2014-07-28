package org.lookingpig.Tools.TimingTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class SchedulerTest {

	@Test
	public void testAddJob() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		
		try {
			Date now = sdf.parse("19:00");
			System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		
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
