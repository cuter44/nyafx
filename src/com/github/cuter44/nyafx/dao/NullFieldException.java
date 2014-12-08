package com.github.cuter44.nyafx.dao;

/**
 */
public class NullFieldException
    extends RuntimeException
{
    public NullFieldException()
    {
        super();
    }

    public NullFieldException(String msg)
    {
        super(msg);
    }

    public NullFieldException(Throwable cause)
    {
        super(cause);
    }

    public NullFieldException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
