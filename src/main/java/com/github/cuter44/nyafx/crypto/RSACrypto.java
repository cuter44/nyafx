package com.github.cuter44.nyafx.crypto;

import java.nio.ByteBuffer;
import java.math.BigInteger;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;


/** Encapulated Java Cryptography Architecture endpoints
 * <br />
 * 提供 RSA 的简便封装
 *
 * @version 2.5.0-build20150121
 */
public class RSACrypto extends CryptoBase
{
    private static final String BASE_ALGORITHM = "RSA";

    //RSA/ECB/PKCS1Padding (1024, 2048)
    //RSA/ECB/OAEPWithSHA-1AndMGF1Padding (1024, 2048)
    //RSA/ECB/OAEPWithSHA-256AndMGF1Padding (1024, 2048)
    public static final String RSA_ECB_PKCS1 = "RSA/ECB/PKCS1Padding";
    public static final String RSA_ECB_OAEPSHA1 = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";
    public static final String RSA_ECB_OAEPSHA256 = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    public static final int KEY_LENGTH_1024 = 1024;
    public static final int KEY_LENGTH_2048 = 2048;

    public static final String DEFAULT_ALGORITHM = RSA_ECB_PKCS1;
    public static final int DEFAULT_KEY_LENGTH = KEY_LENGTH_1024;

    protected KeyPairGenerator keyGen;
    protected KeyFactory keyFac;
    protected String algorithm;

    /** 设定密钥长度
     */
    public void setKeyLength(Integer keyLength)
    {
        try
        {
            if (keyLength == null)
                keyLength = DEFAULT_KEY_LENGTH;

            this.keyGen = KeyPairGenerator.getInstance(BASE_ALGORITHM);
            this.keyGen.initialize(keyLength, this.rng);
        }
        catch (NoSuchAlgorithmException ex)
        {
            // never happen
            ex.printStackTrace();
        }

        return;
    }

    /** 设定块密码的工作模式和填充, 可选的算法视乎使用的加密提供库, jdk 提供的 JCA 库由上面的静态常量列出
     */
    public void setAlgorithm(String newAlgorithm)
    {
        this.algorithm = newAlgorithm!=null?newAlgorithm:DEFAULT_ALGORITHM;

        return;
    }

  // SINGLETON
    public RSACrypto()
    {
        super();

        try
        {
            this.keyFac = KeyFactory.getInstance(BASE_ALGORITHM);
            this.setKeyLength(null);
            this.setAlgorithm(null);
        }
        catch (NoSuchAlgorithmException ex)
        {
            throw(new RuntimeException(ex.getMessage(), ex));
        }

        return;
    }

    public RSACrypto(String algorithm, Integer keyLength)
    {
        super();

        try
        {
            this.keyFac = KeyFactory.getInstance(BASE_ALGORITHM);
            this.setAlgorithm(algorithm);
            this.setKeyLength(keyLength);
        }
        catch (NoSuchAlgorithmException ex)
        {
            throw(new RuntimeException(ex.getMessage(), ex));
        }

        return;
    }

    private static class Singleton
    {
        public static RSACrypto instance = new RSACrypto();
    }

    public static RSACrypto getInstance()
    {
        return(
            Singleton.instance
        );
    }

  // KEY
    /** 随机生成用于 RSA 的密钥对
     */
    public KeyPair generateKey()
    {
        return(
            this.keyGen.generateKeyPair()
        );
    }

    /** 利用参数 m(modulus) 和 e(public exponent) 构造公钥
     */
    public RSAPublicKey generatePublic(byte[] m, byte[] e)
    {
        try
        {
            return((RSAPublicKey)
                this.keyFac.generatePublic(
                    new RSAPublicKeySpec(
                        new BigInteger(1, m),
                        new BigInteger(1, e)
                    )
                )
            );
        }
        catch (InvalidKeySpecException ex)
        {
            throw(new RuntimeException(ex.getMessage(), ex));
        }
    }

    /** 利用参数 m(modulus) 和 d(private exponent) 构造私钥
     */
    public RSAPrivateKey generatePrivate(byte[] m, byte[] d)
    {
        try
        {
            return((RSAPrivateKey)
                this.keyFac.generatePublic(
                    new RSAPrivateKeySpec(
                        new BigInteger(1, m),
                        new BigInteger(1, d)
                    )
                )
            );
        }
        catch (InvalidKeySpecException ex)
        {
            throw(new RuntimeException(ex.getMessage(), ex));
        }
    }

  // CRYPTO
    /** 使用给定的公钥加密字节流
     */
    public byte[] encrypt(byte[] in, PublicKey key)
    {
        if (in == null)
            throw(new IllegalArgumentException("param in must not be null."));
        if (key == null)
            throw(new IllegalArgumentException("param key must not be null."));

        try
        {
            Cipher c = Cipher.getInstance(this.algorithm);
            c.init(Cipher.ENCRYPT_MODE, key);

            byte[] out = c.doFinal(in);

            return(out);
        }
        catch (Exception ex)
        {
            throw(new RuntimeException(ex));
        }
    }

    /** 使用给定的私钥解密字节流
     */
    public byte[] decrypt(byte[] in, PrivateKey key)
    {
        if (in == null)
            throw(new IllegalArgumentException("in must have walue."));
        if (key == null)
            throw(new IllegalArgumentException("key must have value."));

        try
        {
            Cipher c = Cipher.getInstance(this.algorithm);
            c.init(Cipher.DECRYPT_MODE, key);

            byte[] out = c.doFinal(in);

            return(out);
        }
        catch (Exception ex)
        {
            throw(new RuntimeException(ex));
        }
    }

}
