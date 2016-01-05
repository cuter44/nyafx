package com.github.cuter44.nyafx.hbac.censor;

import com.github.cuter44.nyafx.dao.*;

import com.alibaba.fastjson.*;


public class DefaultCensorSupport
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
    protected String getString(JSONObject json, String key)
    {
        String v = json.getString(key);

        if (v==null)
            throw(new NullPointerException("Nyafx-hbac: Missing field in config:"+key));

        return(v);
    }
}
