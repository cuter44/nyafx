package com.github.cuter44.nyafx.ac;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

import com.alibaba.fastjson.*;

public class RuleMgr
{
  // CONSTRUCT
    public static final String DEFAULTS = "/nyafx-hbac.json";

  // CONSTRUCT
    protected Map<String, Set<MaterializedRule>> idxAction;
    protected Map<Class, Set<MaterializedRule>> idxSubject;
    protected Map<Class, Set<MaterializedRule>> idxObject;

    protected Map<String, AcCensor> censorPool;

    public RuleMgr()
    {
        this.idxAction = new HashMap<String, Set<MaterializedRule>>();
        this.idxSubject = new HashMap<Class, Set<MaterializedRule>>();
        this.idxObject = new HashMap<Class, Set<MaterializedRule>>();

        this.censorPool = new HashMap<String, AcCensor>();

        return;
    }

    public static RuleMgr forJSONResource(String resPath)
    {
        RuleMgr rm = new RuleMgr();
        rm.parseRuleJSONResource(resPath);

        return(rm);
    }

  // DEFAULT INSTANCE
    private static class Singleton
    {
        public static RuleMgr instance = RuleMgr.forJSONResource(RuleMgr.DEFAULTS);
    }

    public static RuleMgr getDefaultInstance()
    {
        return(Singleton.instance);
    }

  // PARSE
    public void parseRuleJSONResource(String resPath)
    {
        new RuleParserJSON(resPath).startParse();

        return;
    }

    protected class RuleParserJSON
    {
        public static final String KEY_SUB = "sub";
        public static final String KEY_OBJ = "obj";
        public static final String KEY_ACTION = "action";
        public static final String KEY_CENSOR = "censor";
        public static final String KEY_CONFIG = "config";

        protected String resourcePath;

        public RuleParserJSON(String resourcePath)
        {
            this.resourcePath = resourcePath;

            return;
        }

        public void startParse()
        {
            JSONArray ja = readResourceJSON();

            for (int i=0; i<ja.size(); i++)
                parseRule(ja.getJSONObject(i));

        }

        protected JSONArray readResourceJSON()
        {
            try
            {
                final int BL = 4096;
                byte[] buffer = new byte[BL];

                InputStream is = this.getClass().getResourceAsStream(this.resourcePath);
                ByteArrayOutputStream os = new ByteArrayOutputStream(BL);

                while (true)
                {
                    int bi = is.read(buffer);
                    if (bi <= 0)
                        break;
                    os.write(buffer, 0, bi);
                }

                JSONArray ja = JSON.parseArray(os.toString("utf-8"));

                return(ja);
            }
            catch (IOException ex)
            {
                throw(new RuntimeException("Nyafx-hbac-RuleMgr:Parsed hbac-config failed.", ex));
            }
        }

        protected void parseRule(JSONObject j)
        {
            RuleMgr rm = RuleMgr.this;
            MaterializedRule r;

            try
            {
                r = new MaterializedRule();

                r.subjectClass = Class.forName(j.getString(KEY_SUB));
                r.objectClass = Class.forName(j.getString(KEY_OBJ));
                r.action = j.getString(KEY_ACTION);
                r.config = j.getJSONObject(KEY_CONFIG);
                r.censor = (AcCensor)(Class.forName(j.getString(KEY_CENSOR)).newInstance());

                r.censor.init(r);
            }
            catch (Exception ex)
            {
                throw(new RuntimeException("Nyafx-hbac-RuleMgr:Parsed rule failed:\n"+j.toString(), ex));
            }

            Set<MaterializedRule> l;

            l = rm.idxAction.get(r.action);
            if (l == null)
            {
                l = new HashSet<MaterializedRule>();
                rm.idxAction.put(r.action, l);
            }
            l.add(r);

            l = rm.idxSubject.get(r.subjectClass);
            if (l == null)
            {
                l = new HashSet<MaterializedRule>();
                rm.idxSubject.put(r.subjectClass, l);
            }
            l.add(r);

            l = rm.idxObject.get(r.objectClass);
            if (l == null)
            {
                l = new HashSet<MaterializedRule>();
                rm.idxObject.put(r.objectClass, l);
            }
            l.add(r);
        }
    }

  // SELECTOR
    public Set<MaterializedRule> selectRuleDualFullJoin(Class subClass, Class objClass, String action)
    {
        Set<MaterializedRule> r = new HashSet<MaterializedRule>();

        // OBJ
        Set<MaterializedRule> rO = new HashSet<MaterializedRule>();
        while (objClass != null)
        {
            Set<MaterializedRule> s = this.idxObject.get(objClass);
            if (s != null)
                rO.addAll(s);

            objClass = objClass.getSuperclass();
        }

        r.addAll(rO);

        // SUB
        Set<MaterializedRule> rS = new HashSet<MaterializedRule>();
        while (subClass != null)
        {
            Set<MaterializedRule> s = this.idxSubject.get(subClass);
            if (s != null)
                rS.addAll(s);

            subClass = subClass.getSuperclass();
        }

        r.retainAll(rS);

        if (r.isEmpty())
            return(r);

        // ACTION
        Set<MaterializedRule> rA = this.idxAction.get(action);

        r.retainAll(rA);

        return(r);
    }

  // MISC
    public static void main(String[] args)
    {
        RuleMgr rm = RuleMgr.getDefaultInstance();
    }
}
