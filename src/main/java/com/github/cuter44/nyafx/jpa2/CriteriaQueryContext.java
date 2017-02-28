package com.github.cuter44.nyafx.jpa2;

import java.util.List;
import javax.persistence.criteria.*;

public class CriteriaQueryContext<T>
    implements AbstractCriteriaContext<T>
{
    /** Targeted entity class
     */
    public Class e;

    public CriteriaQuery<T> c;

    public CriteriaBuilder b;

    public Root<T> r;

    public Integer start;

    public Integer limit;

    public CriteriaQueryContext()
    {
        return;
    }

    public CriteriaQueryContext(Class<T> e, CriteriaQuery<T> c, CriteriaBuilder b, Root<T> r)
    {
        this();

        this.e = e;
        this.c = c;
        this.b = b;
        this.r = r;

        return;
    }

    public CriteriaQueryContext(Class<T> e, CriteriaQuery<T> c, CriteriaBuilder b, Root<T> r, Integer start, Integer limit)
    {
        this(e, c, b, r);

        this.start = start;
        this.limit = limit;

        return;
    }

  // ABSTRACT
    @Override
    public Class<T> getE()
    {
        return(this.e);
    }

    @Override
    public CriteriaBuilder getB()
    {
        return(this.b);
    }

    @Override
    public Root<T> getR()
    {
        return(this.r);
    }

    @Override
    public Integer getStart()
    {
        return(this.start);
    }
    @Override
    public void setStart(Integer start)
    {
        this.start = start;

        return;
    }

    @Override
    public Integer getLimit()
    {
        return(this.limit);
    }
    @Override
    public void setLimit(Integer limit)
    {
        this.limit = limit;

        return;
    }

    @Override
    public Predicate getRestriction()
    {
        return(
            this.c.getRestriction()
        );
    }

    @Override
    public CriteriaQueryContext<T> where(Expression<Boolean> restriction)
    {
        this.c.where(restriction);
        return(this);
    }

    @Override
    public CriteriaQueryContext<T> where(Predicate ... restrictions)
    {
        this.c.where(restrictions);
        return(this);
    }

    @Override
    public List<Order> getOrderList()
    {
        return(
            this.c.getOrderList()
        );
    }

    @Override
    public CriteriaQueryContext<T> orderBy(List<Order> o)
    {
        this.c.orderBy(o);
        return(this);
    }

    @Override
    public CriteriaQueryContext<T> orderBy(Order ... o)
    {
        this.c.orderBy(o);
        return(this);
    }
}