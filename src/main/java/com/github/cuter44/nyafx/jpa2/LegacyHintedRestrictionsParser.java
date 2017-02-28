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

import com.github.cuter44.nyafx.servlet.ParserBundle;
import com.github.cuter44.nyafx.servlet.ValueParser;
import com.github.cuter44.nyafx.fastjson.FieldInfoLite;
import com.github.cuter44.nyafx.fastjson.TypeUtilsLite;

public class LegacyHintedRestrictionsParser
    implements RestrictionsParser
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
    protected Map<Class, Map<String, FieldInfoLite>> knownClasses = new Hashtable<Class, Map<String, FieldInfoLite>>();

    protected Map<String, FieldInfoLite> getFieldInfo(Class clazz)
    {
        Map<String, FieldInfoLite> mfi = this.knownClasses.get(clazz);
        if (mfi != null)
            return(mfi);

        // ELSE
        this.knownClasses.put(
            clazz,
            mfi = TypeUtilsLite.findFieldsViaGetter(clazz)
        );

        return(mfi);
    }

  // VALUE PARSER
    protected ParserBundle valueParsers;

    public LegacyHintedRestrictionsParser setValueParsers(ParserBundle parsers)
    {
        this.valueParsers = parsers;

        return(this);
    }

    /** @deprecated Misspelled method name.
     */
    @Deprecated
    public LegacyHintedRestrictionsParser setValuePatsers(ParserBundle parsers)
    {
        return(
            this.setValueParsers(parsers)
        );
    }

    public LegacyHintedRestrictionsParser addValueParser(Type type, ValueParser parser)
    {
        this.valueParsers.addPrimitiveParser(type, parser);

        return(this);
    }

  // CRITERION PARSER
    public static interface CriterionParser
    {
        public abstract Predicate parse(AbstractCriteriaContext c, Path path, Class pathClass, JSONArray criterion, String hint)
            throws IllegalArgumentException;
    }

    protected Map<String, CriterionParser> criterionParsers = new HashMap<String, CriterionParser>();

  // BUILT-IN CRITERION PARSER
    protected class EqParser implements CriterionParser
    {
        public final Pattern PATTERN_EQ  = Pattern.compile("\\beq\\b");

        public Predicate parse(AbstractCriteriaContext c, Path path, Class pathClass, JSONArray criterion, String hint)
            throws IllegalArgumentException
        {
            if (LegacyHintedRestrictionsParser.this.constraintApplied)
            {
                if (!PATTERN_EQ.matcher(hint).find())
                    throw(new IllegalArgumentException("'eq' not granted @"+path.toString()));
            }

            return(c.getB().equal(path, LegacyHintedRestrictionsParser.this.valueParsers.parse(criterion.get(1), pathClass)));
        }
    }

    protected class LtParser implements CriterionParser
    {
        public final Pattern PATTERN_LT  = Pattern.compile("\\blt\\b");

        public Predicate parse(AbstractCriteriaContext c, Path path, Class pathClass, JSONArray criterion, String hint)
            throws IllegalArgumentException
        {
            if (LegacyHintedRestrictionsParser.this.constraintApplied)
            {
                if (!PATTERN_LT.matcher(hint).find())
                    throw(new IllegalArgumentException("'lt' not granted @"+path.toString()));
            }

            return(c.getB().lessThan(path, (Comparable)LegacyHintedRestrictionsParser.this.valueParsers.parse(criterion.get(1), pathClass)));
        }
    }

    protected class GtParser implements CriterionParser
    {
        public final Pattern PATTERN_GT  = Pattern.compile("\\bgt\\b");

        public Predicate parse(AbstractCriteriaContext c, Path path, Class pathClass, JSONArray criterion, String hint)
            throws IllegalArgumentException
        {
            if (LegacyHintedRestrictionsParser.this.constraintApplied)
            {
                if (!PATTERN_GT.matcher(hint).find())
                    throw(new IllegalArgumentException("'gt' not granted @"+path.toString()));
            }

            return(c.getB().greaterThan(path, (Comparable)LegacyHintedRestrictionsParser.this.valueParsers.parse(criterion.get(1), pathClass)));
        }
    }

    protected class LeParser implements CriterionParser
    {
        public final Pattern PATTERN_LE  = Pattern.compile("\\ble\\b");

        public Predicate parse(AbstractCriteriaContext c, Path path, Class pathClass, JSONArray criterion, String hint)
            throws IllegalArgumentException
        {
            if (LegacyHintedRestrictionsParser.this.constraintApplied)
            {
                if (!PATTERN_LE.matcher(hint).find())
                    throw(new IllegalArgumentException("'le' not granted @"+path.toString()));
            }

            return(c.getB().lessThanOrEqualTo(path, (Comparable)LegacyHintedRestrictionsParser.this.valueParsers.parse(criterion.get(1), pathClass)));
        }
    }

    protected class GeParser implements CriterionParser
    {
        public final Pattern PATTERN_GE  = Pattern.compile("\\bge\\b");

        public Predicate parse(AbstractCriteriaContext c, Path path, Class pathClass, JSONArray criterion, String hint)
            throws IllegalArgumentException
        {
            if (LegacyHintedRestrictionsParser.this.constraintApplied)
            {
                if (!PATTERN_GE.matcher(hint).find())
                    throw(new IllegalArgumentException("'ge' not granted @"+path.toString()));
            }

            return(c.getB().greaterThanOrEqualTo(path, (Comparable)LegacyHintedRestrictionsParser.this.valueParsers.parse(criterion.get(1), pathClass)));
        }
    }

    protected class InParser implements CriterionParser
    {
        public final Pattern PATTERN_IN  = Pattern.compile("\\bin\\b");

        public Predicate parse(AbstractCriteriaContext c, Path path, Class pathClass, JSONArray criterion, String hint)
            throws IllegalArgumentException
        {
            if (LegacyHintedRestrictionsParser.this.constraintApplied)
            {
                if (!PATTERN_IN.matcher(hint).find())
                    throw(new IllegalArgumentException("'in' not granted @"+path.toString()));
            }

            JSONArray rawValues = (JSONArray)criterion.get(1);
            List parsedValues = new ArrayList(rawValues.size());

            for (Object v:rawValues)
                parsedValues.add(LegacyHintedRestrictionsParser.this.valueParsers.parse(v, pathClass));

            return(
                path.in(parsedValues)
            );
        }
    }

  // CONSTRUCT
    public LegacyHintedRestrictionsParser()
    {
        this.valueParsers = ParserBundle.newDefaultInstance();
        this.setDefaultPredicateParser();

        return;
    }

    public LegacyHintedRestrictionsParser setDefaultPredicateParser()
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
        protected AbstractCriteriaContext<T> c;
        protected List<Predicate> predicates;
        protected JSONObject restrictions;
        protected JSONObject hint;

      // CONSTRUCT
        public Ribosome(AbstractCriteriaContext<T> c, JSONObject restrictions, JSONObject hint)
        {
            this.c = c;
            this.restrictions = restrictions;
            this.hint = hint;

            return;
        }

        public Ribosome(AbstractCriteriaContext<T> c, JSONObject restrictions)
        {
            this(c, restrictions, null);

            return;
        }

      // PROCESS
        protected void parsePath(Path path, Class e, JSONObject search, JSONObject hint)
            throws IllegalArgumentException, NoSuchFieldException
        {
            hint = hint!=null ? hint : LegacyHintedRestrictionsParser.this.BLANK_HINT;

            for (String k:search.keySet())
            {
                String ph = LegacyHintedRestrictionsParser.this.extractHint(hint.get(k));
                if (LegacyHintedRestrictionsParser.this.constraintApplied && LegacyHintedRestrictionsParser.PATTERN_X.matcher(ph).find())
                    continue;

                Object v = search.get(k);
                FieldInfoLite fi = LegacyHintedRestrictionsParser.this.getFieldInfo(e).get(k);
                if (fi == null)
                {
                    if (LegacyHintedRestrictionsParser.this.failException)
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

                CriterionParser parser = LegacyHintedRestrictionsParser.this.criterionParsers.get(predicate);
                if (parser == null)
                {
                    if (LegacyHintedRestrictionsParser.this.failException)
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
                    if (LegacyHintedRestrictionsParser.this.failException)
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

            String rh = LegacyHintedRestrictionsParser.this.extractHint(hint);
            if (LegacyHintedRestrictionsParser.this.constraintApplied && LegacyHintedRestrictionsParser.PATTERN_X.matcher(rh).find())
                return(predicates);

            this.parsePath(this.c.getR(), this.c.getE(), this.restrictions, this.hint);

            return(this.predicates);
        }
    }

  // EXPOSED
    /** @deprecated use <code>parse()</code> instead.
     */
    @Deprecated
    public <X> List<Predicate> export(AbstractCriteriaContext<X> c, JSONObject restrictions, JSONObject hint)
        throws IllegalArgumentException, NoSuchFieldException
    {
        return(
            this.parse(c, restrictions, hint)
        );
    }

    @Override
    public <X> List<Predicate> parse(AbstractCriteriaContext<X> c, JSONObject restrictions, JSONObject hint)
        throws IllegalArgumentException, NoSuchFieldException
    {
        return(
            new Ribosome<X>(c, restrictions, hint).startParse()
        );
    }

    @Override
    public <X> AbstractCriteriaContext<X> apply(AbstractCriteriaContext<X> c, JSONObject restrictions, JSONObject hint)
        throws IllegalArgumentException, NoSuchFieldException
    {
        List<Predicate> predicates = this.parse(c, restrictions, hint);

        c.where(
            predicates.toArray(new Predicate[predicates.size()])
        );

        return(c);
    }

    @Override
    public <X> AbstractCriteriaContext<X> merge(AbstractCriteriaContext<X> c, JSONObject restrictions, JSONObject hint)
        throws IllegalArgumentException, NoSuchFieldException
    {
        List<Predicate> predicates = this.parse(c, restrictions, hint);

        Predicate p0 = c.getRestriction();
        if (p0 != null)
            predicates.add(p0);

        c.where(
            predicates.toArray(new Predicate[predicates.size()])
        );

        return(c);
    }
}
