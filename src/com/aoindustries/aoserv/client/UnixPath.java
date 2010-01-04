/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a full path in Unix style.  Paths must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Start with a <code>/</code></li>
 *   <li>Not contain any null characters</li>
 *   <li>Not contain any /../ or /./ path elements</li>
 *   <li>Not contain any // in the path</li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
final public class UnixPath implements Comparable<UnixPath>, Serializable, ObjectInputValidation, BeanFactory<com.aoindustries.aoserv.client.beans.UnixPath> {

    private static final long serialVersionUID = 1L;

    private static final ConcurrentMap<String,UnixPath> interned = new ConcurrentHashMap<String, UnixPath>();

    /**
     * Interns this path much in the same fashion as <code>String.intern()</code>.
     *
     * @see  String#intern()
     */
    public UnixPath intern() {
        UnixPath existing = interned.get(path);
        if(existing==null) {
            String internedPath = path.intern();
            UnixPath addMe = path==internedPath ? this : new UnixPath(internedPath);
            existing = interned.putIfAbsent(internedPath, addMe);
            if(existing==null) existing = addMe;
        }
        return existing;
    }

    final private String path;

    public UnixPath(String path) {
        this.path = path;
        validate();
    }

    private void validate() {
        // Be non-null
        if(path==null) throw new IllegalArgumentException("path==null");
        // Be non-empty
        if(path.length()==0) throw new IllegalArgumentException("path.length()==0");
        // Start with a /
        if(path.charAt(0)!='/') throw new IllegalArgumentException("path.charAt(0)!='/': "+path.charAt(0));
        // Not contain any null characters
        if(path.indexOf('\0')!=-1) throw new IllegalArgumentException("path.indexOf('\\0')!=-1: "+path.indexOf('\0'));
        // Not contain any /../ or /./ path elements
        if(path.indexOf("/../")!=-1) throw new IllegalArgumentException("path.indexOf(\"/../\")!=-1: "+path.indexOf("/../"));
        if(path.indexOf("/./")!=-1) throw new IllegalArgumentException("path.indexOf(\"/./\")!=-1: "+path.indexOf("/./"));
        // Not contain any // in the path
        if(path.indexOf("//")!=-1) throw new IllegalArgumentException("path.indexOf(\"//\")!=-1: "+path.indexOf("//"));
    }

    /**
     * Perform same validation as constructor on readObject.
     */
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.registerValidation(this, 0);
        ois.defaultReadObject();
    }

    public void validateObject() throws InvalidObjectException {
        try {
            validate();
        } catch(IllegalArgumentException err) {
            InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
            newErr.initCause(err);
            throw newErr;
        }
    }

    @Override
    public boolean equals(Object O) {
    	return
            O!=null
            && O instanceof UnixPath
            && path.equals(((UnixPath)O).path)
    	;
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    public int compareTo(UnixPath other) {
        return path==other.path ? 0 : AOServObject.compareIgnoreCaseConsistentWithEquals(path, other.path);
    }

    @Override
    public String toString() {
        return path;
    }

    public String getPath() {
        return path;
    }

    public com.aoindustries.aoserv.client.beans.UnixPath getBean() {
        return new com.aoindustries.aoserv.client.beans.UnixPath(path);
    }
}
