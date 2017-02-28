package com.github.cuter44.nyafx.jpa2;

import java.lang.ThreadLocal;
import java.util.List;
import javax.persistence.*;
import javax.persistence.criteria.*;

/** DaoBase for JPA 2.0+
 * Auxiliary for application-managed entity manager operation.
 */

public abstract class JPADaoBase<T>
{
  // CONSTRUCT
    /** Thread-bound session implemention.
     * Usually an encapsulation of em-factory, which is responsible to generate new em instance while its initalValue() is called.
     * Must be a shared instance if their representing entities are from same datasource.
     */
    protected ThreadLocal<EntityManager> cem;

    /** NOOP constructor for inhertance
     */
    protected JPADaoBase()
    {
        return;
    }

    public JPADaoBase(ThreadLocal<EntityManager> tlemImpl)
    {
        this.cem = tlemImpl;

        return;
    }

  // EM/EMF
    /** Alias tp <code>this.cem.get()</code>
     * Return the currently using entity manager, create a new one if not yet.
     */
    public EntityManager currentEM()
    {
        return(
            this.cem.get()
        );
    }

    /** Alias to <code>this.currentEM().getEntityManagerFactory().createEntityManager()</code>
     * Create and return a new em other than the current-thread-bound one, and it is the caller's duty to manage its life-cycle.
     * Noted that it always need a current-thread-bound em, which is created if not yet. This may cause resource leak if you forget to end it.
     * This behavior is stand still because we acually not keeping direct access to the emf instance.
     */
    public EntityManager newEM()
    {
        return(
            this.currentEM()
                .getEntityManagerFactory()
                .createEntityManager()
        );
    }

  // TX
    /** Alias to <code>this.currentEM().getTransaction().begin()</code>
     * @see javax.persistence.EntityTransaction#begin()
     */
    public void begin()
        throws IllegalStateException
    {
        this.currentEM()
            .getTransaction()
            .begin();

        return;
    }

    /** Alias to <code>this.currentEM().flush()</code>
     * @see javax.persistence.EntityManager#flush()
     */
    public void flush()
        throws TransactionRequiredException, PersistenceException
    {
        this.currentEM()
            .flush();

        return;
    }

    /** Alias to <code>this.currentEM().getTransaction().commit()</code>
     * @see javax.persistence.EntityTransaction#commit()
     */
    public void commit()
        throws IllegalStateException, RollbackException
    {
        this.currentEM()
            .getTransaction()
            .commit();

        return;
    }

    /** Alias to <code>this.currentEM().getTransaction().rollback()</code>
     * @see javax.persistence.EntityTransaction#rollback()
     */
    public void rollback()
        throws IllegalStateException, PersistenceException
    {
        this.currentEM()
            .getTransaction()
            .rollback();

        return;
    }

    /** Alias to <code>this.currentEM().getTransaction().setRollbackOnly()</code>
     * @see javax.persistence.EntityTransaction#setRolebackOnly()
     */
    public void fail()
        throws IllegalStateException
    {
        this.currentEM()
            .getTransaction()
            .setRollbackOnly();

        return;
    }

    /** Alias to <code>this.currentEM().close()</code>
     * @see javax.persistence.EntityManager#close()
     */
    public void close()
        throws IllegalStateException
    {
        this.currentEM()
            .close();

        this.cem.remove();

        return;
    }

  // AUXILIARY
    public static <X> X nonull(X o)
        throws EntityNotFoundException
    {
        if (o == null)
            throw(new EntityNotFoundException());

        return(o);
    }

    public static <X> X nonull(X o, String msgOnEx)
        throws EntityNotFoundException
    {
        if (o == null)
            throw(new EntityNotFoundException(msgOnEx));

        return(o);
    }

    public static <X> X nonull(X o, Exception ex)
        throws Exception
    {
        if (o == null)
            throw(ex);

        return(o);
    }

    public static <X> X nonull(X o, RuntimeException ex)
    {
        if (o == null)
            throw(ex);

        return(o);
    }

  // CRUD
  // R
    public abstract T get(Object id)
        throws IllegalArgumentException;
    //{
        // default implemention
        //return(
            //this.currentEM()
                //.find(EntityName.class, id)
        //);
    //}

    /** Alias to <code>this.currentEM().find(clazz, id)</code>
     @see javax.persistence.EntityManager#find(java.lang.Class, java.lang.Object)
     */
    public <X> X get(Class<X> clazz, Object id)
        throws IllegalArgumentException
    {
        return(
            this.currentEM()
                .<X>find(clazz, id)
        );
    }

    public <X> X ref(Class<X> clazz, Object id)
        throws IllegalArgumentException, EntityNotFoundException
    {
        return(
            this.currentEM()
            .<X>getReference(clazz, id)
        );
    }

    public <X> X refresh(X o)
    {
        this.currentEM()
            .refresh(o);
        return(o);
    }

  // C
    public void persist(Object o)
        throws EntityExistsException, IllegalArgumentException, TransactionRequiredException
    {
        this.currentEM()
            .persist(o);

        return;
    }

  // U
    /** Alias to <code>this.currentEM().lock()</code>
     * @see javax.persistence.EntityManager#close()
     */
    public void lock(Object e, LockModeType lock)
        throws IllegalStateException
    {
        this.currentEM()
            .lock(e, lock);

        return;
    }

    public <X> X merge(X o)
        throws IllegalArgumentException, TransactionRequiredException
    {
        return(
            this.currentEM()
                .merge(o)
        );
    }

    /** This method is actually NOOP. As JPA use transparent updating
     * It is to provides backward compatility.
     */
    public void update(Object o)
    {
        return;
    }

