package com.github.cuter44.nyafx.dao;

/**
 */
public class EntityNotFoundException
    extends RuntimeException
{
    public EntityNotFoundException()
    {
        super();
    }

    public EntityNotFoundException(String msg)
    {
        super(msg);
    }

    public EntityNotFoundException(Throwable cause)
    {
        super(cause);
    }

    public EntityNotFoundException(String msg, Throwable cause)
    {
        super(msg, cause);
    }

    public static Object entFound(Object o)
    {
        if (o != null)
            return(o);

        // else
        StackTraceElement e = Thread.currentThread().getStackTrace()[2];
        throw(
            new EntityNotFoundException(
                "Entity not found at "+e.getClassName()+'.'+e.getMethodName()+':'+e.getLineNumber()
            )
        );
    }
}
