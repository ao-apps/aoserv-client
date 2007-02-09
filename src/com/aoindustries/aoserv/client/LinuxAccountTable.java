package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  LinuxAccount
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxAccountTable extends CachedTableStringKey<LinuxAccount> {

    LinuxAccountTable(AOServConnector connector) {
        super(connector, LinuxAccount.class);
    }

    void addLinuxAccount(
	Username usernameObject,
        String primaryGroup,
	String name,
	String office_location,
	String office_phone,
	String home_phone,
	String type,
	String shell
    ) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxAccountTable.class, "addLinuxAccount(Username,String,String,String,String,String,String,String)", null);
        try {
            try {
                String validity=LinuxAccount.checkGECOS(name, "full name");
                if(validity!=null) throw new SQLException(validity);

                IntList invalidateList;
                AOServConnection connection=connector.getConnection();
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(AOServProtocol.ADD);
                    out.writeCompressedInt(SchemaTable.LINUX_ACCOUNTS);
                    out.writeUTF(usernameObject.pkey);
                    out.writeUTF(primaryGroup);
                    out.writeUTF(name);
                    if(office_location!=null && office_location.length()==0) office_location=null;
                    out.writeBoolean(office_location!=null); if(office_location!=null) out.writeUTF(office_location);
                    if(office_phone!=null && office_phone.length()==0) office_phone=null;
                    out.writeBoolean(office_phone!=null); if(office_phone!=null) out.writeUTF(office_phone);
                    if(home_phone!=null && home_phone.length()==0) home_phone=null;
                    out.writeBoolean(home_phone!=null); if(home_phone!=null) out.writeUTF(home_phone);
                    out.writeUTF(type);
                    out.writeUTF(shell);
                     out.flush();

                    CompressedDataInputStream in=connection.getInputStream();
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                    else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                } catch(IOException err) {
                    connection.close();
                    throw err;
                } finally {
                    connector.releaseConnection(connection);
                }
                connector.tablesUpdated(invalidateList);
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
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

    public static String generatePassword() {
        Profiler.startProfile(Profiler.FAST, LinuxAccountTable.class, "generatePassword()", null);
        try {
            return generatePassword(AOServConnector.getRandom());
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static String generatePassword(Random r) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxAccountTable.class, "generatePassword(Random)", null);
        try {
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
            } while(PasswordChecker.hasResults(PasswordChecker.checkPassword(null, password, true, false)));
            return password;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
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
    
    public LinuxAccount get(Object pkey) {
	return getUniqueRow(LinuxAccount.COLUMN_USERNAME, pkey);
    }

    public List<LinuxAccount> getMailAccounts() {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxAccountTable.class, "getMailAccounts()", null);
        try {
            List<LinuxAccount> cached = getRows();
            int len = cached.size();
            List<LinuxAccount> matches=new ArrayList<LinuxAccount>(len);
            for (int c = 0; c < len; c++) {
                LinuxAccount linuxAccount = cached.get(c);
                if (linuxAccount.getType().isEmail()) matches.add(linuxAccount);
            }
            return matches;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    List<LinuxAccount> getMailAccounts(Business business) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxAccountTable.class, "getMailAccounts(Business)", null);
        try {
            String accounting=business.pkey;
            List<LinuxAccount> cached = getRows();
            int len = cached.size();
            List<LinuxAccount> matches=new ArrayList<LinuxAccount>(len);
            for (int c = 0; c < len; c++) {
                LinuxAccount linuxAccount = cached.get(c);
                if (
                    linuxAccount.getType().isEmail()
                    && linuxAccount.getUsername().getPackage().accounting.equals(accounting)
                ) matches.add(linuxAccount);
            }
            return matches;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    int getTableID() {
        return SchemaTable.LINUX_ACCOUNTS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxAccountTable.class, "handleCommand(String[],InputStream,TerminalWriter,TerminalWriter,boolean)", null);
        try {
            String command=args[0];
            if(command.equalsIgnoreCase(AOSHCommand.ADD_LINUX_ACCOUNT)) {
                if(AOSH.checkParamCount(AOSHCommand.ADD_LINUX_ACCOUNT, args, 8, err)) {
                    connector.simpleAOClient.addLinuxAccount(
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
                    int result=connector.simpleAOClient.areLinuxAccountPasswordsSet(args[1]);
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
                    PasswordChecker.Result[] results = connector.simpleAOClient.checkLinuxAccountPassword(args[1], args[2]);
                    if(PasswordChecker.hasResults(results)) {
                        PasswordChecker.printResults(results, out, Locale.getDefault());
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
                        connector.simpleAOClient.disableLinuxAccount(
                            args[1],
                            args[2]
                        )
                    );
                    out.flush();
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_LINUX_ACCOUNT)) {
                if(AOSH.checkParamCount(AOSHCommand.ENABLE_LINUX_ACCOUNT, args, 1, err)) {
                    connector.simpleAOClient.enableLinuxAccount(args[1]);
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.GENERATE_PASSWORD)) {
                if(AOSH.checkParamCount(AOSHCommand.GENERATE_PASSWORD, args, 0, err)) {
                    out.println(connector.simpleAOClient.generatePassword());
                    out.flush();
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_LINUX_ACCOUNT)) {
                if(AOSH.checkParamCount(AOSHCommand.REMOVE_LINUX_ACCOUNT, args, 1, err)) {
                    connector.simpleAOClient.removeLinuxAccount(args[1]);
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_HOME_PHONE)) {
                if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_HOME_PHONE, args, 2, err)) {
                    connector.simpleAOClient.setLinuxAccountHomePhone(args[1], args[2]);
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_NAME)) {
                if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_NAME, args, 2, err)) {
                    connector.simpleAOClient.setLinuxAccountName(args[1], args[2]);
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_OFFICE_LOCATION)) {
                if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_OFFICE_LOCATION, args, 2, err)) {
                    connector.simpleAOClient.setLinuxAccountOfficeLocation(args[1], args[2]);
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_OFFICE_PHONE)) {
                if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_OFFICE_PHONE, args, 2, err)) {
                    connector.simpleAOClient.setLinuxAccountOfficePhone(args[1], args[2]);
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_PASSWORD)) {
                if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_PASSWORD, args, 2, err)) {
                    connector.simpleAOClient.setLinuxAccountPassword(args[1], args[2]);
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_SHELL)) {
                if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_SHELL, args, 2, err)) {
                    connector.simpleAOClient.setLinuxAccountShell(args[1], args[2]);
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_LINUX_ACCOUNT_REBUILD)) {
                if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_LINUX_ACCOUNT_REBUILD, args, 1, err)) {
                    connector.simpleAOClient.waitForLinuxAccountRebuild(args[1]);
                }
                return true;
            }
            return false;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    void waitForRebuild(AOServer aoServer) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxAccountTable.class, "waitForRebuild(AOServer)", null);
        try {
            connector.requestUpdate(
                AOServProtocol.WAIT_FOR_REBUILD,
                SchemaTable.LINUX_ACCOUNTS,
                aoServer.pkey
            );
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }
}