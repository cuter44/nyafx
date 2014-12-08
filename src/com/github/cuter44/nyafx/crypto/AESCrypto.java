package com.github.cuter44.nyafx.crypto;

import java.nio.ByteBuffer;

import javax.security.auth.kerberos.KerberosKey;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.KeyGenerator;
import javax.crypto.Cipher;


/** Encapulated Java Security Framework (JSF)
 * <br />
 *
 * @version 2.1.0-build.20141022
 */
public class AESCrypto extends CryptoBase
{
    private static final String BASE_ALGORITHM = "AES";

    //AES/CBC/NoPadding (128)
    //AES/CBC/PKCS5Padding (128)
    //AES/ECB/NoPadding (128)
    //AES/ECB/PKCS5Padding (128)
    public static final String AES_CBC_NOPADDING = "AES/CBC/NoPadding";
    public static final String AES_CBC_PKCS5 = "AES/CBC/PKCS5Padding";
    public static final String AES_ECB_NOPADDING = "AES/ECB/NoPadding";
    public static final String AES_ECB_PKCS5 = "AES/ECB/PKCS5Padding";

    public static final int KEY_LENGTH_128 = 128;

    public static final String DEFAULT_ALGORITHM = AES_ECB_PKCS5;
    public static final int DEFAULT_KEY_LENGTH = KEY_LENGTH_128;

    protected KeyGenerator keyGen;
    //protected SecretKeyFactory keyFac;
    protected String algorithm;

    public void setKeyLength(Integer keyLength)
    {
        try
        {
            if (keyLength == null)
                keyLength = DEFAULT_KEY_LENGTH;

            this.keyGen = KeyGenerator.getInstance(BASE_ALGORITHM);
            this.keyGen.init(keyLength, this.rng);
        }
        catch (NoSuchAlgorithmException ex)
        {
            throw(new RuntimeException(ex.getMessage(), ex));
        }

        return;
    }

    public void setAlgorithm(String newAlgorithm)
    {
        this.algorithm = newAlgorithm!=null?newAlgorithm:DEFAULT_ALGORITHM;

        return;
    }

  // SINGLETON
    public AESCrypto()
    {
        super();

        //try
        //{
            //this.keyFac = SecretKeyFactory.getInstance(BASE_ALGORITHM);
            this.setKeyLength(null);
            this.setAlgorithm(null);
        //}
        //catch (NoSuchAlgorithmException ex)
        //{
            //throw(new RuntimeException(ex.getMessage(), ex));
        //}

        return;
    }

    public AESCrypto(String algorithm, Integer keyLength)
    {
        super();

        //try
        //{
            //this.keyFac = SecretKeyFactory.getInstance(BASE_ALGORITHM);
            this.setAlgorithm(algorithm);
            this.setKeyLength(keyLength);
        //}
        //catch (NoSuchAlgorithmException ex)
        //{
            //throw(new RuntimeException(ex.getMessage(), ex));
        //}

        return;
    }

    private static class Singleton
    {
        public static final AESCrypto INSTANCE = new AESCrypto();
    }

    public static AESCrypto getInstance()
    {
        return(
            Singleton.INSTANCE
        );
    }

  // KEY
    public SecretKey generateKey()
    {
        return(
            this.keyGen.generateKey()
        );
    }

    public SecretKey generateKey(byte[] k)
    {
        return(
            new SecretKeySpec(k, BASE_ALGORITHM)
        );
    }

    /** not guaranteed to work...
     */
    public byte[] extractKey(SecretKey key)
    {
        if (key instanceof SecretKeySpec)
        {
            SecretKeySpec sKey = (SecretKeySpec)key;
            if (BASE_ALGORITHM.equals(sKey.getAlgorithm()))
                return(sKey.getEncoded());
        }

        if (key instanceof KerberosKey)
        {
            KerberosKey sKey = (KerberosKey)key;
            if (BASE_ALGORITHM.equals(sKey.getAlgorithm()))
                return(sKey.getEncoded());
        }

        throw(new IllegalArgumentException("Illegal or unregconized key type:"+key.toString()));
    }

  // CRYPTO
    public byte[] encrypt(byte[] in, SecretKey key)
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

    public byte[] decrypt(byte[] in, SecretKey key)
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
