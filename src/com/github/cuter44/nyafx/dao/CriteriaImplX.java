package com.github.cuter44.nyafx.dao;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.hibernate.*;
import org.hibernate.internal.*;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.CriteriaImpl.Subcriteria;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.sql.JoinType;
import org.hibernate.criterion.*;
import org.hibernate.transform.ResultTransformer;

public class CriteriaImplX extends CriteriaImpl
    implements Criteria
{
    Map<String, SubcriteriaWrap> paths = new HashMap<String, SubcriteriaWrap>();

    public CriteriaImplX(String entityOrClassName, SessionImplementor session)
    {
        super(entityOrClassName, session);

        return;
    }

    public CriteriaImplX(String entityOrClassName, String alias, SessionImplementor session)
    {
        super(entityOrClassName, alias, session);

        return;
    }

    //@Override
    //public String toString()
    //{
        //return(
            //"CriteriaImplX(" +
                //super.getEntityOrClassName() + ":" +
                //(getAlias()==null ? "" : super.getAlias()) +
                //subcriteriaList.toString() +
                //criterionEntries.toString() +
                //( projection==null ? "" : projection.toString() ) +
            //')'
    //}

    /**
     * WARNING: This API is not duplicate-association-path proofed
     */
    @Deprecated
	public Criteria createAlias(String associationPath, String alias) {
		return(
            this.createAlias(associationPath, alias, JoinType.INNER_JOIN)
        );
	}

    /**
     * WARNING: This API is not duplicate-association-path proofed
     */
	public Criteria createAlias(String associationPath, String alias, JoinType joinType)
    {
        SubcriteriaWrap c = new SubcriteriaWrap(
            super.createCriteria(associationPath, alias, joinType)
        );

        if (this.paths.get(c.getFullPath())==null)
            this.paths.put(c.getFullPath(), c);

		return(this);
	}

    /**
     * WARNING: This API is not duplicate-association-path proofed
     */
    @Deprecated
	public Criteria createAlias(String associationPath, String alias, int joinType) throws HibernateException {
		return(
            createAlias( associationPath, alias, JoinType.parse(joinType))
        );
	}

    /**
     * WARNING: This API is not duplicate-association-path proofed
     */
    @Deprecated
	public Criteria createAlias(String associationPath, String alias, JoinType joinType, Criterion withClause) {
        SubcriteriaWrap c = new SubcriteriaWrap(
            super.createCriteria(associationPath, alias, joinType, withClause)
        );

        if (this.paths.get(c.getFullPath())==null)
            this.paths.put(c.getFullPath(), c);

		return(this);
	}

    @Deprecated
	public Criteria createAlias(String associationPath, String alias, int joinType, Criterion withClause)
			throws HibernateException
    {
		return(
            this.createAlias( associationPath, alias, JoinType.parse( joinType ), withClause )
        );
	}

	@Override
	public Criteria createCriteria(String associationPath) {
		return(
            this.createCriteria( associationPath, JoinType.INNER_JOIN )
        );
	}

	@Override
	public Criteria createCriteria(String associationPath, JoinType joinType)
    {
        SubcriteriaWrap c = this.paths.get(associationPath);

        if (c == null)
        {
            c = new SubcriteriaWrap(
                super.createCriteria(associationPath, joinType)
            );

            this.paths.put(c.getFullPath(), c);
        }

		return(c);
	}

    @Deprecated
	public Criteria createCriteria(String associationPath, int joinType) throws HibernateException {
		return(
            this.createCriteria(associationPath, JoinType.parse( joinType ))
        );
	}

    /**
     * WARNING: This API is not duplicate-association-path proofed
     */
	public Criteria createCriteria(String associationPath, String alias) {
		return(
            this.createCriteria( associationPath, alias, JoinType.INNER_JOIN )
        );
	}

    /**
     * WARNING: This API is not duplicate-association-path proofed
     */
	public Criteria createCriteria(String associationPath, String alias, JoinType joinType)
    {
        SubcriteriaWrap c = new SubcriteriaWrap(
            super.createCriteria(associationPath, alias, joinType)
        );

        if (this.paths.get(c.getFullPath())==null)
            this.paths.put(c.getFullPath(), c);

		return(c);
	}

    @Deprecated
	public Criteria createCriteria(String associationPath, String alias, int joinType) throws HibernateException {
		return(
            this.createCriteria( associationPath, alias, JoinType.parse( joinType ) )
        );
	}

    /**
     * WARNING: This API is not duplicate-association-path proofed
     */
    @Deprecated
	public Criteria createCriteria(String associationPath, String alias, JoinType joinType, Criterion withClause)
    {
        SubcriteriaWrap c = new SubcriteriaWrap(
            super.createCriteria(associationPath, alias, joinType, withClause)
        );

        if (this.paths.get(c.getFullPath())==null)
            this.paths.put(c.getFullPath(), c);

		return(c);
	}

    @Deprecated
	public Criteria createCriteria(String associationPath, String alias, int joinType, Criterion withClause)
			throws HibernateException
    {
		return(
            this.createCriteria( associationPath, alias, JoinType.parse( joinType ), withClause )
        );
	}

    public class SubcriteriaWrap implements Criteria
    {
        protected Subcriteria criteria;

        public SubcriteriaWrap(Criteria criteria)
        {
            this.criteria = (Subcriteria)criteria;
        }

        public String getFullPath()
        {
            StringBuilder sb = new StringBuilder(this.criteria.getPath());
            Criteria c = this.criteria.getParent();

            while ((c!=null) && (c instanceof Subcriteria))
            {
                sb  .insert(0, '.')
                    .insert(0, ((Subcriteria)c).getPath());

                c = ((Subcriteria)c).getParent();
            }

            return(sb.toString());
        }

      // CRITERIA
        @Override
        public String getAlias()
        {
            return(this.criteria.getAlias());
        }

        @Override
        public Criteria setProjection(Projection projection)
        {
            return(this.criteria.setProjection(projection));
        }

        @Override
        public Criteria add(Criterion criterion)
        {
            return(this.criteria.add(criterion));
        }

        @Override
        public Criteria addOrder(Order order)
        {
            return(this.criteria.addOrder(order));
        }

        @Override
        public Criteria setFetchMode(String associationPath, FetchMode mode) throws HibernateException
        {
            return(this.criteria.setFetchMode(associationPath, mode));
        }

        @Override
        public Criteria setLockMode(LockMode lockMode)
        {
            return(this.criteria.setLockMode(lockMode));
        }

        @Override
        public Criteria setLockMode(String alias, LockMode lockMode)
        {
            return(this.criteria.setLockMode(alias, lockMode));
        }

        @Override
        public Criteria createAlias(String associationPath, String alias) throws HibernateException
        {
            return(this.criteria.createAlias(associationPath, alias));
        }

        /**
         * WARNING: This API is not duplicate-association-path proofed
         */
        @Override
        public Criteria createAlias(String associationPath, String alias, JoinType joinType) throws HibernateException
        {
            SubcriteriaWrap c = new SubcriteriaWrap(
                this.criteria.createCriteria(associationPath, alias, joinType)
            );

            if (CriteriaImplX.this.paths.get(c.getFullPath())==null)
                CriteriaImplX.this.paths.put(c.getFullPath(), c);

            return(this);

        }

        @Deprecated
        @Override
        public Criteria createAlias(String associationPath, String alias, int joinType) throws HibernateException
        {
            return(this.criteria.createAlias(associationPath, alias, joinType));
        }

        @Override
        public Criteria createAlias(String associationPath, String alias, JoinType joinType, Criterion withClause) throws HibernateException
        {
            SubcriteriaWrap c = new SubcriteriaWrap(
                this.criteria.createCriteria(associationPath, alias, joinType, withClause)
            );

            if (CriteriaImplX.this.paths.get(c.getFullPath())==null)
                CriteriaImplX.this.paths.put(c.getFullPath(), c);

            return(this);
        }

        @Deprecated
        @Override
        public Criteria createAlias(String associationPath, String alias, int joinType, Criterion withClause) throws HibernateException
        {
            return(this.criteria.createAlias(associationPath, alias, joinType, withClause));
        }

        @Override
        public Criteria createCriteria(String associationPath) throws HibernateException
        {
            return(this.criteria.createCriteria(associationPath));
        }

        @Override
        public Criteria createCriteria(String associationPath, JoinType joinType) throws HibernateException
        {
            SubcriteriaWrap c = CriteriaImplX.this.paths.get(associationPath);

            if (c == null)
            {
                c = new SubcriteriaWrap(
                    this.criteria.createCriteria(associationPath, joinType)
                );

                CriteriaImplX.this.paths.put(c.getFullPath(), c);
            }

            return(c);
        }

        @Deprecated
        @Override
        public Criteria createCriteria(String associationPath, int joinType) throws HibernateException
        {
            return(this.criteria.createCriteria(associationPath, joinType));
        }

        @Override
        public Criteria createCriteria(String associationPath, String alias) throws HibernateException
        {
            return(this.criteria.createCriteria(associationPath, alias));
        }

        @Override
        public Criteria createCriteria(String associationPath, String alias, JoinType joinType) throws HibernateException
        {
            SubcriteriaWrap c = new SubcriteriaWrap(
                this.criteria.createCriteria(associationPath, alias, joinType)
            );

            if (CriteriaImplX.this.paths.get(c.getFullPath())==null)
                CriteriaImplX.this.paths.put(c.getFullPath(), c);

            return(c);
        }

        @Deprecated
        @Override
        public Criteria createCriteria(String associationPath, String alias, int joinType) throws HibernateException
        {
            return(this.criteria.createCriteria(associationPath, alias, joinType));
        }

        @Override
        public Criteria createCriteria(String associationPath, String alias, JoinType joinType, Criterion withClause) throws HibernateException
        {
            SubcriteriaWrap c = new SubcriteriaWrap(
                this.criteria.createCriteria(associationPath, alias, joinType, withClause)
            );

            if (CriteriaImplX.this.paths.get(c.getFullPath())==null)
                CriteriaImplX.this.paths.put(c.getFullPath(), c);

            return(c);
        }

        @Deprecated
        @Override
        public Criteria createCriteria(String associationPath, String alias, int joinType, Criterion withClause) throws HibernateException
        {
            return(this.criteria.createCriteria(associationPath, alias, joinType, withClause));
        }

        @Override
        public Criteria setResultTransformer(ResultTransformer resultTransformer)
        {
            return(this.criteria.setResultTransformer(resultTransformer));
        }

        @Override
        public Criteria setMaxResults(int maxResults)
        {
            return(this.criteria.setMaxResults(maxResults));
        }

        @Override
        public Criteria setFirstResult(int firstResult)
        {
            return(this.criteria.setFirstResult(firstResult));
        }

        @Override
        public boolean isReadOnlyInitialized()
        {
            return(this.criteria.isReadOnlyInitialized());
        }

        @Override
        public boolean isReadOnly()
        {
            return(this.criteria.isReadOnly());
        }

        @Override
        public Criteria setReadOnly(boolean readOnly)
        {
            return(this.criteria.setReadOnly(readOnly));
        }

        @Override
        public Criteria setFetchSize(int fetchSize)
        {
            return(this.criteria.setFetchSize(fetchSize));
        }

        @Override
        public Criteria setTimeout(int timeout)
        {
            return(this.criteria.setTimeout(timeout));
        }

        @Override
        public Criteria setCacheable(boolean cacheable)
        {
            return(this.criteria.setCacheable(cacheable));
        }

        @Override
        public Criteria setCacheRegion(String cacheRegion)
        {
            return(this.criteria.setCacheRegion(cacheRegion));
        }

        @Override
        public Criteria setComment(String comment)
        {
            return(this.criteria.setComment(comment));
        }

        @Override
        public Criteria addQueryHint(String hint)
        {
            return(this.criteria.addQueryHint(hint));
        }

        @Override
        public Criteria setFlushMode(FlushMode flushMode)
        {
            return(this.criteria.setFlushMode(flushMode));
        }

        @Override
        public Criteria setCacheMode(CacheMode cacheMode)
        {
            return(this.criteria.setCacheMode(cacheMode));
        }

        @Override
        public List list() throws HibernateException
        {
            return(this.criteria.list());
        }

        @Override
        public ScrollableResults scroll() throws HibernateException
        {
            return(this.criteria.scroll());
        }

        @Override
        public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException
        {
            return(this.criteria.scroll(scrollMode));
        }

        @Override
        public Object uniqueResult() throws HibernateException
        {
            return(this.criteria.uniqueResult());
        }
    }
}