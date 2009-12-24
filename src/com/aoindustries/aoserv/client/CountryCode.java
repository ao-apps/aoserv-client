package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.Locale;

/**
 * A <code>CountryCode</code> is a simple wrapper for country
 * code and name mappings.  Each code is a two-digit ISO 3166-1 alpha-2 country
 * code.
 *
 * See <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2">http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2</a>
 *
 * @author  AO Industries, Inc.
 */
final public class CountryCode extends AOServObjectStringKey<CountryCode> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * <code>CountryCode</code>s used as constants.
     */
    public static final String US="US";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private String name;
    final private boolean charge_com_supported;
    final private String charge_com_name;

    public CountryCode(CountryCodeService<?,?> table, String code, String name, boolean charge_com_supported, String charge_com_name) {
        super(table, code);
        this.name = name;
        this.charge_com_supported = charge_com_supported;
        this.charge_com_name = charge_com_name;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(CountryCode other) {
        return compareIgnoreCaseConsistentWithEquals(name, other.name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    /**
     * Gets the two-character unique code for this country.
     */
    @SchemaColumn(order=0, name="code", unique=true, description="the two-character code for the country")
    public String getCode() {
        return key;
    }

    @SchemaColumn(order=1, name="name", description="the name of the country")
    public String getName() {
    	return name;
    }

    @SchemaColumn(order=2, name="charge_com_supported", description="if the country is supported by Charge.Com")
    public boolean getChargeComSupported() {
        return charge_com_supported;
    }

    @SchemaColumn(order=3, name="charge_com_name", description="the Charge.Com specific name")
    public String getChargeComName() {
    	return charge_com_name==null?name:charge_com_name;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) {
    	return name;
    }
    // </editor-fold>
}
