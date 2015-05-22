package com.github.cuter44.nyafx.hj;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import com.alibaba.fastjson.*;
import com.alibaba.fastjson.parser.*;
//import org.hibernate.*;
import org.hibernate.metadata.*;

/** Jsonize entity according hibernate metadata
 * @since 2.11.0
 */
public class HibernateJsonizer
{
    /** 不序列化此字段
     */
    public static final int SKIP            = 0x1;
    /** 仅序列化(实体)的ID属性
     */
    public static final int ID_ONLY         = 0x2;
    /** 仅序列化给出的字段, 同时进行 INCLUDE_NAMED
     */
    public static final int RETAIN_NAMED    = 0x4;
    /** 不序列化给出的字段, 除此以外的字段以默认的方式输出.
     */
    public static final int EXCLUDE_NAMED   = 0x8;
    /** 并且序列化列出的字段
     */
    public static final int INCLUDE_NAMED   = 0x10;


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
    protected ParserConfig pc;

    public HibernateJsonizer(ClassMetaNarrator classMetaNarrator)
    {
        this.cmn = classMetaNarrator;
        this.pc = new ParserConfig();

        return;
    }

    public JSONObject jsonizeObject(JSONObject json, Object o)
    {
        return(
            this.jsonizeObject(json, o, null)
        );
    }

    /**
     * @param json JSONObject to attach data, automatically new one if null passed.
     * @param o Entity instance to be serialized
     * @param conf Hint for jsonizer, see specification.
     */
    public JSONObject jsonizeObject(JSONObject json, Object o, JSONObject conf)
    {
        try
        {
            // FAIL DEFAULT
            if (o == null)
                return(json);

            json = (json!=null) ? json : new JSONObject();
            conf = (conf!=null) ? conf : new JSONObject();

            Integer rootConf = extractDotConf(conf.get("."), 0x0);

            // SKIP
            if ((rootConf & SKIP) != 0x0)
                return(json);


            // IS ENTITY
            ClassMetadata meta = this.cmn.getClassMetadata(o.getClass());
            List<String> names = new ArrayList<String>();
            if (meta != null)
            {
                //json.put(meta.getIdentifierPropertyName(), meta.getIdentifier(o));
                String id = meta.getIdentifierPropertyName();
                if (id != null)
                    json.put(id, getField(o, id).get(o));

                names.addAll(Arrays.asList(meta.getPropertyNames()));
            }

            // ID_ONLY
            if ((rootConf & ID_ONLY) != 0x0)
            {
                return(json);
            }

            // RETAIN & EXCLUDE
            if ((rootConf & RETAIN_NAMED) != 0x0)
            {
                names.retainAll(conf.keySet());
                names.addAll(conf.keySet());
            }

            if ((rootConf & EXCLUDE_NAMED) != 0x0)
                names.removeAll(conf.keySet());


            if ((rootConf & INCLUDE_NAMED) != 0x0)
                names.addAll(conf.keySet());


            // SERIALIZE
            for (String s:names)
            {
                if (".".equals(s))
                    continue;

                Integer nodeConf = extractDotConf(conf.get(s), ID_ONLY);

                // SKIP
                if ((nodeConf & SKIP) != 0x0)
                    continue;

                Field f = getField(o, s);
                if (f == null)
                    throw(new RuntimeException("Field not found:"+o.getClass()+"#"+s));
                Class c = f.getType();

                // PRIMITIVE
                if (this.pc.isPrimitive(c))
                {
                    json.put(s, f.get(o));

                    continue;
                }


                // ARRAY
                if (c.isArray())
                {
                    if (this.pc.isPrimitive(c.getComponentType()))
                    {
                        json.put(s, f.get(o));
                        continue;
                    }

                    // else
                    // {
                        json.put(
                            s,
                            this.jsonizeArray(
                                null,
                                (Object[])f.get(o),
                                wrapDotConf(conf.get(s), nodeConf)
                            )
                        );

                        continue;
                    // }
                }

                // COLLECTION
                if (Collection.class.isAssignableFrom(c))
                {
                    json.put(
                        s,
                        this.jsonizeCollection(
                            null,
                            (Collection)f.get(o),
                            wrapDotConf(conf.get(s), nodeConf)
                        )
                    );

                    continue;
                }

                // COMPOSITE
                // if (Object.class.isAssignableFrom(c))
                // {
                    json.put(
                        s,
                        this.jsonizeObject(
                            null,
                            f.get(o),
                            wrapDotConf(conf.get(s), nodeConf)
                        )
                    );

                    continue;
                // }

            }
        }
        catch (IllegalAccessException ex)
        {
            throw(new RuntimeException(ex));
        }

        return(json);
    }

    /**
     * @param json JSONArray to attach data, automatically new one if null passed.
     * @param a Entity array to be serialized
     * @param conf Hint for jsonizer, see specification.
     */
    public JSONArray jsonizeArray(JSONArray json, Object[] a, JSONObject conf)
    {
        if (a == null)
            return(json);

        json = (json!=null) ? json : new JSONArray();
        conf = (conf!=null) ? conf : new JSONObject();

        Class componentType = a.getClass().getComponentType();

        // PRIMITIVE[]
        if (this.pc.isPrimitive(componentType))
        {
            json.addAll((JSONArray)JSON.toJSON(a));

            return(json);
        }

        for (Object o:a)
        {
            // ENTITY
            if (this.cmn.getClassMetadata(componentType) != null)
            {
                json.add(
                    this.jsonizeObject(null, o, conf)
                );
                continue;
            }


            // DEFAULT
            json.add(JSON.toJSON(o));
        }

        return(json);
    }

    public JSONArray jsonizeCollection(JSONArray json, Collection c)
    {
        return(
            this.jsonizeCollection(json, c, null)
        );
    }

    /**
     * @param json JSONArray to attach data, automatically new one if null passed.
     * @param a Entity array to be serialized
     * @param conf Hint for jsonizer, see specification.
     */
    public JSONArray jsonizeCollection(JSONArray json, Collection c, JSONObject conf)
    {
        if (c == null)
            return(json);

        json = (json!=null) ? json : new JSONArray();
        conf = (conf!=null) ? conf : new JSONObject();

        for (Object o:c)
        {
            Class componentType = o.getClass();

            // PRIMITIVE
            if (this.pc.isPrimitive(componentType))
            {
                json.add(o);

                continue;
            }

            // ENTITY
            if (this.cmn.getClassMetadata(componentType) != null)
            {
                json.add(
                    jsonizeObject(null, o, conf)
                );
                continue;
            }


            // DEFAULT
            json.add(JSON.toJSON(o));
        }

        return(json);
    }

    public static void main(String[] args)
    {

    }
}
