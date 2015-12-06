package com.github.cuter44.nyafx.servlet;

import java.util.Map;
import java.util.HashMap;
//import java.util.ArrayList;
import java.util.Arrays;
import java.nio.CharBuffer;
import java.io.StringReader;
import java.io.IOException;

/** CharSequence escape implemented with KMP.
 */
public class Escape
{
    public static final Map<String, String> TWEAK_HTML_TEXT = new HashMap<String, String>();
    {
        TWEAK_HTML_TEXT.put("<", "&lt;");
        TWEAK_HTML_TEXT.put(">", "&gt;");
        TWEAK_HTML_TEXT.put("&", "&amp;");
    }

    /** Tweak to apply, no changes allowed after <code>compile()</code>-ed.
     */
    protected Map<String, String> tweak;
    protected String[] keys;
    /** KMP required fallback
     */
    protected int[][] fallback;
    /** Estimated output expand ratio
     */
    protected float bufferSize;

    public Escape(Map<String, String> tweak)
    {
        this.compile(tweak);

        return;
    }

    protected void compile(Map<String, String> tweak)
    {
        this.keys = tweak.keySet().toArray(
            new String[tweak.keySet().size()]
        );
        this.tweak = tweak;
        this.fallback = new int[this.keys.length][];
        this.bufferSize = 1.0f;

        int i = 0;

        for (String k:this.keys)
        {
            String v = this.tweak.get(k);
            float b = (float)v.length()/k.length();
            this.bufferSize = (bufferSize < b) ? b : this.bufferSize;

            int[] f = new int[k.length()];
            f[0] = -1;
            for (int j=1; j<f.length; j++)
            {
                f[j] = f[j-1];
                if (k.charAt(j) == k.charAt(f[j]+1))
                {
                    f[j]++;
                }
                else
                {
                    while (f[j]>-1)
                    {
                        if (k.charAt(j) == k.charAt(f[j]+1))
                        {
                            f[j]++;
                            break;
                        }
                        else
                            f[j] = f[f[j]];
                    }
                }
            }

            this.fallback[i++] = f;
        }

        return;
    }

    /** transient context of KMP tweak
     */
    protected class EscapeImplKMP
    {
        protected int[] p;
        protected String s;

        protected CharBuffer b;

        public EscapeImplKMP(String s)
        {
            this.p = new int[Escape.this.keys.length];
            Arrays.fill(this.p, -1);
            this.s = s;

            this.b = CharBuffer.allocate((int)(s.length()*Escape.this.bufferSize+1));

            return;
        }

        public String escape()
        {
            try
            {
                StringReader r = new StringReader(this.s);

                int c = r.read();
                while (c != -1)
                {
                    this.b.put((char)c);

                    for (int i=0; i<this.p.length; i++)
                        if (this.match((char)c, i))
                        {
                            Arrays.fill(this.p, -1);
                            this.b.position(this.b.position() - Escape.this.keys[i].length());
                            this.b.put(Escape.this.tweak.get(Escape.this.keys[i]));
                            break;
                        }

                    c = r.read();
                }

                int eof = this.b.position();
                this.b.position(0);
                this.b.limit(eof);
                return(this.b.toString());
            }
            catch (IOException ex)
            {
                // rarely occured
                throw(new RuntimeException(ex));
            }
        }

        protected boolean match(char c, int k)
        {
            int pk = this.p[k];

            if (c == Escape.this.keys[k].charAt(pk+1))
            {
                pk++;
            }
            else
            {
                while (pk>-1)
                {
                    if (c == Escape.this.keys[k].charAt(pk+1))
                    {
                        pk++;
                        break;
                    }
                    else
                        pk = Escape.this.fallback[k][pk];
                }
            }

            this.p[k] = pk;

            return(pk == Escape.this.keys[k].length()-1);
        }
    }

    public String escape(String s)
    {
        return(
            new EscapeImplKMP(s).escape()
        );
    }

    public static void main(String[] args)
    {
        try
        {
            Escape e = new Escape(TWEAK_HTML_TEXT);
            for (int i=0; i<e.keys.length; i++)
            {
                System.out.print(e.keys[i]+":"+e.tweak.get(e.keys[i])+":");
                for (int c:e.fallback[i])
                    System.out.print(c+" ");
                System.out.println();
            }

            System.out.println(e.escape("<html>&fake html ababa</html>"));
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
