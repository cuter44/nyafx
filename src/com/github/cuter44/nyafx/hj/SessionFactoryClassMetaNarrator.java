package com.github.cuter44.nyafx.hj;

import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;

public class SessionFactoryClassMetaNarrator implements ClassMetaNarrator
{
    protected SessionFactory sf;

    public SessionFactoryClassMetaNarrator(SessionFactory sf)
    {
        this.sf = sf;

        return;
    }

    @Override
    public ClassMetadata getClassMetadata(Class c)
    {
        return(
            this.sf.getClassMetadata(c)
        );
    }
}
