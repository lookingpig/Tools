package org.lookingpig.Tools.Service.Socket;

import java.net.Socket;

import org.junit.Assert;
import org.junit.Test;

public class NIOSocketServerTest {

	@Test
	public void testStart() {
		try {
			NIOSocketServer server = new NIOSocketServer("utf-8", 1024);
			Assert.assertTrue(server.start("192.168.1.100", 8807));
//			
//			Thread.sleep(5000);
			
//			Socket socket = new Socket("117.36.93.186", 8807);
//			Socket socket = new Socket("192.168.1.102", 8807);
			
			Thread.sleep(100000000000000L);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		try {
//			Thread.sleep(5000);
//			Socket socket = new Socket("localhost", 8807);
//			Reader read = new InputStreamReader(socket.getInputStream());
//			Writer writ = new OutputStreamWriter(socket.getOutputStream());
//			Thread.sleep(5000);
//			server.sendMessageToAll("Hello");
//			char []buf = new char[1024];
//			read.read(buf);
//			System.out.println(buf);
//			writ.write("say hello");
//			writ.flush();
//			Thread.sleep(20000);
//			socket.close();
//			Thread.sleep(20000);
//			Thread.sleep(100000000000000L);
//		} catch (UnknownHostException e1) {
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
	}

}
