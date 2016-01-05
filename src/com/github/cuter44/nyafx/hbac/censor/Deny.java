package com.github.cuter44.nyafx.hbac.censor;

import java.util.List;

import com.github.cuter44.nyafx.dao.*;
import com.github.cuter44.nyafx.hbac.*;

import com.alibaba.fastjson.*;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.engine.spi.SessionImplementor;

public class Deny extends DefaultCensorSupport
    implements AcCensor<Object, Object>
{

  // CENSOR
    /** NOOP
     */
    @Override
    public Criteria acCriteria(Criteria c, Object sub, Class<? extends Object> objClass, Rule rule)
    {
        c.add(Restrictions.sqlRestriction("false"));

        return(c);
    }

    /** NOOP
     */
    @Override
    public DetachedCriteria acCriteria(DetachedCriteria c, Object sub, Class<? extends Object> obj, Rule rule)
    {
        c.add(Restrictions.sqlRestriction("false"));

        return(c);
    }

    @Override
    public Judgement acAssert(Object sub, Object obj, Rule rule)
    {
        return(Judgement.DENY);
    }

    @Override
    public void init(Rule rule)
    {
        // NOOP
        return;
    }

}
