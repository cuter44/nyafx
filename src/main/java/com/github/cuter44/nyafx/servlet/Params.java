package com.github.cuter44.nyafx.servlet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/** Servlet 工具类
 * @version 1.1.0 builld 20140404
 */
public class Params
{
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

    /**
     * 从 HTTP 请求中检出相应参数值的简便封装
     *
     * 没有该命名的参数时返回null
     * @warning 不能用于在 Session 中检出 Object, 会返回它们的 toString()
     * 优先顺序为 Http请求参数 > Session > Cookie
     * @param req Http请求
     * @param name 参数的名字
     * @return String 参数的值
     */
    public static String get(HttpServletRequest req, String name)
    {
        // Server-side Attribute
        Object ra = req.getAttribute(name);
        if (ra != null)
            return(ra.toString());

        // Http Parameter
        String value = null;
        if ((value = req.getParameter(name)) != null)
            return(value);

        // Session
        HttpSession s = req.getSession();
        Object sa = s.getAttribute(name);
        if (sa != null)
            return(sa.toString());

        // Cookies
        Cookie[] carr = req.getCookies();
        if (carr != null)
            for (int i=0; i<carr.length; i++)
                if (carr[i].getName().equals(name))
                    return(carr[i].getValue());

        return(null);
    }

    public static String getString(HttpServletRequest req, String name)
    {
        return (
            get(req, name)
        );
    }

    public static String needString(HttpServletRequest req, String name)
    {
        String value = getString(req, name);
        if (value == null)
            throw(new MissingParameterException("Required parameter/cookies/attribute but not found:"+name));

        return(value);
    }

  // WRAPPER
    /**
     * 同 get() 但是转换为 Integer 返回
     *
     * 对于无法转换的值返回null, 没有对应的值返回null
     * 同理因为有一次转换所以效率略低
     * @param req Http请求
     * @param name 参数的名字
     * @return Integer 参数的值
     */
    public static Integer getInt(HttpServletRequest req, String name)
    {
        try
        {
            String v = get(req, name);
            return(v==null?null:Integer.valueOf(v));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return(null);
        }
    }

    public static Integer needInt(HttpServletRequest req, String name)
    {
        Integer value = getInt(req, name);
        if (value == null)
            throw(new MissingParameterException("Required int parameter/cookies/attribute but not found:"+name));

        return(value);
    }

    public static Float getFloat(HttpServletRequest req, String name)
    {
        try
        {
            String v = get(req, name);
            return(v==null?null:Float.valueOf(v));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return(null);
        }
    }

    public static Float needFloat(HttpServletRequest req, String name)
    {
        Float value = getFloat(req, name);
        if (value == null)
            throw(new MissingParameterException("Required float parameter/cookies/attribute but not found:"+name));

        return(value);
    }

    /**
     * 同 get() 但是转换为 Double 返回
     *
     * 对于无法转换的值返回null, 没有对应的值返回null
     * 同理因为有一次转换所以效率略低
     * @param req Http请求
     * @param name 参数的名字
     * @return Double 参数的值
     */
    public static Double getDouble(HttpServletRequest req, String name)
    {
        try
        {
            String v = get(req, name);
            return(v==null?null:Double.valueOf(v));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return(null);
        }
    }

    public static Double needDouble(HttpServletRequest req, String name)
    {
        Double value = getDouble(req, name);
        if (value == null)
            throw(new MissingParameterException("Required double parameter/cookies/attribute but not found:"+name));

        return(value);
    }

    /**
     * 同 get() 但是转换为 Byte 返回
     *
     * 对于无法转换的值返回null, 没有对应的值返回null
     * 同理因为有一次转换所以效率略低
     * @param req Http请求
     * @param name 参数的名字
     * @return Byte 参数的值
     */
    public static Byte getByte(HttpServletRequest req, String name)
    {
        try
        {
            String v = get(req, name);
            return(v==null?null:Byte.valueOf(v));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return(null);
        }
    }

    public static Byte needByte(HttpServletRequest req, String name)
    {
        Byte value = getByte(req, name);
        if (value == null)
            throw(new MissingParameterException("Required byte parameter/cookies/attribute but not found:"+name));

        return(value);
    }

    public static Long getLong(HttpServletRequest req, String name)
    {
        try
        {
            String v = get(req, name);
            return(v==null?null:Long.valueOf(v));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return(null);
        }
    }

    public static Long needLong(HttpServletRequest req, String name)
    {
        Long value = getLong(req, name);
        if (value == null)
            throw(new MissingParameterException("Required long parameter/cookies/attribute but not found:"+name));

        return(value);
    }

    /**
     * accept a Java/javascript:Date.getTime() as param-value.
     */
    public static Date getDate(HttpServletRequest req, String name)
    {
        try
        {
            Long v = getLong(req, name);
            return(v==null?null:new Date(v));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return(null);
        }
    }

