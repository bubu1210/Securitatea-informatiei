package com.company;

import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

class AES {

    private static SecretKeySpec secretKey;
    private static byte[] key;
    public static String k1_ecb = "c h e i a e c b ";
    public String k2_cbc = "c h e i a c b c ";
    public static String k3 = "c h e i a a e s";
    public String initVector = "vectorul de init";

    public static void setKey(String myKey)
    {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String encryptECB(String strToEncrypt, String key)
    {
        try
        {
            setKey(key);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encVal = cipher.doFinal(strToEncrypt.getBytes());
            return Base64.getEncoder().encodeToString(encVal);
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decryptECB(String strToDecrypt, String secret)
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES");
            //cipher.init(Cipher.DECRYPT_MODE, secretKey);
            //return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decordedValue = Base64.getDecoder().decode(strToDecrypt);
            byte[] decValue = cipher.doFinal(decordedValue);
            return new String(decValue);

        }



        catch (Exception e)
        {
            System.out.println("Error while decrypting with ECB: " + e.toString());
        }
        return null;
    }
    public static String encryptCBC(String strToDecrypt, String secret)
    {
        String ceva="";
        return ceva;
    }

    public static String decryptCBC(String strToDecrypt, String secret)
    {
        String altceva="";
        return altceva;
    }
}