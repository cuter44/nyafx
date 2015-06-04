package com.github.cuter44.nyafx.crypto;

import java.nio.ByteBuffer;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;

//import com.github.cuter44.nyafx.crypto.Base64;


/** Nyaguru Crypto Util
 * Encapulated Java Cryptography Architecture endpoints
 * 基于 JCA 封装的简化 API
 * @version 2.1.0-build.20141004
 */
public class CryptoBase
{
    public static final String SR_SHA1PRNG = "SHA1PRNG";

    public static final String DEFAULT_RANDOM = SR_SHA1PRNG;

    protected SecureRandom rng;

    /** 设定随机数生成器, 一般无需调用这个方法
     */
    public void setRng(String algorithm)
        throws NoSuchAlgorithmException
    {
        this.rng = SecureRandom.getInstance("SHA1PRNG");

        return;
    }

  // SINGLETON
    public CryptoBase()
    {
        try
        {
            this.setRng(SR_SHA1PRNG);
        }
        catch (Exception ex)
        {
            throw(new RuntimeException(ex));
        }

        return;
    }

    private static class Singleton
    {
        public static CryptoBase instance = new CryptoBase();
    }

    public static CryptoBase getInstance()
    {
        return(
            Singleton.instance
        );
    }

  // DIGEST
    /** 计算字节流 in 的 MD5
     * 通常需要用 bytesToHex 转换为可读的字符串
     */
    public byte[] MD5Digest(byte[] in)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] out = md.digest(in);

            return(out);
        }
        catch (NoSuchAlgorithmException ex)
        {
            // never occur
            ex.printStackTrace();
            return(null);
        }
    }

    /** 计算字节流 in 的 SHA-1
     * 通常需要用 bytesToHex 转换为可读的字符串
     */
    public byte[] SHA1Digest(byte[] in)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            byte[] out = md.digest(in);

            return(out);
        }
        catch (NoSuchAlgorithmException ex)
        {
            // never occur
            ex.printStackTrace();
            return(null);
        }
    }

  // BASE64
    /** 转换字节数组 bytes 为 base64 编码
     */
    public String base64Encode(byte[] bytes)
    {
        return(
            Base64.encode(bytes)
        );
    }

    /** 转换Base64串 b64String 为 字节数组
     */
    public byte[] base64Decode(String b64String)
    {
        return(
            Base64.decode(b64String)
        );
    }


  // RANDOM
    /** 生成长度为 length 的随机字节数组
     */
    public byte[] randomBytes(int length)
    {
        byte[] bytes = new byte[length];

        this.rng.nextBytes(bytes);
        return(bytes);
    }

  // CONVERT
    /** 转换十六进制字符串为字节数组
     */
    public static byte[] hexToBytes(String s)
    {
        int l = s.length() / 2;

        ByteBuffer buf = ByteBuffer.allocate(l);
        for (int i=0; i<s.length(); i+=2)
        {
            buf.put(
                Integer.valueOf(
                    s.substring(i, i+2),
                    16
                ).byteValue()
            );
        }

        return(buf.array());
    }

    /** 转换字节数组为十六进制字符串
     */
    public static String byteToHex(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder(bytes.length*2);

        for (int i=0; i<bytes.length; i++)
            sb.append(
                String.format("%02x", bytes[i] & 0xff)
            );

        return(sb.toString());
    }

    /** @copy
     */
    public static String bytesToHex(byte[] bytes)
    {
        return(byteToHex(bytes));
    }
}
