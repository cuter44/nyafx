package com.github.cuter44.nyafx.servlet;

import javax.servlet.ServletException;

/** 表示参数缺失的 exception
 * @version 1.0.0 builld 20131212
 */
public class MissingParameterException
    extends RuntimeException
{
    public MissingParameterException()
    {
        super();
    }

    public MissingParameterException(String paramName)
    {
        super("Missing parameter:" + paramName);
    }
}
