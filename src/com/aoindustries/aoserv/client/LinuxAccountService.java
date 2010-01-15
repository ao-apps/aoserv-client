/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  LinuxAccount
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.linux_accounts)
public interface LinuxAccountService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,LinuxAccount> {

    /* TODO
    void addLinuxAccount(
    	final Username usernameObject,
        final String primaryGroup,
        final String name,
        String office_location,
        String office_phone,
        String home_phone,
        final String type,
        final String shell
    ) throws IOException, SQLException {
        String validity=LinuxAccount.checkGECOS(name, "full name");
        if(validity!=null) throw new SQLException(validity);

        if(office_location!=null && office_location.length()==0) office_location=null;
        final String finalOfficeLocation = office_location;
        if(office_phone!=null && office_phone.length()==0) office_phone=null;
        final String finalOfficePhone = office_phone;
        if(home_phone!=null && home_phone.length()==0) home_phone=null;
        final String finalHomePhone = home_phone;
        connector.requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
                    out.writeCompressedInt(SchemaTable.TableID.LINUX_ACCOUNTS.ordinal());
                    out.writeUTF(usernameObject.pkey);
                    out.writeUTF(primaryGroup);
                    out.writeUTF(name);
                    out.writeBoolean(finalOfficeLocation!=null); if(finalOfficeLocation!=null) out.writeUTF(finalOfficeLocation);
                    out.writeBoolean(finalOfficePhone!=null); if(finalOfficePhone!=null) out.writeUTF(finalOfficePhone);
                    out.writeBoolean(finalHomePhone!=null); if(finalHomePhone!=null) out.writeUTF(finalHomePhone);
                    out.writeUTF(type);
                    out.writeUTF(shell);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                    else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public void afterRelease() {
                    connector.tablesUpdated(invalidateList);
                }
            }
        );
    }
    
    private static final String[] CONS = {
        "b",            "bl",   "br",
        "c",    "cl",   "cr",
        "ch",
        "d",                    "dr",   "dw",
        "f",            "fl",   "fr",
        "g",            "gl",   "gr",   "gw",
        "h",
        "j",
        "k",            "kl",   "kr",
        "l",
        "m",
        "n",
        "p",    "ph",   "pl",   "pr",
        "qu",
        "r",
        "s",    "sc",   "scr",  "sk",   "sl",   "sm",   "sn",   "sp",   "spl",  "spr",  "st",  "str",  "sw",
        "sh",   
        "t",    "tr",   "tw",
        "th",   "thr",
        "v",
        "w",    "wh",
        "y",
        "z",
    };
    
    private static final String[] TERM_CONS = {
        "b",
        "ch",   "tch",  "ck",
        "d",
        "f",    "ff",
        "g",
        "k",
        "l",	"lch",  "ld",	"lf",	"lk",	"lm",	"lp",   "lsh",  "lt",	"lth",  "lve",    "ll",
        "m",	"mp",
        "n",	"nd",	"ng",	"nk",	"nt",
        "p",																
        "r",	"rch", "rd",	"rf",	"rg",	"rk",	"rm",	"rn",	"rp",	"rsh",  "rt",	"rth",  "rve", 
	"sk",	"sp",	"ss",	"st",
        "sh",
        "t",	"tt",
        "th",   
        "ve",																
        "x",
        "z",    "zz",
    };
    
    private static final String[] VOWS = {
        "a",   
        "e",   
        "i",  
        "o",  
        "u"
    };
    
    private static final String[] TERM_VOWS = {
        "a",    "ay",   "ya",   "ah",   "ar",   "al",
        "ey",   "ee",   "er",   "el",
        "i",    "io",   "yo",
        "o",    "oi",   "oy",   "oh",   "or",   "ol",
        "uh",   "ul",
        "y"
    };

    public static String generatePassword() throws IOException {
        return generatePassword(AOServConnector.getRandom());
    }

    public static String generatePassword(Random r) throws IOException {
        StringBuilder pw = new StringBuilder();
        String password;
        do {
            long entropy;
            do {
                pw.setLength(0);
                entropy = 1;

                int temp1 = 0;
                int temp2 = 0;

                // determine which template to use
                int template = r.nextInt(3);
                entropy*=3;
                switch (template) {
                    case 0: {
                        temp1 = r.nextBoolean()?321:412;
                        temp2 = r.nextBoolean()?321:412;
                        entropy*=4;
                        break;
                    }
                    case 1: {
                        if (r.nextBoolean()) {
                            temp1 = r.nextBoolean()?361:412;
                            temp2 = r.nextBoolean()?4161:3612;
                        } else {
                            temp2 = r.nextBoolean()?361:412;
                            temp1 = r.nextBoolean()?4161:3612; 
                        }
                        entropy*=8;
                        break;
                    }
                    case 2: {
                        temp1 = r.nextBoolean()?416161:361612;
                        entropy*=2;
                        break;
                    }
                }
                // parse the word templates
                StringBuilder word1 = new StringBuilder();
                StringBuilder word2 = new StringBuilder();
                for (int i = 0; i<2; i++) {

                    StringBuilder currWord = (i==0)?word1:word2;
                    int currTemp = (i==0)?temp1:temp2;
                    int digit = currTemp % 10;

                    while (digit>0) {
                        currTemp /= 10;
                        switch (digit) {
                            case 1: {
                                currWord.append(VOWS[r.nextInt(VOWS.length)]);
                                entropy*=VOWS.length;
                                break;
                            }
                            case 2: {
                                currWord.append(CONS[r.nextInt(CONS.length)]);
                                entropy*=CONS.length;
                                break;
                            }
                            case 3: {
                                currWord.append(TERM_VOWS[r.nextInt(TERM_VOWS.length)]);
                                entropy*=TERM_VOWS.length;
                                break;
                            }
                            case 4: {
                                currWord.append(TERM_CONS[r.nextInt(TERM_CONS.length)]);
                                entropy*=TERM_CONS.length;
                                break;
                            }
                            case 6: {
                                boolean a = r.nextBoolean();
                                currWord.append(a?CONS[r.nextInt(CONS.length)]:TERM_CONS[r.nextInt(TERM_CONS.length)]);
                                entropy*=(a?CONS:TERM_CONS).length;
                                break;
                            }
                        }
                        digit = currTemp % 10;
                    }
                    // post-processing checks
                    if (currWord.length()>0) {
                        String ppWord = currWord.toString();
                        ppWord = StringUtility.replace(ppWord, "uu", "ui");
                        ppWord = StringUtility.replace(ppWord, "iw", "u");
                        ppWord = StringUtility.replace(ppWord, "yy", "y");
                        ppWord = StringUtility.replace(ppWord, "lal", r.nextBoolean()?"ral":"lar");
                        ppWord = StringUtility.replace(ppWord, "rar", "ral");
                        ppWord = StringUtility.replace(ppWord, "lel", r.nextBoolean()?"rel":"ler");
                        ppWord = StringUtility.replace(ppWord, "rer", "rel");
                        ppWord = StringUtility.replace(ppWord, "lol", r.nextBoolean()?"rol":"lor");
                        ppWord = StringUtility.replace(ppWord, "ror", "rol");
                        ppWord = StringUtility.replace(ppWord, "lul", r.nextBoolean()?"rul":"lur");
                        ppWord = StringUtility.replace(ppWord, "rur", "rul");
                        ppWord = StringUtility.replace(ppWord, "lil", r.nextBoolean()?"ril":"lir");
                        ppWord = StringUtility.replace(ppWord, "rir", "ril");
                        ppWord = StringUtility.replace(ppWord, "lyl", r.nextBoolean()?"ryl":"lyr");
                        ppWord = StringUtility.replace(ppWord, "ryr", "ryl");
                        if (ppWord.indexOf("rve")<ppWord.length()-3) ppWord = StringUtility.replace(ppWord, "rve", "rv");
                        if (ppWord.indexOf("lve")<ppWord.length()-3) ppWord = StringUtility.replace(ppWord, "lve", "lv");

                        currWord.setLength(0);
                        currWord.append(ppWord);
                    }
                }

                int dig1 = r.nextInt(8)+2;
                int dig2 = r.nextInt(8)+2;
                entropy*=64;
                int dig1pos = r.nextInt(3);
                int dig2pos = r.nextInt(3);
                entropy*=6;
                if (dig1pos==0) pw.append(dig1);
                if (dig2pos==0) pw.append(dig2);
                appendCapped(pw, word1);
                if (dig1pos==1) pw.append(dig1);
                if (dig2pos==1) pw.append(dig2);
                appendCapped(pw, word2);
                if (dig1pos==2) pw.append(dig1);
                if (dig2pos==2) pw.append(dig2);
                //pw.append(" - ").append(entropy/1000000000L);

            } while(entropy<413000000000L);
            password=pw.toString();
        } while(PasswordChecker.hasResults(Locale.getDefault(), PasswordChecker.checkPassword(Locale.getDefault(), null, password, true, false)));
        return password;
    }

    private static void appendCapped(StringBuilder to, StringBuilder from) {
        int len=from.length();
        if(len>0) {
            int ch=from.charAt(0);
            if(ch>='a' && ch<='z') ch=ch-('a'-'A');
            to.append((char)ch);
            for(int c=1;c<len;c++) to.append(from.charAt(c));
        }
    }

    public List<LinuxAccount> getMailAccounts() throws IOException, SQLException {
        List<LinuxAccount> cached = getRows();
        int len = cached.size();
        List<LinuxAccount> matches=new ArrayList<LinuxAccount>(len);
        for (int c = 0; c < len; c++) {
            LinuxAccount linuxAccount = cached.get(c);
            if (linuxAccount.getType().isEmail()) matches.add(linuxAccount);
        }
        return matches;
    }

    List<LinuxAccount> getMailAccounts(Business business) throws IOException, SQLException {
        String accounting=business.pkey;
        List<LinuxAccount> cached = getRows();
        int len = cached.size();
        List<LinuxAccount> matches=new ArrayList<LinuxAccount>(len);
        for (int c = 0; c < len; c++) {
            LinuxAccount linuxAccount = cached.get(c);
            if (
                linuxAccount.getType().isEmail()
                && linuxAccount.getUsername().accounting.equals(accounting)
            ) matches.add(linuxAccount);
        }
        return matches;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_LINUX_ACCOUNT)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_LINUX_ACCOUNT, args, 8, err)) {
                connector.getSimpleAOClient().addLinuxAccount(
                    args[1],
                    args[2],
                    args[3],
                    args[4],
                    args[5],
                    args[6],
                    args[7],
                    args[8]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.ARE_LINUX_ACCOUNT_PASSWORDS_SET)) {
            if(AOSH.checkParamCount(AOSHCommand.ARE_LINUX_ACCOUNT_PASSWORDS_SET, args, 1, err)) {
                int result=connector.getSimpleAOClient().areLinuxAccountPasswordsSet(args[1]);
                if(result==PasswordProtected.NONE) out.println("none");
                else if(result==PasswordProtected.SOME) out.println("some");
                else if(result==PasswordProtected.ALL) out.println("all");
                else throw new RuntimeException("Unexpected value for result: "+result);
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_LINUX_ACCOUNT_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_LINUX_ACCOUNT_NAME, args, 1, err)) {
                try {
                    SimpleAOClient.checkLinuxAccountName(args[1]);
                    out.println("true");
                } catch(IllegalArgumentException ia) {
                    out.println("false");
                }
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_LINUX_ACCOUNT_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_LINUX_ACCOUNT_PASSWORD, args, 2, err)) {
                PasswordChecker.Result[] results = connector.getSimpleAOClient().checkLinuxAccountPassword(args[1], args[2]);
                if(PasswordChecker.hasResults(Locale.getDefault(), results)) {
                    PasswordChecker.printResults(results, out);
                    out.flush();
                }
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_LINUX_ACCOUNT_USERNAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_LINUX_ACCOUNT_USERNAME, args, 1, err)) {
                SimpleAOClient.checkLinuxAccountUsername(args[1]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_LINUX_ACCOUNT)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_LINUX_ACCOUNT, args, 2, err)) {
                out.println(
                    connector.getSimpleAOClient().disableLinuxAccount(
                        args[1],
                        args[2]
                    )
                );
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_LINUX_ACCOUNT)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_LINUX_ACCOUNT, args, 1, err)) {
                connector.getSimpleAOClient().enableLinuxAccount(args[1]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.GENERATE_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.GENERATE_PASSWORD, args, 0, err)) {
                out.println(connector.getSimpleAOClient().generatePassword());
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_LINUX_ACCOUNT)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_LINUX_ACCOUNT, args, 1, err)) {
                connector.getSimpleAOClient().removeLinuxAccount(args[1]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_HOME_PHONE)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_HOME_PHONE, args, 2, err)) {
                connector.getSimpleAOClient().setLinuxAccountHomePhone(args[1], args[2]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_NAME, args, 2, err)) {
                connector.getSimpleAOClient().setLinuxAccountName(args[1], args[2]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_OFFICE_LOCATION)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_OFFICE_LOCATION, args, 2, err)) {
                connector.getSimpleAOClient().setLinuxAccountOfficeLocation(args[1], args[2]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_OFFICE_PHONE)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_OFFICE_PHONE, args, 2, err)) {
                connector.getSimpleAOClient().setLinuxAccountOfficePhone(args[1], args[2]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_PASSWORD, args, 2, err)) {
                connector.getSimpleAOClient().setLinuxAccountPassword(args[1], args[2]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_SHELL)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_SHELL, args, 2, err)) {
                connector.getSimpleAOClient().setLinuxAccountShell(args[1], args[2]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_LINUX_ACCOUNT_REBUILD)) {
            if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_LINUX_ACCOUNT_REBUILD, args, 1, err)) {
                connector.getSimpleAOClient().waitForLinuxAccountRebuild(args[1]);
            }
            return true;
        }
        return false;
    }

    void waitForRebuild(AOServer aoServer) throws IOException, SQLException {
        connector.requestUpdate(
            true,
            AOServProtocol.CommandID.WAIT_FOR_REBUILD,
            SchemaTable.TableID.LINUX_ACCOUNTS,
            aoServer.pkey
        );
    }
     */
}