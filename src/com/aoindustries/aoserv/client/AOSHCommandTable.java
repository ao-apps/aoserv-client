package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  AOSHCommand
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class AOSHCommandTable extends GlobalTableStringKey<AOSHCommand> implements Map<String,AOSHCommand> {

    private static final String GLOBAL_COMMANDS="[[GLOBAL]]";

    private final Map<String,List<AOSHCommand>> tableCommands=new HashMap<String,List<AOSHCommand>>();

    AOSHCommandTable(AOServConnector connector) {
	super(connector, AOSHCommand.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(AOSHCommand.COLUMN_COMMAND_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    List<AOSHCommand> getAOSHCommands(SchemaTable table) {
        Profiler.startProfile(Profiler.UNKNOWN, AOSHCommandTable.class, "getAOSHCommands(SchemaTable)", null);
        try {
	    synchronized(this) {
		// Table might be null
		String name=table==null?GLOBAL_COMMANDS:table.name;
		List<AOSHCommand> list=tableCommands.get(name);
		if(list!=null) return list;

		List<AOSHCommand> cached=getRows();
		List<AOSHCommand> matches=new ArrayList<AOSHCommand>();
		int size=cached.size();
		for(int c=0;c<size;c++) {
		    AOSHCommand command=cached.get(c);
		    if(
                        table==null
                        ?command.table_name==null
                        :name.equals(command.table_name)
                    ) matches.add(command);
		}
                matches=Collections.unmodifiableList(matches);
		tableCommands.put(name, matches);
		return matches;
	    }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public List<AOSHCommand> getGlobalAOSHCommands() {
        return getAOSHCommands(null);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.AOSH_COMMANDS;
    }

    /**
     * Avoid repeated array copies.
     */
    private static final int numTables = SchemaTable.TableID.values().length;

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
        Profiler.startProfile(Profiler.UNKNOWN, AOSHCommandTable.class, "handleCommand(String[],InputStream,TerminalWriter,TerminalWriter,boolean)", null);
        try {
            String command=args[0];
            if(command.equalsIgnoreCase(AOSHCommand.HELP) || command.equals("?")) {
                int argCount=args.length;
                if(argCount==1) {
                    SchemaTableTable schemaTableTable=connector.schemaTables;
                    for(int c=-1;c<numTables;c++) {
                        SchemaTable schemaTable=c==-1?null:schemaTableTable.get(c);
                        String title=c==-1?"Global Commands:":(schemaTable.getDisplay()+':');
                        List<AOSHCommand> commands=c==-1?getGlobalAOSHCommands():schemaTable.getAOSHCommands(connector);
                        try {
                            printHelpList(out, title, commands, true, c>=0);
                        } catch(IOException err2) {
                            throw new WrappedException(err2);
                        }
                    }
                    out.flush();
                } else if(argCount==2) {
                    if(args[1].equals("syntax")) {
                        SchemaTableTable schemaTableTable=connector.schemaTables;
                        for(int c=-1;c<numTables;c++) {
                            SchemaTable schemaTable=c==-1?null:schemaTableTable.get(c);
                            String title=c==-1?"Global Commands:":(schemaTable.getDisplay()+':');
                            List<AOSHCommand> commands=c==-1?getGlobalAOSHCommands():schemaTable.getAOSHCommands(connector);
                            try {
                                printHelpList(out, title, commands, false, c>=0);
                            } catch(IOException err2) {
                                throw new WrappedException(err2);
                            }
                        }
                        out.flush();
                    } else {
                        // Try to find the command
                        AOSHCommand aoshCom=get(args[1].toLowerCase());
                        if(aoshCom!=null) {
                            try {
                                aoshCom.printCommandHelp(out);
                            } catch(IOException err2) {
                                throw new WrappedException(err2);
                            }
                            out.flush();
                        } else {
                            err.print("aosh: help: help on command not found: ");
                            err.println(args[1]);
                            err.flush();
                        }
                    }
                } else {
                    // Get help for one specific task here
                    err.println("aosh: help: too many parameters");
                    err.flush();
                }
                return true;
            }
            return false;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    private void printHelpList(TerminalWriter out, String title, List<AOSHCommand> commands, boolean shortOrSchema, boolean println) throws IOException {
        Profiler.startProfile(Profiler.IO, AOSHCommandTable.class, "printHelpList(TerminalWriter,String,List<AOSHCommand>,boolean,boolean)", null);
        try {
            int len=commands.size();
            if(len>0) {
                if(println) out.println();
                out.boldOn();
                out.println(title);
                out.attributesOff();
                for(int c=0;c<len;c++) {
                    AOSHCommand aoshCom=commands.get(c);
                    String command=aoshCom.getCommand();
                    out.print("    ");
                    out.print(command);
                    int space=Math.max(1, 40-command.length());
                    for(int d=0;d<space;d++) out.print(d>0 && d<(space-1)?'.':' ');
                    // Print the description without the HTML tags
                    String desc=shortOrSchema?aoshCom.getShortDesc():aoshCom.getSyntax();
                    AOSHCommand.printNoHTML(out, desc);
                    out.println();
                }
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void clearCache() {
        Profiler.startProfile(Profiler.FAST, AOSHCommandTable.class, "clearCache()", null);
        try {
            super.clearCache();
            synchronized(this) {
                tableCommands.clear();
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public AOSHCommand get(Object command) {
        return getUniqueRow(AOSHCommand.COLUMN_COMMAND, command);
    }
}