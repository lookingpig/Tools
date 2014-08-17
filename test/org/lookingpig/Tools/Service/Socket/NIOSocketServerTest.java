package org.lookingpig.Tools.Service.Socket;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;

public class NIOSocketServerTest {

	@Test
	public void testStart() {
		NIOSocketServer server = new NIOSocketServer("utf-8", 1024);
		assertTrue(server.start(8807));
		
		try {
			Thread.sleep(5000);
			Socket socket = new Socket("localhost", 8807);
			Reader read = new InputStreamReader(socket.getInputStream());
			Writer writ = new OutputStreamWriter(socket.getOutputStream());
			Thread.sleep(5000);
			server.sendMessageToAll("Hello");
			char []buf = new char[1024];
			read.read(buf);
			System.out.println(buf);
			writ.write("say hello");
			writ.flush();
			Thread.sleep(20000);
			socket.close();
			Thread.sleep(20000);
			Thread.sleep(100000000000000L);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
