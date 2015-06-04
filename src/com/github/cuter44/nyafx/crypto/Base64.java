package com.github.cuter44.nyafx.crypto;

import java.io.*;

public class Base64
{
    /**
     * @return crumb represents char c
     */
    protected static Integer fromB64Char(int c)
    {
        switch (c)
        {
            case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
            case 'G': case 'H': case 'I': case 'J': case 'K': case 'L':
            case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R':
            case 'S': case 'T': case 'U': case 'V': case 'W': case 'X':
            case 'Y': case 'Z':
                return(c-'A'+ 0);
            case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
            case 'g': case 'h': case 'i': case 'j': case 'k': case 'l':
            case 'm': case 'n': case 'o': case 'p': case 'q': case 'r':
            case 's': case 't': case 'u': case 'v': case 'w': case 'x':
            case 'y': case 'z':
                return(c-'a'+26);
            case '0': case '1': case '2': case '3': case '4': case '5':
            case '6': case '7': case '8': case '9':
                return(c-'0'+52);
            case '+':
                return(62);
            case '/':
                return(63);
            default:
                return(null);
        }
    }

    protected static int toB64Char(int crumb)
    {
        switch (crumb)
        {
            case  0: case  1: case  2: case  3: case  4: case  5:
            case  6: case  7: case  8: case  9: case 10: case 11:
            case 12: case 13: case 14: case 15: case 16: case 17:
            case 18: case 19: case 20: case 21: case 22: case 23:
            case 24: case 25:
                return((char)('A'+crumb- 0));
            case 26: case 27: case 28: case 29: case 30: case 31:
            case 32: case 33: case 34: case 35: case 36: case 37:
            case 38: case 39: case 40: case 41: case 42: case 43:
            case 44: case 45: case 46: case 47: case 48: case 49:
            case 50: case 51:
                return((char)('a'+crumb-26));
            case 52: case 53: case 54: case 55: case 56: case 57:
            case 58: case 59: case 60: case 61:
                return((char)('0'+crumb-52));
            case 62:
                return('+');
            case 63:
                return('/');
            default:
                throw(new IllegalArgumentException("crumb excess 63:"+crumb));
        }
    }

    /** Decode Base64 stream into bytes
     * @param byteStream In-param to receive result, usually a ByteArrayOutputStream
     * @see ByteArrayOutputStream
     */
    public static void decode(InputStream b64CharStream, OutputStream byteStream)
        throws IOException
    {
        InputStream s = b64CharStream;
        OutputStream t = byteStream;

        int c=0, b=0;
        Integer cr;
        int st = 0;

        while ((c=s.read())!=-1)
        {
            cr = fromB64Char(c);
            if (cr != null)
            {
                b = (b<<6) | cr;
                switch (st)
                {
                    case 0:
                        break;
                    case 1:
                        t.write((b & 0x0FF0)>>4);
                        break;
                    case 2:
                        t.write((b & 0x03FC)>>2);
                        break;
                    case 3:
                        t.write((b & 0x00FF));
                        break;
                }

                st = (st+1) % 4;
            }
        }

        return;
    }

    /** Encode bytes into Base64 stream
     * @param b64CharStream In-param to receive result, usually a ByteArrayOutputStream, then convert to String using String(byte[], Charset).
     * @see ByteArrayOutputStream
     */
    public static void encode(InputStream byteStream, OutputStream b64CharStream)
        throws IOException
    {
        InputStream s = byteStream;
        OutputStream t = b64CharStream;

        int c=0, b=0;
        int st=0, col=0;

        while ((c=s.read())!=-1)
        {
            b = (b<<8) | (c&0xFF);
            switch (st)
            {
                case 0:
                    t.write(
                        toB64Char((b & 0x00FC)>>2)
                    );
                    break;
                case 1:
                    t.write(
                        toB64Char((b & 0x03F0)>>4)
                    );
                    break;
                case 2:
                    t.write(
                        toB64Char((b & 0x0FC0)>>6)
                    );
                    t.write(
                        toB64Char((b & 0x003F))
                    );
                    break;
            }

            st = (st+1) % 3;

            col = (col+1) % 57;
            if (col == 0)
                t.write('\n');

        }

        // PADDING
        if (st == 1)
        {
            b = b<<8;
            t.write(
                toB64Char((b & 0x03F0)>>4)
            );
            t.write('=');
            t.write('=');
        }

        if (st == 2)
        {
            b = b<<8;
            t.write(
                toB64Char((b & 0x0FC0)>>6)
            );
            t.write('=');
        }

        return;
    }

    public static byte[] decode(String b64String)
    {
        try
        {
            ByteArrayInputStream b64Stream = new ByteArrayInputStream(b64String.getBytes("US-ASCII"));
            ByteArrayOutputStream bStream = new ByteArrayOutputStream(b64String.length()/4*3);

            decode(b64Stream, bStream);

            return(bStream.toByteArray());
        }
        catch (IOException ex)
        {
            // never occur
            throw(new RuntimeException(ex));
        }
    }

    public static String encode(byte[] bytes)
    {
        try
        {
            ByteArrayInputStream bStream = new ByteArrayInputStream(bytes);
            ByteArrayOutputStream b64Stream = new ByteArrayOutputStream((bytes.length/3+1)*4);

            encode(bStream, b64Stream);

            return(new String(b64Stream.toByteArray(), "US-ASCII"));
        }
        catch (IOException ex)
        {
            // never occur
            throw(new RuntimeException(ex));
        }
    }

    public static void main(String[] args)
    {
        try
        {
            byte[] s = "喵喵喵~! ฅ(・ω・ )ฅ".getBytes("UTF-8");
            for (int i=0; i<s.length; i++)
                System.out.print(
                    String.format("%02x", s[i] & 0xff)
                );
            System.out.println();

            String e = encode(s);
            System.out.println(e);

            byte[] d = decode(e);
            for (int i=0; i<d.length; i++)
                System.out.print(
                    String.format("%02x", d[i] & 0xff)
                );
            System.out.println();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

}
