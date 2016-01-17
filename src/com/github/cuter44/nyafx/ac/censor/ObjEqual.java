package com.github.cuter44.nyafx.ac.censor;

import com.github.cuter44.nyafx.dao.*;
import com.github.cuter44.nyafx.ac.*;

import com.alibaba.fastjson.*;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.engine.spi.SessionImplementor;

public class ObjEqual extends CensorNyaDaoSupport
    implements AcCensor<Object, Object>
{
    public static final String KEY_OBJ_PROP = "objProp";
    public static final String KEY_REFERENCE = "reference";
    //public static final String KEY_OBJ_ID = "objId";
    //public static final String PARAM_OBJ_ID = ":"+KEY_OBJ_ID;
    public static final String KEY_SUB_ID = "subId";
    public static final String PARAM_SUB_ID = ":"+KEY_SUB_ID;

  // CENSOR
    @Override
    public Criteria acCriteria(Criteria c, Object sub, Class<? extends Object> objClass, Rule rule)
    {
        try
        {
            Session s = super.defaultDao.getThisSession();

            String p = super.needString(rule.config, KEY_OBJ_PROP);
            String hql = super.needString(rule.config, KEY_REFERENCE);

            Query q = this.defaultDao.createQuery(hql);
            if (hql.contains(PARAM_SUB_ID))
            {
                ClassMetadata cm = super.defaultDao.getClassMetadata(sub.getClass());
                Object id = cm.getIdentifier(sub, (SessionImplementor)s);
                q.setParameter(KEY_SUB_ID, id);
            }

            Object r = q.uniqueResult();

            if (r == null)
            {
                c.add(Restrictions.sqlRestriction("false"));
            }
            else
            {
                Criteria subc = c;
                String[] pa = p.split("\\.");
                for (int i=0; i<pa.length-1; i++)
                    subc = subc.createCriteria(pa[i]);

                subc.add(Restrictions.eq(pa[pa.length-1], r));
            }

            return(c);
        }
        catch (Exception ex)
        {
            throw(new RuntimeException("Fail applying criteria.", ex));
        }
    }

    @Override
    public DetachedCriteria acCriteria(DetachedCriteria c, Object sub, Class<? extends Object> obj, Rule rule)
    {
        try
        {
            Session s = super.defaultDao.getThisSession();

            String p = super.needString(rule.config, KEY_OBJ_PROP);
            String hql = super.needString(rule.config, KEY_REFERENCE);

            Query q = this.defaultDao.createQuery(hql);
            if (hql.contains(PARAM_SUB_ID))
            {
                ClassMetadata cm = super.defaultDao.getClassMetadata(sub.getClass());
                Object id = cm.getIdentifier(sub, (SessionImplementor)s);
                q.setParameter(KEY_SUB_ID, id);
            }

            Object r = q.uniqueResult();

            if (r == null)
            {
                c.add(Restrictions.sqlRestriction("false"));
            }
            else
            {
                DetachedCriteria subc = c;
                String[] pa = p.split("\\.");
                for (int i=0; i<pa.length-1; i++)
                    subc = subc.createCriteria(pa[i]);

                subc.add(Restrictions.eq(pa[pa.length-1], r));
            }

            return(c);
        }
        catch (Exception ex)
        {
            throw(new RuntimeException("Fail applying criteria.", ex));
        }
    }

    @Override
    public Judgement acAssert(Object sub, Object obj, Rule rule)
    {
        try
        {
            Session s = super.defaultDao.getThisSession();

            String p = super.needString(rule.config, KEY_OBJ_PROP);
            String hql = super.needString(rule.config, KEY_REFERENCE);

            DetachedCriteria c = DetachedCriteria.forClass(rule.objectClass);

            ClassMetadata cmObj = super.defaultDao.getClassMetadata(obj.getClass());
            Object idObj = cmObj.getIdentifier(obj, (SessionImplementor)s);
            c.add(Restrictions.idEq(idObj));

            Query q = this.defaultDao.createQuery(hql);
            if (hql.contains(PARAM_SUB_ID))
            {
                ClassMetadata cm = super.defaultDao.getClassMetadata(sub.getClass());
                Object id = cm.getIdentifier(sub, (SessionImplementor)s);
                q.setParameter(KEY_SUB_ID, id);
            }

            Object r = q.uniqueResult();

            if (r == null)
                return(Judgement.RESIST);

            // ELSE
            DetachedCriteria subc = c;
            String[] pa = p.split("\\.");
            for (int i=0; i<pa.length-1; i++)
                subc = subc.createCriteria(pa[i]);

            subc.add(Restrictions.eq(pa[pa.length-1], r));

            Object t = super.defaultDao.get(c);

            if (obj.equals(t))
                return(Judgement.ALLOW);

            // ELSE
            return(Judgement.RESIST);
        }
        catch (Exception ex)
        {
            throw(new RuntimeException("Fail applying criteria.", ex));
        }
    }

    @Override
    public void init(Rule rule)
    {
        // NOOP
        return;
    }

}
