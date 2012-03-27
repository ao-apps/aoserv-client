package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.ShellInterpreter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * <code>AOSH</code> is a command interpreter and scripting language
 * based on the Bourne shell.  It may be used to control the
 * <code>AOServ Client</code> utilities.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class AOSH extends ShellInterpreter {

    private static final Logger logger = Logger.getLogger(AOSH.class.getName());

    private static final InputStream nullInput=new ByteArrayInputStream(new byte[0]);

    final private AOServConnector connector;

    public AOSH(AOServConnector connector, InputStream in, TerminalWriter out, TerminalWriter err) {
	super(in, out, err);
        this.connector=connector;
    }

    public AOSH(AOServConnector connector, InputStream in, TerminalWriter out, TerminalWriter err, String[] args) {
	super(in, out, err, args);
        this.connector=connector;
    }

    public AOSH(AOServConnector connector, InputStream in, OutputStream out, OutputStream err) {
	super(in, out, err);
        this.connector=connector;
    }

    public AOSH(AOServConnector connector, InputStream in, OutputStream out, OutputStream err, String[] args) {
	super(in, out, err, args);
        this.connector=connector;
    }

    static boolean checkMinParamCount(String function, String[] args, int minCount, PrintWriter err) {
        int paramCount=args.length-1;
        if(paramCount<minCount) {
            err.print("aosh: ");
            err.print(function);
            err.println(": not enough parameters");
            err.flush();
            return false;
        }
        return true;
    }

    static boolean checkParamCount(String function, String[] args, int requiredCount, PrintWriter err) {
        return checkRangeParamCount(function, args, requiredCount, requiredCount, err);
    }

    static boolean checkRangeParamCount(String function, String[] args, int minCount, int maxCount, PrintWriter err) {
        int paramCount=args.length-1;
        if(paramCount<minCount) {
            err.print("aosh: ");
            err.print(function);
            err.println(": not enough parameters");
            err.flush();
            return false;
        } else if(paramCount>maxCount) {
            err.print("aosh: ");
            err.print(function);
            err.println(": too many parameters");
            err.flush();
            return false;
        }
        return true;
    }

    private void echo(String[] args) {
        for(int c=1;c<args.length;c++) {
            if(c>1) out.print(' ');
            out.print(args[c]);
        }
        out.println();
        out.flush();
    }

    static String executeCommand(AOServConnector connector, String[] args) throws IOException, SQLException {
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        TerminalWriter out=new TerminalWriter(bytes);
        out.setEnabled(false);
        AOSH sh=new AOSH(connector, nullInput, out, out);
        sh.handleCommand(args);
        out.flush();
        return new String(bytes.toByteArray());
    }

    protected String getName() {
        return "aosh";
    }

    protected String getPrompt() throws SQLException, IOException {
        return '['+connector.getThisBusinessAdministrator().toString()+'@'+connector.getHostname()+"]$ ";
    }

    /** Avoid repeated array copies. */
    private static final int numTables = SchemaTable.TableID.values().length;

    /**
     * Processes one command and returns.
     *
     * @param  args  the command and argments to process
     *
     * @return  <code>true</code> if more commands should be processed
     */
    protected boolean handleCommand(String[] args) throws IOException, SQLException {
        int argCount=args.length;
        if(argCount>0) {
            String command=args[0];
            if(AOSHCommand.EXIT.equalsIgnoreCase(command)) {
                if(argCount!=1) {
                    err.println("aosh: "+AOSHCommand.EXIT+": too many parameters");
                    err.flush();
                } else return false;
            } else {
                if(AOSHCommand.CLEAR.equalsIgnoreCase(command)) clear(args);
                else if(AOSHCommand.ECHO.equalsIgnoreCase(command)) echo(args);
                else if(AOSHCommand.INVALIDATE.equalsIgnoreCase(command)) invalidate(args);
                else if(AOSHCommand.JOBS.equalsIgnoreCase(command)) jobs(args);
                else if(AOSHCommand.PING.equalsIgnoreCase(command)) ping(args);
                else if(AOSHCommand.REPEAT.equalsIgnoreCase(command)) repeat(args);
                else if(AOSHCommand.SLEEP.equalsIgnoreCase(command)) sleep(args);
                else if(AOSHCommand.SU.equalsIgnoreCase(command)) su(args);
                else if(AOSHCommand.TIME.equalsIgnoreCase(command)) time(args);
                else if(AOSHCommand.WHOAMI.equalsIgnoreCase(command)) whoami(args);
                else {
                    boolean done=false;
                    // Use the aosh_commands table for faster lookups
                    String lowerCommand=command.toLowerCase();
                    AOSHCommand aoshCommand=connector.getAoshCommands().get(lowerCommand);
                    if(aoshCommand!=null) {
                        AOServTable table=aoshCommand.getSchemaTable(connector).getAOServTable(connector);
                        done=table.handleCommand(args, in, out, err, isInteractive());
                        if(!done) throw new RuntimeException("AOSHCommand found, but command not processed.  command='"+lowerCommand+"', table='"+table.getTableName()+'\'');
                    }
                    /*
                    for(int c=0;c<numTables;c++) {
                        AOServTable table=connector.getTable(c);
                        if(table.handleCommand(args, in, out, err, isInteractive())) {
                            done=true;
                            break;
                        }
                    }*/
                    if(!done) {
                        err.println("aosh: "+command+": command not found");
                        err.flush();
                    }
                }
            }
        }
        return true;
    }

    private void invalidate(String[] args) throws IllegalArgumentException, SQLException, IOException {
        if(checkRangeParamCount(AOSHCommand.INVALIDATE, args, 1, 2, err)) {
            String tableName=args[1];
            SchemaTableTable schemaTableTable=connector.getSchemaTables();
            // Find the table ID
            int tableID=-1;
            for(int d=0;d<numTables;d++) {
                if(schemaTableTable.get(d).getName().equalsIgnoreCase(tableName)) {
                    tableID=d;
                    break;
                }
            }
            if(tableID>=0) {
                connector.getSimpleAOClient().invalidate(tableID, args.length>2?args[2]:null);
            } else {
                err.print("aosh: "+AOSHCommand.INVALIDATE+": unable to find table: ");
                err.println(tableName);
                err.flush();
            }
        }
    }

    public static void main(String[] args) {
        TerminalWriter out=new TerminalWriter(System.out);
        TerminalWriter err=new TerminalWriter(System.err);
        try {
            String username=getConfigUsername(System.in, err);
            String password=getConfigPassword(System.in, err);
            AOServConnector connector=AOServConnector.getConnector(username, password, logger);
            AOSH aosh=new AOSH(connector, System.in, out, err, args);
            aosh.run();
            if(aosh.isInteractive()) {
                out.println();
                out.flush();
            }
        } catch(IOException exc) {
            err.println("aosh: unable to connect: "+exc.getMessage());
            err.flush();
        }
    }

    public static String getConfigUsername(InputStream in, TerminalWriter err) throws IOException {
        String username=AOServClientConfiguration.getUsername();
        if(username==null || username.length()==0) {
            // Prompt for the username
            err.print("Username: ");
            err.flush();
            username=readLine(in);
        }
        return username;
    }
    
    public static String getConfigPassword(InputStream in, TerminalWriter err) throws IOException {
        String password=AOServClientConfiguration.getPassword();
        if(password==null || password.length()==0) {
            // Prompt for the password
            err.print("Password: ");
            err.flush();
            password=readLine(in);
            err.flush();
        }
        return password;
    }

    protected ShellInterpreter newShellInterpreter(InputStream in, TerminalWriter out, TerminalWriter err, String[] args) {
        return new AOSH(connector, in, out, err, args);
    }

    static boolean parseBoolean(String S, String field) {
        if(
            S.equalsIgnoreCase("true")
            || S.equalsIgnoreCase("t")
            || S.equalsIgnoreCase("yes")
            || S.equalsIgnoreCase("y")
            || S.equalsIgnoreCase("vang")
            || S.equalsIgnoreCase("da")
            || S.equalsIgnoreCase("si")
            || S.equalsIgnoreCase("oui")
            || S.equalsIgnoreCase("ja")
            || S.equalsIgnoreCase("nam")
        ) return true;
        else if(
            S.equalsIgnoreCase("false")
            || S.equalsIgnoreCase("f")
            || S.equalsIgnoreCase("no")
            || S.equalsIgnoreCase("n")
            || S.equalsIgnoreCase("khong")
            || S.equalsIgnoreCase("nyet")
            || S.equalsIgnoreCase("non")
            || S.equalsIgnoreCase("nien")
            || S.equalsIgnoreCase("la")
        ) return false;
        else throw new IllegalArgumentException("invalid argument for boolean "+field+": "+S);
    }

    static long parseDate(String S, String field) {
        try {
            return SQLUtility.getDate(S).getTime();
        } catch(NumberFormatException err) {
            throw new IllegalArgumentException("invalid argument for date "+field+": "+S);
        }
    }

    static int parseInt(String S, String field) {
        try {
            return Integer.parseInt(S);
        } catch(NumberFormatException err) {
            throw new IllegalArgumentException("invalid argument for int "+field+": "+S);
        }
    }

    static float parseFloat(String S, String field) {
        try {
            return Float.parseFloat(S);
        } catch(NumberFormatException err) {
            throw new IllegalArgumentException("invalid argument for float "+field+": "+S);
        }
    }

    static long parseLong(String S, String field) {
        try {
            return Long.parseLong(S);
        } catch(NumberFormatException err) {
            throw new IllegalArgumentException("invalid argument for long "+field+": "+S);
        }
    }

    static int parseMillis(String S, String field) {
        try {
            return SQLUtility.getMillis(S);
        } catch(NumberFormatException err) {
            throw new IllegalArgumentException("invalid argument for decimal "+field+": "+S);
        }
    }

    static int parseOctalInt(String S, String field) {
        try {
            return Integer.parseInt(S, 8);
        } catch(NumberFormatException err) {
            throw new IllegalArgumentException("invalid argument for octal int "+field+": "+S);
        }
    }

    static long parseOctalLong(String S, String field) {
        try {
            return Long.parseLong(S, 8);
        } catch(NumberFormatException err) {
            throw new IllegalArgumentException("invalid argument for octal long "+field+": "+S);
        }
    }

    static int parsePennies(String S, String field) {
        try {
            return SQLUtility.getPennies(S);
        } catch(NumberFormatException err) {
            throw new IllegalArgumentException("invalid argument for decimal "+field+": "+S);
        }
    }

    static short parseShort(String S, String field) {
        try {
            return Short.parseShort(S);
        } catch(NumberFormatException err) {
            throw new IllegalArgumentException("invalid argument for short "+field+": "+S);
        }
    }

    private void ping(String[] args) throws IOException, SQLException {
        if(checkParamCount(AOSHCommand.PING, args, 0, err)) {
            out.print(connector.getSimpleAOClient().ping());
            out.println(" ms");
            out.flush();
        }
    }

    public static String readLine(InputStream in) throws IOException {
        StringBuilder SB=new StringBuilder();
        readLine(in, SB);
        return SB.toString();
    }
    
    public static void readLine(InputStream in, StringBuilder SB) throws IOException {
        SB.setLength(0);
        int ch;
        while((ch=in.read())!=-1 && ch!='\n') if(ch!='\r') SB.append((char)ch);
    }

    private void repeat(String[] args) throws IOException, SQLException {
        int argCount=args.length;
        if(argCount>2) {
            try {
                int count=Integer.parseInt(args[1]);
                if(count>=0) {
                    String[] newArgs=new String[argCount-2];
                    System.arraycopy(args, 2, newArgs, 0, argCount-2);

                    for(int c=0;c<count;c++) handleCommand(newArgs);
                } else {
                    err.print("aosh: "+AOSHCommand.REPEAT+": invalid loop count: ");
                    err.println(count);
                    err.flush();
                }
            } catch(NumberFormatException nfe) {
                err.print("aosh: "+AOSHCommand.REPEAT+": invalid loop count: ");
                err.println(args[1]);
                err.flush();
            }
        } else {
            err.println("aosh: "+AOSHCommand.REPEAT+": not enough parameters");
            err.flush();
        }
    }

    private void sleep(String[] args) {
        if(args.length>1) {
            try {
                for(int c=1;c<args.length;c++) {
                    try {
                        long time=1000*Integer.parseInt(args[c]);
                        if(time<0) {
                            err.println("aosh: "+AOSHCommand.SLEEP+": invalid time interval: "+args[c]);
                            err.flush();
                        } else {
                            Thread.sleep(time);
                        }
                    } catch(NumberFormatException nfe) {
                        err.println("aosh: "+AOSHCommand.SLEEP+": invalid time interval: "+args[c]);
                        err.flush();
                    }
                }
            } catch(InterruptedException ie) {
                status="Interrupted";
                err.println("aosh: "+AOSHCommand.SLEEP+": interrupted");
                err.flush();
            }
        } else {
            err.println("aosh: "+AOSHCommand.SLEEP+": too few arguments");
            err.flush();
        }
    }

    private void su(String[] args) throws IOException {
        int argCount=args.length;
        if(argCount>=2) {
            String[] newArgs=new String[argCount+(isInteractive()?0:-1)];
            int pos=0;
            if(isInteractive()) newArgs[pos++]="-i";
            newArgs[pos++]="--";
            System.arraycopy(args, 2, newArgs, pos, argCount-2);
            new AOSH(connector.switchUsers(args[1]), in, out, err, newArgs).run();
        } else {
            err.println("aosh: "+AOSHCommand.SU+": not enough parameters");
            err.flush();
        }
    }

    private void time(String[] args) throws IOException, SQLException {
        int argCount=args.length;
        if(argCount>1) {
            String[] newArgs=new String[argCount-1];
            System.arraycopy(args, 1, newArgs, 0, argCount-1);
            long startTime=System.currentTimeMillis();
            try {
                handleCommand(newArgs);
            } finally {
                long timeSpan=System.currentTimeMillis()-startTime;
                int mins=(int)(timeSpan/60000);
                int secs=(int)(timeSpan%60000);
                out.println();
                out.print("real    ");
                out.print(mins);
                out.print('m');
                out.print(SQLUtility.getMilliDecimal(secs));
                out.println('s');
                out.flush();
            }
        } else {
            err.println("aosh: "+AOSHCommand.TIME+": not enough parameters");
            err.flush();
        }
    }

    private void whoami(String[] args) throws SQLException, IOException {
        if(args.length==1) {
            out.println(connector.getThisBusinessAdministrator().getUsername().getUsername());
            out.flush();
        } else {
            err.println("aosh: "+AOSHCommand.WHOAMI+": too many parameters");
            err.flush();
        }
    }
}