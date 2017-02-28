package com.github.cuter44.nyafx.jpa2;

import java.util.List;
import java.util.ArrayList;
import javax.persistence.criteria.*;

public class CriteriaDeleteContext<T>
    implements AbstractCriteriaContext<T>
{
    /** Targeted entity class
     */
    public Class<T> e;

    public CriteriaDelete<T> c;

    public CriteriaBuilder b;

    public Root<T> r;

    public CriteriaDeleteContext()
    {
        return;
    }

    public CriteriaDeleteContext(Class<T> e, CriteriaDelete<T> c, CriteriaBuilder b, Root<T> r)
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
    public CriteriaDeleteContext<T> where(Expression<Boolean> restriction)
    {
        this.c.where(restriction);
        return(this);
    }

    @Override
    public CriteriaDeleteContext<T> where(Predicate ... restrictions)
    {
        this.c.where(restrictions);
        return(this);
    }

    @Override
    public List<Order> getOrderList()
    {
        return(new ArrayList<Order>(0));
    }

    @Override
    public CriteriaDeleteContext<T> orderBy(List<Order> o)
    {
        //NOOP
        return(this);
    }

    @Override
    public CriteriaDeleteContext<T> orderBy(Order ... o)
    {
        //NOOP
        return(this);
    }
}