package com.github.cuter44.nyafx.text;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;

public class URLParser
{
    protected byte type;
    protected static final byte TYPE_URL = 1;
    protected static final byte TYPE_QUERY_STRING = 2;

    protected String url;
    protected String baseUrl;
    protected String queryString;
    protected String label;
    protected String charset = "utf-8";

    protected boolean compiled = false;
    public Map<String, String> parsedParams;

    protected URLDecoder urld = new URLDecoder();

    protected URLParser()
    {
        return;
    }

    public static URLParser fromURL(String url)
    {
        URLParser parser = new URLParser();

        parser.type = TYPE_URL;
        parser.url = url;

        String[] split = url.split("\\?", 2);
        parser.baseUrl = split[0];
        parser.queryString = split.length>1?split[1]:"";

        String[] split2 = url.split("#", 2);
        parser.label = split2.length>1?split2[1]:null;

        return(parser);
    }

    public static URLParser fromQueryString(String queryString)
    {
        URLParser parser = new URLParser();

        parser.type = TYPE_QUERY_STRING;
        parser.queryString = queryString;

        return(parser);
    }

    public URLParser useCharset(String charset)
    {
        this.charset = charset;
        return(this);
    }

    /**
     * Parse and cache results, improve performance while re-used.
     */
    public URLParser compile()
        throws UnsupportedEncodingException
    {
        if (this.compiled)
            return(this);

        String paramString = this.queryString.split("#")[0];
        String[] params = paramString.split("&");

        this.parsedParams = new HashMap<String, String>(params.length);
        for (String p:params)
        {
            String[] kv = p.split("=");
            if (kv.length==2)
                this.parsedParams.put(kv[0], this.urld.decode(kv[1], this.charset));
        }

        this.compiled = true;

        return(this);
    }

    public String getParameter(String name)
    {
        if (this.compiled)
            return(this.parsedParams.get(name));

        String paramString = this.queryString.split("#")[0];
        Matcher match = Pattern.compile("(^|&)"+name+"=([^&]*)").matcher(paramString);
        match.lookingAt();

        return(match.group(2));
    }

    public URLParser setParameter(String name, String value)
        throws UnsupportedEncodingException
    {
        if (!this.compiled)
            this.compile();

        this.parsedParams.put(name, value);

        return(this);
    }

    /**
     * Rebuilt the URL using the stored info, including the modification.
     */
    public String toURL()
        throws UnsupportedEncodingException
    {
        if (!this.compiled)
            this.compile();

        URLBuilder builder = new URLBuilder();

        if (this.type == TYPE_URL)
            builder.appendPath(this.baseUrl);

        for (String k:this.parsedParams.keySet())
            builder.appendParamEncode(k, this.parsedParams.get(k), this.charset);

        if (this.label != null)
            builder.appendLabel(this.label);

        return(builder.toString());
    }

    public static void main(String[] args)
    {
        try
        {
            System.out.println(
                URLParser.fromURL(
                    "https://www.google.com/search?q=test&hl=zh_cn&oq=test&gs_l=heirloom-serp.3...38011332.38012012.0.38012235.4.4.0.0.0.0.0.0..0.0.msedr...0...1ac.1.34.heirloom-serp..4.0.0.1q6YK2r8vHI"
                ).getParameter("q")
            );
            System.out.println(
                URLParser.fromURL(
                    "https://www.google.com/search?q=test&hl=zh_cn&oq=test&gs_l=heirloom-serp.3...38011332.38012012.0.38012235.4.4.0.0.0.0.0.0..0.0.msedr...0...1ac.1.34.heirloom-serp..4.0.0.1q6YK2r8vHI"
                ).compile()
                .getParameter("q")
            );
            System.out.println(
                URLParser.fromURL(
                    "https://www.google.com/search?q=test&hl=zh_cn&oq=test&gs_l=heirloom-serp.3...38011332.38012012.0.38012235.4.4.0.0.0.0.0.0..0.0.msedr...0...1ac.1.34.heirloom-serp..4.0.0.1q6YK2r8vHI#test-label"
                ).compile()
                .setParameter("q", "tweaked")
                .toURL()
            );
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
