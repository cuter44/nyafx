package com.github.cuter44.nyafx.jpa2;

import java.util.List;
import java.lang.reflect.Type;
import javax.persistence.criteria.*;

import com.alibaba.fastjson.*;

import com.github.cuter44.nyafx.servlet.*;

public interface RestrictionsParser
{
    public abstract RestrictionsParser setValueParsers(ParserBundle parsers);
    public abstract RestrictionsParser addValueParser(Type type, ValueParser parser);

    public abstract <X> List<Predicate> parse(AbstractCriteriaContext<X> c, JSONObject restrictions, JSONObject hint)
        throws IllegalArgumentException, NoSuchFieldException;
    public abstract <X> AbstractCriteriaContext<X> apply(AbstractCriteriaContext<X> c, JSONObject restrictions, JSONObject hint)
        throws IllegalArgumentException, NoSuchFieldException;
    public abstract <X> AbstractCriteriaContext<X> merge(AbstractCriteriaContext<X> c, JSONObject restrictions, JSONObject hint)
        throws IllegalArgumentException, NoSuchFieldException;
}
