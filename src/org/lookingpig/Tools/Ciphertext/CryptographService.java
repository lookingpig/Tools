package org.lookingpig.Tools.Ciphertext;

import java.security.InvalidKeyException;

/**
 * 密文服务
 * @author Pig
 *
 */
public interface CryptographService {
	/**
	 * 加密
	 * @param content 需要加密的内容
	 * @return 加密后的内容
	 * @throws InvalidKeyException 
	 */
	public String encrypt(String content, String key) throws InvalidKeyException;
	
	/**
	 * 解密
	 * @param content 需要解密的内容
	 * @return 解密后的内容
	 * @throws InvalidKeyException 
	 */
	public String decrypt(String ciphertext, String key) throws InvalidKeyException;
}
