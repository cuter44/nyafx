package com.github.cuter44.nyafx.hbac;

import java.io.Serializable;

import com.alibaba.fastjson.*;

/** Internal present of rules
 */
public class Rule
    implements Serializable
{
    public Class subjectClass;
    public Class objectClass;
    public String action;
    public JSONObject config;
}
