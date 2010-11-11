/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.command.*;
import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.security.LoginException;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.ShellInterpreter;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * <code>AOSH</code> is a command interpreter and scripting language
 * based on the Bourne shell.  It may be used to control the
 * <code>AOServ Client</code> utilities.
 *
 * @author  AO Industries, Inc.
 */
final public class AOSH extends ShellInterpreter {

    private static final String eol = System.getProperty("line.separator");

    private static final Reader nullInput=new CharArrayReader(new char[0]);

    // TODO: Make this be a -d (debug) switch
    private static final boolean STACK_TRACES = false;

    /**
     * Built-in commands.
     */
    public enum BuiltIn {
        clear(""),
        echo("[<i>param</i>]..."),
        exit(""),
        help("[<b>builtin</b>|<b>global</b>|<i>table_name</i>|<i>command</i>] [<b>syntax</b>]"),
        jobs(""),
        repeat("<i>count</i> <i>command</i> [<i>param</i>]..."),
        sleep("<i>seconds</i>"),
        su("<i>username</i> [<i>command</i> [<i>param</i>]...]"),
        time("<i>command</i> [<i>param</i>]..."),
        whoami("");

        /**
         * Unmodifiable list of values.
         */
        public static final List<BuiltIn> values = Collections.unmodifiableList(Arrays.asList(values()));

        private final String syntax;

        private BuiltIn(String syntax) {
            this.syntax = syntax;
        }

        public String getSyntax() {
            return syntax;
        }

        /**
         * Gets a short description of the command.
         */
        public String getShortDesc() {
            return ApplicationResources.accessor.getMessage("AOSH.BuiltIn."+name()+".shortDesc");
        }
    }

    static class CommandResult {
        final String out;
        final String err;
        CommandResult(String out, String err) {
            this.out = out;
            this.err = err;
        }
    }

    static CommandResult executeCommand(AOServConnector<?,?> connector, String[] args) throws IOException {
        CharArrayWriter outChars=new CharArrayWriter();
        TerminalWriter out = new TerminalWriter(outChars);
        out.setEnabled(false);
        CharArrayWriter errChars=new CharArrayWriter();
        TerminalWriter err = new TerminalWriter(errChars);
        err.setEnabled(false);
        AOSH sh=new AOSH(connector, nullInput, out, err);
        sh.handleCommand(args);
        out.flush();
        err.flush();
        return new CommandResult(outChars.toString(), errChars.toString());
    }

    /**
     * Prints, converting bold HTML to TerminalWriter calls.
     */
    static void printBoldItalic(String s, TerminalWriter out) throws IOException {
        int len = s.length();
        int pos = 0;
        int bOn = s.indexOf("<b>");
        if(bOn==-1) bOn=len;
        int BOn = s.indexOf("<B>");
        if(BOn==-1) BOn=len;
        int bOff = s.indexOf("</b>");
        if(bOff==-1) bOff=len;
        int BOff = s.indexOf("</B>");
        if(BOff==-1) BOff=len;
        while(
            pos<len
            && (
                bOn!=len
                || BOn!=len
                || bOff!=len
                || BOff!=len
            )
        ) {
            if(bOn<BOn && bOn<bOff && bOn<BOff) {
                out.print(s.substring(pos, bOn));
                out.boldOn();
                pos = bOn+3;
                bOn = s.indexOf("<b>", pos);
                if(bOn==-1) bOn=len;
            } else if(BOn<bOn && BOn<bOff && BOn<BOff) {
                out.print(s.substring(pos, BOn));
                out.boldOn();
                pos = BOn+3;
                BOn = s.indexOf("<B>", pos);
                if(BOn==-1) BOn=len;
            } else if(bOff<bOn && bOff<BOn && bOff<BOff) {
                out.print(s.substring(pos, bOff));
                out.attributesOff();
                pos = bOff+4;
                bOff = s.indexOf("</b>", pos);
                if(bOff==-1) bOff=len;
            } else if(BOff<bOn && BOff<BOn && BOff<bOff) {
                out.print(s.substring(pos, BOff));
                out.attributesOff();
                pos = BOff+4;
                BOff = s.indexOf("</B>", pos);
                if(BOff==-1) BOff=len;
            } else {
                throw new AssertionError();
            }
        }
        if(pos<len) out.print(s.substring(pos, len));
    }

    final private AOServConnector<?,?> connector;

    public AOSH(AOServConnector<?,?> connector, Reader in, TerminalWriter out, TerminalWriter err) {
    	super(in, out, err);
        this.connector=connector;
    }

    public AOSH(AOServConnector<?,?> connector, Reader in, TerminalWriter out, TerminalWriter err, String[] args) {
    	super(in, out, err, args);
        this.connector=connector;
    }

    /* TODO
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
    */

    private void echo(String[] args) {
        for(int c=1;c<args.length;c++) {
            if(c>1) out.print(' ');
            out.print(args[c]);
        }
        out.println();
        out.flush();
    }

    @Override
    protected String getName() {
        return "aosh";
    }

    @Override
    protected String getPrompt() throws IOException {
        return ApplicationResources.accessor.getMessage("AOSH.prompt", connector.getConnectAs());
    }

