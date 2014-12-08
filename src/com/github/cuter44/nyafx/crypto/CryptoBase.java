package com.github.cuter44.nyafx.crypto;

import java.nio.ByteBuffer;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;


/** Nyaguru Crypto Util
 * Encapulated Java Cryptography Architecture endpoints
 * @version 2.1.0-build.20141004
 */
public class CryptoBase
{
    public static final char[] base64char =
        {'A','B','C','D','E','F','G','H',
         'I','J','K','L','M','N','O','P',
         'Q','R','S','T','U','V','W','X',
         'Y','Z','a','b','c','d','e','f',
         'g','h','i','j','k','l','m','n',
         'o','p','q','r','s','t','u','v',
         'w','x','y','z','0','1','2','3',
         '4','5','6','7','8','9','+','/'};

    public static final String SR_SHA1PRNG = "SHA1PRNG";

    public static final String DEFAULT_RANDOM = SR_SHA1PRNG;

    protected SecureRandom rng;

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
        public static final CryptoBase INSTANCE = new CryptoBase();
    }

    public static CryptoBase getInstance()
    {
        return(
            Singleton.INSTANCE
        );
    }

  // DIGEST
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

  // BASE64
    public String base64Encode(byte[] bytes)
    {
        final int[] patterns =
            {0x0000, 0x0001, 0x0003, 0x0007, 0x000f, 0x001f, 0x003f, 0x007f,
            0x00ff, 0x01ff, 0x03ff,0x07ff, 0x0fff, 0x1fff, 0x3fff, 0x7fff};

        StringBuilder sb = new StringBuilder();
        int inbuf = 0;
        int inpos = 0;
        int inbufsize = 0;

        while (inpos < bytes.length)
        {
            inbuf = (inbuf << 8) | (bytes[inpos] & 0xff);
            inpos += 1;
            inbufsize += 8;

            if ((inpos%57) == 0 )
                sb.append("\n");

            while (inbufsize >= 6)
            {
                int outbuf = inbuf >> (inbufsize - 6);
                inbuf = inbuf & patterns[inbufsize - 6];
                inbufsize -= 6;

                sb.append(base64char[outbuf]);
            }
        }

        if (inpos%3 != 0)
        {
            inbuf = (inbuf << 8);
            inbufsize += 8;
            int outbuf = inbuf >> (inbufsize - 6);
            sb.append(base64char[outbuf]);

            while (inpos%3 != 0)
            {
                sb.append("=");
                inpos++;
            }
        }

        return(sb.toString());
    }

  // RANDOM
    public byte[] randomBytes(int length)
    {
        byte[] bytes = new byte[length];

        this.rng.nextBytes(bytes);
        return(bytes);
    }

  // CONVERT
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

    public static String byteToHex(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder(bytes.length*2);

        for (int i=0; i<bytes.length; i++)
            sb.append(
                String.format("%02x", bytes[i] & 0xff)
            );

        return(sb.toString());
    }

    public static String bytesToHex(byte[] bytes)
    {
        return(byteToHex(bytes));
    }
}
