package com.aoindustries.aoserv.client;

/*
 * Copyright 2004-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates a reason and optional dependent object.
 *
 * @see Removable
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class CannotRemoveReason<T extends AOServObject> {

    private final String reason;
    private final List<T> dependentObjects;

    public CannotRemoveReason(String reason) {
        this.reason=reason;
        this.dependentObjects=null;
    }

    public CannotRemoveReason(String reason, T dependentObject) {
        Profiler.startProfile(Profiler.FAST, CannotRemoveReason.class, "<init>(String,AOServObject)", null);
        try {
            this.reason=reason;
            this.dependentObjects=dependentObject==null?null:Collections.singletonList(dependentObject);
       } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    
    public CannotRemoveReason(String reason, List<T> dependentObjects) {
        this.reason=reason;
        this.dependentObjects=dependentObjects;
    }

    public String getReason() {
        return reason;
    }

    public List<T> getDependentObjects() {
        return dependentObjects;
    }
}