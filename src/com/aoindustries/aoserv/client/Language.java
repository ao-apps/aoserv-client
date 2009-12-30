package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.Locale;

/**
 * @author  AO Industries, Inc.
 */
final public class Language extends AOServObjectStringKey<Language> implements BeanFactory<com.aoindustries.aoserv.client.beans.Language> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String
        EN="en",
        JA="ja"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public Language(LanguageService<?,?> table, String code) {
        super(table, code);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="code", unique=true, description="the language code")
    public String getCode() {
        return key;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.Language getBean() {
        return new com.aoindustries.aoserv.client.beans.Language(key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "Language."+key+".toString");
    }
    // </editor-fold>
}