package com.github.cuter44.nyafx.ac;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

import org.hibernate.Hibernate;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;

/** Topmost rule enforcer
 * This class implements rules pick-up and application.
 * Do not regieter it to <code>RuleMgr</code>, or it is causing infinite recursion.
 */
public class Enforcer
{
    protected RuleMgr ruleMgr;

    public Enforcer(RuleMgr ruleMgr)
    {
        this.ruleMgr = ruleMgr;
    }

  // DEFAULT INSTANCE
    private static class Singleton
    {
        public static Enforcer instance = new Enforcer(RuleMgr.getDefaultInstance());
    }

    public static Enforcer getDefaultInstance()
    {
        return(Singleton.instance);
    }

  // SELECTOR
    public Set<MaterializedRule> selectRule(Object sub, Class objClass, String action)
    {
        return(
            this.ruleMgr.selectRuleDualFullJoin(
                Hibernate.getClass(sub),
                objClass,
                action
            )
        );
    }

  // EXPOSED
    /** Apply additional criterion to Criteria
     * @return modified Criteria <code>c</code>
     */
    public Criteria acCriteria(Criteria c, Object sub, Class objClass, String action)
    {
        Collection<MaterializedRule> rules = this.selectRule(sub, objClass, action);

        if (rules.size() == 0)
            return(c);

        // else
        for (MaterializedRule r:rules)
            c = r.censor.acCriteria(c, sub, objClass, r);

        return(c);
    }

    /** Apply additional criterion to DetachedCriteria
     * @return modified DetachedCriteria <code>c</code>
     */
    public DetachedCriteria acCriteria(DetachedCriteria c, Object sub, Class objClass, String action)
    {
        Collection<MaterializedRule> rules = this.selectRule(sub, objClass, action);

        if (rules.size() == 0)
            return(c);

        // else
        for (MaterializedRule r:rules)
            c = r.censor.acCriteria(c, sub, objClass, r);

        return(c);
    }

    /** Judge if Rule is satisified.
     * @return the suggestion, see @link Judgement to see how it works
     */
    public Judgement acAssert(Object sub, Object obj, String action)
    {
        Collection<MaterializedRule> rules = this.selectRule(sub, Hibernate.getClass(obj), action);

        Judgement j = Judgement.ACQUIESCE;

        if (rules.size() == 0)
            return(j);

        // else
        for (MaterializedRule r:rules)
        {
            Judgement jr = r.censor.acAssert(sub, obj, r);
            if (jr.priorTo(j))
                j = jr;
        }

        return(j);
    }
}
