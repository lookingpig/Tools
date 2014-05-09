package org.lookingpig.Tools.Ciphertext;

import static org.junit.Assert.*;

import java.security.InvalidKeyException;

import org.junit.Test;

public class AESCryptographServiceTest {

	@Test
	public void test() throws InvalidKeyException {
		CryptographService service = new AESCryptographService();
		String key = AESCryptographService.generateKey();
		assertNotNull(key);
		System.out.println("key : " + key);
		String content = "≤‚ ‘≤‚ ‘≤‚ ‘≤‚ ‘£°";
		System.out.println("content : " + content);
		String ciphertext = service.encrypt(content, key);
		assertNotNull(ciphertext);
		System.out.println("ciphertext : " + ciphertext);
		content = service.decrypt(ciphertext, key);
		assertNotNull(content);;
		System.out.println("content : " + content);
	}
}
