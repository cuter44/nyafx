package com.github.cuter44.nyafx.hbac;

import java.io.Serializable;
import java.util.List;

import com.github.cuter44.nyafx.dao.*;

import org.hibernate.Hibernate;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.criterion.DetachedCriteria;

public abstract class DaoBaseAc<T> extends DaoBase<T>
{
    protected Enforcer enforcer;

  // CONSTRUCT
    public DaoBaseAc()
    {
        super();

        this.enforcer = Enforcer.getDefaultInstance();

        return;
    }

    public DaoBaseAc(HibernateSessionFactoryWrap factory)
    {
        super(factory);

        this.enforcer = Enforcer.getDefaultInstance();

        return;
    }

    public DaoBaseAc(HibernateSessionFactoryWrap factory, Enforcer enforcer)
    {
        super(factory);

        this.enforcer = enforcer;

        return;
    }

  // CRUD
    public T acGet(Serializable id, Object originator)
        throws SecurityException
    {
        T o = super.get(id);

        Judgement j = this.enforcer.acAssert(originator, o, "R");

        switch (j)
        {
            case ALLOW:
            case ACQUIESCE:
                return(o);
            default:
                throw(new SecurityException("[hbac]Access denied."));
        }
    }

    public Object acGet(Class c, Serializable id, Object originator)
    {
        Object o = super.get(c, id);

        Judgement j = this.enforcer.acAssert(originator, o, "R");

        switch (j)
        {
            case ALLOW:
            case ACQUIESCE:
                return(o);
            default:
                throw(new SecurityException("[hbac]Access denied."));
        }
    }

    public T acGet(DetachedCriteria dc, Object originator)
    {
        T o = super.get(dc);

        Judgement j = this.enforcer.acAssert(originator, o, "R");

        switch (j)
        {
            case ALLOW:
            case ACQUIESCE:
                return(o);
            default:
                throw(new SecurityException("[hbac]Access denied."));
        }
    }

    /**
     * Suspected issue:
     * Due to the cache mechanism, not-yet-saved item may not have a mapping db record.
     * On such situation running a query on the to-save object may result to blank result-set, which may cause faulty judgement.
     */
    public Serializable acSave(Object o, Object originator)
    {
        Serializable id = super.save(o);

        super.flush();

        Judgement j = this.enforcer.acAssert(originator, o, "C");

        switch (j)
        {
            case ALLOW:
            case ACQUIESCE:
                return(id);
            default:
                throw(new SecurityException("[hbac]Access denied."));
        }
    }

    public void acUpdate(Object o, Object originator)
    {
        Class co = Hibernate.getClass(o);
        ClassMetadata cm = super.getClassMetadata(co);

        Object oo = super.get(co, cm.getIdentifier(o));

        Judgement j = this.enforcer.acAssert(originator, oo, "U");

        switch (j)
        {
            case ALLOW:
            case ACQUIESCE:
                return;
            default:
                throw(new SecurityException("[hbac]Access denied."));
        }
    }

    public void acDelete(Object o, Object originator)
    {
        Class co = Hibernate.getClass(o);
        ClassMetadata cm = super.getClassMetadata(co);

        Object oo = super.get(co, cm.getIdentifier(o));

        Judgement j = this.enforcer.acAssert(originator, oo, "D");

        switch (j)
        {
            case ALLOW:
            case ACQUIESCE:
                return;
            default:
                throw(new SecurityException("[hbac]Access denied."));
        }
    }

    public void acRemove(Serializable id, Object originator)
    {
        Object o = super.get(this.classOfT(), id);

        Judgement j = this.enforcer.acAssert(originator, o, "D");

        switch (j)
        {
            case ALLOW:
            case ACQUIESCE:
                return;
            default:
                throw(new SecurityException("[hbac]Access denied."));
        }
    }

    public List acSearch(DetachedCriteria dc, Class objClass, Object originator)
    {
        this.enforcer.acCriteria(dc, originator, objClass, "R");

        return(
            super.search(dc)
        );
    }

    public List acSearch(DetachedCriteria dc, Integer start, Integer size, Class objClass, Object originator)
    {
        this.enforcer.acCriteria(dc, originator, objClass, "R");

        return(
            super.search(dc, start, size)
        );
    }

    /** Judge if Rule is satisified. Throw a security exception if not.
     * @return the obj
     */
    public Object acAssert(Object obj, Object sub, String action)
        throws SecurityException
    {
        Judgement j = this.enforcer.acAssert(obj, sub, action);

        switch (j)
        {
            case RESIST:
            case DENY:
                throw(new SecurityException("hbac:Action "+j.toString()));
            default:
        }

        return(obj);
    }

}
