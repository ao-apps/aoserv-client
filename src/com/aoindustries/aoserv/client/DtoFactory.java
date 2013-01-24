/*
 * Copyright 2009-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * Indicates an object is capable of making a simple Data Transfer Object (DTO) representation of itself.
 *
 * @author  AO Industries, Inc.
 */
public interface DtoFactory<B> {

    B getDto();
}
