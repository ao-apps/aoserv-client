package com.aoindustries.aoserv.client.validator;

/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.i18n.EditableResourceBundle;
import java.io.File;
import java.util.Locale;

/**
 * Provides a simplified interface for obtaining localized values from the ApplicationResources.properties files.
 * Is also an editable resource bundle.
 *
 * @author  AO Industries, Inc.
 */
public final class ApplicationResources_ja extends EditableResourceBundle {

    /**
     * Do not use directly.
     */
    public ApplicationResources_ja() {
        super(
            new File(System.getProperty("user.home")+"/common/ao/cvswork/aoserv-client/src/com/aoindustries/aoserv/client/validator/ApplicationResources_ja.properties"),
            Locale.JAPANESE,
            ApplicationResources.bundleSet
        );
    }
}
