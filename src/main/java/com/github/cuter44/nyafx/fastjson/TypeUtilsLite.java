package com.github.cuter44.nyafx.fastjson;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.*;

public class TypeUtilsLite
{
    /** Synonym of <code>findFieldsViaGetter(clazz, true)</code>
     */
    public static Map<String, FieldInfoLite> findFieldsViaGetter(Class clazz)
    {
        return(
            findFieldsViaGetter(clazz, true)
        );
    }

    /** Attempt to find fields via their getter.
     *
     * Ported from TypeUtilsLite.computeGetters() for lite usage.
     * <br />
     * This method does not really preform the way as its preceder does,
     * such as filter out the proxies attachement/chasing the best
     * performance/compatiblity. It provides a lite, rough class meta for
     * JSONActualizer/JSONBuilder/RestrictionsParser, who keep out impurity
     * naturally.
     * <br />
     * Do not invoke this INTERNAL purposed method unless you clearly realize
     * its certain imperfection.
     */
    public static Map<String, FieldInfoLite> findFieldsViaGetter(Class clazz, boolean includeBareFields)
    {
        Map<String, FieldInfoLite>  fieldInfoMap    = new HashMap<String, FieldInfoLite>();

        Map<String, Field>          fields          = findFields(clazz);

        for (Method getter : clazz.getMethods())
        {
            String getterName = getter.getName();

            if (getterName.equals("getClass")) {
                continue;
            }

            int pfn = -1;
            if (getterName.startsWith("is"))    pfn = 2;
            if (getterName.startsWith("get"))   pfn = 3;

            if ((pfn == -1) || (pfn == getterName.length()))
                continue;

            String propertyName = decapitalize(getterName.substring(pfn));

            Field field = fields.get(propertyName);
            if (field == null)
                continue;

            // This method can be unreliable.
            String setterName = "set"+getterName.substring(pfn);

            Method setter = null;
            try
            {
                setter = clazz.getMethod(setterName, (Class)field.getGenericType());
            }
            catch (NoSuchMethodException ex)
            {
                // NOOP
            }
            catch (ClassCastException ex)
            {
                // NOOP
            }

            FieldInfoLite fi = new FieldInfoLite(field, getter, setter);
            fieldInfoMap.put(propertyName, fi);
        }

        if (includeBareFields)
        {
            for (String fieldName:fields.keySet())
            {
                if (!fieldInfoMap.keySet().contains(fieldName))
                    fieldInfoMap.put(fieldName, new FieldInfoLite(fields.get(fieldName), null, null));
            }
        }

        return(fieldInfoMap);
    }

  // AUXILIARY
    protected static boolean inGetterSchema(Method method)
    {
        if (Modifier.isStatic(method.getModifiers())) {
            return(false);
        }

        if (method.getReturnType().equals(Void.TYPE)) {
            return(false);
        }

        if (method.getParameterTypes().length != 0) {
            return(false);
        }

        return(true);
    }

    protected static boolean inSetterSchema(Method method)
    {
        if (Modifier.isStatic(method.getModifiers())) {
            return(false);
        }

        if (!method.getReturnType().equals(Void.TYPE)) {
            return(false);
        }

        if (method.getParameterTypes().length != 1) {
            return(false);
        }

        return(true);
    }

    /** Decapitalize name from mutator.
     */
    protected static String decapitalize(String name)
    {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) && Character.isUpperCase(name.charAt(0))) {
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    protected static Map<String, Field> findFields(Class<?> clazz)
    {
        Map<String, Field> map = new HashMap<String, Field>(32);

        findFieldsRecursive(
            clazz,
            map
        );

        return(map);
    }

    protected static void findFieldsRecursive(Class<?> clazz, Map<String , Field> foundFields)
    {
        if (clazz.getSuperclass() != null)
            findFieldsRecursive(clazz.getSuperclass(), foundFields);

        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields)
        {
            if (Modifier.isStatic(f.getModifiers()))
                continue;

            foundFields.put(f.getName(), f);
        }

        return;
    }

}
