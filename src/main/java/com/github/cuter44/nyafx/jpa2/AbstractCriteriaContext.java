package com.github.cuter44.nyafx.jpa2;

import java.util.List;
import javax.persistence.criteria.*;

public interface AbstractCriteriaContext<T>
{
    public abstract Class<T> getE();
    public abstract CriteriaBuilder getB();
    public abstract Root<T> getR();

    public abstract Integer getStart();
    public abstract void setStart(Integer start);

    public abstract Integer getLimit();
    public abstract void setLimit(Integer limit);

    public abstract Predicate getRestriction();
    public abstract AbstractCriteriaContext<T> where(Expression<Boolean> restriction);
    public abstract AbstractCriteriaContext<T> where(Predicate ... restrictions);

    public abstract List<Order> getOrderList();
    public abstract AbstractCriteriaContext<T> orderBy(List<Order> o);
    public abstract AbstractCriteriaContext<T> orderBy(Order ... o);
}
