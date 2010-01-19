/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.validator;

import com.aoindustries.aoserv.client.AOServObjectUtils;
import com.aoindustries.aoserv.client.BeanFactory;
import com.aoindustries.util.Internable;
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
final public class UnixPath implements Comparable<UnixPath>, Serializable, ObjectInputValidation, BeanFactory<com.aoindustries.aoserv.client.beans.UnixPath>, Internable<UnixPath> {

    private static final long serialVersionUID = 1L;

    public static void validate(String path) throws ValidationException {
        // Be non-null
        if(path==null) throw new ValidationException(ApplicationResources.accessor, "UnixPath.validate.isNull");
        // If found in interned, it is valid
        if(!interned.containsKey(path)) {
            // Be non-empty
            if(path.length()==0) throw new ValidationException(ApplicationResources.accessor, "UnixPath.validate.empty");
            // Start with a /
            if(path.charAt(0)!='/') throw new ValidationException(ApplicationResources.accessor, "UnixPath.validate.startWithNonSlash", path.charAt(0));
            // Not contain any null characters
            if(path.indexOf('\0')!=-1) throw new ValidationException(ApplicationResources.accessor, "UnixPath.validate.containsNullCharacter", path.indexOf('\0'));
            // Not contain any /../ or /./ path elements
            if(path.indexOf("/../")!=-1) throw new ValidationException(ApplicationResources.accessor, "UnixPath.validate.containsDotDot", path.indexOf("/../"));
            if(path.indexOf("/./")!=-1) throw new ValidationException(ApplicationResources.accessor, "UnixPath.validate.containsDot", path.indexOf("/./"));
            // Not end with / unless "/"
            if(path.length()>1 && path.endsWith("/")) throw new ValidationException(ApplicationResources.accessor, "UnixPath.validate.endsSlash");
            // Not end with /.. or /.
            if(path.endsWith("/.")) throw new ValidationException(ApplicationResources.accessor, "UnixPath.validate.endsSlashDot");
            if(path.endsWith("/..")) throw new ValidationException(ApplicationResources.accessor, "UnixPath.validate.endsSlashDotDot");
            // Not contain any // in the path
            if(path.indexOf("//")!=-1) throw new ValidationException(ApplicationResources.accessor, "UnixPath.validate.containsDoubleSlash", path.indexOf("//"));
        }
    }

    private static final ConcurrentMap<String,UnixPath> interned = new ConcurrentHashMap<String, UnixPath>();

    public static UnixPath valueOf(String path) throws ValidationException {
        UnixPath existing = interned.get(path);
        return existing!=null ? existing : new UnixPath(path);
    }

    final private String path;

    private UnixPath(String path) throws ValidationException {
        this.path = path;
        validate();
    }

    private void validate() throws ValidationException {
        validate(path);
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
        } catch(ValidationException err) {
            InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
            newErr.initCause(err);
            throw newErr;
        }
    }

    /**
     * Automatically uses previously interned values on deserialization.
     */
    private Object readResolve() throws InvalidObjectException {
        UnixPath existing = interned.get(path);
        return existing!=null ? existing : this;
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
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(path, other.path);
    }

    @Override
    public String toString() {
        return path;
    }

    /**
     * Interns this path much in the same fashion as <code>String.intern()</code>.
     *
     * @see  String#intern()
     */
    public UnixPath intern() {
        try {
            UnixPath existing = interned.get(path);
            if(existing==null) {
                String internedPath = path.intern();
                UnixPath addMe = path==internedPath ? this : new UnixPath(internedPath);
                existing = interned.putIfAbsent(internedPath, addMe);
                if(existing==null) existing = addMe;
            }
            return existing;
        } catch(ValidationException err) {
            // Should not fail validation since original object passed
            throw new AssertionError(err.getMessage());
        }
    }

    public String getPath() {
        return path;
    }

    public com.aoindustries.aoserv.client.beans.UnixPath getBean() {
        return new com.aoindustries.aoserv.client.beans.UnixPath(path);
    }
}
