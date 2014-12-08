package com.github.cuter44.nyafx.dao;

/**
 */
public class EntityReferencedException
    extends IllegalStateException
{
    public EntityReferencedException()
    {
        super();
    }

    public EntityReferencedException(String msg)
    {
        super(msg);
    }

    public EntityReferencedException(Throwable cause)
    {
        super(cause);
    }

    public EntityReferencedException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
