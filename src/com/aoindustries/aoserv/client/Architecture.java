package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * An <code>Architecture</code> is a simple wrapper for the type
 * of computer architecture used in a server.
 *
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
final public class Architecture extends AOServObjectStringKey<Architecture> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String
        ALPHA="alpha",
        ARM="arm",
        I386="i386",
        I486="i486",
        I586="i586",
        I686="i686",
        I686_AND_X86_64="i686,x86_64",
        M68K="m68k",
        MIPS="mips",
        PPC="ppc",
        SPARC="sparc",
        X86_64="x86_64"
    ;

    public static final String DEFAULT_ARCHITECTURE=I686;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int bits;

    public Architecture(ArchitectureService<?,?> service, String name, int bits) {
        super(service, name);
        this.bits = bits;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="name", unique=true, description="the unique name of the architecture")
    public String getName() {
        return key;
    }

    @SchemaColumn(order=1, name="bits", description="the number of bits used by the architecture")
    public int getBits() {
        return bits;
    }
    // </editor-fold>
}
