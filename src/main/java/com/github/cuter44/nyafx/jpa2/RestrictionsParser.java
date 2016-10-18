package com.github.cuter44.nyafx.jpa2;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.regex.Pattern;
import java.lang.reflect.Type;
import javax.persistence.*;
import javax.persistence.criteria.*;

import com.alibaba.fastjson.*;
import com.alibaba.fastjson.util.*;

import com.github.cuter44.nyafx.servlet.ParserBundle;
import com.github.cuter44.nyafx.servlet.ValueParser;

public class RestrictionsParser
{
    public static final String OPERATOR_EQ  = "eq";
    public static final String OPERATOR_LT  = "lt";
    public static final String OPERATOR_LE  = "le";
    public static final String OPERATOR_GT  = "gt";
    public static final String OPERATOR_GE  = "ge";
    public static final String OPERATOR_IN  = "in";

    public static final Pattern PATTERN_X   = Pattern.compile("\\bx\\b");

    public JSONObject BLANK_HINT = JSON.parseObject("{}");

    public String DEFAULT_HINT_VALUE = "eq lt le gt ge in";

    public boolean constraintApplied = true;

    public boolean failException = false;

  // HINT
    protected String extractHint(Object o)
    {
        if (o == null)
            return(this.DEFAULT_HINT_VALUE);

        if (o instanceof String)
            return((String)o);

        if (o instanceof JSONObject)
            return(extractHint(((JSONObject)o).get(".")));

        return(this.DEFAULT_HINT_VALUE);
    }

    protected JSONObject wrapHint(Object o)
    {
        if (o == null)
            return(BLANK_HINT);

        if (o instanceof JSONObject)
            return((JSONObject)o);

        if (o instanceof String)
            return(JSON.parseObject("{'.':'"+(String)o+"'}"));

        return(BLANK_HINT);
    }

  // FIELD INFO
    protected Map<Class, Map<String, FieldInfo>> knownClasses = new Hashtable<Class, Map<String, FieldInfo>>();

    protected Map<String, FieldInfo> getFieldInfos(Class clazz)
    {
        Map<String, FieldInfo> mfi = this.knownClasses.get(clazz);
        if (mfi != null)
            return(mfi);

        // ELSE
        List<FieldInfo> lfi = TypeUtils.computeGetters(clazz, null, null, false);
        mfi = new HashMap<String, FieldInfo>();
        for (FieldInfo fi:lfi)
            mfi.put(fi.name, fi);

        this.knownClasses.put(clazz, mfi);

        return(mfi);
    }

  // VALUE PARSER
    protected ParserBundle valueParsers;

    public RestrictionsParser setValuePatsers(ParserBundle parsers)
    {
        this.valueParsers = parsers;

        return(this);
    }

    public RestrictionsParser addValueParser(Type type, ValueParser parser)
    {
        this.valueParsers.primaryParsers.put(type, parser);

        return(this);
    }

  // CRITERION PARSER
    public static interface CriterionParser
    {
        public abstract Predicate parse(CriteriaQueryContext c, Path path, Class pathClass, JSONArray criterion, String hint)
            throws IllegalArgumentException;
    }

    protected Map<String, CriterionParser> criterionParsers = new HashMap<String, CriterionParser>();

  // BUILT-IN CRITERION PARSER
    protected class EqParser implements CriterionParser
    {
        public final Pattern PATTERN_EQ  = Pattern.compile("\\beq\\b");

        public Predicate parse(CriteriaQueryContext c, Path path, Class pathClass, JSONArray criterion, String hint)
            throws IllegalArgumentException
        {
            if (RestrictionsParser.this.constraintApplied)
            {
                if (!PATTERN_EQ.matcher(hint).find())
                    throw(new IllegalArgumentException("'eq' required but not found @"+path.toString()));
            }

            return(c.b.equal(path, RestrictionsParser.this.valueParsers.parse(criterion.get(1), pathClass)));
        }
    }

    protected class LtParser implements CriterionParser
    {
        public final Pattern PATTERN_LT  = Pattern.compile("\\blt\\b");

