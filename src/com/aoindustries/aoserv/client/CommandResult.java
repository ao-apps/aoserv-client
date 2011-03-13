package com.aoindustries.aoserv.client;

/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.Serializable;
import java.util.Set;

/**
 * Each command result may have the set of modified tables attached.  The tables
 * may have been modified by the command or by the commands of any other user.
 *
 * @author  AO Industries, Inc.
 */
public class CommandResult<R> implements Serializable {

    private static final long serialVersionUID = 1L;

    final private R result;
    final private Set<ServiceName> modifiedServiceNames;

    public CommandResult(R result, Set<ServiceName> modifiedServiceNames) {
        if(modifiedServiceNames==null) throw new IllegalArgumentException("modifiedServiceNames==null");
        this.result = result;
        this.modifiedServiceNames = modifiedServiceNames;
    }

    public R getResult() {
        return result;
    }

    public Set<ServiceName> getModifiedServiceNames() {
        return modifiedServiceNames;
    }
}
