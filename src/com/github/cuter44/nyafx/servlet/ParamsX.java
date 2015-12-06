package com.github.cuter44.nyafx.servlet;

import java.lang.reflect.Type;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/** Servlet Parameter Toolkit
 *
 * Advanced version of Param.
 *
 * Differences from old:
 * <ul>
 * <li>Adjusted <code>get()</code> method sequence for better security
 * <li>General <code>get()</code>/<code>need()</code> for reduced code, and more flexiblity
 * <li>Tweak <code>parsers</code> to
 *
 * @version 2.0.0 build 2015/11/3
 */
public class ParamsX
{
    public static ParserBundle parsers = ParserBundle.getDefaultInstance();
    /**
     * 从 HTTP 请求中检出相应参数值的简便封装
     *
     * 没有该命名的参数时返回null
     * 优先顺序为 ReqAttribute > Session > ReqParam
     * No longer parse cookies as it is too low performance
     */
    public static Object get(HttpServletRequest req, String name)
    {
        Object r = null;

        // Request Attribute
        r = req.getAttribute(name);
        if (r!=null) return(r);

        // Session
        HttpSession s = req.getSession(false);
        if (s!=null)
        {
            r = s.getAttribute(name);
            if (r!=null) return(r);
        }

        // Request Parameter
        r = req.getParameter(name);
        if (r != null)
            return(r);

        //// Cookies
        //Cookie[] carr = req.getCookies();
        //if (carr != null)
            //for (int i=0; i<carr.length; i++)
                //if (carr[i].getName().equals(name))
                    //return(carr[i].getValue());

        return(r);
    }

    public static Object notNull(Object o)
        throws MissingParameterException
    {
        if (o != null)
            return(o);

        //else
        StackTraceElement e = Thread.currentThread().getStackTrace()[2];
        throw(
            new MissingParameterException(
                "Null parameter at "+e.getClassName()+'.'+e.getMethodName()+':'+e.getLineNumber()
            )
        );
    }

    public static Object get(HttpServletRequest req, String name, Type type)
    {
        return(
            parsers.parse(
                get(req, name),
                type
            )
        );
    }

    public static Object need(HttpServletRequest req, String name, Type type)
    {
        Object o = get(req, name, type);

        if (o != null)
            return(o);

        // else
        throw(
            new MissingParameterException("Missing required parameter: "+name+":"+type.toString())
        );
    }

    /** null-safed <code>Array.asList()</code>
     * supports only non-primitive array
     */
    public static List list(Object o)
    {
        return(
            (o != null) ?
                Arrays.asList((Object[])o)
            :
                null
        );
    }

  // STATIC STRING
    public static String getString(HttpServletRequest req, String name)
    {
        return(
            (String)get(req, name, String.class)
        );
    }

    public static String needString(HttpServletRequest req, String name)
    {
        return(
            (String)need(req, name, String.class)
        );
    }

    public static String[] getStringArray(HttpServletRequest req, String name)
    {
        return(
            (String[])get(req, name, String[].class)
        );
    }

    public static String[] needStringArray(HttpServletRequest req, String name)
    {
        return(
            (String[])need(req, name, String[].class)
        );
    }

    public static Escape escapeInstanceHTML0 = new Escape(Escape.TWEAK_HTML_TEXT);

    public static String escapeHTML0(String s)
    {
        if (s == null)
            return(null);

        return(
            escapeInstanceHTML0.escape(s)
        );
    }

  // STATIC INT
    public static Integer getInteger(HttpServletRequest req, String name)
    {
        return(
            (Integer)get(req, name, Integer.class)
        );
    }

    public static Integer needInteger(HttpServletRequest req, String name)
    {
        return(
            (Integer)need(req, name, Integer.class)
        );
    }

    public static Integer[] getIntegerArray(HttpServletRequest req, String name)
    {
        return(
            (Integer[])get(req, name, Integer[].class)
        );
    }

    public static Integer[] needIntegerArray(HttpServletRequest req, String name)
    {
        return(
            (Integer[])need(req, name, Integer[].class)
        );
    }

  // STATIC BYTE
    public static Byte getByte(HttpServletRequest req, String name)
    {
        return(
            (Byte)get(req, name, Byte.class)
        );
    }

    public static Byte needByte(HttpServletRequest req, String name)
    {
        return(
            (Byte)need(req, name, Byte.class)
        );
    }

    public static Byte[] getByteArray(HttpServletRequest req, String name)
    {
        return(
            (Byte[])get(req, name, Byte[].class)
        );
    }

    public static Byte[] needByteArray(HttpServletRequest req, String name)
    {
        return(
            (Byte[])need(req, name, Byte[].class)
        );
    }

  // STATIC LONG
    public static Long getLong(HttpServletRequest req, String name)
    {
        return(
            (Long)get(req, name, Long.class)
        );
    }

    public static Long needLong(HttpServletRequest req, String name)
    {
        return(
            (Long)need(req, name, Long.class)
        );
    }

    public static Long[] getLongArray(HttpServletRequest req, String name)
    {
        return(
            (Long[])get(req, name, Long[].class)
        );
    }

    public static Long[] needLongArray(HttpServletRequest req, String name)
    {
        return(
            (Long[])need(req, name, Long[].class)
        );
    }

  // STATIC DOUBLE
    public static Double getDouble(HttpServletRequest req, String name)
    {
        return(
            (Double)get(req, name, Double.class)
        );
    }

    public static Double needDouble(HttpServletRequest req, String name)
    {
        return(
            (Double)need(req, name, Double.class)
        );
    }

    public static Double[] getDoubleArray(HttpServletRequest req, String name)
    {
        return(
            (Double[])get(req, name, Double[].class)
        );
    }

    public static Double[] needDoubleArray(HttpServletRequest req, String name)
    {
        return(
            (Double[])need(req, name, Double[].class)
        );
    }

  // STATIC FLOAT
    public static Float getFloat(HttpServletRequest req, String name)
    {
        return(
            (Float)get(req, name, Float.class)
        );
    }

    public static Float needFloat(HttpServletRequest req, String name)
    {
        return(
            (Float)need(req, name, Float.class)
        );
    }

    public static Float[] getFloatArray(HttpServletRequest req, String name)
    {
        return(
            (Float[])get(req, name, Float[].class)
        );
    }

    public static Float[] needFloatArray(HttpServletRequest req, String name)
    {
        return(
            (Float[])need(req, name, Float[].class)
        );
    }

  // STATIC DATE
    public static Date getDate(HttpServletRequest req, String name)
    {
        return(
            (Date)get(req, name, Date.class)
        );
    }

    public static Date needDate(HttpServletRequest req, String name)
    {
        return(
            (Date)need(req, name, Date.class)
        );
    }

    public static Date[] getDateArray(HttpServletRequest req, String name)
    {
        return(
            (Date[])get(req, name, Date[].class)
        );
    }

    public static Date[] needDateArray(HttpServletRequest req, String name)
    {
        return(
            (Date[])need(req, name, Date[].class)
        );
    }

  // TEST STUB
    public static void main(String[] args)
    {
        Integer[] a = {1,2,3,4,5};

        List l = list(a);

        System.out.println(l.getClass());

        return;
    }

}
