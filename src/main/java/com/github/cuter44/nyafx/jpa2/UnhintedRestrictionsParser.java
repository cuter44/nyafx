package com.github.cuter44.nyafx.jpa2;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;
import java.lang.reflect.Type;
import javax.persistence.*;
import javax.persistence.criteria.*;

import com.alibaba.fastjson.*;

import com.github.cuter44.nyafx.servlet.ParserBundle;
import com.github.cuter44.nyafx.servlet.ValueParser;
import com.github.cuter44.nyafx.fastjson.FieldInfoLite;
import com.github.cuter44.nyafx.fastjson.TypeUtilsLite;

public class UnhintedRestrictionsParser
    implements RestrictionsParser
{
    public static final String OPERATOR_EQ  = "eq";
    public static final String OPERATOR_LT  = "lt";
    public static final String OPERATOR_LE  = "le";
    public static final String OPERATOR_GT  = "gt";
    public static final String OPERATOR_GE  = "ge";
    public static final String OPERATOR_IN  = "in";

    public boolean failException = false;

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

    public UnhintedRestrictionsParser setValueParsers(ParserBundle parsers)
    {
        this.valueParsers = parsers;

        return(this);
    }

    public UnhintedRestrictionsParser addValueParser(Type type, ValueParser parser)
    {
        this.valueParsers.addPrimitiveParser(type, parser);

        return(this);
    }

  // CRITERION PARSER
    public static interface CriterionParser
    {
        public abstract Predicate parse(AbstractCriteriaContext c, Path path, Class pathClass, JSONArray criterion)
            throws IllegalArgumentException;
    }

    protected Map<String, CriterionParser> criterionParsers = new HashMap<String, CriterionParser>();

  // BUILT-IN CRITERION PARSER
    protected class EqParser implements CriterionParser
    {
        public Predicate parse(AbstractCriteriaContext c, Path path, Class pathClass, JSONArray criterion)
            throws IllegalArgumentException
        {
            return(
                c.getB().equal(
                    path,
                    UnhintedRestrictionsParser.this.valueParsers.parse(criterion.get(1), pathClass)
            ));
        }
    }

    protected class LtParser implements CriterionParser
    {
        public Predicate parse(AbstractCriteriaContext c, Path path, Class pathClass, JSONArray criterion)
            throws IllegalArgumentException
        {
            return(
                c.getB().lessThan(
                    path,
                    (Comparable)UnhintedRestrictionsParser.this.valueParsers.parse(criterion.get(1), pathClass)
            ));
        }
    }

    protected class GtParser implements CriterionParser
    {
        public Predicate parse(AbstractCriteriaContext c, Path path, Class pathClass, JSONArray criterion)
            throws IllegalArgumentException
        {
            return(
                c.getB().greaterThan(
                    path,
                    (Comparable)UnhintedRestrictionsParser.this.valueParsers.parse(criterion.get(1), pathClass)
            ));
        }
    }

    protected class LeParser implements CriterionParser
    {
        public Predicate parse(AbstractCriteriaContext c, Path path, Class pathClass, JSONArray criterion)
            throws IllegalArgumentException
        {
            return(
                c.getB().lessThanOrEqualTo(
                    path,
                    (Comparable)UnhintedRestrictionsParser.this.valueParsers.parse(criterion.get(1), pathClass)
            ));
        }
    }

    protected class GeParser implements CriterionParser
    {
        public Predicate parse(AbstractCriteriaContext c, Path path, Class pathClass, JSONArray criterion)
            throws IllegalArgumentException
        {
            return(
                c.getB().greaterThanOrEqualTo(
                    path,
                    (Comparable)UnhintedRestrictionsParser.this.valueParsers.parse(criterion.get(1), pathClass)
            ));
        }
    }

    protected class InParser implements CriterionParser
    {
        public Predicate parse(AbstractCriteriaContext c, Path path, Class pathClass, JSONArray criterion)
            throws IllegalArgumentException
        {
            JSONArray rawValues = (JSONArray)criterion.get(1);
            List parsedValues = new ArrayList(rawValues.size());

            for (Object v:rawValues)
                parsedValues.add(UnhintedRestrictionsParser.this.valueParsers.parse(v, pathClass));

            return(
                path.in(parsedValues)
            );
        }
    }

  // CONSTRUCT
    public UnhintedRestrictionsParser()
    {
        this.valueParsers = ParserBundle.newDefaultInstance();
        this.setDefaultPredicateParser();

        return;
    }

    public UnhintedRestrictionsParser setDefaultPredicateParser()
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

      // CONSTRUCT
        public Ribosome(AbstractCriteriaContext<T> c, JSONObject restrictions)
        {
            this.c = c;
            this.restrictions = restrictions;

            return;
        }

      // PROCESS
        protected void parsePath(Path path, Class clazz, JSONObject search)
            throws IllegalArgumentException, NoSuchFieldException
        {
            for (String k:search.keySet())
            {
                Object v = search.get(k);
                FieldInfoLite fi = UnhintedRestrictionsParser.this.getFieldInfo(clazz).get(k);
                if (fi == null)
                {
                    if (UnhintedRestrictionsParser.this.failException)
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
                        (JSONObject)v
                    );

                    continue;
                }

                // CASE:CRITERIONS
                if (v instanceof JSONArray)
                {
                    this.parseCriterions(
                        path.get(k),
                        fi.fieldClass,
                        (JSONArray)v
                    );

                    continue;
                }
            }
        }

        public void parseCriterions(Path path, Class pClass, JSONArray criterions)
            throws IllegalArgumentException
        {
            for (Object e:criterions)
            {
                //try
                //{
                    JSONArray criterion = (JSONArray)e;

                    String predicate = criterion.getString(0).toLowerCase();

                    CriterionParser parser = UnhintedRestrictionsParser.this.criterionParsers.get(predicate);
                    if (parser == null)
                    {
                        if (UnhintedRestrictionsParser.this.failException)
                            throw(new IllegalArgumentException("Unrecognized predicate "+predicate+"@"+path.toString()));
                        continue;
                    }

                    this.predicates.add(
                        parser.parse(
                            this.c,
                            path,
                            pClass,
                            criterion
                        )
                    );
                //}
                //catch(IllegalArgumentException ex)
                //{
                    //if (UnhintedRestrictionsParser.this.failException)
                        //throw(ex);
                    //continue;
                //}
            }
        }

      // EXPOSED
        public List<Predicate> startParse()
            throws IllegalArgumentException, NoSuchFieldException
        {
            this.predicates = new ArrayList<Predicate>();

            this.parsePath(this.c.getR(), this.c.getE(), this.restrictions);

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
            new Ribosome<X>(c, restrictions).startParse()
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
