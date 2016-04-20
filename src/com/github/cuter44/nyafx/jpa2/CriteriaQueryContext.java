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

    public CriteriaQueryContext()
    {
        return;
    }

    public CriteriaQueryContext(Class<T> e, CriteriaQuery<T> c, CriteriaBuilder b, Root<T> r)
    {
        this.e = e;
        this.c = c;
        this.b = b;
        this.r = r;

        return;
    }
}
