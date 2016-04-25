package com.github.cuter44.nyafx.fastjson;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.List;
import java.util.regex.Pattern;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.lang.reflect.InvocationTargetException;

import com.alibaba.fastjson.*;
import com.alibaba.fastjson.util.*;

/** Programatically configurable json builder.
 *
 * requires fastjson-1.2.9+
 */
public class JSONBuilder
{
  // UTIL
    /** x: Exclude property, or skip all properties while attached on <code>.</code> .
     */
    protected static final Pattern PATTERN_EXCLUDE = Pattern.compile("\\bx\\b");
    /** i: Include property, needed on non-primitives to recursive in.
     */
    protected static final Pattern PATTERN_INCLUDE = Pattern.compile("\\bi\\b");
    /** bb: Serialize byte array into base64.
     */
    protected static final Pattern PATTERN_BINARY_AS_BASE64 = Pattern.compile("\\bbb\\b");
    /** bh: Serialize byte array into hexa, this is the default behaviour.
     */
    protected static final Pattern PATTERN_BINARY_AS_HEXA = Pattern.compile("\\bbh\\b");

    protected String DEFAULT_HINT_VALUE = "";

    protected String extractHint(Object o)
    {
        if (o == null)
            return(this.DEFAULT_HINT_VALUE);

        if (o instanceof String)
            return((String)o);

        if (o instanceof JSONObject)
            return(extractHint(((JSONObject)o).get(".")));

        return(this.DEFAULT_HINT_VALUE);
    }

    protected JSONObject wrapHint(Object o)
    {
        if (o == null)
            return(null);

        if (o instanceof JSONObject)
            return((JSONObject)o);

        if (o instanceof String)
            return(JSON.parseObject("{'.':'"+(String)o+"'}"));

        return(null);
    }

    protected boolean isPrimitive(Class<?> clazz)
    {
        return(
            clazz.isPrimitive()
                || clazz == String.class
                || clazz == Long.class
                || clazz == java.util.Date.class
                || clazz == Boolean.class
                || clazz == Double.class
                || clazz == Float.class
                || clazz == Integer.class
                || clazz == BigInteger.class
                || clazz == BigDecimal.class
                || clazz == Character.class
                || clazz == Byte.class
                || clazz == Short.class
                || clazz == java.sql.Date.class
                || clazz == java.sql.Time.class
                || clazz == java.sql.Timestamp.class
        );
    }

    protected Map<Class, List<FieldInfo>> knownClasses = new Hashtable<Class, List<FieldInfo>>();

    protected List<FieldInfo> getFieldInfos(Class clazz)
    {
        List<FieldInfo> fi = this.knownClasses.get(clazz);
        if (fi != null)
            return(fi);

        this.knownClasses.put(clazz, fi = TypeUtils.computeGetters(clazz, null, null, false));
        return(fi);
    }


  // EXPOSED
    /** jsonize Java bean to a JSONObject.
     *
     * @param json          the JSONObject to attach attributes on,
     *                      automatically new one if null passed in.
     * @param javaObject    object to be jsonized.
     * @param hint          hints to customized output.
     */
    public JSONObject jsonizeObject(JSONObject json, Object javaObject, JSONObject hint)
        throws IllegalAccessException, InvocationTargetException
    {
        json = json!=null ? json : new JSONObject();
        hint = hint!=null ? hint : new JSONObject();

        String rh = this.extractHint(hint);

        if (PATTERN_EXCLUDE.matcher(rh).find())
            return(json);

        List<FieldInfo> fields = this.getFieldInfos(javaObject.getClass());

        for (FieldInfo f:fields)
        {
            String k = f.name;
            String ph = this.extractHint(hint.get(k));

            if (PATTERN_EXCLUDE.matcher(ph).find())
                continue;

            Class c = f.fieldClass;

            // SWITCH CLASS
            // CASE PRIMITIVE
            if (this.isPrimitive(c))
            {
                json.put(
                    k,
                    f.get(javaObject)
                );

                continue;
            }

            // CASE ARRAY
            if (c.isArray())
            {
                if (PATTERN_INCLUDE.matcher(ph).find())
                {
                    json.put(
                        k,
                        this.jsonizeArray(
                            null,
                            (Object[])f.get(javaObject),
                            wrapHint(hint.get(k))
                        )
                    );
                }

                continue;
            }

            // CASE COLLECTION
            if (Collection.class.isAssignableFrom(c))
            {
                if (PATTERN_INCLUDE.matcher(ph).find())
                {
                    json.put(
                        k,
                        this.jsonizeCollection(
                            null,
                            (Collection)f.get(javaObject),
                            wrapHint(hint.get(k))
                        )
                    );
                }

                continue;
            }

            // CASE MAP
            if (Map.class.isAssignableFrom(c))
            {
                if (PATTERN_INCLUDE.matcher(ph).find())
                {
                    json.put(
                        k,
                        this.jsonizeMap(
                            null,
                            (Map)f.get(javaObject),
                            this.wrapHint(hint.get(k))
                        )
                    );
                }

                continue;
            }
        }

        return(json);
    }

