package com.github.cuter44.nyafx.hj;

import java.lang.reflect.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.http.*;

import org.hibernate.metadata.*;
import com.alibaba.fastjson.*;

import com.github.cuter44.nyafx.servlet.*;
import static com.github.cuter44.nyafx.servlet.Params.*;

/** Parameter parser according mapped entity
 * @since 2.12.0
 */
public class HibernateParamParser
{
    /** 不解析此字段
     */
    public static final int SKIP            = 0x1;
    ///** 仅序列化(实体)的ID属性
     //*/
    //public static final int ID_ONLY         = 0x2;
    /** 仅解析给出的字段, 包括未被持久化映射的字段
     */
    public static final int RETAIN_NAMED    = 0x4;
    /** 不解析给出的字段
     */
    public static final int EXCLUDE_NAMED   = 0x8;
    /** 不写入NULL
     */
    public static final int IGNORE_NULL     = 0x10;
    /** 必填
     */
    public static final int REQUIRED        = 0x20;

    protected static int extractDotConf(Object o, int defaults)
    {
        if (o == null)
            return(defaults);

        if (o instanceof Integer)
            return((Integer)o);

        if (o instanceof JSONObject)
        {
            try
            {
                return(((JSONObject)o).getIntValue("."));
            }
            catch (Exception ex)
            {
                return(defaults);
            }
        }

        return(defaults);
    }

    protected static JSONObject wrapDotConf(Object o, int defaults)
    {
        JSONObject j = new JSONObject();
        j.put(".", defaults);

        if (o == null)
            return(j);

        if (o instanceof Integer)
        {
            j.put(".", o);
            return(j);
        }

        if (o instanceof JSONObject)
            return((JSONObject)o);

        return(j);
    }

    protected static Field getField(Object o, String fieldName)
    {
        Field f = null;
        Class c = o.getClass();

        while ((f == null) && (c!=null))
        {
            try
            {
                f = c.getDeclaredField(fieldName);
            }
            catch (NoSuchFieldException ex)
            {
                c = c.getSuperclass();
            }
        }

        if (f != null)
            f.setAccessible(true);

        // else
        return(f);
    }

    protected ClassMetaNarrator cmn;

    public HibernateParamParser(ClassMetaNarrator classMetaNarrator)
    {
        this.cmn = classMetaNarrator;

        return;
    }


    public Object parse(Object o, Class clazz, HttpServletRequest req, JSONObject conf)
        throws InstantiationException
    {
        try
        {
            // VALIDATE
            if (clazz == null)
                throw(new IllegalArgumentException("Parameter c must not be null."));

            if (req == null)
                throw(new IllegalArgumentException("Parameter req must not be null."));

            o    = (o    != null) ? o    : clazz.newInstance();
            if (!clazz.isInstance(o))
                throw(new IllegalArgumentException("o is not instance of clazz"));

            // EXTRACT ROOT CONF
            conf = (conf != null) ? conf : new JSONObject();
            Integer rootConf = extractDotConf(conf.get("."), 0x0);

            List<String> names = new ArrayList<String>();
            ClassMetadata meta = this.cmn.getClassMetadata(clazz);
            if (meta != null)
            {
                names.addAll(Arrays.asList(meta.getPropertyNames()));
                names.add(meta.getIdentifierPropertyName());
            }

            // RETAIN & EXCLUDE
            if ((rootConf & RETAIN_NAMED) != 0x0)
            {
                names.retainAll(conf.keySet());
                names.addAll(conf.keySet());
            }

            if ((rootConf & EXCLUDE_NAMED) != 0x0)
                names.retainAll(conf.keySet());


            // SERIALIZE
            for (String s:names)
            {
                if (".".equals(s))
                    continue;

                Integer nodeConf = extractDotConf(conf.get(s), 0x0);

                // SKIP
                if ((nodeConf & SKIP) != 0x0)
                    continue;

                // NOT NULL
                if ((nodeConf & REQUIRED) != 0x0)
                    if (get(req, s) == null)
                        throw(new MissingParameterException(s));


                Field f = getField(o, s);
                if (f == null)
                    throw(new RuntimeException("Field not found:"+clazz+"#"+s));
                Class c = f.getType();

                if (String.class.equals(c))
                {
                    Object v = getString(req, s);
                    if (v==null && ((nodeConf & IGNORE_NULL) != 0x0))
                        continue;
                    f.set(o, v);
                    continue;
                }

                if (Byte.class.equals(c))
                {
                    Object v = getByte(req, s);
                    if (v==null && ((nodeConf & IGNORE_NULL) != 0x0))
                        continue;
                    f.set(o, v);
                    continue;
                }

                if (Integer.class.equals(c))
                {
                    Object v = getInt(req, s);
                    if (v==null && ((nodeConf & IGNORE_NULL) != 0x0))
                        continue;
                    f.set(o, v);
                    continue;
                }

                if (Boolean.class.equals(c))
                {
                    Object v = getBoolean(req, s);
                    if (v==null && ((nodeConf & IGNORE_NULL) != 0x0))
                        continue;
                    f.set(o, v);
                    continue;
                }

                if (Long.class.equals(c))
                {
                    Object v = getLong(req, s);
                    if (v==null && ((nodeConf & IGNORE_NULL) != 0x0))
                        continue;
                    f.set(o, v);
                    continue;
                }

                if (Date.class.equals(c))
                {
                    Object v = getDate(req, s);
                    if (v==null && ((nodeConf & IGNORE_NULL) != 0x0))
                        continue;
                    f.set(o, v);
                    continue;
                }

            }
        }
        catch (IllegalAccessException ex)
        {
            throw(new RuntimeException(ex));
        }


        return(o);

    }

}
