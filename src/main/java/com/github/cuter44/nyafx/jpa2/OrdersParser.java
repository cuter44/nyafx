package com.github.cuter44.nyafx.jpa2;

import java.util.List;
import javax.persistence.criteria.*;

import com.alibaba.fastjson.*;

public interface OrdersParser
{
    public abstract <X> List<Order> parse(AbstractCriteriaContext<X> c, JSONArray orders, List hints);
    public abstract <X> AbstractCriteriaContext<X> apply(AbstractCriteriaContext<X> c, JSONArray orders, List hints);
    public abstract <X> AbstractCriteriaContext<X> prepend(AbstractCriteriaContext<X> c, JSONArray orders, List hints);
    public abstract <X> AbstractCriteriaContext<X> append(AbstractCriteriaContext<X> c, JSONArray orders, List hints);
}
