package com.github.cuter44.nyafx.fastjson;

import java.lang.reflect.*;

public class FieldInfoLite
{
    public Field field;
    public Class fieldClass;
    public Method getter;
    public Method setter;
    public Class declarer;
    public boolean readOnly;

    public FieldInfoLite()
    {
        return;
    }

    public FieldInfoLite(Field field, Method getter, Method setter)
    {
        this();

        this.field = field;
        this.fieldClass = field!=null ? field.getType() : null;

        this.getter = getter;
        this.setter = setter;

        this.declarer = field!=null ? field.getDeclaringClass() : null;
        this.readOnly = field!=null ? Modifier.isFinal(field.getModifiers()) : false;

        try
        {
            this.field.setAccessible(true);
        }
        catch (SecurityException ex)
        {
            // NOOP
        }

        return;
    }

    public Object get(Object object)
        throws IllegalAccessException, InvocationTargetException
    {
        if (this.getter != null)
        {
            return(
                this.getter.invoke(object)
            );
        }

        // else
        return(
            this.field.get(object)
        );
    }

    public void set(Object object, Object value)
        throws IllegalAccessException, InvocationTargetException
    {
        if (this.readOnly)
            throw(new IllegalAccessException("Field is read-only."));

        if (this.setter != null)
        {
            this.setter.invoke(object, new Object[] { value });
            return;
        }

        // else
        this.field.set(object, value);

        return;
    }

    @Override
    public String toString()
    {
        return(
            new StringBuilder(128)
                .append('{')
                .append("field=")   .append(this.field)
                .append(',')
                .append("getter=")  .append(this.getter)
                .append(',')
                .append("setter=")  .append(this.setter)
                .append(',')
                .append("declarer=").append(this.declarer)
                .append(',')
                .append("readOnly=").append(this.readOnly)
                .append('}')
                .toString()
        );
    }
}
