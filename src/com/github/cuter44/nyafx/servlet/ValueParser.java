package com.github.cuter44.nyafx.servlet;

import java.lang.reflect.Type;
import java.util.List;

public interface ValueParser<T>
{
    /** Parse parameter to expected Object
     * @param valueToParse Value to be parsed, may comes from parameter/request/session/cookies, so it can be various type but mostly String, then likely to be numeric.
     * @param type The expecting returned class, may be null.
     * @return The parsed object.
     */
    public abstract T parse(Object valueToParse, Type type);
    //{
        //throw(new UnsupportedOperationException());
    //}

    public abstract T parseString(String valueToParse, Type type);
    //{
        //throw(new UnsupportedOperationException());
    //}

    public static interface ArrayParser<T> extends ValueParser
    {
        /** Parse parameter to expected Object
         * @param valueToParse Value to be parsed, may comes from parameter/request/session/cookies, so it can be various type but mostly String, then likely to be numeric.
         * @param componentType The expecting returned class of array component, may be null.
         * @return The parsed object.
         */
        @Override
        public abstract T[] parse(Object valueToParse, Type componentType);
        //{
            //throw(new UnsupportedOperationException());
        //}

        @Override
        public abstract T[] parseString(String valueToParse, Type componentType);
        //{
            //throw(new UnsupportedOperationException());
        //}

        public abstract T[] parseStringArray(String[] valueToParse, Type componentType);
        //{
            //throw(new UnsupportedOperationException());
        //}
    }

    public static interface ListParser<T> extends ValueParser
    {
        /** Parse parameter to expected Object
         * @param valueToParse Value to be parsed, may comes from parameter/request/session/cookies, so it can be various type but mostly String, then likely to be numeric.
         * @param componentType The expecting returned class of element, may be null.
         * @return The parsed object.
         */
        @Override
        public abstract List<T> parse(Object valueToParse, Type elementType);
        //{
            //throw(new UnsupportedOperationException());
        //}

        @Override
        public abstract List<T> parseString(String valueToParse, Type elementType);
        //{
            //throw(new UnsupportedOperationException());
        //}

        public abstract List<T> parseStringArray(String[] valueToParse, Type elementType);
        //{
            //throw(new UnsupportedOperationException());
        //}
    }

}
