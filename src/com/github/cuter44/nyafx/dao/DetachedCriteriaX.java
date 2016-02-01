package com.github.cuter44.nyafx.dao;

import java.lang.reflect.*;

import org.hibernate.*;
import org.hibernate.internal.*;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.sql.JoinType;
import org.hibernate.criterion.*;
import org.hibernate.transform.ResultTransformer;

/** Patch hibernate4 DetachedCriteria to add duplicate criteria support.
 * Meanwhile, it is known to conflict with alias and features depending on alias.
 * Be sure to read the source file to confirm the unsupported APIs.
 */
public class DetachedCriteriaX extends DetachedCriteria
{
    // UNSUPPORTED
    //protected DetachedCriteria(String entityName);
    //protected DetachedCriteria(String entityName, String alias);

    protected DetachedCriteriaX(CriteriaImpl impl, Criteria criteria)
    {
        super(impl, criteria);

        return;
    }

    protected CriteriaImpl getImpl()
    {
        try
        {
            Field fImpl = DetachedCriteria.class.getDeclaredField("impl");
            fImpl.setAccessible(true);
            return(
                (CriteriaImpl)(
                    fImpl.get(this)
                )
            );
        }
        catch (NoSuchFieldException ex)
        {
            throw(new RuntimeException(ex));
        }
        catch (IllegalAccessException ex)
        {
            throw(new RuntimeException(ex));
        }
    }

    protected Criteria getCriteria()
    {
        try
        {
            Field fCriteria = DetachedCriteria.class.getDeclaredField("criteria");
            fCriteria.setAccessible(true);
            return(
                (Criteria)(
                    fCriteria.get(this)
                )
            );
        }
        catch (NoSuchFieldException ex)
        {
            throw(new RuntimeException(ex));
        }
        catch (IllegalAccessException ex)
        {
            throw(new RuntimeException(ex));
        }
    }

    // INHERITED SAFE
    //public Criteria getExecutableCriteria(Session session);
    //CriteriaImpl getImpl();

    /** Override super.forEntityName(String entityName)
     */
    public static DetachedCriteriaX forEntityName(String entityName)
    {
        CriteriaImpl impl = new CriteriaImplX(entityName, null);

        return(
            new DetachedCriteriaX(impl, impl)
        );
    }

    /** Override super.forEntityName(Stirng entityName, String alias)
     * UNSUPPORTED due to alias feature conflict
     */
    public static DetachedCriteriaX forEntityName(String entityName, String alias)
    {
        CriteriaImpl impl = new CriteriaImplX(entityName, alias, null);

        return(
            new DetachedCriteriaX(impl, impl)
        );
    }

    /** Override super.forClass(Class clazz)
     */
    public static DetachedCriteriaX forClass(Class clazz)
    {
        return(
            DetachedCriteriaX.forEntityName(clazz.getName())
        );
    }

    /** Override super.forClass(Class clazz, String alias)
     * UNSUPPORTED due to alias feature conflict
     */
    public static DetachedCriteriaX forClass(Class clazz, String alias)
    {
        return(
            DetachedCriteriaX.forEntityName(clazz.getName(), alias)
        );
    }

    @Override
    public DetachedCriteriaX add(Criterion criterion)
    {
        super.add(criterion);
        return(this);
    }

    @Override
    public DetachedCriteriaX addOrder(Order order)
    {
        super.addOrder(order);
        return(this);
    }
    @Override

    public DetachedCriteriaX setFetchMode(String associationPath, FetchMode mode)
    {
        super.setFetchMode(associationPath, mode);
        return(this);
    }

    @Override
    public DetachedCriteriaX setProjection(Projection projection)
    {
        super.setProjection(projection);
        return(this);
    }

    @Override
    public DetachedCriteriaX setResultTransformer(ResultTransformer resultTransformer)
    {
        super.setResultTransformer(resultTransformer);
        return(this);
    }

    /**
     * Warning: this API is not duplicated-association-path proofed.
     */
    public DetachedCriteriaX createAlias(String associationPath, String alias)
    {
		this.getCriteria().createAlias( associationPath, alias );
		return this;
    }

