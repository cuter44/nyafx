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

  // CONSTRUCT
    public SearchBuilder()
    {
        this.restrictionsParser = new RestrictionsParser();

        this.restrictionsParser.constraintApplied = true;
        this.restrictionsParser.failException = false;

        return;
    }

  // SUR PROCEDURE
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
        c.start = search.getInteger(CLAUSE_START);
        // LIMIT
        c.limit = search.getInteger(CLAUSE_LIMIT);

        return(c);
    }

}