  // D
    public void remove(Object o)
        throws IllegalArgumentException, TransactionRequiredException
    {
        this.currentEM()
            .remove(o);

        return;
    }

    public <X> void delete(Class<X> clazz, Object id)
        throws IllegalArgumentException, TransactionRequiredException, EntityNotFoundException
    {
        this.currentEM()
            .remove(
            this.<X>ref(clazz, id)
        );

        return;
    }

  // SEARCH
    public Query createNativeQuery(String sql)
    {
        return(
            this.currentEM()
                .createNativeQuery(sql)
        );
    }

  // JPQL/HQL
    /** It is unknown whether it can parse HQL while the underlying implemention is Hibernate...
     */
    public Query query(String jpql)
    {
        return(
            this.currentEM()
                .createQuery(jpql)
        );
    }

    /** It is unknown whether it can parse HQL while the underlying implemention is Hibernate...
     */
    public <X> TypedQuery<X> query(String jpql, Class<X> returnClass)
    {
        return(
            this.currentEM()
                .<X>createQuery(jpql, returnClass)
        );
    }

  // CRITERIA
    public <X> CriteriaQueryContext<X> criteria(Class<X> clazz)
    {
        CriteriaQueryContext<X> ctx = new CriteriaQueryContext<X>();

        ctx.e = clazz;
        ctx.b = this.currentEM().getCriteriaBuilder();
        ctx.c = ctx.b.<X>createQuery(clazz);
        ctx.r = ctx.c.from(clazz);

        ctx.c.select(ctx.r);

        return(ctx);
    }

    public <X> CriteriaCountContext<X> criteriaCount(Class<X> clazz)
    {
        CriteriaCountContext<X> ctx = new CriteriaCountContext<X>();

        ctx.e = clazz;
        ctx.b = this.currentEM().getCriteriaBuilder();
        ctx.c = ctx.b.createQuery(Long.class);
        ctx.r = ctx.c.from(clazz);

        ctx.c.select(ctx.b.countDistinct(ctx.r));

        return(ctx);
    }

    public <X> CriteriaUpdateContext<X> criteriaUpdate(Class<X> clazz)
    {
        CriteriaUpdateContext<X> ctx = new CriteriaUpdateContext<X>();

        ctx.e = clazz;
        ctx.b = this.currentEM().getCriteriaBuilder();
        ctx.c = ctx.b.<X>createCriteriaUpdate(clazz);
        ctx.r = ctx.c.from(clazz);

        return(ctx);
    }

    public <X> CriteriaDeleteContext<X> criteriaDelete(Class<X> clazz)
    {
        CriteriaDeleteContext<X> ctx = new CriteriaDeleteContext<X>();

        ctx.e = clazz;
        ctx.b = this.currentEM().getCriteriaBuilder();
        ctx.c = ctx.b.<X>createCriteriaDelete(clazz);
        ctx.r = ctx.c.from(clazz);

        return(ctx);
    }

    /** Alias to <code>this.currentEM().createQuery(criteria)</code>
     * Shortened method to directly create a create a executable query.
     */
    public <X> TypedQuery<X> query(CriteriaQuery<X> criteria)
    {
        return(
            this.currentEM()
                .<X>createQuery(criteria)
        );
    }

    public Query query(CriteriaUpdate criteria)
    {
        return(
            this.currentEM()
                .createQuery(criteria)
        );
    }

    public Query query(CriteriaDelete criteria)
    {
        return(
            this.currentEM()
                .createQuery(criteria)
        );
    }

    /** Alias to <code>this.currentEM().createQuery(c).getSingleResult()</code>
     * Shortened method to directly get a single result.
     */
    public <X> X getRequired(CriteriaQuery<X> c)
        throws NoResultException, NonUniqueResultException,
            QueryTimeoutException, TransactionRequiredException,
            PessimisticLockException, LockTimeoutException,
            PersistenceException
    {
        return(
            this.currentEM()
                .<X>createQuery(c)
                .getSingleResult()
        );
    }

    /** Alias to <code>this.currentEM().createQuery(c).getSingleResult()</code>, but return null if no result fetched.
     * Shortened method to directly get a single result.
     */
    public <X> X get(CriteriaQuery<X> c)
        throws NonUniqueResultException, PersistenceException,
            QueryTimeoutException, TransactionRequiredException,
            PessimisticLockException, LockTimeoutException
    {
        try
        {
            return(
                this.<X>getRequired(c)
            );
        }
        catch (NoResultException ex)
        {
            return(null);
        }
    }

    public <X> List<X> search(CriteriaQuery<X> criteria)
    {
        return(
            this.<X>query(criteria)
                .getResultList()
        );
    }

    public <X> List<X> search(CriteriaQuery<X> criteria, Integer paginationStart, Integer paginationLimit)
    {
        return(
            paginate(
                this.<X>query(criteria),
                paginationStart,
                paginationLimit
            )
            .getResultList()
        );
    }

  // PAGINATE
    public static Query paginate(Query q, Integer paginationStart, Integer paginationLimit)
    {
        if (paginationStart != null)
            q.setFirstResult(paginationStart);
        if (paginationLimit != null)
            q.setMaxResults(paginationLimit);

        return(q);
    }

    public static <X> TypedQuery<X> paginate(TypedQuery<X> q, Integer paginationStart, Integer paginationLimit)
    {
        if (paginationStart != null)
            q.setFirstResult(paginationStart);
        if (paginationLimit != null)
            q.setMaxResults(paginationLimit);

        return(q);
    }
}
