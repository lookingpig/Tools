package com.jadyer.util.codec;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * AES�ԳƼ����㷨
 * @see ===========================================================================================================
 * @see ������ʾ������Java6.0��ʵ��,������Ȼ��BouncyCastleҲ֧��AES�ԳƼ����㷨
 * @see ����,����Ҳ������AES�㷨ʵ��Ϊ�ο�,���RC2,RC4��Blowfish�㷨��ʵ��
 * @see ===========================================================================================================
 * @see ����DES�Ĳ���ȫ���Լ�DESede�㷨�ĵ�Ч,���Ǵ�����AES�㷨(Advanced Encryption Standard)
 * @see ���㷨��DESҪ��,��ȫ�Ը�,��Կ����ʱ���,�����Ժ�,�ڴ������,�ڸ�������Ӧ�ù㷺
 * @see Ŀǰ,AES�㷨ͨ�������ƶ�ͨ��ϵͳ�Լ�һЩ����İ�ȫ���,����һЩ����·������Ҳ����AES�㷨��������Э��
 * @see ===========================================================================================================
 * @see ����Java6.0֧�ִ󲿷ֵ��㷨,���ܵ���������,����Կ���Ȳ�����������
 * @see �����ر���Ҫע�����:���ʹ��256λ����Կ,����Ҫ�����������ļ�(Unlimited Strength Jurisdiction Policy Files)
 * @see ����Sun��ͨ��Ȩ���ļ�local_poblicy.jar��US_export_policy.jar������Ӧ����,���ǿ�����Sun���������滻�ļ�,�����������
 * @see ��ַΪhttp://www.oracle.com/technetwork/java/javase/downloads/index.html
 * @see �ڸ�ҳ������·��ҵ�Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 6,�������
 * @see http://download.oracle.com/otn-pub/java/jce_policy/6/jce_policy-6.zip
 * @see http://download.oracle.com/otn-pub/java/jce/7/UnlimitedJCEPolicyJDK7.zip
 * @see Ȼ�󸲸Ǳ���JDKĿ¼��JREĿ¼�µ�securityĿ¼�µ��ļ�����
 * @see ===========================================================================================================
 * @see ����AES�ĸ�����ϸ����,���Բο���ү�Ĳ���http://blog.csdn.net/kongqz/article/category/800296
 * @create Jul 17, 2012 6:35:36 PM
 * @author ����(http://blog.csdn/net/jadyer)
 */
public class AESCodec {
	//��Կ�㷨
	public static final String KEY_ALGORITHM = "AES";
	
	//�ӽ����㷨/����ģʽ/��䷽ʽ,Java6.0֧��PKCS5Padding��䷽ʽ,BouncyCastle֧��PKCS7Padding��䷽ʽ
	public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
	
	/**
	 * ������Կ
	 */
	public static String initkey() throws Exception{
		KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM); //ʵ������Կ������
		kg.init(128);                                              //��ʼ����Կ������:AESҪ����Կ����Ϊ128,192,256λ
		SecretKey secretKey = kg.generateKey();                    //������Կ
		return Base64.encodeBase64String(secretKey.getEncoded());  //��ȡ��������Կ������ʽ
	}
	
	
	/**
	 * ת����Կ
	 */
	public static Key toKey(byte[] key) throws Exception{
		return new SecretKeySpec(key, KEY_ALGORITHM);
	}
	
	
	/**
	 * ��������
	 * @param data ����������
	 * @param key  ��Կ
	 * @return ���ܺ������
	 * */
	public static String encrypt(String data, String key) throws Exception{
		Key k = toKey(Base64.decodeBase64(key));                           //��ԭ��Կ
		//ʹ��PKCS7Padding��䷽ʽ,����͵���ôд��(������BouncyCastle���ʵ��)
		//Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);              //ʵ����Cipher�������������ʵ�ʵļ��ܲ���
		cipher.init(Cipher.ENCRYPT_MODE, k);                               //��ʼ��Cipher��������Ϊ����ģʽ
		return Base64.encodeBase64String(cipher.doFinal(data.getBytes())); //ִ�м��ܲ��������ܺ�Ľ��ͨ��������Base64������д���
	}
	
	
	/**
	 * ��������
	 * @param data ����������
	 * @param key  ��Կ
	 * @return ���ܺ������
	 * */
	public static String decrypt(String data, String key) throws Exception{
		Key k = toKey(Base64.decodeBase64(key));
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, k);                          //��ʼ��Cipher��������Ϊ����ģʽ
		return new String(cipher.doFinal(Base64.decodeBase64(data))); //ִ�н��ܲ���
	}
	
	
	public static void main(String[] args) throws Exception {
		String source = "վ���ƶˣ����¼��̣�����ͨ��������һͷ�����ȴ���ֻΪ���Ƕ���0��1���ˡ���";
		System.out.println("ԭ�ģ�" + source);
		
		String key = initkey();
		System.out.println("��Կ��" + key);
		
		String encryptData = encrypt(source, key);
		System.out.println("���ܣ�" + encryptData);
		
		String decryptData = decrypt(encryptData, key);
		System.out.println("����: " + decryptData);
	}
}