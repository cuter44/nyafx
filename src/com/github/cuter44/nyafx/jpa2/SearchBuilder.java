package com.github.cuter44.nyafx.jpa2;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.regex.Pattern;
import javax.persistence.*;
import javax.persistence.criteria.*;

import com.alibaba.fastjson.*;
import com.alibaba.fastjson.util.*;

import com.github.cuter44.nyafx.servlet.ParserBundle;

public class SearchBuilder
{
  // CONSTANTS
    public static final String CLAUSE_WHERE = "where";
    public static final String CLAUSE_ORDER = "order";
    public static final String CLAUSE_START = "start";
    public static final String CLAUSE_LIMIT = "limit";

    public static final String ORDER_ASC    = "asc";
    public static final String ORDER_DESC   = "desc";

    public static final String OPERATOR_EQ  = "eq";
    public static final String OPERATOR_LT  = "lt";
    public static final String OPERATOR_LE  = "le";
    public static final String OPERATOR_GT  = "gt";
    public static final String OPERATOR_GE  = "ge";
    public static final String OPERATOR_IN  = "in";

    public static final Pattern PATTERN_EQ  = Pattern.compile("\\beq\\b");
    public static final Pattern PATTERN_LT  = Pattern.compile("\\blt\\b");
    public static final Pattern PATTERN_LE  = Pattern.compile("\\ble\\b");
    public static final Pattern PATTERN_GT  = Pattern.compile("\\bgt\\b");
    public static final Pattern PATTERN_GE  = Pattern.compile("\\bge\\b");
    public static final Pattern PATTERN_IN  = Pattern.compile("\\bin\\b");
    public static final Pattern PATTERN_X   = Pattern.compile("\\bx\\b");

    protected String DEFAULT_HINT_VALUE = "eq lt le gt ge in";

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


  // CLASS NARRATOR
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

  // TYPE PARSE
    protected ParserBundle parsers = ParserBundle.newDefaultInstance();

  // SUB PROCESS
    protected class RestrictionsParser<T>
    {
        protected CriteriaQueryContext<T> c;
        protected List<Predicate> predicates;
        protected JSONObject restrictions;
        protected JSONObject hint;

      // CONSTRUCT
        public RestrictionsParser(CriteriaQueryContext c, JSONObject restrictions, JSONObject hint)
        {
            this.c = c;
            this.restrictions = restrictions;
            this.hint = hint;

            return;
        }

      // PROCESS
        protected void parsePath(Path p, Class e, JSONObject search, JSONObject hint)
        {
            hint = hint!=null ? hint : new JSONObject();

            for (String k:search.keySet())
            {
                String ph = SearchBuilder.this.extractHint(k);
                if (SearchBuilder.PATTERN_X.matcher(ph).find())
                    continue;

                Object v = search.get(k);
                FieldInfo fi = SearchBuilder.this.getFieldInfos(e).get(k);
                if (fi == null)
                {
                    // slient fail on unrecognized property k
                    continue;
                }
                Class pClass = fi.fieldClass;

                // SWITCH
                // CASE:SUB HIERARCHY
                if (v instanceof JSONObject)
                {
                    this.parsePath(
                        p.get(k),
                        pClass,
                        (JSONObject)v,
                        wrapHint(hint.get(k))
                    );

                    continue;
                }

                // CASE:CRITERIONS
                if (v instanceof JSONArray)
                {
                    this.parseCriterions(
                        p,
                        pClass,
                        (JSONArray)v,
                        ph
                    );

                    continue;
                }
            }
        }

