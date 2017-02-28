package com.github.cuter44.nyafx.jpa2;

import java.util.List;
import java.util.ArrayList;
import javax.persistence.criteria.*;

import com.alibaba.fastjson.*;

public class UnhintedOrdersParser
    implements OrdersParser
{
    public static final String ORDER_ASC    = "asc";
    public static final String ORDER_DESC   = "desc";

    @Override
    public <X> List<Order> parse(AbstractCriteriaContext<X> c, JSONArray orders, List hints)
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

        return(parsedOrders);

    }

    @Override
    public <X> AbstractCriteriaContext<X> apply(AbstractCriteriaContext<X> c, JSONArray orders, List hint)
    {
        List<Order> p = this.parse(c, orders, hint);

        c.orderBy(p);

        return(c);
    }

    @Override
    public <X> AbstractCriteriaContext<X> append(AbstractCriteriaContext<X> c, JSONArray orders, List hint)
    {
        List<Order> p1 = this.parse(c, orders, hint);
        List<Order> p0 = c.getOrderList();

        List<Order> p = new ArrayList<Order>(p0.size() + p1.size());
        p.addAll(p0);
        p.addAll(p1);

        c.orderBy(p);

        return(c);
    }

    @Override
    public <X> AbstractCriteriaContext<X> prepend(AbstractCriteriaContext<X> c, JSONArray orders, List hint)
    {
        List<Order> p1 = this.parse(c, orders, hint);
        List<Order> p0 = c.getOrderList();

        List<Order> p = new ArrayList<Order>(p0.size() + p1.size());
        p.addAll(p1);
        p.addAll(p0);

        c.orderBy(p);

        return(c);
    }

}
