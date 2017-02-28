package com.github.cuter44.nyafx.fastjson;

import java.util.*;
import java.lang.reflect.Type;
import java.lang.reflect.InvocationTargetException;

import com.alibaba.fastjson.*;

import com.github.cuter44.nyafx.servlet.*;

public class JSONActualizer
{
    public boolean failException = false;

  // FIELD INFO
    protected Map<Class, Map<String, FieldInfoLite>> knownClasses = new Hashtable<Class, Map<String, FieldInfoLite>>();

    protected Map<String, FieldInfoLite> getFieldInfo(Class clazz)
    {
        Map<String, FieldInfoLite> mfi = this.knownClasses.get(clazz);
        if (mfi != null)
            return(mfi);

        // ELSE
        this.knownClasses.put(
            clazz,
            mfi = TypeUtilsLite.findFieldsViaGetter(clazz)
        );

        return(mfi);
    }

  // VALUE PARSER
    protected ParserBundle valueParsers;

    public JSONActualizer setValueParsers(ParserBundle parsers)
    {
        this.valueParsers = parsers;

        return(this);
    }

    /** @deprecated Misspelled method name.
     */
    @Deprecated
    public JSONActualizer setValuePatsers(ParserBundle parsers)
    {
        return(
            this.setValueParsers(parsers)
        );
    }

    public JSONActualizer addValueParser(Type type, ValueParser parser)
    {
        this.valueParsers.addPrimitiveParser(type, parser);

        return(this);
    }

  // CONSTRUCT
    public JSONActualizer()
    {
        this.valueParsers = ParserBundle.newDefaultInstance();

        return;
    }

  // ACTUALIZE
    /** Actualize object without valid-fields list.
     * Invoker should filtered out unwanted fields before inputing the data.
     * ALL top-most fields of <code>json</code> are being iterated and parsed.
     * Bit higher performance than the hinted one.
     */
    public <T> T actualizeObject(T object, Class<T> clazz, JSONObject json)
        throws InstantiationException, NoSuchFieldException, IllegalAccessException, InvocationTargetException
    {
        object = object!=null ? object : clazz.newInstance();

        Set<String> keys = json.keySet();
        Map<String, FieldInfoLite> mfi = this.getFieldInfo(clazz);

        for (String k:keys)
        {
            FieldInfoLite fi = mfi.get(k);
            if (fi == null)
            {
                if (this.failException)
                    throw(new NoSuchFieldException("No such field "+k));
                // else
                continue;
            }

            fi.set(
                object,
                this.valueParsers.parse(
                    json.get(k),
                    fi.fieldClass
                )
            );
        }

        return(object);
    }

    /** Actualize object with valid-fields list.
     * All top-most fields of <code>json</code> are being iterated, whose name not in <code>fields</code> are skipped.
     */
    public <T> T actualizeObject(T object, Class<T> clazz, JSONObject json, Collection<String> fields)
        throws InstantiationException, NoSuchFieldException, IllegalAccessException, InvocationTargetException
    {
        object = object!=null ? object : clazz.newInstance();

        Set<String> keys = json.keySet();
        Map<String, FieldInfoLite> mfi = this.getFieldInfo(clazz);

        for (String k:keys)
        {
            if (!fields.contains(k))
                continue;

            FieldInfoLite fi = mfi.get(k);
            if (fi == null)
            {
                if (this.failException)
                    throw(new NoSuchFieldException("No such field "+k));
                // else
                continue;
            }

            fi.set(
                object,
                this.valueParsers.parse(
                    json.get(k),
                    fi.fieldClass
                )
            );
        }

        return(object);
    }
}