        public void parseCriterions(Path p, Class pClass, JSONArray criterions, String hint)
        {
            if (SearchBuilder.PATTERN_X.matcher(hint).find())
                return;

            for (Object e:criterions)
            {
                JSONArray cr = (JSONArray)e;

                String predicate = cr.getString(0).toLowerCase();

                // SWITCH
                // CASE:EQ
                if (OPERATOR_EQ.equals(predicate))
                {
                    if (SearchBuilder.PATTERN_EQ.matcher(hint).find())
                    {
                        this.predicates.add(
                            this.c.b.equal(
                                p,
                                SearchBuilder.this.parsers.parse(cr.get(1), pClass)
                            )
                        );
                    }

                    continue;
                }

                // CASE:LT
                if (OPERATOR_LT.equals(predicate))
                {
                    if (SearchBuilder.PATTERN_LT.matcher(hint).find())
                    {
                        this.predicates.add(
                            this.c.b.lessThan(
                                p,
                                (Comparable)SearchBuilder.this.parsers.parse(cr.get(1), pClass)
                            )
                        );
                    }

                    continue;
                }

                // CASE:GT
                if (OPERATOR_GT.equals(predicate))
                {
                    if (SearchBuilder.PATTERN_LT.matcher(hint).find())
                    {
                        this.predicates.add(
                            this.c.b.greaterThan(
                                p,
                                (Comparable)SearchBuilder.this.parsers.parse(cr.get(1), pClass)
                            )
                        );
                    }

                    continue;
                }

                // CASE:LE
                if (OPERATOR_LE.equals(predicate))
                {
                    if (SearchBuilder.PATTERN_LT.matcher(hint).find())
                    {
                        this.predicates.add(
                            this.c.b.lessThanOrEqualTo(
                                p,
                                (Comparable)SearchBuilder.this.parsers.parse(cr.get(1), pClass)
                            )
                        );
                    }

                    continue;
                }

                // CASE:GE
                if (OPERATOR_GE.equals(predicate))
                {
                    if (SearchBuilder.PATTERN_GE.matcher(hint).find())
                    {
                        this.predicates.add(
                            this.c.b.greaterThanOrEqualTo(
                                p,
                                (Comparable)SearchBuilder.this.parsers.parse(cr.get(1), pClass)
                            )
                        );
                    }

                    continue;
                }

                // CASE:IN
                if (OPERATOR_IN.equals(predicate))
                {
                    if (SearchBuilder.PATTERN_IN.matcher(hint).find())
                    {
                        JSONArray rawValues = (JSONArray)criterions.get(1);
                        List parsedValues = new ArrayList(rawValues.size());

                        for (Object v:rawValues)
                            parsedValues.add(SearchBuilder.this.parsers.parse(v, pClass));

                        this.predicates.add(
                            p.in(parsedValues)
                        );
                    }

                    continue;
                }
            }
        }

      // EXPOSED
        public CriteriaQueryContext<T> startParse()
        {
            String rh = SearchBuilder.this.extractHint(hint);
            if (SearchBuilder.PATTERN_X.matcher(rh).find())
                return(this.c);

            this.predicates = new ArrayList<Predicate>();

            this.parsePath(this.c.r, this.c.e, this.restrictions, this.hint);

            //Predicate[] p = this.predicates.toArray(new Predicate[this.predicates.size()]);
            this.c.c.where(
                this.c.b.and(
                    this.predicates.toArray(new Predicate[this.predicates.size()])
                )
            );

            return(this.c);
        }

    }

    /**
     * @return c
     */
    protected <X> CriteriaQueryContext<X> parseOrder(CriteriaQueryContext<X> c, JSONArray orders)
    {
        List<Order> parsedOrders = new ArrayList<Order>(orders.size());

        for (Object e0:orders)
        {
            try
            {
                JSONArray ord = (JSONArray)e0;

                String p = ord.getString(0);

                if (ord.size()==1)
                {
                    parsedOrders.add(
                        c.b.asc(
                            c.r.get(p)
                        )
                    );

                    continue;
                }

                String o = ord.getString(1);

                if (ORDER_DESC.equalsIgnoreCase(o))
                {
                    parsedOrders.add(
                        c.b.desc(
                            c.r.get(p)
                        )
                    );
                    continue;
                }

                if (ORDER_ASC.equalsIgnoreCase(o))
                {
                    parsedOrders.add(
                        c.b.asc(
                            c.r.get(p)
                        )
                    );
                    continue;
                }
            }
            catch (ClassCastException ex)
            {
                ex.printStackTrace();
            }
            catch (IndexOutOfBoundsException ex)
            {
                ex.printStackTrace();
            }
        }

        c.c.orderBy(parsedOrders);

        return(c);
    }

  // EXPOSED
    public <X> CriteriaQueryContext<X> parseSearch(CriteriaQueryContext<X> c, JSONObject search, JSONObject hint)
    {
        // WHERE
        JSONObject restrictions = search.getJSONObject(CLAUSE_WHERE);
        if (restrictions != null)
        {
            c = new RestrictionsParser<X>(c, restrictions, hint).startParse();
        }

        // ORDER
        JSONArray orders = search.getJSONArray(CLAUSE_ORDER);
        if (orders != null)
        {
            c = this.parseOrder(c, orders);
        }

        // START
        c.start = search.getInteger(CLAUSE_START);
        // LIMIT
        c.limit = search.getInteger(CLAUSE_LIMIT);

        return(c);
    }

  //
}
