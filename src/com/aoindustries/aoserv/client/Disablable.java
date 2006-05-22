package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;

/**
 * Classes that are <code>Disablable</code> can be disable and reenabled.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public interface Disablable {

    DisableLog getDisableLog();

    boolean canDisable();

    boolean canEnable();

    void disable(DisableLog dl);

    void enable();
}