package org.shaolin.javacc.context;

import java.io.Serializable;

import org.shaolin.javacc.exception.ParsingException;

public interface ICacheableContext extends Serializable
{

    void putClassObject(String className, Class classObject);

    /**
     *  getClass Object from cache. only find Class througth base type not array 
     *  find sequence is local cache-> essential type (primitive->java.lang.*->load class)->loadClassimport*
     *
     */
    Class getClassObject(String className) throws ParsingException;
    
    /**
     * only load non-array class
     * if you want to load all kinds of classes includes array type then through getClass method
     */
    Class loadClass(String className) throws ParsingException;

}