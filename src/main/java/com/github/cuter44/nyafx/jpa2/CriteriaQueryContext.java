package com.github.cuter44.nyafx.jpa2;

import javax.persistence.*;
import javax.persistence.criteria.*;

public class CriteriaQueryContext<T>
{
    /** Targeted entity class
     */
    public Class<T> e;

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
}
