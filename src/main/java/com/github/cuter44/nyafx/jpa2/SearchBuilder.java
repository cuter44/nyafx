package com.github.cuter44.nyafx.jpa2;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;
import javax.persistence.*;
import javax.persistence.criteria.*;

import com.alibaba.fastjson.*;
import com.alibaba.fastjson.util.*;

public class SearchBuilder
{
  // CONSTANTS
    public static final String CLAUSE_WHERE = "where";
    public static final String CLAUSE_ORDER = "order";
    public static final String CLAUSE_START = "start";
    public static final String CLAUSE_LIMIT = "limit";

    public static final String ORDER_ASC    = "asc";
    public static final String ORDER_DESC   = "desc";

    protected RestrictionsParser restrictionsParser;
    protected OrdersParser ordersParser;

  // CONSTRUCT
    public SearchBuilder()
    {
        return;
    }

    public SearchBuilder set(RestrictionsParser parser)
    {
        return(
            this.setRestrictionsParser(parser)
        );
    }

    public SearchBuilder setRestrictionsParser(RestrictionsParser parser)
    {
        this.restrictionsParser = parser;

        return(this);
    }

    public SearchBuilder set(OrdersParser parser)
    {
        return(
            this.setOrdersParser(parser)
        );
    }

    public SearchBuilder setOrdersParser(OrdersParser parser)
    {
        this.ordersParser = parser;

        return(this);
    }

  // SUR PROCEDURE
    /**
     * @deprecated Order parsing is delegated to <code>ordersParser</code> since 4.1. This function is preserved to compatibility.
     * @return <code>c</code>
     */
    @Deprecated
    protected <X> AbstractCriteriaContext<X> parseOrder(AbstractCriteriaContext<X> c, JSONArray orders)
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
                        c.getB().asc(
                            c.getR().get(p)
                        )
                    );

                    continue;
                }

                String o = ord.getString(1);

                if (ORDER_DESC.equalsIgnoreCase(o))
                {
                    parsedOrders.add(
                        c.getB().desc(
                            c.getR().get(p)
                        )
                    );
                    continue;
                }

                if (ORDER_ASC.equalsIgnoreCase(o))
                {
                    parsedOrders.add(
                        c.getB().asc(
                            c.getR().get(p)
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

        c.orderBy(parsedOrders);

        return(c);
    }

  // EXPOSED
   /**
    * @deprecated this is no longer used as hint syntax is deprecated.
    */
    @Deprecated
    public <X> AbstractCriteriaContext<X> parseSearch(AbstractCriteriaContext<X> c, JSONObject search, JSONObject hint)
        throws IllegalArgumentException, NoSuchFieldException
    {
        // WHERE
        JSONObject restrictions = search.getJSONObject(CLAUSE_WHERE);
        if (restrictions != null)
        {
            c = this.restrictionsParser.merge(c, restrictions, hint);
        }

        // ORDER
        JSONArray orders = search.getJSONArray(CLAUSE_ORDER);
        if (orders != null)
        {
            c = this.parseOrder(c, orders);
        }

        // START
        c.setStart(search.getInteger(CLAUSE_START));
        // LIMIT
        c.setLimit(search.getInteger(CLAUSE_LIMIT));

        return(c);
    }

    /** Parsing a json-form search to jpa-form.
     * Since 4.1 orders parsing is delegated to <code>ordersParser</code>
     */
    public <X> AbstractCriteriaContext<X> parse(AbstractCriteriaContext<X> c, JSONObject search, JSONObject hint)
        throws IllegalArgumentException, NoSuchFieldException
    {
        // WHERE
        JSONObject restrictions = search.getJSONObject(CLAUSE_WHERE);
        if (restrictions != null)
        {
            c = this.restrictionsParser.apply(c, restrictions, hint!=null ? hint.getJSONObject(CLAUSE_WHERE) : null);
        }

        // ORDER
        JSONArray orders = search.getJSONArray(CLAUSE_ORDER);
        if (orders != null)
        {
            c = this.ordersParser.apply(c, orders, hint!=null ? hint.getJSONArray(CLAUSE_ORDER) : null);
            //c = this.parseOrder(c, orders);
        }

        // START
        c.setStart(search.getInteger(CLAUSE_START));
        // LIMIT
        c.setLimit(search.getInteger(CLAUSE_LIMIT));

        return(c);
    }

    /** Tweak existing criteria query with the given criterions.
     */
    public <X> AbstractCriteriaContext<X> tweakRestrictions(AbstractCriteriaContext<X> c, JSONObject search, JSONObject hint)
        throws IllegalArgumentException, NoSuchFieldException
    {
        // WHERE
        JSONObject restrictions = search.getJSONObject(CLAUSE_WHERE);
        if (restrictions != null)
        {
            c = this.restrictionsParser.apply(c, restrictions, hint!=null ? hint.getJSONObject(CLAUSE_WHERE) : null);
        }

        return(c);
    }

    /** Tweak existing criteria query with the given orders.
     */
    public <X> AbstractCriteriaContext<X> tweakOrders(AbstractCriteriaContext<X> c, JSONObject search, JSONObject hint)
        throws IllegalArgumentException, NoSuchFieldException
    {
        // ORDER
        JSONArray orders = search.getJSONArray(CLAUSE_ORDER);
        if (orders != null)
        {
            c = this.ordersParser.apply(c, orders, hint!=null ? hint.getJSONArray(CLAUSE_ORDER) : null);
            //c = this.parseOrder(c, orders);
        }
        return(c);
    }
    /** Merge given criterions into existing criteria query.
     */
    public <X> AbstractCriteriaContext<X> mergeRestrictions(AbstractCriteriaContext<X> c, JSONObject search, JSONObject hint)
        throws IllegalArgumentException, NoSuchFieldException
    {
        // WHERE
        JSONObject restrictions = search.getJSONObject(CLAUSE_WHERE);
        if (restrictions != null)
        {
            c = this.restrictionsParser.merge(c, restrictions, hint!=null ? hint.getJSONObject(CLAUSE_WHERE) : null);
        }

        return(c);
    }

    /** Append given orders to the tail of orders list of existing criteria query.
     */
    public <X> AbstractCriteriaContext<X> appendOrders(AbstractCriteriaContext<X> c, JSONObject search, JSONObject hint)
        throws IllegalArgumentException, NoSuchFieldException
    {
        // ORDER
        JSONArray orders = search.getJSONArray(CLAUSE_ORDER);
        if (orders != null)
        {
            c = this.ordersParser.append(c, orders, hint!=null ? hint.getJSONArray(CLAUSE_ORDER) : null);
            //c = this.parseOrder(c, orders);
        }
        return(c);
    }

    /** Prepend given orders to the head of orders list of 2016/11/18existing criteria query.
     */
    public <X> AbstractCriteriaContext<X> prependOrders(AbstractCriteriaContext<X> c, JSONObject search, JSONObject hint)
        throws IllegalArgumentException, NoSuchFieldException
    {
        // ORDER
        JSONArray orders = search.getJSONArray(CLAUSE_ORDER);
        if (orders != null)
        {
            c = this.ordersParser.prepend(c, orders, hint!=null ? hint.getJSONArray(CLAUSE_ORDER) : null);
            //c = this.parseOrder(c, orders);
        }

        return(c);
    }
}
