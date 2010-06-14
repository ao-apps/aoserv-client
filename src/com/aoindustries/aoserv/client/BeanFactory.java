/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * Indicates an object is capable of making a simple JavaBean representation of itself.
 *
 * @see  AOServConnector#getConnector()
 *
 * @author  AO Industries, Inc.
 */
public interface BeanFactory<B> {

    B getBean();
}
