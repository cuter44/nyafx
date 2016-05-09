package com.github.cuter44.nyafx.fastjson;

import java.util.*;
import java.lang.reflect.Type;
import java.lang.reflect.InvocationTargetException;

import com.alibaba.fastjson.*;
import com.alibaba.fastjson.util.*;

import com.github.cuter44.nyafx.servlet.*;

public class JSONActualizer
{
    public boolean failException = false;

  // FIELD INFO
    protected Map<Class, Map<String, FieldInfo>> knownClasses = new Hashtable<Class, Map<String, FieldInfo>>();

    protected Map<String, FieldInfo> getFieldInfos(Class clazz)
    {
        Map<String, FieldInfo> mfi = this.knownClasses.get(clazz);
        if (mfi != null)
            return(mfi);

        // ELSE
        List<FieldInfo> lfi = TypeUtils.computeGetters(clazz, null, null, false);
        mfi = new HashMap<String, FieldInfo>();
        for (FieldInfo fi:lfi)
            mfi.put(fi.name, fi);

        this.knownClasses.put(clazz, mfi);

        return(mfi);
    }

  // VALUE PARSER
    protected ParserBundle valueParsers;

    public JSONActualizer setValuePatsers(ParserBundle parsers)
    {
        this.valueParsers = parsers;

        return(this);
    }

    public JSONActualizer addValueParser(Type type, ValueParser parser)
    {
        this.valueParsers.primaryParsers.put(type, parser);

        return(this);
    }

  // CONSTRUCT
    public JSONActualizer()
    {
        this.valueParsers = ParserBundle.newDefaultInstance();

        return;
    }

  // ACTUALIZE
    public <T> T actualizeObject(T object, Class<T> clazz, JSONObject json, Collection<String> fields)
        throws InstantiationException, NoSuchFieldException, IllegalAccessException, InvocationTargetException
    {
        object = object!=null ? object : clazz.newInstance();

        Set<String> keys = json.keySet();
        Map<String, FieldInfo> mfi = this.getFieldInfos(clazz);

        for (String k:keys)
        {
            if (!fields.contains(k))
                continue;

            FieldInfo fi = mfi.get(k);
            if (fi == null)
            {
                if (this.failException)
                    throw(new NoSuchFieldException("No such field "+k));
                continue;
            }

            fi.field.set(
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
