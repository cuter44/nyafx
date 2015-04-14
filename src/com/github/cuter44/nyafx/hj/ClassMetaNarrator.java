package com.github.cuter44.nyafx.hj;

import org.hibernate.metadata.ClassMetadata;

public interface ClassMetaNarrator
{
    /**
     * @param c entity class
     * @return ClassMetadata mapped to the given class, or null if not an entity.
     */
    public abstract ClassMetadata getClassMetadata(Class c);
}