        public Predicate parse(CriteriaQueryContext c, Path path, Class pathClass, JSONArray criterion, String hint)
            throws IllegalArgumentException
        {
            if (RestrictionsParser.this.constraintApplied)
            {
                if (!PATTERN_LT.matcher(hint).find())
                    throw(new IllegalArgumentException("'lt' required but not found @"+path.toString()));
            }

            return(c.b.lessThan(path, (Comparable)RestrictionsParser.this.valueParsers.parse(criterion.get(1), pathClass)));
        }
    }

    protected class GtParser implements CriterionParser
    {
        public final Pattern PATTERN_GT  = Pattern.compile("\\bgt\\b");

        public Predicate parse(CriteriaQueryContext c, Path path, Class pathClass, JSONArray criterion, String hint)
            throws IllegalArgumentException
        {
            if (RestrictionsParser.this.constraintApplied)
            {
                if (!PATTERN_GT.matcher(hint).find())
                    throw(new IllegalArgumentException("'gt' required but not found @"+path.toString()));
            }

            return(c.b.greaterThan(path, (Comparable)RestrictionsParser.this.valueParsers.parse(criterion.get(1), pathClass)));
        }
    }

    protected class LeParser implements CriterionParser
    {
        public final Pattern PATTERN_LE  = Pattern.compile("\\ble\\b");

        public Predicate parse(CriteriaQueryContext c, Path path, Class pathClass, JSONArray criterion, String hint)
            throws IllegalArgumentException
        {
            if (RestrictionsParser.this.constraintApplied)
            {
                if (!PATTERN_LE.matcher(hint).find())
                    throw(new IllegalArgumentException("'le' required but not found @"+path.toString()));
            }

            return(c.b.lessThanOrEqualTo(path, (Comparable)RestrictionsParser.this.valueParsers.parse(criterion.get(1), pathClass)));
        }
    }

    protected class GeParser implements CriterionParser
    {
        public final Pattern PATTERN_GE  = Pattern.compile("\\bge\\b");

        public Predicate parse(CriteriaQueryContext c, Path path, Class pathClass, JSONArray criterion, String hint)
            throws IllegalArgumentException
        {
            if (RestrictionsParser.this.constraintApplied)
            {
                if (!PATTERN_GE.matcher(hint).find())
                    throw(new IllegalArgumentException("'ge' required but not found @"+path.toString()));
            }

            return(c.b.greaterThanOrEqualTo(path, (Comparable)RestrictionsParser.this.valueParsers.parse(criterion.get(1), pathClass)));
        }
    }

    protected class InParser implements CriterionParser
    {
        public final Pattern PATTERN_IN  = Pattern.compile("\\bin\\b");

        public Predicate parse(CriteriaQueryContext c, Path path, Class pathClass, JSONArray criterion, String hint)
            throws IllegalArgumentException
        {
            if (RestrictionsParser.this.constraintApplied)
            {
                if (!PATTERN_IN.matcher(hint).find())
                    throw(new IllegalArgumentException("'in' required but not found @"+path.toString()));
            }

            JSONArray rawValues = (JSONArray)criterion.get(1);
            List parsedValues = new ArrayList(rawValues.size());

            for (Object v:rawValues)
                parsedValues.add(RestrictionsParser.this.valueParsers.parse(v, pathClass));

            return(
                path.in(parsedValues)
            );
        }
    }

  // CONSTRUCT
    public RestrictionsParser()
    {
        this.valueParsers = ParserBundle.newDefaultInstance();
        this.setDefaultPredicateParser();

        return;
    }

    public RestrictionsParser setDefaultPredicateParser()
    {
        this.criterionParsers.put(OPERATOR_EQ, new EqParser());
        this.criterionParsers.put(OPERATOR_LT, new LtParser());
        this.criterionParsers.put(OPERATOR_GT, new GtParser());
        this.criterionParsers.put(OPERATOR_LE, new LeParser());
        this.criterionParsers.put(OPERATOR_GE, new GeParser());
        this.criterionParsers.put(OPERATOR_IN, new InParser());

        return(this);
    }

  // RIBOSOME
    protected class Ribosome<T>
    {
        protected CriteriaQueryContext<T> c;
        protected List<Predicate> predicates;
        protected JSONObject restrictions;
        protected JSONObject hint;

      // CONSTRUCT
        public Ribosome(CriteriaQueryContext c, JSONObject restrictions, JSONObject hint)
        {
            this.c = c;
            this.restrictions = restrictions;
            this.hint = hint;

            return;
        }