    public static Date needDate(HttpServletRequest req, String name)
    {
        Date value = getDate(req, name);
        if (value == null)
            throw(new MissingParameterException("Required date parameter/cookies/attribute but not found:"+name));

        return(value);
    }

    public static byte[] getByteArray(HttpServletRequest req, String name)
    {
        try
        {
            String v = get(req, name);
            if (v == null)
                return(null);

            int l = v.length() / 2;

            ByteBuffer buf = ByteBuffer.allocate(l);
            for (int i=0; i<v.length(); i+=2)
            {
                buf.put(
                    Integer.valueOf(
                        v.substring(i, i+2),
                        16
                    ).byteValue()
                );
            }
            return(buf.array());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return(null);
        }
    }

    public static byte[] needByteArray(HttpServletRequest req, String name)
    {
        byte[] value = getByteArray(req, name);
        if (value == null)
            throw(new MissingParameterException("Required byte array parameter/cookies/attribute but not found:"+name));

        return(value);
    }

    public static Boolean getBoolean(HttpServletRequest req, String name)
    {
        String v = get(req, name);
        return(v==null?null:Boolean.valueOf(v));
    }

    public static Boolean needBoolean(HttpServletRequest req, String name)
    {
        Boolean value = getBoolean(req, name);
        if (value == null)
            throw(new MissingParameterException("Required boolean parameter/cookies/attribute but not found:"+name));

        return(value);
    }



    public static String[] getStringArray(HttpServletRequest req, String name)
    {
        try
        {
            String v = get(req, name);
            if (v == null)
                return(null);

            String[] sa = v.split(",");

            return(sa);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return(null);
        }
    }

    public static List<String> getStringList(HttpServletRequest req, String name)
    {
        String[] sa = getStringArray(req, name);
        return(
            sa != null ? Arrays.asList(sa) : null
        );
    }

    public static List<String> needStringList(HttpServletRequest req, String name)
    {
        List<String> value = getStringList(req, name);
        if (value == null)
            throw(new MissingParameterException("Required float parameter/cookies/attribute but not found:"+name));

        return(value);
    }

    public static Integer[] getIntArray(HttpServletRequest req, String name)
    {
        try
        {
            String[] sa = getStringArray(req, name);
            if (sa == null)
                return(null);

            Integer[] ia = new Integer[sa.length];
            for (int i=0; i<sa.length; i++)
                ia[i] = sa[i].isEmpty() ? null : Integer.valueOf(sa[i]);

            return(ia);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return(null);
        }
    }


    public static List<Long> getLongList(HttpServletRequest req, String name)
    {
        try
        {
            String[] sa = getStringArray(req, name);
            if (sa == null)
                return(null);

            List<Long> l = new ArrayList<Long>(sa.length);
            for (int i=0; i<sa.length; i++)
                l.add(
                    sa[i].isEmpty() ? null : Long.valueOf(sa[i])
                );

            return(l);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return(null);
        }
    }

    public static List<Long> needLongList(HttpServletRequest req, String name)
    {
        List<Long> value = getLongList(req, name);
        if (value == null)
            throw(new MissingParameterException("Required float parameter/cookies/attribute but not found:"+name));

        return(value);
    }

    public static Double[] getDoubleArray(HttpServletRequest req, String name)
    {
        try
        {
            String[] sa = getStringArray(req, name);
            if (sa == null)
                return(null);

            Double[] da = new Double[sa.length];
            for (int i=0; i<sa.length; i++)
                da[i] = sa[i].isEmpty() ? null : Double.valueOf(sa[i]);

            return(da);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return(null);
        }
    }

    public static List<Double> getDoubleList(HttpServletRequest req, String name)
    {
        Double[] da = getDoubleArray(req, name);
        return(
            da != null ? Arrays.asList(da) : null
        );
    }

    public static Date[] getDateArray(HttpServletRequest req, String name)
    {
        try
        {
            String[] sa = getStringArray(req, name);
            if (sa == null)
                return(null);

            Date[] da = new Date[sa.length];
            for (int i=0; i<sa.length; i++)
                da[i] = sa[i].isEmpty() ? null : new Date(Long.valueOf(sa[i]));

            return(da);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return(null);
        }
    }

    public static List<Date> getDateList(HttpServletRequest req, String name)
    {
        Date[] da = getDateArray(req, name);
        return(
            da != null ? Arrays.asList(da) : null
        );
    }

    public static List<Date> needDateList(HttpServletRequest req, String name)
    {
        List<Date> value = getDateList(req, name);
        if (value == null)
            throw(new MissingParameterException("Required float parameter/cookies/attribute but not found:"+name));

        return(value);
    }


}
