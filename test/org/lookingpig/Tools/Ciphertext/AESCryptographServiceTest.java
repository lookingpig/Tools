package org.lookingpig.Tools.Ciphertext;

import static org.junit.Assert.*;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

public class AESCryptographServiceTest {

	@Test
	public void test() throws InvalidKeyException {
		CryptographService service = new AESCryptographService();
		String key = AESCryptographService.generateKey();
		assertNotNull(key);
		
		key = "pXBy7oGW81P16qs8kC6AWj9GWs5Su8lHjwHkpgNI++A=";
		String text = "qVX2IBf2JEJHgVALfDJHE6nZyCRomW6Kd8BRr6AtqQs0M8TRWRytQ/4adFEphTDAJa0ZgwgUbGnuoNf4OtSpMKA2Vye24IUV++axRzZp6vkQJdat6CvY//STka5mfOrr57i4fzm50Z6KGHeI+IHCx7IjjOTcFW555n9+jw34/zq4n52jYwKpgoszYPzpAB184EEa4nqx2Yk/JVndD4tHziHIn4mMTe3qhlPmsSUZwooKOfV1qyLgtZR/9RU3a9oYu69KJxUxMug/q2nseQb7XcieLxl83S53WeRzfAhHraB6rWLpLwfl4lEK3zg4AOnOtbCsI7Cueg1++9bikPMuHx3l1QRBp7U+XL6sIEl3Basj1/35jkC0j5ae4wkibh5BJzrL+8yEcAUPqkwf56+YunIBdho4AHZP8Yf3ekcGzG3x+9CLLjh6tyvoP/jRYcGt&wN7pUCr96Zq/JIQDFxZo+w==";
		String content = new String(Base64.decodeBase64(text));
		content = "qVX2IBf2JEJHgVALfDJHE6nZyCRomW6Kd8BRr6AtqQs0M8TRWRytQ/4adFEphTDAJa0ZgwgUbGnuoNf4OtSpMKA2Vye24IUV++axRzZp6vkQJdat6CvY//STka5mfOrr57i4fzm50Z6KGHeI+IHCx7IjjOTcFW555n9+jw34/zq4n52jYwKpgoszYPzpAB184EEa4nqx2Yk/JVndD4tHziHIn4mMTe3qhlPmsSUZwooKOfV1qyLgtZR/9RU3a9oYu69KJxUxMug/q2nseQb7XcieLxl83S53WeRzfAhHraB6rWLpLwfl4lEK3zg4AOnOtbCsI7Cueg1++9bikPMuHx3l1QRBp7U+XL6sIEl3Basj1/35jkC0j5ae4wkibh5BJzrL+8yEcAUPqkwf56+YunIBdho4AHZP8Yf3ekcGzG3x+9CLLjh6tyvoP/jRYcGt";
//		System.out.println(content);
//		String cripherText = service.encrypt(content, key);
//		System.out.println(cripherText);
		String t = service.decrypt(content, key);
		System.out.println(t);
		
		String msg = "DHTJvYtYu5M04tc0vmSPm/qtKkhvWneFogcMjdvbRlnb5Wv2iWRaVZhYo44NNj80AhVTgwDRj9pCBCwLjGQ9ngTVZNk2zpHGNhyHBRzXhXqcFpoU2htip5d9Ty3/QTopLtv0qhxO7VYJLxmD/8WemiSEiu5C2FJoSnxdL0dYarVxq9AVt+fsdmMChMekPsJ909qGbB9T8iSYiJ8SpR0wHdBESTUYR/llUvl7V/4n0qg+LRHtlQeUscNJL4+rR13O5eMmUOhUfvued45DQlI4t+l0962EO2Ps8ch7r/fJFR0EucdsnFLqX6Fxa5VIwxmLf2hMoK7MjENJ2blYNjU9yzlZZ0ZWUoK9u2+1EtTXF7M9qXbhECk/s8w5ZMZIuu5bu3NymrRCZGekHVNBr5aD5AYQGrmyWcUoZgp931ty9mxeRX4wYa5CAt0Ex0PDRmkR";
		String time = "2014-08-29 18:21:41";
 
		
		try {
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update((msg + time).getBytes());
			System.out.println(Base64.encodeBase64String(mdInst.digest()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
