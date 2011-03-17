/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.validator;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.io.FastExternalizable;
import com.aoindustries.io.FastObjectInput;
import com.aoindustries.io.FastObjectOutput;
import com.aoindustries.util.Internable;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputValidation;
import java.io.ObjectOutput;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a DNS domain name.  Domain names must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>May not be "default" (case-insensitive)</li>
 *   <li>
 *     Confirm to definition in {@link http://en.wikipedia.org/wiki/Hostname#Internet_hostnames}
 *     and {@link http://en.wikipedia.org/wiki/DNS_label#Parts_of_a_domain_name}
 *   </li>
 *   <li>Last domain label must be alphabetic (not be all numeric)</li>
 *   <li>For reverse IP address delegation, if the domain ends with ".in-addr.arpa", the first label may also be in the format "##/##".</li>
 *   <li>Not end with a period (.)</li>
 * </ul>
 * 
 * @author  AO Industries, Inc.
 */
final public class DomainName
implements
    Comparable<DomainName>,
    FastExternalizable,
    ObjectInputValidation,
    DtoFactory<com.aoindustries.aoserv.client.dto.DomainName>,
    Internable<DomainName> {

    public enum TopLevelDomain {
        AC,
        AD,
        AE,
        AERO,
        AF,
        AG,
        AI,
        AL,
        AM,
        AN,
        AO,
        AQ,
        AR,
        ARPA,
        AS,
        ASIA,
        AT,
        AU,
        AW,
        AX,
        AZ,
        BA,
        BB,
        BD,
        BE,
        BF,
        BG,
        BH,
        BI,
        BIZ,
        BJ,
        BM,
        BN,
        BO,
        BR,
        BS,
        BT,
        BV,
        BW,
        BY,
        BZ,
        CA,
        CAT,
        CC,
        CD,
        CF,
        CG,
        CH,
        CI,
        CK,
        CL,
        CM,
        CN,
        CO,
        COM,
        COOP,
        CR,
        CU,
        CV,
        CX,
        CY,
        CZ,
        DE,
        DJ,
        DK,
        DM,
        DO,
        DZ,
        EC,
        EDU,
        EE,
        EG,
        ER,
        ES,
        ET,
        EU,
        FI,
        FJ,
        FK,
        FM,
        FO,
        FR,
        GA,
        GB,
        GD,
        GE,
        GF,
        GG,
        GH,
        GI,
        GL,
        GM,
        GN,
        GOV,
        GP,
        GQ,
        GR,
        GS,
        GT,
        GU,
        GW,
        GY,
        HK,
        HM,
        HN,
        HR,
        HT,
        HU,
        ID,
        IE,
        IL,
        IM,
        IN,
        INFO,
        INT,
        IO,
        IQ,
        IR,
        IS,
        IT,
        JE,
        JM,
        JO,
        JOBS,
        JP,
        KE,
        KG,
        KH,
        KI,
        KM,
        KN,
        KP,
        KR,
        KW,
        KY,
        KZ,
        LA,
        LB,
        LC,
        LI,
        LK,
        LR,
        LS,
        LT,
        LU,
        LV,
        LY,
        MA,
        MC,
        MD,
        ME,
        MG,
        MH,
        MIL,
        MK,
        ML,
        MM,
        MN,
        MO,
        MOBI,
        MP,
        MQ,
        MR,
        MS,
        MT,
        MU,
        MUSEUM,
        MV,
        MW,
        MX,
        MY,
        MZ,
        NA,
        NAME,
        NC,
        NE,
        NET,
        NF,
        NG,
        NI,
        NL,
        NO,
        NP,
        NR,
        NU,
        NZ,
        OM,
        ORG,
        PA,
        PE,
        PF,
        PG,
        PH,
        PK,
        PL,
        PM,
        PN,
        PR,
        PRO,
        PS,
        PT,
        PW,
        PY,
        QA,
        RE,
        RO,
        RS,
        RU,
        RW,
        SA,
        SB,
        SC,
        SD,
        SE,
        SG,
        SH,
        SI,
        SJ,
        SK,
        SL,
        SM,
        SN,
        SO,
        SR,
        ST,
        SU,
        SV,
        SY,
        SZ,
        TC,
        TD,
        TEL,
        TF,
        TG,
        TH,
        TJ,
        TK,
        TL,
        TM,
        TN,
        TO,
        TP,
        TR,
        TRAVEL,
        TT,
        TV,
        TW,
        TZ,
        UA,
        UG,
        UK,
        US,
        UY,
        UZ,
        VA,
        VC,
        VE,
        VG,
        VI,
        VN,
        VU,
        WF,
        WS,
        XN__0ZWM56D,
        XN__11B5BS3A9AJ6G,
        XN__3E0B707E,
        XN__45BRJ9C,
        XN__80AKHBYKNJ4F,
        XN__9T4B11YI5A,
        XN__CLCHC0EA0B2G2A9GCD,
        XN__DEBA0AD,
        XN__FIQS8S,
        XN__FIQZ9S,
        XN__FPCRJ9C3D,
        XN__FZC2C9E2C,
        XN__G6W251D,
        XN__GECRJ9C,
        XN__H2BRJ9C,
        XN__HGBK6AJ7F53BBA,
        XN__HLCJ6AYA9ESC7A,
        XN__J6W193G,
        XN__JXALPDLP,
        XN__KGBECHTV,
        XN__KPRW13D,
        XN__KPRY57D,
        XN__MGBAAM7A8H,
        XN__MGBAYH7GPA,
        XN__MGBBH1A71E,
        XN__MGBERP4A5D4AR,
        XN__O3CW4H,
        XN__OGBPF8FL,
        XN__P1AI,
        XN__PGBS0DH,
        XN__S9BRJ9C,
        XN__WGBH1C,
        XN__WGBL6A,
        XN__XKC2AL3HYE2A,
        XN__XKC2DL3A5EE0H,
        XN__YFRO4I67O,
        XN__YGBI2AMMX,
        XN__ZCKZAH,
        YE,
        YT,
        ZA,
        ZM,
        ZW;

        public static final String SOURCE_URL = "http://data.iana.org/TLD/tlds-alpha-by-domain.txt";
        public static final Timestamp LAST_DOWNLOADED = new Timestamp(1300332279678L); // 2011-03-16

        private final DomainLabel label;

        private TopLevelDomain() {
            try {
                label = DomainLabel.valueOf(name().toLowerCase(Locale.ENGLISH).replace('_', '-')).intern();
            } catch(ValidationException exc) {
                throw new AssertionError(exc);
            }
        }

        @Override
        public String toString() {
            return label.toString();
        }

        public DomainLabel getLabel() {
            return label;
        }

        private static final Map<String,TopLevelDomain> lowerTldMap;
        static {
            TopLevelDomain[] tlds = values();
            lowerTldMap = new HashMap<String,TopLevelDomain>(tlds.length*4/3+1);
            for(TopLevelDomain tld : tlds) lowerTldMap.put(tld.label.toString(), tld);
        }

        /**
         * Provides a way to get the top level domain based on label (case insensitive).
         * Since the enum identifiers cannot contain hyphens (-), they have been replaced
         * with underscores.  This also converts them back.
         */
        public static TopLevelDomain getByLabel(String label) {
            return lowerTldMap.get(label.toLowerCase(Locale.ENGLISH));
        }

        /**
         * Gets the top level domain for the provided label.
         *
         * @see #valueOfLabel(java.lang.String)
         */
        public static TopLevelDomain getByLabel(DomainLabel label) {
            return getByLabel(label.toString());
        }
    }

    public static final int MAX_LENGTH = 253;

    private static boolean isNumeric(String label) {
        return isNumeric(label, 0, label.length());
    }

    private static boolean isNumeric(String label, int start, int end) {
        if((end-start)<=0) throw new IllegalArgumentException("empty label");
        for(int i=start; i<end; i++) {
            char ch = label.charAt(i);
            if(ch<'0' || ch>'9') return false;
        }
        return true;
    }

    /**
     * Checks if is in the format numeric / numeric.
     */
    private static boolean isArpaDelegationFirstLabel(String label, int beginIndex, int endIndex) {
        int slashPos = -1;
        for(int i=beginIndex; i<endIndex; i++) {
            if(label.charAt(i)=='/') {
                slashPos = i;
                break;
            }
        }
        return
            slashPos!=-1
            && isNumeric(label, beginIndex, slashPos)
            && isNumeric(label, slashPos+1, endIndex)
        ;
    }

    /**
     * Checks if ends with .in-addr.arpa (case insensitive)
     */
    public static boolean isArpa(String domain) {
        // Stupid fast implementation - performance vs. complexity gone too far?
        int pos = domain.length()-13;
        char ch;
        return
            pos>=0
            &&      domain.charAt(pos++) =='.'
            && ((ch=domain.charAt(pos++))=='i' || ch=='I')
            && ((ch=domain.charAt(pos++))=='n' || ch=='N')
            &&      domain.charAt(pos++) =='-'
            && ((ch=domain.charAt(pos++))=='a' || ch=='A')
            && ((ch=domain.charAt(pos++))=='d' || ch=='D')
            && ((ch=domain.charAt(pos++))=='d' || ch=='D')
            && ((ch=domain.charAt(pos++))=='r' || ch=='R')
            &&      domain.charAt(pos++) =='.'
            && ((ch=domain.charAt(pos++))=='a' || ch=='A')
            && ((ch=domain.charAt(pos++))=='r' || ch=='R')
            && ((ch=domain.charAt(pos++))=='p' || ch=='P')
            && ((ch=domain.charAt(pos  ))=='a' || ch=='A')
        ;
        //return domain.toLowerCase(Locale.ENGLISH).endsWith(".in-addr.arpa");
    }

    /**
     * Validates a domain name, but doesn't allow an ending period.
     *
     * @see DomainLabel#validate(java.lang.String)
     */
    public static void validate(String domain) throws ValidationException {
        if(domain==null) throw new ValidationException(ApplicationResources.accessor, "DomainName.validate.isNull");
        int len = domain.length();
        if(len==0) throw new ValidationException(ApplicationResources.accessor, "DomainName.validate.empty");
        char ch;
        if(
            // "default".equalsIgnoreCase(domain)
            domain.length()==7
            && ((ch=domain.charAt(0))=='d' || ch=='D')
            && ((ch=domain.charAt(1))=='e' || ch=='E')
            && ((ch=domain.charAt(2))=='f' || ch=='F')
            && ((ch=domain.charAt(3))=='a' || ch=='A')
            && ((ch=domain.charAt(4))=='u' || ch=='U')
            && ((ch=domain.charAt(5))=='l' || ch=='L')
            && ((ch=domain.charAt(6))=='t' || ch=='T')
        ) throw new ValidationException(ApplicationResources.accessor, "DomainName.validate.isDefault");
        if(len>MAX_LENGTH) throw new ValidationException(ApplicationResources.accessor, "DomainName.validate.tooLong", MAX_LENGTH, len);
        boolean isArpa = isArpa(domain);
        int labelStart = 0;
        for(int pos=0; pos<len; pos++) {
            if(domain.charAt(pos)=='.') {
                // For reverse IP address delegation, if the domain ends with ".in-addr.arpa", the first label may also be in the format "##/##".
                if(!isArpa || labelStart!=0 || !isArpaDelegationFirstLabel(domain, labelStart, pos)) DomainLabel.validate(domain, labelStart, pos);
                labelStart = pos+1;
            }
        }
        DomainLabel.validate(domain, labelStart, len);
        // Last domain label must be alphabetic (not be all numeric)
        if(isNumeric(domain, labelStart, len)) throw new ValidationException(ApplicationResources.accessor, "DomainName.validate.lastLabelAllDigits");
        // Last label must be a valid top level domain
        String lastLabel = domain.substring(labelStart, len);
        if(TopLevelDomain.getByLabel(lastLabel)==null) throw new ValidationException(ApplicationResources.accessor, "DomainName.validate.notEndTopLevelDomain");
    }

    private static final ConcurrentMap<String,DomainName> interned = new ConcurrentHashMap<String,DomainName>();

    /**
     * If domain is null, returns null.
     */
    public static DomainName valueOf(String domain) throws ValidationException {
        if(domain==null) return null;
        //DomainName existing = interned.get(domain);
        //return existing!=null ? existing : new DomainName(domain);
        return new DomainName(domain);
    }

    private String domain;

    private DomainName(String domain) throws ValidationException {
        this.domain = domain;
        validate();
    }

    private void validate() throws ValidationException {
        validate(domain);
    }

    @Override
    public boolean equals(Object O) {
    	return
            O!=null
            && O instanceof DomainName
            && domain.equals(((DomainName)O).domain)
    	;
    }

    @Override
    public int hashCode() {
        return domain.hashCode();
    }

    /**
     * Sorts by top level domain, then subdomain, then sub-subdomain, ...
     */
    @Override
    public int compareTo(DomainName other) {
        if(this==other) return 0;
        String domain1 = domain;
        String domain2 = other.domain;
        if(domain1==domain2) return 0; // Shortcut for interned
        while(domain1.length()>0 && domain2.length()>0) {
            int pos=domain1.lastIndexOf('.');
            String section1;
            if(pos==-1) {
                section1=domain1;
                domain1="";
            } else {
                section1=domain1.substring(pos+1);
                domain1=domain1.substring(0, pos);
            }

            pos=domain2.lastIndexOf('.');
            String section2;
            if(pos==-1) {
                section2=domain2;
                domain2="";
            } else {
                section2=domain2.substring(pos+1);
                domain2=domain2.substring(0, pos);
            }

            int diff=AOServObject.compareIgnoreCaseConsistentWithEquals(section1, section2);
            if(diff!=0) return diff;
        }
        return AOServObject.compareIgnoreCaseConsistentWithEquals(domain1, domain2);
    }

    @Override
    public String toString() {
        return domain;
    }

    public boolean isArpa() {
        return isArpa(domain);
    }

    /**
     * Interns this domain much in the same fashion as <code>String.intern()</code>.
     *
     * @see  String#intern()
     */
    @Override
    public DomainName intern() {
        try {
            DomainName existing = interned.get(domain);
            if(existing==null) {
                String internedDomain = domain.intern();
                DomainName addMe = domain==internedDomain ? this : new DomainName(internedDomain);
                existing = interned.putIfAbsent(internedDomain, addMe);
                if(existing==null) existing = addMe;
            }
            return existing;
        } catch(ValidationException err) {
            // Should not fail validation since original object passed
            throw new AssertionError(err.getMessage());
        }
    }

    @Override
    public com.aoindustries.aoserv.client.dto.DomainName getDto() {
        return new com.aoindustries.aoserv.client.dto.DomainName(domain);
    }

    // <editor-fold defaultstate="collapsed" desc="FastExternalizable">
    private static final long serialVersionUID = 2384488670340662487L;

    public DomainName() {
    }

    @Override
    public long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        FastObjectOutput fastOut = FastObjectOutput.wrap(out);
        try {
            fastOut.writeFastUTF(domain);
        } finally {
            fastOut.unwrap();
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        if(domain!=null) throw new IllegalStateException();
        FastObjectInput fastIn = FastObjectInput.wrap(in);
        try {
            domain = fastIn.readFastUTF();
        } finally {
            fastIn.unwrap();
        }
    }

    @Override
    public void validateObject() throws InvalidObjectException {
        try {
            validate();
        } catch(ValidationException err) {
            InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
            newErr.initCause(err);
            throw newErr;
        }
    }
    // </editor-fold>
}
