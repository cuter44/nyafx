package com.github.cuter44.nyafx.servlet;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ParserBundle
    implements ValueParser
{
    public HashMap<Type, ValueParser> primaryParsers;
    //public HashMap<Type, ValueParser.ListParser> listParsers;

  // SINGLETON
    private static class Singleton
    {
        public static ParserBundle instance = ParserBundle.newDefaultInstance();
    }

    public static ParserBundle getDefaultInstance()
    {
        return(Singleton.instance);
    }

  // CONSTRUCT
    public ParserBundle()
    {
        this.primaryParsers = new HashMap<Type, ValueParser>();
        //this.listParsers = new HashMap<Type, ValueParser.ListParser>();

        return;
    }

    public static ParserBundle newDefaultInstance()
    {
        ParserBundle b = defaultSetup(new ParserBundle());

        return(b);
    }

    /** As there is no mean for bundle to recognize a List<Type> (excpet iterating the whole map),
     *  it is currently unable to parse Lists
     */
    public static ParserBundle defaultSetup(ParserBundle b)
    {
        b.primaryParsers.put(String.class       , PrimitiveParsers.StringParser.instance        );
        b.primaryParsers.put(String[].class     , PrimitiveParsers.StringArrayParser.instance   );

        b.primaryParsers.put(Byte.class         , PrimitiveParsers.ByteParser.instance          );
        b.primaryParsers.put(Byte[].class       , PrimitiveParsers.ByteArrayParser.instance     );

        b.primaryParsers.put(Integer.class      , PrimitiveParsers.IntParser.instance           );
        b.primaryParsers.put(Integer[].class    , PrimitiveParsers.IntArrayParser.instance      );

        b.primaryParsers.put(Long.class         , PrimitiveParsers.LongParser.instance          );
        b.primaryParsers.put(Long[].class       , PrimitiveParsers.LongArrayParser.instance     );

        b.primaryParsers.put(Float.class        , PrimitiveParsers.FloatParser.instance         );
        b.primaryParsers.put(Float[].class      , PrimitiveParsers.FloatArrayParser.instance    );

        b.primaryParsers.put(Double.class       , PrimitiveParsers.DoubleParser.instance        );
        b.primaryParsers.put(Double[].class     , PrimitiveParsers.DoubleArrayParser.instance   );

        b.primaryParsers.put(Boolean.class      , PrimitiveParsers.BooleanParser.instance       );
        b.primaryParsers.put(Boolean[].class    , PrimitiveParsers.BooleanArrayParser.instance  );

        b.primaryParsers.put(Date.class         , PrimitiveParsers.DateParser.instance          );
        b.primaryParsers.put(Date[].class       , PrimitiveParsers.DateArrayParser.instance     );

        return(b);
    }

  // VALUE PARSER
    @Override
    public Object parse(Object v, Type type)
    {
        ValueParser p = this.primaryParsers.get(type);

        if (p!=null)
            return(p.parse(v, type));
        else
            throw(new IllegalArgumentException("No parser is able to parse such type:"+type));
    }

    @Override
    public Object parseString(String v, Type type)
    {
        ValueParser p = this.primaryParsers.get(type);

        if (p!=null)
            return(p.parseString(v, type));
        else
            throw(new IllegalArgumentException("No parser is able to parse such type:"+type));
    }

  // TEST
    public static void main(String args[])
    {
        try
        {
            ParserBundle b = ParserBundle.getDefaultInstance();

            String o1 = (String)b.parse("string", String.class);
            System.out.println(o1.getClass()+" "+o1);

            Byte o2 = (Byte)b.parse("1", Byte.class);
            System.out.println(o2.getClass()+" "+o2);

            Integer o3 = (Integer)b.parse("1", Integer.class);
            System.out.println(o3.getClass()+" "+o3);

            Long o4 = (Long)b.parse("1", Long.class);
            System.out.println(o4.getClass()+" "+o4);

            Float o5 = (Float)b.parse("1.23", Float.class);
            System.out.println(o5.getClass()+" "+o5);

            Double o6 = (Double)b.parse("1.23", Double.class);
            System.out.println(o6.getClass()+" "+o6);

            Boolean o7 = (Boolean)b.parse("1.23", Boolean.class);
            System.out.println(o7.getClass()+" "+o7);

            Date o8 = (Date)b.parse("1442392400000", Date.class);
            System.out.println(o8.getClass()+" "+o8);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
