package com.github.cuter44.nyafx.jpa2;

import java.util.List;
import javax.persistence.criteria.*;

public class CriteriaCountContext<T>
    implements AbstractCriteriaContext<T>
{
    /** Targeted entity class
     */
    public Class<T> e;

    public CriteriaQuery<Long> c;

    public CriteriaBuilder b;

    public Root<T> r;

    public CriteriaCountContext()
    {
        return;
    }

    public CriteriaCountContext(Class<T> e, CriteriaQuery<Long> c, CriteriaBuilder b, Root<T> r)
    {
        this();

        this.e = e;
        this.c = c;
        this.b = b;
        this.r = r;

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
        return(null);
    }
    @Override
    public void setStart(Integer start)
    {
        // NOOP

        return;
    }

    @Override
    public Integer getLimit()
    {
        return(null);
    }
    @Override
    public void setLimit(Integer limit)
    {
        // NOOP

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
    public CriteriaCountContext<T> where(Expression<Boolean> restriction)
    {
        this.c.where(restriction);
        return(this);
    }

    @Override
    public CriteriaCountContext<T> where(Predicate ... restrictions)
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
    public CriteriaCountContext<T> orderBy(List<Order> o)
    {
        this.c.orderBy(o);
        return(this);
    }

    @Override
    public CriteriaCountContext<T> orderBy(Order ... o)
    {
        this.c.orderBy(o);
        return(this);
    }

}