    /**
     * Processes one command and returns.
     *
     * @param  args  the command and argments to process
     *
     * @return  <code>true</code> if more commands should be processed (used for exit command)
     */
    @Override
    protected boolean handleCommand(String[] args) throws IOException {
        int argCount=args.length;
        if(argCount>0) {
            String command=args[0];
            if(BuiltIn.exit.name().equals(command)) {
                if(argCount!=1) {
                    err.println(ApplicationResources.accessor.getMessage("AOSH.tooManyParameters", BuiltIn.exit));
                    err.flush();
                } else return false;
            } else {
                if(BuiltIn.clear.name().equals(command)) clear(args);
                else if(BuiltIn.echo.name().equals(command)) echo(args);
                // TODO: else if(AOSHCommand.INVALIDATE.equals(command)) invalidate(args);
                else if(BuiltIn.help.name().equals(command)) help(args);
                else if(BuiltIn.jobs.name().equals(command)) jobs(args);
                // TODO: else if(AOSHCommand.PING.equals(command)) ping(args);
                else if(BuiltIn.repeat.name().equals(command)) repeat(args);
                else if(BuiltIn.sleep.name().equals(command)) sleep(args);
                else if(BuiltIn.su.name().equals(command)) su(args);
                else if(BuiltIn.time.name().equals(command)) time(args);
                else if(BuiltIn.whoami.name().equals(command)) whoami(args);
                else {
                    try {
                        Constructor<? extends AOServCommand<?>> constructor = AOServCommand.getCommandConstructor(CommandName.valueOf(command));
                        Class<?>[] parameterTypes = constructor.getParameterTypes();
                        if(args.length<(parameterTypes.length+1)) {
                            err.println(ApplicationResources.accessor.getMessage("AOSH.notEnoughParameters", command));
                            err.flush();
                        } else {
                            if(args.length>(parameterTypes.length+1)) {
                                err.println(ApplicationResources.accessor.getMessage("AOSH.tooManyParameters", command));
                                err.flush();
                            } else {
                                try {
                                    // Convert the parameters
                                    Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
                                    Object[] parameters = new Object[parameterTypes.length];
                                    for(int c=0; c<parameterTypes.length; c++) {
                                        // Find the @Param annotation
                                        Param paramAnnotation = null;
                                        String arg = args[c+1];
                                        for(Annotation anno : parameterAnnotations[c]) {
                                            if(anno instanceof Param) {
                                                paramAnnotation = (Param)anno;
                                                break;
                                            }
                                        }
                                        parameters[c] = parseParameter(paramAnnotation.name(), parameterTypes[c], paramAnnotation.nullable(), arg);
                                    }
                                    AOServCommand<?> aoservCommand = constructor.newInstance(parameters);
                                    // Make sure current user is enabled (this is done server-side)

                                    // Check permissions
                                    Set<AOServPermission.Permission> permissions = aoservCommand.getCommandName().getPermissions();
                                    BusinessAdministrator thisBa = connector.getThisBusinessAdministrator();
                                    if(permissions.isEmpty() || thisBa.hasPermissions(permissions)) {
                                        // Validate
                                        Map<String,List<String>> errors = aoservCommand.validate(connector);
                                        if(!errors.isEmpty()) throw new CommandValidationException(aoservCommand, errors);

                                        displayCommandResult(aoservCommand.execute(connector, isInteractive()));
                                    } else {
                                        err.println(ApplicationResources.accessor.getMessage("AOSH.PermissionDenied.command", command));
                                        for(AOServPermission.Permission permission : permissions) {
                                            if(thisBa.hasPermission(permission)) err.println(ApplicationResources.accessor.getMessage("AOSH.PermissionDenied.hasPermission", permission));
                                            else err.println(ApplicationResources.accessor.getMessage("AOSH.PermissionDenied.notHasPermission", permission));
                                        }
                                        err.flush();
                                    }
                                } catch(InstantiationException exc) {
                                    err.println(ApplicationResources.accessor.getMessage("AOSH.InstantiationException", command, exc.getMessage()));
                                    if(STACK_TRACES) exc.printStackTrace(err);
                                    err.flush();
                                } catch(IllegalAccessException exc) {
                                    err.println(ApplicationResources.accessor.getMessage("AOSH.IllegalAccessException", command, exc.getMessage()));
                                    if(STACK_TRACES) exc.printStackTrace(err);
                                    err.flush();
                                } catch(InvocationTargetException exc) {
                                    err.println(ApplicationResources.accessor.getMessage("AOSH.InvocationTargetException", command, exc.getMessage()));
                                    if(STACK_TRACES) exc.printStackTrace(err);
                                    err.flush();
                                } catch(AOServPermissionException exc) {
                                    err.println("TODO: AOServPermission: "+exc.getMessage());
                                    // TODO
                                    if(STACK_TRACES) exc.printStackTrace(err);
                                    err.flush();
                                } catch(CommandValidationException exc) {
                                    StringBuilder SB = new StringBuilder();
                                    SB.append(ApplicationResources.accessor.getMessage("AOSH.CommandValidationException", command)).append(eol);
                                    for(Map.Entry<String,List<String>> entry : exc.getErrors().entrySet()) {
                                        String paramName = entry.getKey();
                                        for(String message : entry.getValue()) SB.append(ApplicationResources.accessor.getMessage("AOSH.CommandValidationExceptionError", paramName, message)).append(eol);
                                    }
                                    err.print(SB.toString());
                                    if(STACK_TRACES) exc.printStackTrace(err);
                                    err.flush();
                                } catch(ParameterException exc) {
                                    Throwable cause = exc.getCause();
                                    if(cause instanceof NoSuchElementException) {
                                        err.println(
                                            ApplicationResources.accessor.getMessage(
                                                "AOSH.ParameterException.NoSuchElementException",
                                                command,
                                                exc.parameterName,
                                                cause.getMessage()
                                            )
                                        );
                                    } else {
                                        err.println(
                                            ApplicationResources.accessor.getMessage(
                                                "AOSH.ParameterException",
                                                command,
                                                exc.parameterName,
                                                (cause==null ? exc : cause).getMessage()
                                            )
                                        );
                                    }
                                    if(STACK_TRACES) exc.printStackTrace(err);
                                    err.flush();
                                } catch(RemoteException exc) {
                                    err.println(ApplicationResources.accessor.getMessage("AOSH.RemoteException", command, exc.getMessage()));
                                    if(STACK_TRACES) exc.printStackTrace(err);
                                    err.flush();
                                } catch(RuntimeException exc) {
                                    err.println(ApplicationResources.accessor.getMessage("AOSH.RuntimeException", command, exc.getMessage()));
                                    if(STACK_TRACES) exc.printStackTrace(err);
                                    err.flush();
                                }
                            }
                        }
                    } catch(IllegalArgumentException exc) {
                        err.println(ApplicationResources.accessor.getMessage("AOSH.commandNotFound", command));
                        if(STACK_TRACES) exc.printStackTrace(err);
                        err.flush();
                    }
                }
            }
        }
        return true;
    }