    /** jsonize map to a JSONObject.
     *
     * @param json          the JSONObject to attach attributes on,
     *                      automatically new one if null passed in.
     * @param javaObject    object to be jsonized.
     * @param hint          hints to customized output.
     */
    public JSONObject jsonizeMap(JSONObject json, Map javaObject, JSONObject hint)
        throws IllegalAccessException, InvocationTargetException
    {
        json = json!=null ? json : new JSONObject();
        hint = hint!=null ? hint : new JSONObject();

        String rh = this.extractHint(hint);

        if (PATTERN_EXCLUDE.matcher(rh).find())
            return(json);

        Map map = (Map)javaObject;

        Set keys = map.keySet();

        for (Object key:keys)
        {
            String k = key.toString();
            String ph = this.extractHint(hint.get(k));
            if (PATTERN_EXCLUDE.matcher(ph).find())
                continue;

            Object v = map.get(key);
            Class c = v.getClass();

            // SWITCH CLASS
            // CASE:PRIMITIVE
            if (this.isPrimitive(c))
            {
                json.put(k, v);

                continue;
            }

            // CASE:ARRAY
            if (c.isArray())
            {
                if (PATTERN_INCLUDE.matcher(ph).find())
                {
                    json.put(
                        k,
                        this.jsonizeArray(
                            null,
                            (Object[])v,
                            this.wrapHint(hint.get(k))
                        )
                    );
                }

                continue;
            }

            // CASE:COLLECTION
            if (Collection.class.isAssignableFrom(c))
            {
                if (PATTERN_INCLUDE.matcher(ph).find())
                {
                    json.put(
                        k,
                        this.jsonizeCollection(
                            null,
                            (Collection)v,
                            this.wrapHint(hint.get(k))
                        )
                    );
                }

                continue;
            }

            // CASE:MAP
            if (Map.class.isAssignableFrom(c))
            {
                if (PATTERN_INCLUDE.matcher(ph).find())
                {
                    json.put(
                        k,
                        this.jsonizeMap(
                            null,
                            (Map)v,
                            this.wrapHint(hint.get(k))
                        )
                    );
                }

                continue;
            }

            // DEFAULT
            if (PATTERN_INCLUDE.matcher(ph).find())
            {
                json.put(
                    k,
                    this.jsonizeObject(
                        null,
                        v,
                        this.wrapHint(hint.get(k))
                    )
                );

                continue;
            }
        }

        return(json);
    }

    /** jsonize list to a JSONArray.
     *
     * @param json          the JSONArray to attach attributes on,
     *                      automatically new one if null passed in.
     * @param javaObject    object to be jsonized.
     * @param hint          hints to customized output.
     */
    public JSONArray jsonizeCollection(JSONArray json, Collection javaObject, JSONObject hint)
        throws IllegalAccessException, InvocationTargetException
    {
        json = json!=null ? json : new JSONArray();
        hint = hint!=null ? hint : new JSONObject();

        String rh = this.extractHint(hint);

        if (PATTERN_EXCLUDE.matcher(rh).find())
            return(json);

        Collection collection = (Collection)javaObject;

        for (Object v:collection)
        {
            Class c = v.getClass();

            // SWITCH CLASS
            // CASE:PRIMITIVE
            if (this.isPrimitive(c))
            {
                json.add(v);

                continue;
            }

            // CASE:ARRAY
            if (c.isArray())
            {
                json.add(
                    this.jsonizeArray(
                        null,
                        (Object[])v,
                        this.wrapHint(hint)
                    )
                );

                continue;
            }

            // CASE:COLLECTION
            if (Collection.class.isAssignableFrom(c))
            {
                json.add(
                    this.jsonizeCollection(
                        null,
                        (Collection)v,
                        this.wrapHint(hint)
                    )
                );

                continue;
            }

            // CASE:MAP
            if (Map.class.isAssignableFrom(c))
            {
                json.add(
                    this.jsonizeMap(
                        null,
                        (Map)v,
                        this.wrapHint(hint)
                    )
                );

                continue;
            }

            // DEFAULT
            //if (PATTERN_INCLUDE.matcher(ph).find())
            {
                json.add(
                    this.jsonizeObject(
                        null,
                        v,
                        this.wrapHint(hint)
                    )
                );

                continue;
            }
        }

        return(json);
    }

    /** jsonize list to a JSONArray.
     *
     * @param json          the JSONArray to attach attributes on,
     *                      automatically new one if null passed in.
     * @param javaObject    object to be jsonized.
     * @param hint          hints to customized output.
     */
    public JSONArray jsonizeArray(JSONArray json, Object[] javaObject, JSONObject hint)
        throws IllegalAccessException, InvocationTargetException
    {
        json = json!=null ? json : new JSONArray();
        hint = hint!=null ? hint : new JSONObject();

        String rh = this.extractHint(hint);

        if (PATTERN_EXCLUDE.matcher(rh).find())
            return(json);

        Object[] array = (Object[])javaObject;
        Class c = array.getClass().getComponentType();

        // SWITCH CLASS
        // CASE:PRIMITIVE
        if (this.isPrimitive(c))
        {
            for (Object v:array)
                json.add(v);

            return(json);
        }

        // CASE:ARRAY
        if (c.isArray())
        {
            for (Object v:array)
            {
                json.add(
                    this.jsonizeArray(
                        null,
                        (Object[])v,
                        this.wrapHint(hint)
                    )
                );
            }

            return(json);
        }

        // CASE:COLLECTION
        if (Collection.class.isAssignableFrom(c))
        {
            for (Object v:array)
            {
                json.add(
                    this.jsonizeCollection(
                        null,
                        (Collection)v,
                        this.wrapHint(hint)
                    )
                );
            }

            return(json);
        }

        // CASE:MAP
        if (Map.class.isAssignableFrom(c))
        {
            for (Object v:array)
            {
                json.add(
                    this.jsonizeMap(
                        null,
                        (Map)v,
                        this.wrapHint(hint)
                    )
                );
            }

            return(json);
        }
        // DEFAULT
        //if (PATTERN_INCLUDE.matcher(ph).find())
        {
            for (Object v:array)
            {
                json.add(
                    this.jsonizeObject(
                        null,
                        v,
                        this.wrapHint(hint)
                    )
                );
            }

            return(json);
        }

        //return(json);
    }
}
