package com.github.cuter44.nyafx.servlet;

import java.util.List;

public interface ValueParser<T>
{
    /** Parse parameter to expected Object
     * @param valueToParse Value to be parsed, may comes from parameter/request/session/cookies, so it can be various conv but mostly String, then likely to be numeric.
     * @param conv The convert option, defined by user, by default null.
     * @return The parsed object.
     */
    public abstract T parse(Object valueToParse, Object conv);
    //{
        //throw(new UnsupportedOperationException());
    //}

    public abstract T parseString(String valueToParse, Object conv);
    //{
        //throw(new UnsupportedOperationException());
    //}

    public static interface ArrayParser<T> extends ValueParser
    {
        /** Parse parameter to expected Object
         * @param valueToParse Value to be parsed, may comes from parameter/request/session/cookies, so it can be various conv but mostly String, then likely to be numeric.
         * @param conv The convert option, defined by user, by default null.
         * @return The parsed object.
         */
        @Override
        public abstract T[] parse(Object valueToParse, Object conv);
        //{
            //throw(new UnsupportedOperationException());
        //}

        @Override
        public abstract T[] parseString(String valueToParse, Object conv);
        //{
            //throw(new UnsupportedOperationException());
        //}

        public abstract T[] parseStringArray(String[] valueToParse, Object conv);
        //{
            //throw(new UnsupportedOperationException());
        //}
    }

    @Deprecated
    public static interface ListParser<T> extends ValueParser
    {
        /** Parse parameter to expected Object
         * @param valueToParse Value to be parsed, may comes from parameter/request/session/cookies, so it can be various conv but mostly String, then likely to be numeric.
         * @param conv The convert option, defined by user, by default null.
         * @return The parsed object.
         */
        @Override
        public abstract List<T> parse(Object valueToParse, Object conv);
        //{
            //throw(new UnsupportedOperationException());
        //}

        @Override
        public abstract List<T> parseString(String valueToParse, Object conv);
        //{
            //throw(new UnsupportedOperationException());
        //}

        public abstract List<T> parseStringArray(String[] valueToParse, Object conv);
        //{
            //throw(new UnsupportedOperationException());
        //}
    }

}