    private void displayCommandResult(Object result) throws IOException {
        if(result!=null) {
            if(result instanceof Collection) {
                int num=1;
                for(Object obj : (Collection)result) {
                    String s = (num++) + ": " +(obj==null ? "null" : obj.toString());
                    if(isInteractive()) {
                        printBoldItalic(s, out);
                    } else {
                        out.print(s);
                    }
                    if(!s.endsWith(eol)) out.println();
                }
            } else {
                String s = result.toString();
                if(isInteractive()) {
                    printBoldItalic(s, out);
                } else {
                    out.print(s);
                }
                if(!s.endsWith(eol)) out.println();
            }
            out.flush();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Parameter Conversion">
    public static class ParameterException extends Exception {

        private static final long serialVersionUID = 1L;

        private final String parameterName;

        public ParameterException(String parameterName, String message) {
            super(message);
            this.parameterName = parameterName;
        }

        public ParameterException(String parameterName, Throwable cause) {
            super(cause.getMessage(), cause);
            this.parameterName = parameterName;
        }

        public String getParameterName() {
            return parameterName;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T parseParameter(String paramName, Class<T> parameterType, boolean nullable, String arg) throws ParameterException, RemoteException {
        if(parameterType==AOServer.class) return (T)parseParameterAoServer(paramName, nullable, arg);
        if(parameterType==BusinessAdministrator.class) return (T)parseParameterBusinessAdministrator(paramName, nullable, arg);
        if(parameterType==DomainName.class) return (T)parseParameterDomainName(paramName, nullable, arg);
        if(parameterType==LinuxAccount.class) return (T)parseParameterLinuxAccount(paramName, nullable, arg);
        if(parameterType==MySQLServer.class) return (T)parseParameterMySQLServer(paramName, nullable, arg);
        if(parameterType==MySQLServerName.class) return (T)parseParameterMySQLServerName(paramName, nullable, arg);
        if(parameterType==MySQLUser.class) return (T)parseParameterMySQLUser(paramName, nullable, arg);
        if(parameterType==MySQLUserId.class) return (T)parseParameterMySQLUserId(paramName, nullable, arg);
        if(parameterType==String.class) return (T)parseParameterString(paramName, nullable, arg);
        if(parameterType==PostgresServer.class) return (T)parseParameterPostgresServer(paramName, nullable, arg);
        if(parameterType==PostgresServerName.class) return (T)parseParameterPostgresServerName(paramName, nullable, arg);
        if(parameterType==PostgresUser.class) return (T)parseParameterPostgresUser(paramName, nullable, arg);
        if(parameterType==PostgresUserId.class) return (T)parseParameterPostgresUserId(paramName, nullable, arg);
        if(parameterType==UserId.class) return (T)parseParameterUserId(paramName, nullable, arg);
        if(parameterType==Username.class) return (T)parseParameterUsername(paramName, nullable, arg);
        throw new AssertionError("Unexpected parameter type: "+parameterType.getName());
    }

    /**
     * Parses an AoServer as either integer pkey or hostname
     */
    public AOServer parseParameterAoServer(String paramName, boolean nullable, String arg) throws ParameterException, RemoteException {
        // If allows null, convert empty string to null
        if(nullable && arg.length()==0) return null;
        AOServer aoServer;
        try {
            Integer server = Integer.valueOf(arg);
            try {
                Server se = connector.getServers().get(server);
                aoServer = se.getAoServer();
            } catch(NoSuchElementException exc) {
                throw new ParameterException(paramName, exc);
            }
        } catch(NumberFormatException exc) {
            aoServer = connector.getAoServers().filterUnique(AOServer.COLUMN_HOSTNAME, parseParameterDomainName(paramName, false, arg));
        }
        if(aoServer==null) throw new ParameterException(paramName, ApplicationResources.accessor.getMessage("AOSH.parseParameterAoServer.aoServerNotFound", arg));
        return aoServer;
    }

    public BusinessAdministrator parseParameterBusinessAdministrator(String paramName, boolean nullable, String arg) throws RemoteException, ParameterException {
        // If allows null, convert empty string to null
        if(nullable && arg.length()==0) return null;
        try {
            return connector.getBusinessAdministrators().get(parseParameterUserId(paramName, false, arg));
        } catch(NoSuchElementException exc) {
            throw new ParameterException(paramName, exc);
        }
    }

    public static DomainName parseParameterDomainName(String paramName, boolean nullable, String arg) throws ParameterException {
        // If allows null, convert empty string to null
        if(nullable && arg.length()==0) return null;
        else {
            try {
                return DomainName.valueOf(arg);
            } catch(ValidationException validationException) {
                throw new ParameterException(paramName, validationException);
            }
        }
    }

    /**
     * Parses a linux account as either an integer pkey or as username@server
     */
    public LinuxAccount parseParameterLinuxAccount(String paramName, boolean nullable, String arg) throws RemoteException, ParameterException {
        // If allows null, convert empty string to null
        if(nullable && arg.length()==0) return null;
        try {
            int atPos = arg.lastIndexOf('@');
            if(atPos==-1) {
                return connector.getLinuxAccounts().get(Integer.valueOf(arg));
            } else {
                return parseParameterAoServer(paramName, false, arg.substring(atPos+1)).getLinuxAccount(parseParameterUserId(paramName, false, arg.substring(0, atPos)));
            }
        } catch(NoSuchElementException exc) {
            throw new ParameterException(paramName, exc);
        }
    }

    /**
     * Parses a MySQL server as either an integer pkey or as server/name
     */
    public MySQLServer parseParameterMySQLServer(String paramName, boolean nullable, String arg) throws RemoteException, ParameterException {
        // If allows null, convert empty string to null
        if(nullable && arg.length()==0) return null;
        try {
            int slashPos = arg.lastIndexOf('/');
            if(slashPos==-1) {
                return connector.getMysqlServers().get(Integer.valueOf(arg));
            } else {
                return parseParameterAoServer(paramName, false, arg.substring(0, slashPos)).getMysqlServer(parseParameterMySQLServerName(paramName, false, arg.substring(slashPos+1)));
            }
        } catch(NoSuchElementException exc) {
            throw new ParameterException(paramName, exc);
        }
    }

    public static MySQLServerName parseParameterMySQLServerName(String paramName, boolean nullable, String arg) throws ParameterException {
        // If allows null, convert empty string to null
        if(nullable && arg.length()==0) return null;
        else {
            try {
                return MySQLServerName.valueOf(arg);
            } catch(ValidationException validationException) {
                throw new ParameterException(paramName, validationException);
            }
        }
    }

    /**
     * Parses a MySQL user as either an integer pkey or as username@mysqlServer
     */
    public MySQLUser parseParameterMySQLUser(String paramName, boolean nullable, String arg) throws RemoteException, ParameterException {
        // If allows null, convert empty string to null
        if(nullable && arg.length()==0) return null;
        try {
            int atPos = arg.lastIndexOf('@');
            if(atPos==-1) {
                return connector.getMysqlUsers().get(Integer.valueOf(arg));
            } else {
                return parseParameterMySQLServer(paramName, false, arg.substring(atPos+1)).getMysqlUser(parseParameterMySQLUserId(paramName, false, arg.substring(0, atPos)));
            }
        } catch(NoSuchElementException exc) {
            throw new ParameterException(paramName, exc);
        }
    }

    public static MySQLUserId parseParameterMySQLUserId(String paramName, boolean nullable, String arg) throws ParameterException {
        // If allows null, convert empty string to null
        if(nullable && arg.length()==0) return null;
        else {
            try {
                return MySQLUserId.valueOf(arg);
            } catch(ValidationException validationException) {
                throw new ParameterException(paramName, validationException);
            }
        }
    }

    public static String parseParameterString(String paramName, boolean nullable, String arg) {
        // If allows null, convert empty string to null
        if(nullable && arg.length()==0) return null;
        else return arg;
    }

    /**
     * Parses a Postgres server as either an integer pkey or as server/name
     */
    public PostgresServer parseParameterPostgresServer(String paramName, boolean nullable, String arg) throws RemoteException, ParameterException {
        // If allows null, convert empty string to null
        if(nullable && arg.length()==0) return null;
        try {
            int slashPos = arg.lastIndexOf('/');
            if(slashPos==-1) {
                return connector.getPostgresServers().get(Integer.valueOf(arg));
            } else {
                return parseParameterAoServer(paramName, false, arg.substring(0, slashPos)).getPostgresServer(parseParameterPostgresServerName(paramName, false, arg.substring(slashPos+1)));
            }
        } catch(NoSuchElementException exc) {
            throw new ParameterException(paramName, exc);
        }
    }

    public static PostgresServerName parseParameterPostgresServerName(String paramName, boolean nullable, String arg) throws ParameterException {
        // If allows null, convert empty string to null
        if(nullable && arg.length()==0) return null;
        else {
            try {
                return PostgresServerName.valueOf(arg);
            } catch(ValidationException validationException) {
                throw new ParameterException(paramName, validationException);
            }
        }
    }

    /**
     * Parses a Postgres user as either an integer pkey or as username@postgresServer
     */
    public PostgresUser parseParameterPostgresUser(String paramName, boolean nullable, String arg) throws RemoteException, ParameterException {
        // If allows null, convert empty string to null
        if(nullable && arg.length()==0) return null;
        try {
            int atPos = arg.lastIndexOf('@');
            if(atPos==-1) {
                return connector.getPostgresUsers().get(Integer.valueOf(arg));
            } else {
                return parseParameterPostgresServer(paramName, false, arg.substring(atPos+1)).getPostgresUser(parseParameterPostgresUserId(paramName, false, arg.substring(0, atPos)));
            }
        } catch(NoSuchElementException exc) {
            throw new ParameterException(paramName, exc);
        }
    }

    public static PostgresUserId parseParameterPostgresUserId(String paramName, boolean nullable, String arg) throws ParameterException {
        // If allows null, convert empty string to null
        if(nullable && arg.length()==0) return null;
        else {
            try {
                return PostgresUserId.valueOf(arg);
            } catch(ValidationException validationException) {
                throw new ParameterException(paramName, validationException);
            }
        }
    }

    public static UserId parseParameterUserId(String paramName, boolean nullable, String arg) throws ParameterException {
        // If allows null, convert empty string to null
        if(nullable && arg.length()==0) return null;
        else {
            try {
                return UserId.valueOf(arg);
            } catch(ValidationException validationException) {
                throw new ParameterException(paramName, validationException);
            }
        }
    }

    public Username parseParameterUsername(String paramName, boolean nullable, String arg) throws RemoteException, ParameterException {
        // If allows null, convert empty string to null
        if(nullable && arg.length()==0) return null;
        try {
            return connector.getUsernames().get(parseParameterUserId(paramName, false, arg));
        } catch(NoSuchElementException exc) {
            throw new ParameterException(paramName, exc);
        }
    }
    // </editor-fold>

    /* TODO
    private void invalidate(String[] args) throws IllegalArgumentException, IOException {
        if(checkRangeParamCount(AOSHCommand.INVALIDATE, args, 1, 2, err)) {
            String tableName=args[1];
            SchemaTableTable schemaTableTable=connector.getSchemaTables();
            // Find the table ID
            int tableID=-1;
            for(int d=0;d<numTables;d++) {
                if(schemaTableTable.get(d).getName().equals(tableName)) {
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
    }*/

    public static void main(String[] args) {
        Reader in = new InputStreamReader(System.in);
        TerminalWriter out = new TerminalWriter(new OutputStreamWriter(System.out));
        TerminalWriter err = new TerminalWriter(new OutputStreamWriter(System.err));
        try {
            UserId username = getConfigUsername(in, err);
            String password = getConfigPassword(in, err);
            AOServConnector<?,?> connector = AOServClientConfiguration.getConnector(username, password);
            AOSH aosh = new AOSH(connector, in, out, err, args);
            aosh.run();
            if(aosh.isInteractive()) {
                out.println();
                out.flush();
            }
            System.exit(0);
        } catch(LoginException exc) {
            err.println(ApplicationResources.accessor.getMessage("AOSH.main.LoginException", exc.getMessage()));
            if(STACK_TRACES) exc.printStackTrace(err);
            err.flush();
            System.exit(1);
        } catch(IOException exc) {
            err.println(ApplicationResources.accessor.getMessage("AOSH.main.IOException", exc.getMessage()));
            if(STACK_TRACES) exc.printStackTrace(err);
            err.flush();
            System.exit(2);
        }
    }

    public static UserId getConfigUsername(Reader in, TerminalWriter err) throws IOException {
        UserId username;
        try {
            username=AOServClientConfiguration.getUsername();
        } catch(com.aoindustries.aoserv.client.validator.ValidationException exc) {
            IOException ioErr = new IOException(exc.getMessage());
            ioErr.initCause(exc);
            throw ioErr;
        }
        while(username==null) {
            // Prompt for the username
            err.print(ApplicationResources.accessor.getMessage("AOSH.prompt.username"));
            err.flush();
            String id = readLine(in);
            try {
                username = UserId.valueOf(id);
            } catch(com.aoindustries.aoserv.client.validator.ValidationException exc) {
                err.print(ApplicationResources.accessor.getMessage("AOSH.prompt.username.invalid", exc.getLocalizedMessage()));
                err.flush();
            }
        }
        return username;
    }

    public static String getConfigPassword(Reader in, TerminalWriter err) throws IOException {
        String password=AOServClientConfiguration.getPassword();
        if(password==null || password.length()==0) {
            err.print(ApplicationResources.accessor.getMessage("AOSH.prompt.password"));
            err.flush();
            //try {
                return new String(System.console().readPassword());
                //Object console = System.class.getMethod("console").invoke(null);
                //return new String((char[])console.getClass().getMethod("readPassword").invoke(console));
            //} catch(Exception exception) {
                // Not Java 1.6
            //}
            // Prompt for the password
            //password=readLine(in);
        }
        return password;
    }

    @Override
    protected ShellInterpreter newShellInterpreter(Reader in, TerminalWriter out, TerminalWriter err, String[] args) {
        return new AOSH(connector, in, out, err, args);
    }

    /* TODO
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

    private void ping(String[] args) throws IOException {
        if(checkParamCount(AOSHCommand.PING, args, 0, err)) {
            out.print(connector.getSimpleAOClient().ping());
            out.println(" ms");
            out.flush();
        }
    }
    */
    public static String readLine(Reader in) throws IOException {
        StringBuilder SB=new StringBuilder();
        readLine(in, SB);
        return SB.toString();
    }
    
    public static void readLine(Reader in, StringBuilder SB) throws IOException {
        SB.setLength(0);
        int ch;
        while((ch=in.read())!=-1 && ch!='\n') if(ch!='\r') SB.append((char)ch);
    }

    private static int longestCommand;
    static {
        for(BuiltIn buildIn : BuiltIn.values) {
            int len = buildIn.name().length();
            if(len>longestCommand) longestCommand = len;
        }
        for(CommandName commandName : CommandName.values) {
            int len = commandName.name().length();
            if(len>longestCommand) longestCommand = len;
        }
    }

    private static String getNoHTML(String S) throws IOException {
        StringBuilder SB = new StringBuilder(S.length());
        printNoHTML(SB, S);
        return SB.toString();
    }

    private static void printNoHTML(Appendable out, String S) throws IOException {
        if(S==null) out.append("null");
        else {
            int len=S.length();
            int pos=0;
            while(pos<len) {
                char ch;
                if((ch=S.charAt(pos++))=='<') {
                    if((ch=S.charAt(pos++))=='/') {
                        if(
                            (ch=S.charAt(pos++))=='b'
                            || ch=='B'
                        ) out.append('"');
                        else if(
                            ch=='i'
                            || ch=='I'
                        ) out.append('>');
                        pos++;
                    } else {
                        if(
                            ch=='b'
                            || ch=='B'
                        ) out.append('"');
                        else if(
                            ch=='i'
                            || ch=='I'
                        ) out.append('<');
                        pos++;
                    }
                } else out.append(ch);
            }
        }
    }

    private void printBuiltInHelp(TerminalWriter out, boolean showSyntax) throws IOException {
        out.boldOn();
        out.print(ApplicationResources.accessor.getMessage("AOSH.help.header.builtInCommands"));
        out.attributesOff();
        out.println();
        for(BuiltIn builtIn : BuiltIn.values) {
            out.print("    ");
            String command = builtIn.name();
            out.print(command);
            int space=Math.max(1, longestCommand+3-command.length());
            for(int d=0;d<space;d++) out.print(d>0 && d<(space-1)?'.':' ');
            // Print the description without the HTML tags
            printNoHTML(out, showSyntax ? builtIn.getSyntax() : builtIn.getShortDesc());
            out.println();
        }
    }

    private static String getSyntax(CommandName commandName) {
        StringBuilder syntax = new StringBuilder();
        Constructor<? extends AOServCommand<?>> constructor = AOServCommand.getCommandConstructor(commandName);
        // Convert the parameters
        Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
        for(int c=0; c<parameterAnnotations.length; c++) {
            // Find the @Param annotation
            Param paramAnnotation = null;
            for(Annotation anno : parameterAnnotations[c]) {
                if(anno instanceof Param) {
                    paramAnnotation = (Param)anno;
                    break;
                }
            }
            if(c>0) syntax.append(' ');
            String paramSyntax = paramAnnotation.syntax();
            if("[[AUTO_SYNTAX]]".equals(paramSyntax)) {
                if(paramAnnotation.nullable()) paramSyntax = "{\"\"|<i>" + paramAnnotation.name() + "</i>}";
                else paramSyntax = "<i>" + paramAnnotation.name() + "</i>";
            }
            syntax.append(paramSyntax);
        }
        return syntax.toString();
    }

    private void printHelpList(TerminalWriter out, String title, ServiceName service, boolean showSyntax, boolean println) throws IOException {
        List<CommandName> commands = new ArrayList<CommandName>();
        for(CommandName command : CommandName.values) {
            if(command.getServiceName()==service) commands.add(command);
        }
        if(!commands.isEmpty()) {
            if(println) out.println();
            out.boldOn();
            out.print(title);
            out.attributesOff();
            out.println();
            for(CommandName commandName : commands) {
                String command=commandName.name();
                out.print("    ");
                out.print(command);
                int space=Math.max(1, longestCommand+3-command.length());
                for(int d=0;d<space;d++) out.print(d>0 && d<(space-1)?'.':' ');
                // Print the description without the HTML tags
                if(showSyntax) {
                    printNoHTML(out, getSyntax(commandName));
                } else {
                    printNoHTML(out, commandName.getShortDesc());
                }
                out.println();
            }
        }
    }

    private void printAllHelp(TerminalWriter out, boolean showSyntax) throws IOException {
        // Print the built-in commands first
        printBuiltInHelp(out, showSyntax);
        // Print the global commands
        printHelpList(out, ApplicationResources.accessor.getMessage("AOSH.help.header.globalCommands"), null, showSyntax, true);

        // Print table commands
        for(ServiceName service : ServiceName.values) {
            printHelpList(out, ApplicationResources.accessor.getMessage("AOSH.help.header.command", service.toString()), service, showSyntax, true);
        }
        out.flush();
    }

    private void printCommandHelp(TerminalWriter out, String command, String shortDesc, String syntax, boolean syntaxOnly, Set<AOServPermission.Permission> permissions) throws IOException {
        if(!syntaxOnly) {
            out.boldOn();
            out.print(ApplicationResources.accessor.getMessage("AOSH.help.header.name"));
            out.attributesOff();
            out.println();
            out.println(ApplicationResources.accessor.getMessage("AOSH.help.command.description", command, getNoHTML(shortDesc)));
            out.println();
        }
        out.boldOn();
        out.print(ApplicationResources.accessor.getMessage("AOSH.help.header.syntax"));
        out.attributesOff();
        out.println();
        if(syntax.length()==0) {
            out.println(ApplicationResources.accessor.getMessage("AOSH.help.command.syntax.noParams", command));
        } else {
            out.println(ApplicationResources.accessor.getMessage("AOSH.help.command.syntax.params", command, getNoHTML(syntax)));
        }
        if(permissions!=null) {
            out.println();
            out.boldOn();
            out.print(ApplicationResources.accessor.getMessage("AOSH.help.header.permissions"));
            out.attributesOff();
            out.println();
            if(permissions.isEmpty()) out.println(ApplicationResources.accessor.getMessage("AOSH.help.command.permissions.none", command));
            else {
                for(AOServPermission.Permission permission : permissions) {
                    out.println(ApplicationResources.accessor.getMessage("AOSH.help.command.permissions.granted", permission.toString()));
                    // TODO: Add bold, red indicator if doesn't have the permission
                }
            }
        }
    }

    private void printCommandHelp(TerminalWriter out, CommandName commandName, boolean syntaxOnly) throws IOException {
        printCommandHelp(out, commandName.name(), commandName.getShortDesc(), getSyntax(commandName), syntaxOnly, commandName.getPermissions());
    }

    private void printCommandHelp(TerminalWriter out, BuiltIn builtIn, boolean syntaxOnly) throws IOException {
        printCommandHelp(out, builtIn.name(), builtIn.getShortDesc(), builtIn.getSyntax(), syntaxOnly, null);
    }

    private void help(String[] args) throws IOException {
        int argCount=args.length;
        if(argCount==1) {
            printAllHelp(out, false);
            out.flush();
        } else if(argCount==2) {
            if("syntax".equals(args[1])) {
                printAllHelp(out, true);
                out.flush();
            } else if("builtin".equals(args[1])) {
                printBuiltInHelp(out, false);
                out.flush();
            } else if("global".equals(args[1])) {
                printHelpList(out, ApplicationResources.accessor.getMessage("AOSH.help.header.globalCommands"), null, false, false);
                out.flush();
            } else {
                try {
                    ServiceName service = ServiceName.valueOf(args[1]);
                    printHelpList(out, ApplicationResources.accessor.getMessage("AOSH.help.header.command", service.toString()), service, false, false);
                    out.flush();
                } catch(IllegalArgumentException exc) {
                    try {
                        BuiltIn builtIn = BuiltIn.valueOf(args[1]);
                        printCommandHelp(out, builtIn, false);
                        out.flush();
                    } catch(IllegalArgumentException exc2) {
                        try {
                            CommandName commandName = CommandName.valueOf(args[1]);
                            printCommandHelp(out, commandName, false);
                            out.flush();
                        } catch(IllegalArgumentException exc3) {
                            err.println(ApplicationResources.accessor.getMessage("AOSH.invalidParameterValue", BuiltIn.help, "object", args[1]));
                            if(STACK_TRACES) exc3.printStackTrace(err);
                            err.flush();
                        }
                    }
                }
            }
        } else if(argCount==3) {
            if("builtin".equals(args[1])) {
                if("syntax".equals(args[2])) {
                    printBuiltInHelp(out, true);
                    out.flush();
                } else {
                    err.println(ApplicationResources.accessor.getMessage("AOSH.invalidParameterValue", BuiltIn.help, "syntax", args[2]));
                    err.flush();
                }
            } else if("global".equals(args[1])) {
                if("syntax".equals(args[2])) {
                    printHelpList(out, ApplicationResources.accessor.getMessage("AOSH.help.header.globalCommands"), null, true, false);
                    out.flush();
                } else {
                    err.println(ApplicationResources.accessor.getMessage("AOSH.invalidParameterValue", BuiltIn.help, "syntax", args[2]));
                    err.flush();
                }
            } else {
                try {
                    ServiceName service = ServiceName.valueOf(args[1]);
                    if("syntax".equals(args[2])) {
                        printHelpList(out, ApplicationResources.accessor.getMessage("AOSH.help.header.command", service.toString()), service, true, false);
                        out.flush();
                    } else {
                        err.println(ApplicationResources.accessor.getMessage("AOSH.invalidParameterValue", BuiltIn.help, "syntax", args[2]));
                        err.flush();
                    }
                } catch(IllegalArgumentException exc) {
                    try {
                        BuiltIn builtIn = BuiltIn.valueOf(args[1]);
                        printCommandHelp(out, builtIn, true);
                        out.flush();
                    } catch(IllegalArgumentException exc2) {
                        try {
                            CommandName commandName = CommandName.valueOf(args[1]);
                            if("syntax".equals(args[2])) {
                                printCommandHelp(out, commandName, true);
                                out.flush();
                            } else {
                                err.println(ApplicationResources.accessor.getMessage("AOSH.invalidParameterValue", BuiltIn.help, "syntax", args[2]));
                                err.flush();
                            }
                        } catch(IllegalArgumentException exc3) {
                            err.println(ApplicationResources.accessor.getMessage("AOSH.invalidParameterValue", BuiltIn.help, "object", args[1]));
                            if(STACK_TRACES) exc3.printStackTrace(err);
                            err.flush();
                        }
                    }
                }
            }
        } else {
            err.println(ApplicationResources.accessor.getMessage("AOSH.tooManyParameters", BuiltIn.help));
            err.flush();
        }
    }

    private void repeat(String[] args) throws IOException {
        int argCount=args.length;
        if(argCount>2) {
            try {
                int count=Integer.parseInt(args[1]);
                if(count>=0) {
                    String[] newArgs=new String[argCount-2];
                    System.arraycopy(args, 2, newArgs, 0, argCount-2);

                    for(int c=0;c<count;c++) handleCommand(newArgs);
                } else {
                    err.println(ApplicationResources.accessor.getMessage("AOSH.invalidLoopCount", BuiltIn.repeat, count));
                    err.flush();
                }
            } catch(NumberFormatException nfe) {
                err.println(ApplicationResources.accessor.getMessage("AOSH.invalidLoopCount", BuiltIn.repeat, args[1]));
                if(STACK_TRACES) nfe.printStackTrace(err);
                err.flush();
            }
        } else {
            err.println(ApplicationResources.accessor.getMessage("AOSH.notEnoughParameters", BuiltIn.repeat));
            err.flush();
        }
    }

    private void sleep(String[] args) throws RemoteException {
        if(args.length>1) {
            try {
                for(int c=1;c<args.length;c++) {
                    try {
                        long time=1000*Integer.parseInt(args[c]);
                        if(time<0) {
                            err.println(ApplicationResources.accessor.getMessage("AOSH.invalidTimeInterval", BuiltIn.sleep, args[c]));
                            err.flush();
                        } else {
                            Thread.sleep(time);
                        }
                    } catch(NumberFormatException nfe) {
                        err.println(ApplicationResources.accessor.getMessage("AOSH.invalidTimeInterval", BuiltIn.sleep, args[c]));
                        if(STACK_TRACES) nfe.printStackTrace(err);
                        err.flush();
                    }
                }
            } catch(InterruptedException ie) {
                status="Interrupted";
                err.println(ApplicationResources.accessor.getMessage("AOSH.interrupted", BuiltIn.sleep));
                if(STACK_TRACES) ie.printStackTrace(err);
                err.flush();
            }
        } else {
            err.println(ApplicationResources.accessor.getMessage("AOSH.notEnoughParameters", BuiltIn.sleep));
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
            try {
                new AOSH(
                    connector.getFactory().getConnector(
                        connector.getLocale(),
                        UserId.valueOf(args[1]),
                        connector.getAuthenticateAs(),
                        connector.getPassword(),
                        null
                    ),
                    in,
                    out,
                    err,
                    newArgs
                ).run();
            } catch(com.aoindustries.aoserv.client.validator.ValidationException exc) {
                err.println(ApplicationResources.accessor.getMessage("AOSH.ValidationException", BuiltIn.su, exc.getMessage()));
                if(STACK_TRACES) exc.printStackTrace(err);
                err.flush();
            } catch(LoginException exc) {
                err.println(ApplicationResources.accessor.getMessage("AOSH.LoginException", BuiltIn.su, exc.getMessage()));
                if(STACK_TRACES) exc.printStackTrace(err);
                err.flush();
            }
        } else {
            err.println(ApplicationResources.accessor.getMessage("AOSH.notEnoughParameters", BuiltIn.su));
            err.flush();
        }
    }

    private void time(String[] args) throws IOException {
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
            err.println(ApplicationResources.accessor.getMessage("AOSH.notEnoughParameters", BuiltIn.time));
            err.flush();
        }
    }

    private void whoami(String[] args) throws IOException {
        if(args.length==1) {
            out.println(connector.getConnectAs());
            out.flush();
        } else {
            err.println(ApplicationResources.accessor.getMessage("AOSH.notEnoughParameters", BuiltIn.whoami));
            err.flush();
        }
    }
}