        public Ribosome(CriteriaQueryContext c, JSONObject restrictions)
        {
            this(c, restrictions, null);

            return;
        }

      // PROCESS
        protected void parsePath(Path path, Class e, JSONObject search, JSONObject hint)
            throws IllegalArgumentException, NoSuchFieldException
        {
            hint = hint!=null ? hint : RestrictionsParser.this.BLANK_HINT;

            for (String k:search.keySet())
            {
                String ph = RestrictionsParser.this.extractHint(hint.get(k));
                if (RestrictionsParser.this.constraintApplied && RestrictionsParser.PATTERN_X.matcher(ph).find())
                    continue;

                Object v = search.get(k);
                FieldInfo fi = RestrictionsParser.this.getFieldInfos(e).get(k);
                if (fi == null)
                {
                    if (RestrictionsParser.this.failException)
                        throw(new NoSuchFieldException("Unable to build path "+k+"@"+path.toString()));
                    continue;
                }

                // SWITCH
                // CASE:SUB HIERARCHY
                if (v instanceof JSONObject)
                {
                    this.parsePath(
                        path.get(k),
                        fi.fieldClass,
                        (JSONObject)v,
                        wrapHint(hint.get(k))
                    );

                    continue;
                }

                // CASE:CRITERIONS
                if (v instanceof JSONArray)
                {
                    this.parseCriterions(
                        path.get(k),
                        fi.fieldClass,
                        (JSONArray)v,
                        ph
                    );

                    continue;
                }
            }
        }

        public void parseCriterions(Path path, Class pClass, JSONArray criterions, String hint)
            throws IllegalArgumentException
        {
            for (Object e:criterions)
            {
                JSONArray criterion = (JSONArray)e;

                String predicate = criterion.getString(0).toLowerCase();

                CriterionParser parser = RestrictionsParser.this.criterionParsers.get(predicate);
                if (parser == null)
                {
                    if (RestrictionsParser.this.failException)
                        throw(new IllegalArgumentException("Unrecognized predicate "+predicate+"@"+path.toString()));
                    continue;
                }

                try
                {
                    this.predicates.add(
                        parser.parse(
                            this.c,
                            path,
                            pClass,
                            criterion,
                            hint
                        )
                    );
                }
                catch(IllegalArgumentException ex)
                {
                    if (RestrictionsParser.this.failException)
                        throw(ex);
                    continue;
                }
            }
        }

      // EXPOSED
        public List<Predicate> startParse()
            throws IllegalArgumentException, NoSuchFieldException
        {
            this.predicates = new ArrayList<Predicate>();

            String rh = RestrictionsParser.this.extractHint(hint);
            if (RestrictionsParser.this.constraintApplied && RestrictionsParser.PATTERN_X.matcher(rh).find())
                return(predicates);

            this.parsePath(this.c.r, this.c.e, this.restrictions, this.hint);

            return(this.predicates);
            //this.c.c.where(
                //this.c.b.and(
                    //this.predicates.toArray(new Predicate[this.predicates.size()])
                //)
            //);

        }
    }

  // EXPOSED
    public <X> List<Predicate> export(CriteriaQueryContext<X> c, JSONObject restrictions, JSONObject hint)
        throws IllegalArgumentException, NoSuchFieldException
    {
        return(
            new Ribosome(c, restrictions, hint).startParse()
        );
    }

    public <X> CriteriaQueryContext<X> apply(CriteriaQueryContext<X> c, JSONObject restrictions, JSONObject hint)
        throws IllegalArgumentException, NoSuchFieldException
    {
        List<Predicate> predicates = this.export(c, restrictions, hint);

        c.c.where(
            predicates.toArray(new Predicate[predicates.size()])
        );

        return(c);
    }

    public <X> CriteriaQueryContext<X> merge(CriteriaQueryContext<X> c, JSONObject restrictions, JSONObject hint)
        throws IllegalArgumentException, NoSuchFieldException
    {
        List<Predicate> predicates = this.export(c, restrictions, hint);

        Predicate p0 = c.c.getRestriction();
        if (p0 != null)
            predicates.add(p0);

        c.c.where(
            predicates.toArray(new Predicate[predicates.size()])
        );

        return(c);
    }
}
