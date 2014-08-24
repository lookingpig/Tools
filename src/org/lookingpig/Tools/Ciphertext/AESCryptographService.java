package org.lookingpig.Tools.Ciphertext;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * AES加密服务
 * 
 * @author Pig
 * 
 */
public class AESCryptographService implements CryptographService {
	private static Logger logger = LogManager
			.getLogger(AESCryptographService.class);
	private static String KEY_ALGORITHM = "AES";
	private static String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
	private static int DEFAULT_KEY_LENGTH = 256;
	private CryptographService ornament;

	public AESCryptographService() {
	}

	public AESCryptographService(CryptographService ornament) {
		this.ornament = ornament;
	}

	
	/**
	 * 生成一个256位的密匙
	 * 
	 * @return 密匙
	 */
	public static String generateKey() {
		return generateKey(DEFAULT_KEY_LENGTH);
	}
	
	/**
	 * 生成一个密匙
	 * @param length 密匙长度
	 * @return 密匙
	 */
	public static String generateKey(int lenght) {
		KeyGenerator kg;
		String key = null;

		try {
			kg = KeyGenerator.getInstance(KEY_ALGORITHM);
			kg.init(lenght);
			SecretKey secretKey = kg.generateKey();
			key = Base64.encodeBase64String(secretKey.getEncoded());
		} catch (NoSuchAlgorithmException e) {
			logger.error("AES密匙生成失败！", e);
		}

		return key;
	}

	/**
	 * 获得密匙对象
	 * 
	 * @param key
	 *            密匙
	 * @return 密匙对象
	 */
	private static Key getKeyObject(String key) {
		return new SecretKeySpec(Base64.decodeBase64(key), KEY_ALGORITHM);
	}

	@Override
	public String encrypt(String content, String key) throws InvalidKeyException {
		if (null != ornament) {
			content = ornament.encrypt(content, key);
		}

		Key k = getKeyObject(key);
		String ciphertext = null;

		try {
			// 使用PKCS5Padding填充方式
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, k);
			// 将二进制密文使用Base64封装װ
			ciphertext = Base64.encodeBase64String(cipher.doFinal(content
					.getBytes()));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			logger.error("沒有找到AES算法或ECB模式或PKCS5Padding填充方式！", e);
		} catch (InvalidKeyException e) {
			logger.error("错误的密匙", e);
			throw e;
		} catch (IllegalBlockSizeException e) {
			logger.error("块大小错误！", e);
		} catch (BadPaddingException e) {
			logger.error("填充失败！", e);
		}

		return ciphertext;
	}

	@Override
	public String decrypt(String ciphertext, String key) throws InvalidKeyException {
		if (null != ornament) {
			ciphertext = ornament.decrypt(ciphertext, key);
		}

		Key k = getKeyObject(key);
		String content = null;
		Cipher cipher;

		try {
			cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, k);
			content = new String(
					cipher.doFinal(Base64.decodeBase64(ciphertext)));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			logger.error("沒有找到AES算法或ECB模式或PKCS5Padding填充方式！", e);
		} catch (InvalidKeyException e) {
			logger.error("错误的密匙", e);
			throw e;
		} catch (IllegalBlockSizeException e) {
			logger.error("块大小错误！", e);
		} catch (BadPaddingException e) {
			logger.error("填充失败！", e);
		}

		return content;
	}

}
