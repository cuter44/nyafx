package com.github.cuter44.nyafx.dump;

import java.lang.StackTraceElement;

/**
 */
public class Dump
{
    public static void dump(byte[] bytes)
    {
        for (int i=0; i<bytes.length; i++)
            System.err.print(String.format("%02x", bytes[i] & 0xff));
        System.err.println();
        return;
    }

    public static void stack()
    {
        StackTraceElement stack[] = Thread.currentThread().getStackTrace();
        for (int i=2; i<stack.length; i++)
            System.err.println(stack[i]);
        return;
    }

}