    /**
     * Warning: this API is not duplicated-association-path proofed.
     */
    public DetachedCriteriaX createAlias(String associationPath, String alias, JoinType joinType)
    {
		this.getCriteria().createAlias( associationPath, alias, joinType );
		return this;
	}

    /**
     * Warning: this API is not duplicated-association-path proofed.
     */
    public DetachedCriteriaX createAlias(String associationPath, String alias, JoinType joinType, Criterion withClause)
    {
		this.getCriteria().createAlias( associationPath, alias, joinType, withClause );
		return this;
	}

    @Deprecated
    public DetachedCriteriaX createAlias(String associationPath, String alias, int joinType)
    {
        return(
            this.createAlias( associationPath, alias, JoinType.parse( joinType ) )
        );
    }

    @Deprecated
    public DetachedCriteriaX createAlias(String associationPath, String alias, int joinType, Criterion withClause)
    {
        return(
            this.createAlias( associationPath, alias, JoinType.parse( joinType ), withClause )
        );
    }

    /**
     * @deprecated alias feature unsupported
     */
    @Deprecated
    public DetachedCriteriaX createCriteria(String associationPath, String alias) {
        return(
            new DetachedCriteriaX(
                this.getImpl(),
                this.getCriteria().createCriteria(associationPath, alias)
            )
        );
    }

    /**
     * Creates an nested DetachedCriteria representing the association path.
     *
     * @param associationPath The association path
     *
     * @return the newly created, nested DetachedCriteria
     */
    public DetachedCriteriaX createCriteria(String associationPath) {
        return(
            new DetachedCriteriaX(
                this.getImpl(),
                this.getCriteria().createCriteria(associationPath)
            )
        );
    }

    /**
     * Creates an nested DetachedCriteria representing the association path, specifying the type of join to use.
     * Notice that DetachedCriteriaX reuses existing criteria, specified {@code joinType} may not actually applied.
     *
     * @param associationPath The association path
     * @param joinType The type of join to use
     *
     * @return the newly created, nested DetachedCriteria
     */
    public DetachedCriteriaX createCriteria(String associationPath, JoinType joinType) {
        return(
            new DetachedCriteriaX(
                this.getImpl(),
                this.getCriteria().createCriteria(associationPath, joinType)
            )
        );
    }

    /**
     * @deprecated alias feature unsupported
     */
    @Deprecated
    public DetachedCriteriaX createCriteria(String associationPath, String alias, JoinType joinType) {
        return(
            new DetachedCriteriaX(
                this.getImpl(),
                this.getCriteria().createCriteria(associationPath, alias, joinType)
            )
        );
    }

    /**
     * @deprecated alias feature unsupported
     */
    @Deprecated
    public DetachedCriteriaX createCriteria(String associationPath, String alias, JoinType joinType, Criterion withClause)  {
        return(
            new DetachedCriteriaX(
                this.getImpl(),
                this.getCriteria().createCriteria(associationPath, alias, joinType, withClause)
            )
        );
    }

    /**
     * @deprecated alias feature unsupported
     */
    @Deprecated
    public DetachedCriteriaX createCriteria(String associationPath, int joinType) {
        return createCriteria( associationPath, JoinType.parse( joinType ) );
    }

    /**
     * @deprecated alias feature unsupported
     */
    @Deprecated
    public DetachedCriteriaX createCriteria(String associationPath, String alias, int joinType) {
        return createCriteria( associationPath, alias, JoinType.parse( joinType ) );
    }

    /**
     * @deprecated alias feature unsupported
     */
    @Deprecated
    public DetachedCriteriaX createCriteria(String associationPath, String alias, int joinType, Criterion withClause) {
        return createCriteria( associationPath, alias, JoinType.parse( joinType ), withClause );
    }

    // INHERITED SAFE
    @Override
    public DetachedCriteriaX setComment(String comment)
    {
        super.setComment(comment);
        return(this);
    }

    @Override
    public DetachedCriteriaX setLockMode(LockMode lockMode)
    {
        super.setLockMode(lockMode);
        return(this);
    }

    @Override
    public DetachedCriteriaX setLockMode(String alias, LockMode lockMode)
    {
        super.setLockMode(alias, lockMode);
        return(this);
    }

    @Override
    public String toString()
    {
        return "DetachableCriteriaX(" + this.getCriteria().toString() + ')';
    }
}
