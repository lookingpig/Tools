package org.lookingpig.Tools.Service.MessageService;

import static org.junit.Assert.*;

import org.junit.Test;
import org.lookingpig.Tools.Service.MessageService.Model.Message;

public class MessageFactoryTest {

	@Test
	public void testResolve() {
		String message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <message> 	<sender>aaa</sender> 	<send-number>000</send-number> 	<send-time>2014-08-08 11:12:32</send-time> 	<contents>	<key>value</key> 	</contents> </message>";
		System.out.println(message);
		
		Message m = MessageFactory.getFactory().resolve(message);
		assertNotNull(m);
		
		System.out.println("sendder : " + m.getSender());
		System.out.println("send-number : " + m.getSendNumber());
		System.out.println("send-time : " + m.getSendTime());
		
		for (String key : m.getContents().keySet()) {
			System.out.println(key + " : " + m.getContent(key));
		}
		
		String xml = MessageFactory.getFactory().blend(m);
		assertNotNull(xml);
		System.out.println(xml);
	}

}
