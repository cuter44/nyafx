package com.github.cuter44.nyafx.servlet;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.List;
import java.util.Collection;
import java.util.Arrays;

/** Provides basic implemention that mainly focus on converting string to java
 * primitive types.
 *
 * No intelligent sensitive, hence no depending on conv param. This is notably
 * on coverting Dates, which take in only a unix-timestamp in millisecond, but
 * cannont handle any other form.
 * User should DIY for advanced/customized usage.
 */
public class PrimitiveParsers
{
  // String
    public static class StringParser
        implements ValueParser<String>
    {
        public static StringParser instance = new StringParser();

        @Override
        public String parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof String)
                return((String)v);

            return(v.toString());
        }

        @Override
        public String parseString(String v, Object conv)
        {
            return(v);
        }
    }

    public static class StringArrayParser
        implements ValueParser.ArrayParser<String>
    {
        public static StringArrayParser instance = new StringArrayParser();

        public String regexSeperator = ",";

        @Override
        public String[] parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof String[])
                return((String[])v);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            if (v.getClass().isArray())
            {
                String[] a = new String[Array.getLength(v)];
                for (int i=0; i<a.length; i++)
                    a[i] = StringParser.instance.parse(Array.get(v, i), conv);

                return(a);
            }

            if (Collection.class.isAssignableFrom(v.getClass()))
            {
                Collection<?> c = (Collection<?>)v;
                String[] a = new String[c.size()];

                int i=0;
                for (Object e : c)
                    a[i++] = StringParser.instance.parse(e, conv);

                return(a);
            }

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public String[] parseString(String v, Object conv)
        {
            return(
                v.split(this.regexSeperator)
            );
        }

        @Override
        public String[] parseStringArray(String[] v, Object conv)
        {
            return(v);
        }
    }

    public static class StringListParser
        implements ValueParser.ListParser<String>
    {
        public static StringArrayParser instance = new StringArrayParser();

        @Override
        public List<String> parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            if (v instanceof String[])
                return(this.parseStringArray((String[])v, conv));

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public List<String> parseString(String v, Object conv)
        {
            if (v.length() == 0)
                return(null);

            return(
                this.parseStringArray(
                    StringArrayParser.instance.parseString(v, String[].class),
                    List.class
                )
            );
        }

        @Override
        public List<String> parseStringArray(String[] v, Object conv)
        {
            return(
                Arrays.asList(v)
            );
        }
    }

  // Byte
    public static class ByteParser
        implements ValueParser<Byte>
    {
        public static ByteParser instance = new ByteParser();

        @Override
        public Byte parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof Number)
                return(((Number)v).byteValue());

            if (Byte.TYPE.isInstance(v))
                return((Byte)v);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public Byte parseString(String v, Object conv)
        {
            return(
                v!=null ? Byte.valueOf(v) : null
            );
        }
    }

    public static class ByteArrayParser
        implements ValueParser.ArrayParser<Byte>
    {
        public static ByteArrayParser instance = new ByteArrayParser();

        @Override
        public Byte[] parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof Byte[])
                return((Byte[])v);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            if (v instanceof String[])
                return(this.parseStringArray((String[])v, conv));

            if (v.getClass().isArray())
            {
                Byte[] a = new Byte[Array.getLength(v)];
                for (int i=0; i<a.length; i++)
                    a[i] = ByteParser.instance.parse(Array.get(v, i), conv);

                return(a);
            }

            if (Collection.class.isAssignableFrom(v.getClass()))
            {
                Collection<?> c = (Collection<?>)v;
                Byte[] a = new Byte[c.size()];

                int i=0;
                for (Object e : c)
                    a[i++] = ByteParser.instance.parse(e, conv);

                return(a);
            }

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public Byte[] parseString(String v, Object conv)
        {
            if (v.length() == 0)
                return(null);

            return(
                this.parseStringArray(
                    StringArrayParser.instance.parseString(v, String.class),
                    Byte.class
                )
            );
        }

        @Override
        public Byte[] parseStringArray(String[] v, Object conv)
        {
            Byte[] b = new Byte[v.length];

            for (int i=0; i<v.length; i++)
                b[i] = (v[i]!=null) ? Byte.valueOf(v[i]) : null;

            return(b);
        }
    }

    public static class ByteListParser
        implements ValueParser.ListParser<Byte>
    {
        public static ByteListParser instance = new ByteListParser();

        @Override
        public List<Byte> parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            if (v instanceof String[])
                return(this.parseStringArray((String[])v, conv));

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public List<Byte> parseString(String v, Object conv)
        {
            if (v.length() == 0)
                return(null);

            return(
                Arrays.asList(
                    ByteArrayParser.instance.parseString(v, Byte.class)
                )
            );
        }

        @Override
        public List<Byte> parseStringArray(String[] v, Object conv)
        {
            return(
                Arrays.asList(
                    ByteArrayParser.instance.parseStringArray(v, Byte.class)
                )
            );
        }
    }

  // Int
    public static class IntParser
        implements ValueParser<Integer>
    {
        public static IntParser instance = new IntParser();

        @Override
        public Integer parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof Number)
                return((Integer)v);

            if (Integer.TYPE.isInstance(v))
                return(((Number)v).intValue());

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public Integer parseString(String v, Object conv)
        {
            return(
                v!=null ? Integer.valueOf(v) : null
            );
        }
    }

    public static class IntArrayParser
        implements ValueParser.ArrayParser<Integer>
    {
        public static IntArrayParser instance = new IntArrayParser();

        @Override
        public Integer[] parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof Integer[])
                return((Integer[])v);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            if (v instanceof String[])
                return(this.parseStringArray((String[])v, conv));

            if (v.getClass().isArray())
            {
                Integer[] a = new Integer[Array.getLength(v)];
                for (int i=0; i<a.length; i++)
                    a[i] = IntParser.instance.parse(Array.get(v, i), conv);

                return(a);
            }

            if (Collection.class.isAssignableFrom(v.getClass()))
            {
                Collection<?> c = (Collection<?>)v;
                Integer[] a = new Integer[c.size()];

                int i=0;
                for (Object e : c)
                    a[i++] = IntParser.instance.parse(e, conv);

                return(a);
            }

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public Integer[] parseString(String v, Object conv)
        {
            if (v.length() == 0)
                return(null);

            return(
                this.parseStringArray(
                    StringArrayParser.instance.parseString(v, String.class),
                    Integer.class
                )
            );
        }

        @Override
        public Integer[] parseStringArray(String[] v, Object conv)
        {
            Integer[] b = new Integer[v.length];

            for (int i=0; i<v.length; i++)
                b[i] = (v[i]!=null) ? Integer.valueOf(v[i]) : null;

            return(b);
        }
    }

    public static class IntListParser
        implements ValueParser.ListParser<Integer>
    {
        public static IntListParser instance = new IntListParser();

        @Override
        public List<Integer> parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            if (v instanceof String[])
                return(this.parseStringArray((String[])v, conv));

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public List<Integer> parseString(String v, Object conv)
        {

            if (v.length() == 0)
                return(null);

            return(
                Arrays.asList(
                    IntArrayParser.instance.parseString(v, Integer.class)
                )
            );
        }

        @Override
        public List<Integer> parseStringArray(String[] v, Object conv)
        {
            return(
                Arrays.asList(
                    IntArrayParser.instance.parseStringArray(v, Integer.class)
                )
            );
        }
    }

  // Long
    public static class LongParser
        implements ValueParser<Long>
    {
        public static LongParser instance = new LongParser();

        @Override
        public Long parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof Number)
                return(((Number)v).longValue());

            if (Long.TYPE.isInstance(v))
                return((Long)v);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public Long parseString(String v, Object conv)
        {
            return(
                v!=null ? Long.valueOf(v) : null
            );
        }
    }

    public static class LongArrayParser
        implements ValueParser.ArrayParser<Long>
    {
        public static LongArrayParser instance = new LongArrayParser();

        @Override
        public Long[] parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof Long[])
                return((Long[])v);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            if (v instanceof String[])
                return(this.parseStringArray((String[])v, conv));

            if (v.getClass().isArray())
            {
                Long[] a = new Long[Array.getLength(v)];
                for (int i=0; i<a.length; i++)
                    a[i] = LongParser.instance.parse(Array.get(v, i), conv);

                return(a);
            }

            if (Collection.class.isAssignableFrom(v.getClass()))
            {
                Collection<?> c = (Collection<?>)v;
                Long[] a = new Long[c.size()];

                int i=0;
                for (Object e : c)
                    a[i++] = LongParser.instance.parse(e, conv);

                return(a);
            }

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public Long[] parseString(String v, Object conv)
        {
            if (v.length() == 0)
                return(null);

            return(
                this.parseStringArray(
                    StringArrayParser.instance.parseString(v, String.class),
                    Long.class
                )
            );
        }

        @Override
        public Long[] parseStringArray(String[] v, Object conv)
        {
            Long[] b = new Long[v.length];

            for (int i=0; i<v.length; i++)
                b[i] = (v[i]!=null) ? Long.valueOf(v[i]) : null;

            return(b);
        }
    }

    public static class LongListParser
        implements ValueParser.ListParser<Long>
    {
        public static LongListParser instance = new LongListParser();

        @Override
        public List<Long> parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            if (v instanceof String[])
                return(this.parseStringArray((String[])v, conv));

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public List<Long> parseString(String v, Object conv)
        {
            if (v.length() == 0)
                return(null);

            return(
                Arrays.asList(
                    LongArrayParser.instance.parseString(v, Long.class)
                )
            );
        }

        @Override
        public List<Long> parseStringArray(String[] v, Object conv)
        {
            return(
                Arrays.asList(
                    LongArrayParser.instance.parseStringArray(v, Long.class)
                )
            );
        }
    }

  // Float
    public static class FloatParser
        implements ValueParser<Float>
    {
        public static FloatParser instance = new FloatParser();

        @Override
        public Float parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof Number)
                return(((Number)v).floatValue());

            if (Float.TYPE.isInstance(v))
                return((Float)v);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public Float parseString(String v, Object conv)
        {
            return(
                v!=null ? Float.valueOf(v) : null
            );
        }
    }

    public static class FloatArrayParser
        implements ValueParser.ArrayParser<Float>
    {
        public static FloatArrayParser instance = new FloatArrayParser();

        @Override
        public Float[] parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof Float[])
                return((Float[])v);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            if (v instanceof String[])
                return(this.parseStringArray((String[])v, conv));

            if (v.getClass().isArray())
            {
                Float[] a = new Float[Array.getLength(v)];
                for (int i=0; i<a.length; i++)
                    a[i] = FloatParser.instance.parse(Array.get(v, i), conv);

                return(a);
            }

            if (Collection.class.isAssignableFrom(v.getClass()))
            {
                Collection<?> c = (Collection<?>)v;
                Float[] a = new Float[c.size()];

                int i=0;
                for (Object e : c)
                    a[i++] = FloatParser.instance.parse(e, conv);

                return(a);
            }

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public Float[] parseString(String v, Object conv)
        {
            if (v.length() == 0)
                return(null);

            return(
                this.parseStringArray(
                    StringArrayParser.instance.parseString(v, String.class),
                    Float.class
                )
            );
        }

        @Override
        public Float[] parseStringArray(String[] v, Object conv)
        {
            Float[] b = new Float[v.length];

            for (int i=0; i<v.length; i++)
                b[i] = (v[i]!=null) ? Float.valueOf(v[i]) : null;

            return(b);
        }
    }

    public static class FloatListParser
        implements ValueParser.ListParser<Float>
    {
        public static FloatListParser instance = new FloatListParser();

        @Override
        public List<Float> parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            if (v instanceof String[])
                return(this.parseStringArray((String[])v, conv));

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public List<Float> parseString(String v, Object conv)
        {
            if (v.length() == 0)
                return(null);

            return(
                Arrays.asList(
                    FloatArrayParser.instance.parseString(v, Float.class)
                )
            );
        }

        @Override
        public List<Float> parseStringArray(String[] v, Object conv)
        {
            return(
                Arrays.asList(
                    FloatArrayParser.instance.parseStringArray(v, Float.class)
                )
            );
        }
    }

  // Double
    public static class DoubleParser
        implements ValueParser<Double>
    {
        public static DoubleParser instance = new DoubleParser();

        @Override
        public Double parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof Number)
                return(((Number)v).doubleValue());

            if (Double.TYPE.isInstance(v))
                return((Double)v);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public Double parseString(String v, Object conv)
        {
            return(
                v!=null ? Double.valueOf(v) : null
            );
        }
    }

    public static class DoubleArrayParser
        implements ValueParser.ArrayParser<Double>
    {
        public static DoubleArrayParser instance = new DoubleArrayParser();

        @Override
        public Double[] parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof Double[])
                return((Double[])v);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            if (v instanceof String[])
                return(this.parseStringArray((String[])v, conv));

            if (v.getClass().isArray())
            {
                Double[] a = new Double[Array.getLength(v)];
                for (int i=0; i<a.length; i++)
                    a[i] = DoubleParser.instance.parse(Array.get(v, i), conv);

                return(a);
            }

            if (Collection.class.isAssignableFrom(v.getClass()))
            {
                Collection<?> c = (Collection<?>)v;
                Double[] a = new Double[c.size()];

                int i=0;
                for (Object e : c)
                    a[i++] = DoubleParser.instance.parse(e, conv);

                return(a);
            }

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public Double[] parseString(String v, Object conv)
        {
            if (v.length() == 0)
                return(null);

            return(
                this.parseStringArray(
                    StringArrayParser.instance.parseString(v, String.class),
                    Double.class
                )
            );
        }

        @Override
        public Double[] parseStringArray(String[] v, Object conv)
        {
            Double[] b = new Double[v.length];

            for (int i=0; i<v.length; i++)
                b[i] = (v[i]!=null) ? Double.valueOf(v[i]) : null;

            return(b);
        }
    }

    public static class DoubleListParser
        implements ValueParser.ListParser<Double>
    {
        public static DoubleListParser instance = new DoubleListParser();

        @Override
        public List<Double> parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            if (v instanceof String[])
                return(this.parseStringArray((String[])v, conv));

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public List<Double> parseString(String v, Object conv)
        {
            if (v.length() == 0)
                return(null);

            return(
                Arrays.asList(
                    DoubleArrayParser.instance.parseString(v, Double.class)
                )
            );
        }

        @Override
        public List<Double> parseStringArray(String[] v, Object conv)
        {
            return(
                Arrays.asList(
                    DoubleArrayParser.instance.parseStringArray(v, Double.class)
                )
            );
        }
    }

  // Boolean
    public static class BooleanParser
        implements ValueParser<Boolean>
    {
        public static BooleanParser instance = new BooleanParser();

        @Override
        public Boolean parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof Boolean)
                return((Boolean)v);

            if (Boolean.TYPE.isInstance(v))
                return((Boolean)v);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public Boolean parseString(String v, Object conv)
        {
            return(
                v!=null ? Boolean.valueOf(v) : null
            );
        }
    }

    public static class BooleanArrayParser
        implements ValueParser.ArrayParser<Boolean>
    {
        public static BooleanArrayParser instance = new BooleanArrayParser();

        @Override
        public Boolean[] parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof Boolean[])
                return((Boolean[])v);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            if (v instanceof String[])
                return(this.parseStringArray((String[])v, conv));

            if (v.getClass().isArray())
            {
                Boolean[] a = new Boolean[Array.getLength(v)];
                for (int i=0; i<a.length; i++)
                    a[i] = BooleanParser.instance.parse(Array.get(v, i), conv);

                return(a);
            }

            if (Collection.class.isAssignableFrom(v.getClass()))
            {
                Collection<?> c = (Collection<?>)v;
                Boolean[] a = new Boolean[c.size()];

                int i=0;
                for (Object e : c)
                    a[i++] = BooleanParser.instance.parse(e, conv);

                return(a);
            }

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public Boolean[] parseString(String v, Object conv)
        {
            if (v.length() == 0)
                return(null);

            return(
                this.parseStringArray(
                    StringArrayParser.instance.parseString(v, String.class),
                    Boolean.class
                )
            );
        }

        @Override
        public Boolean[] parseStringArray(String[] v, Object conv)
        {
            Boolean[] b = new Boolean[v.length];

            for (int i=0; i<v.length; i++)
                b[i] = (v[i]!=null) ? Boolean.valueOf(v[i]) : null;

            return(b);
        }
    }

    public static class BooleanListParser
        implements ValueParser.ListParser<Boolean>
    {
        public static BooleanListParser instance = new BooleanListParser();

        @Override
        public List<Boolean> parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            if (v instanceof String[])
                return(this.parseStringArray((String[])v, conv));

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public List<Boolean> parseString(String v, Object conv)
        {
            if (v.length() == 0)
                return(null);

            return(
                Arrays.asList(
                    BooleanArrayParser.instance.parseString(v, Boolean.class)
                )
            );
        }

        @Override
        public List<Boolean> parseStringArray(String[] v, Object conv)
        {
            return(
                Arrays.asList(
                    BooleanArrayParser.instance.parseStringArray(v, Boolean.class)
                )
            );
        }
    }

  // Date
    public static class DateParser
        implements ValueParser<Date>
    {
        public static DateParser instance = new DateParser();

        @Override
        public Date parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof Date)
                return((Date)v);

            if (v instanceof Number)
                return(this.parseLong(((Number)v).longValue(), conv));

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public Date parseString(String v, Object conv)
        {
            Long l = LongParser.instance.parseString(v, Long.class);

            return(this.parseLong(l, conv));
        }

        public Date parseLong(Long v, Object conv)
        {
            return(
                v!=null ? new Date(v) : null
            );
        }
    }

    public static class DateArrayParser
        implements ValueParser.ArrayParser<Date>
    {
        public static DateArrayParser instance = new DateArrayParser();

        @Override
        public Date[] parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof Date[])
                return((Date[])v);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            if (v instanceof String[])
                return(this.parseStringArray((String[])v, conv));

            if (v instanceof Long[])
                return(this.parseLongArray((Long[])v, conv));

            if (v.getClass().isArray())
            {
                Date[] a = new Date[Array.getLength(v)];
                for (int i=0; i<a.length; i++)
                    a[i] = DateParser.instance.parse(Array.get(v, i), conv);

                return(a);
            }

            if (Collection.class.isAssignableFrom(v.getClass()))
            {
                Collection<?> c = (Collection<?>)v;
                Date[] a = new Date[c.size()];

                int i=0;
                for (Object e : c)
                    a[i++] = DateParser.instance.parse(e, conv);

                return(a);
            }

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public Date[] parseString(String v, Object conv)
        {
            if (v.length() == 0)
                return(null);

            return(
                this.parseStringArray(
                    StringArrayParser.instance.parseString(v, String.class),
                    Date.class
                )
            );
        }

        @Override
        public Date[] parseStringArray(String[] v, Object conv)
        {
            return(
                this.parseLongArray(
                    LongArrayParser.instance.parseStringArray(v, String.class),
                    Long.class
                )
            );
        }

        public Date[] parseLongArray(Long[] v, Object conv)
        {
            Date[] b = new Date[v.length];

            for (int i=0; i<v.length; i++)
                b[i] = (v[i]!=null) ? new Date(v[i]) : null;

            return(b);
        }
    }

    public static class DateListParser
        implements ValueParser.ListParser<Date>
    {
        public static DateListParser instance = new DateListParser();

        @Override
        public List<Date> parse(Object v, Object conv)
        {
            if (v == null)
                return(null);

            if (v instanceof String)
                return(this.parseString((String)v, conv));

            if (v instanceof String[])
                return(this.parseStringArray((String[])v, conv));

            throw(new IllegalArgumentException("Unable to parse:"+v.toString()));
        }

        @Override
        public List<Date> parseString(String v, Object conv)
        {
            if (v.length() == 0)
                return(null);

            return(
                Arrays.asList(
                    DateArrayParser.instance.parseString(v, Date.class)
                )
            );
        }

        @Override
        public List<Date> parseStringArray(String[] v, Object conv)
        {
            return(
                Arrays.asList(
                    DateArrayParser.instance.parseStringArray(v, Date.class)
                )
            );
        }
    }

}
