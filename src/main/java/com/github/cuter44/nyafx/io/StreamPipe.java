package com.github.cuter44.nyafx.io;

import java.io.*;

public class StreamPipe
{
    protected InputStream is;
    protected OutputStream os;
    protected int bufferSize;

    public StreamPipe(InputStream is, OutputStream os, int bufferSize)
    {
        this.is = is;
        this.os = os;
        this.bufferSize = bufferSize;

        return;
    }

    public StreamPipe(InputStream is, OutputStream os)
    {
        this(is, os, 4096);

        return;
    }

    public void start()
        throws IOException
    {
        byte[] buffer = new byte[this.bufferSize];
        int read = -1;

        while ((read=is.read(buffer, 0, this.bufferSize))!=-1)
            this.os.write(buffer, 0, read);

        this.is.close();
        this.os.close();

        return;
    }
}
