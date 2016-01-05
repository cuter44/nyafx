package com.github.cuter44.nyafx.hbac;

/** Judgement returned by AcCensor.acAssert()
 * DENY:
 */
public enum Judgement
{
    DENY(0),
    ALLOW(1),
    RESIST(2),
    ACQUIESCE(3);

    private int priority;

    private Judgement(int priority)
    {
        this.priority = priority;

        return;
    }

    public boolean priorTo(Judgement j)
    {
        return(
            this.priority < j.priority
        );
    }
}
