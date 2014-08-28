package org.lookingpig.Tools.Client;

import static org.junit.Assert.*;

import org.junit.Test;

public class HTTPClientTest {

	@Test
	public void testSendPost() {
		StringBuffer message = new StringBuffer();
		message.append("<xml>");
		message.append("<ServiceName>SocketConnectService</ServiceName>");
		message.append("</xml>");
		
		String response = HTTPClient.sendPost("http://durkauto.sinaapp.com/service/", message.toString());
//		String response = HTTPClient.sendPost("http://localhost:8808/service/", message.toString());
		assertNotNull(response);
		System.out.println(response);
	}

}
