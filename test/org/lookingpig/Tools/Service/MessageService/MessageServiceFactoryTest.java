package org.lookingpig.Tools.Service.MessageService;

import static org.junit.Assert.*;

import org.junit.Test;

public class MessageServiceFactoryTest {

	@Test
	public void testLoadServicesString() {
		String path = this.getClass().getResource("/").getPath() + "messageservice_config.xml";
		System.out.println(path);
		String name = "";
		MessageServiceFactory.getFactory().loadServices(path);
		MessageService service = MessageServiceFactory.getFactory().getService(name);
		assertNotNull(service);
	}

}
