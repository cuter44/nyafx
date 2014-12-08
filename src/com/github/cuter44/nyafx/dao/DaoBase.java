package com.github.cuter44.nyafx.dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.io.*;

import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.exception.ConstraintViolationException;
import static com.github.cuter44.nyafx.dao.EntityNotFoundException.entFound;

/**
 */
public abstract class DaoBase<T>
{
  // CONSTRUCT
    protected HibernateSessionFactoryWrap factory;

    /** shortcut
     */
    protected ThreadLocal<Session> thisSession;

    public DaoBase()
    {
        this.factory = HibernateSessionFactoryWrap.getInstance();
        this.thisSession = this.factory.getThreadLocal();

        return;
    }

    public DaoBase(HibernateSessionFactoryWrap factory)
    {
        this.factory = factory;
        this.thisSession = this.factory.getThreadLocal();

        return;
    }

  // TRANSACTION
    /** begin a session and transaction
     * Both session and xact are cross-dao, aka. once you invoke CatDao.begin(),
     * it is no needed to invoke DogDao.begin() in the same thread.
     */
    public void begin()
    {
        Session s;

        if (!(s=this.thisSession.get()).isOpen())
            this.thisSession.set(s=this.factory.openSession());
        if (!s.getTransaction().isActive())
            s.beginTransaction();

        return;
    }
    /** flush cached entities to disk.
     * Since session and xact are cross-dao, CatDao.flush() will also flush Dogs.
     */
    public void flush()
    {
        this.thisSession.get()
            .flush();

        return;
    }

    /** commit transaction
     * Since session and xact are cross-dao, It's no need to call commit() on every Dao.
     */
    public void commit()
    {
        this.thisSession.get()
            .getTransaction()
            .commit();

        return;
    }

    /** rollback transaction
     * Since session and xact are cross-dao, DogDao.rollback() will also rollback Cats.
     */
    public void rollback()
    {
        this.thisSession.get()
            .getTransaction()
            .rollback();

        return;
    }

    /** close session
     * Since session and xact are cross-dao, DogDao.rollback() will also rollback Cats.
     * Uncommited xact will be automatically rolled-back on session close.
     */
    public void close()
    {
        this.thisSession.get()
            .close();
        this.thisSession.remove();

        return;
    }

  // CRUD
    protected abstract Class classOfT();

    // NOT DELEGATED
    //public T create()
    //{
    //}

    public T get(Serializable id)
    {
        return(
            (T)(
                this.thisSession.get()
                    .get(this.classOfT(), id)
            )
        );
    }

    public T get(DetachedCriteria dc)
    {
        return(
            (T)(
                dc.getExecutableCriteria(
                    this.thisSession.get()
                ).uniqueResult()
        ));
    }

    public Object get(Class c, Serializable id)
    {
        return(
            this.thisSession.get()
                .get(c, id)
        );
    }

    public void lock(Object o)
    {
        this.thisSession.get()
            .buildLockRequest(LockOptions.UPGRADE).lock(o);
    }

    public Serializable save(Object o)
    {
        try
        {
            return(
                this.thisSession.get()
                    .save(o)
            );
        }
        catch (ConstraintViolationException ex)
        {
            throw(new EntityDuplicatedException(ex.getMessage(), ex));
        }
    }

    public void update(Object o)
    {
        this.thisSession.get()
            .update(o);

        return;
    }

    public void saveOrUpdate(Object o)
    {
        this.thisSession.get()
            .saveOrUpdate(o);

        return;
    }

    public void delete(Object o)
    {
        try
        {
            this.thisSession.get()
                .delete(o);
        }
        catch (ConstraintViolationException ex)
        {
            throw(new EntityReferencedException(ex.getMessage(), ex));
        }

        return;
    }

    public void remove(Serializable id)
    {
        this.delete(
            entFound(this.get(id))
        );
    }

  // EXTENDED
    public Long count(DetachedCriteria dc)
    {
        Criteria c = dc.getExecutableCriteria(
                this.thisSession.get()
            ).setProjection(
                Projections.rowCount()
            );

        return((Long)c.uniqueResult());
    }

    public List search(DetachedCriteria dc, Integer start, Integer size)
    {
        Criteria c = dc.getExecutableCriteria(
            this.thisSession.get()
        );

        if (start != null)
            c.setFirstResult(start);
        if (size != null)
            c.setMaxResults(size);

        return(
            c.list()
        );
    }

    public List search(DetachedCriteria dc)
    {
        return(
            this.search(dc, null, null)
        );
    }

    public Query createQuery(String hql)
    {
        return(
            this.thisSession.get()
                .createQuery(hql)
        );
    }

  // MISC
    public Session getThisSession()
    {
        return(
            this.thisSession.get()
        );
    }
}
