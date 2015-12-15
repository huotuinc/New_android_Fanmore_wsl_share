package cy.com.morefan.util;



import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import android.content.Context;


public class RSAUtil {
	public final static String pubilicKey = "";
	public final static String module = "1K2eMCDWO53Q/YAGioQ3OyVRSObM6laSJ6422Z4kDv+eoBXVqy6OdYw0F9FFAAhLvLbcq/0+PK14ViP4lOJGgufhNVsfywXpvuP/sBNPZqXeOTI/DJZhbMsxv+ZzoIsIWVKpmELuEYpqc6qrl10fxNfZ6oIlRpT+lZ3r/weEjms=";
	public static String exponentString = "AQAB";


	/**
	 * 获取公钥
	 * @param module
	 * @param exponentString
	 * @return
	 * @throws Exception
	 */
//	public static Key getPublicKeyByModuleAndExponent(String module, String exponentString){
//   	 byte[] modulusBytes = Base64.decode(module);
//        byte[] exponentBytes = Base64.decode(exponentString);
//        BigInteger modulus = new BigInteger(1, modulusBytes);
//        BigInteger exponent = new BigInteger(1, exponentBytes);
//
//        RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(modulus, exponent);
//        try {
//        	KeyFactory fact = KeyFactory.getInstance("RSA");
//            return fact.generatePublic(rsaPubKey);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//        return null;
//
//   }

	public static Key getPublicKeyByModuleAndExponent(String module, String exponentString) throws Exception{
   	 byte[] modulusBytes = Base64.decode(module);
        byte[] exponentBytes = Base64.decode(exponentString);
        BigInteger modulus = new BigInteger(1, modulusBytes);
        BigInteger exponent = new BigInteger(1, exponentBytes);

        RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePublic(rsaPubKey);
   }

	 public Key getPrivateKey(String module,String exponentString) throws Exception {
		 byte[] modulusBytes = Base64.decode(module);
	        byte[] exponentBytes = Base64.decode(exponentString);
	        BigInteger modulus = new BigInteger(1, modulusBytes);
	        BigInteger exponent = new BigInteger(1, exponentBytes);

	        RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(modulus, exponent);
	        KeyFactory fact = KeyFactory.getInstance("RSA");
	        return fact.generatePrivate(rsaPubKey);

   }
	/****************************************
     * 函数说明：getPublicKey 取得公钥
     *
     * @param key 公钥字符串
     * @throws Exception
     * @return PublicKey 返回公钥
     ***************************************/
    public static PublicKey getPublicKey(String key) throws Exception
    {
            byte[] keyBytes;
            keyBytes = Base64.decode(key);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
    }

	/**
	 * 获取公钥/私钥
	 * @param mContext
	 * @param fileName
	 * @return
	 */
	public static Key getKeyByAssetsFile(Context mContext, String fileName){
		 ObjectInputStream  ois = null;
		  try {
           /** 将文件中的公钥对象读出 */
           ois = new ObjectInputStream(mContext.getResources().getAssets().open(fileName));
           return (Key) ois.readObject();
         } catch (Exception e) {
             e.printStackTrace();
         }finally{
        	 if(ois != null)
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
         }
		  return null;
	}

	public static Key getKeyByAssetsFile2(Context mContext, String fileName){
		 ObjectInputStream  ois = null;
		  try {
          /** 将文件中的公钥对象读出 */
          ois = new ObjectInputStream(mContext.getResources().getAssets().open(fileName));


          DataInputStream dis = new DataInputStream(ois);
          byte[] keyBytes = new byte[1024];
          dis.readFully(keyBytes);
          dis.close();
          X509EncodedKeySpec spec =
        	      new X509EncodedKeySpec(keyBytes);
        	    KeyFactory kf = KeyFactory.getInstance("RSA");
        	    return kf.generatePublic(spec);
          //return (Key) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
       	 if(ois != null)
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        }
		  return null;
	}

	 /**
     * 加密方法
     * @param source 源数据
     * @return
     * @throws Exception
     */
    public static String encrypt(String source, Key publicKey) {
    	try {
    		 /** 得到Cipher对象来实现对源数据的RSA加密 */
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] b = source.getBytes();
            /** 执行加密操作 */
            byte[] b1 = cipher.doFinal(b);
           // BASE64Encoder encoder = new BASE64Encoder();
            return Base64.encode(b1);
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return null;

    }

    /**
     * 解密算法
     * @param cryptograph    密文
     * @return
     * @throws Exception
     */
    public static String decrypt(String cryptograph, Key privateKey) throws Exception {
        /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        //BASE64Decoder decoder = new BASE64Decoder();
        byte[] b1 = Base64.decode(cryptograph);
        /** 执行解密操作 */
        byte[] b = cipher.doFinal(b1);
        return new String(b);
    }
}
