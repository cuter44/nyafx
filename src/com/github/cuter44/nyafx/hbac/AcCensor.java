package com.github.cuter44.nyafx.hbac;

import org.hibernate.criterion.*;
import org.hibernate.*;

/** Access controll censor applied when called.
 * Then they are sequence
 */
public interface AcCensor<SUBJECT, OBJECT>
{
    /** Apply additional criterion to Criteria
     * @return modified Criteria <code>c</code>
     */
    public abstract Criteria acCriteria(Criteria c, SUBJECT sub, Class<? extends OBJECT> objClass, Rule rule);

    /** Apply additional criterion to DetachedCriteria
     * @return modified DetachedCriteria <code>c</code>
     */
    public abstract DetachedCriteria acCriteria(DetachedCriteria c, SUBJECT sub, Class<? extends OBJECT> objClass, Rule rule);

    /** Judge if Rule is satisified.
     * @return the suggestion, see @link Judgement to see how it works
     */
    public abstract Judgement acAssert(SUBJECT sub, OBJECT obj, Rule rule);

    /** Invoked after instantiated via reflection
     */
    public abstract void init(Rule rule);
}
