package com.github.cuter44.nyafx.dump;

import java.lang.StackTraceElement;

/** 用于打印调试信息的辅助类
 * 这个类目前已经不建议使用, 欲输出的信息会发送到stderr, 这会与其他信息混合在一起, 且可能因为程序挂掉而中止(stderr是无缓冲的). 为了避免发生这种状况, 应该使用 log 或者 assert
 */
public class Dump
{
    /** 将 bytes 以十六进制字符串的方式输出到 stderr
     */
    public static void dump(byte[] bytes)
    {
        for (int i=0; i<bytes.length; i++)
            System.err.print(String.format("%02x", bytes[i] & 0xff));
        System.err.println();
        return;
    }

    /** 将当前的堆栈输出到 stderr
     */
    public static void stack()
    {
        StackTraceElement stack[] = Thread.currentThread().getStackTrace();
        for (int i=2; i<stack.length; i++)
            System.err.println(stack[i]);
        return;
    }

}
