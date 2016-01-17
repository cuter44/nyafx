package com.github.cuter44.nyafx.ac.censor;

import com.github.cuter44.nyafx.dao.*;

import com.alibaba.fastjson.*;


public class CensorNyaDaoSupport
{
  // DAO
    protected static class DefaultDao extends DaoBase<Object>
    {
        public DefaultDao()
        {
            super();

            return;
        }

        @Override
        protected Class classOfT()
        {
            return(Object.class);
        }
    }

    protected static DefaultDao defaultDao = new DefaultDao();

  // MISC
    /** Exception on null
     */
    public static String needString(JSONObject json, String key)
    {
        String v = json.getString(key);

        if (v==null)
            throw(new NullPointerException("[hbac]Missing field in config:"+key));

        return(v);
    }
}
