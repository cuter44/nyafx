package com.github.cuter44.nyafx.dao;

import org.hibernate.*;
import org.hibernate.metadata.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * @require hibernate-4.2+
 */
public class HibernateSessionFactoryWrap
{
  // CONSTRUCT
    public static final String DEFAULTS = "/hibernate.cfg.xml";

    protected SessionFactory sf;

    public HibernateSessionFactoryWrap(String resConf)
    {
        Configuration cfg = new Configuration()
            .configure(resConf);
        ServiceRegistry sr = new ServiceRegistryBuilder()
            .applySettings(
                cfg.getProperties()
            ).buildServiceRegistry();
        this.sf = cfg.buildSessionFactory(sr);

        return;
    }

    @Override
    protected void finalize()
    {
        if (this.sf!=null)
            this.sf.close();

        return;
    }

  // SINGLETON
    private static class Singleton
    {
        public static HibernateSessionFactoryWrap instance =
            new HibernateSessionFactoryWrap(
                HibernateSessionFactoryWrap.DEFAULTS
            );
    }

    public static HibernateSessionFactoryWrap getInstance()
    {
        return(Singleton.instance);
    }

  // SESSION
    public Session openSession()
    {
        return(
            this.sf.openSession()
        );
    }

    protected ThreadLocal<Session> threadLocal = new ThreadLocal<Session>()
    {
        @Override
        protected Session initialValue() { return(HibernateSessionFactoryWrap.this.sf.openSession()); }
    };

    /** returns ThreadLocal session
     */
    public ThreadLocal<Session> getThreadLocal()
    {
        return(
            this.threadLocal
        );
    }

    public SessionFactory getSessionFactory()
    {
        return(
            this.sf
        );
    }

    public ClassMetadata getClassMetadata(Class clazz)
    {
        return(
            this.sf.getClassMetadata(clazz)
        );
    }
}
