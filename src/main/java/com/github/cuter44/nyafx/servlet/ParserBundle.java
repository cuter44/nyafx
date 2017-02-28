package com.github.cuter44.nyafx.servlet;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/** Coverter bundle that automatically select and route convertion to
 * specified parser according required type.
 */
public class ParserBundle
    implements ValueParser
{
    protected Map<Type, ValueParser> primitiveParsers;
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
        this.primitiveParsers = new HashMap<Type, ValueParser>();
        //this.listParsers = new HashMap<Type, ValueParser.ListParser>();

        return;
    }

    public static ParserBundle newDefaultInstance()
    {
        return(
            defaultSetup(new ParserBundle())
        );
    }

    public ParserBundle setPrimitiveParsers(Map<Type, ValueParser> parsers)
    {
        this.primitiveParsers = parsers;

        return(this);
    }

    public ParserBundle addPrimitiveParser(Type t, ValueParser p)
    {
        this.primitiveParsers.put(t, p);

        return(this);
    }

    /** As there is no mean for bundle to recognize a List<Type> (excpet iterating the whole map),
     *  it is currently unable to parse Lists
     */
    public static ParserBundle defaultSetup(ParserBundle b)
    {
        b.primitiveParsers.put(String.class       , PrimitiveParsers.StringParser.instance        );
        b.primitiveParsers.put(String[].class     , PrimitiveParsers.StringArrayParser.instance   );

        b.primitiveParsers.put(Byte.class         , PrimitiveParsers.ByteParser.instance          );
        b.primitiveParsers.put(Byte[].class       , PrimitiveParsers.ByteArrayParser.instance     );

        b.primitiveParsers.put(Integer.class      , PrimitiveParsers.IntParser.instance           );
        b.primitiveParsers.put(Integer[].class    , PrimitiveParsers.IntArrayParser.instance      );

        b.primitiveParsers.put(Long.class         , PrimitiveParsers.LongParser.instance          );
        b.primitiveParsers.put(Long[].class       , PrimitiveParsers.LongArrayParser.instance     );

        b.primitiveParsers.put(Float.class        , PrimitiveParsers.FloatParser.instance         );
        b.primitiveParsers.put(Float[].class      , PrimitiveParsers.FloatArrayParser.instance    );

        b.primitiveParsers.put(Double.class       , PrimitiveParsers.DoubleParser.instance        );
        b.primitiveParsers.put(Double[].class     , PrimitiveParsers.DoubleArrayParser.instance   );

        b.primitiveParsers.put(Boolean.class      , PrimitiveParsers.BooleanParser.instance       );
        b.primitiveParsers.put(Boolean[].class    , PrimitiveParsers.BooleanArrayParser.instance  );

        b.primitiveParsers.put(Date.class         , PrimitiveParsers.DateParser.instance          );
        b.primitiveParsers.put(Date[].class       , PrimitiveParsers.DateArrayParser.instance     );

        return(b);
    }

  // VALUE PARSER
    /**
     * @param conv Must be a <code>Type</code>.
     */
    @Override
    public Object parse(Object v, Object conv)
    {
        ValueParser p = this.primitiveParsers.get((Type)conv);

        if (p!=null)
            return(p.parse(v, conv));
        else
            throw(new IllegalArgumentException("No parser is able to parse such type:"+conv));
    }

    /**
     * @param conv Must be a <code>Type</code>.
     */
    @Override
    public Object parseString(String v, Object conv)
    {
        ValueParser p = this.primitiveParsers.get((Type)conv);

        if (p!=null)
            return(p.parseString(v, conv));
        else
            throw(new IllegalArgumentException("No parser is able to parse such type:"+conv));
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
