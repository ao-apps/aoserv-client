/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoindustries.aoserv.client.aosh;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.hodgepodge.util.ShellInterpreter;
import com.aoapps.lang.SysExits;
import com.aoapps.lang.exception.ConfigurationException;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.DomainName;
import com.aoapps.net.Email;
import com.aoapps.net.HostAddress;
import com.aoapps.net.InetAddress;
import com.aoapps.net.Port;
import com.aoapps.sql.SQLUtility;
import com.aoindustries.aoserv.client.AOServClientConfiguration;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServTable;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.linux.Group;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.linux.User.Gecos;
import com.aoindustries.aoserv.client.net.FirewallZone;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.schema.TableTable;
import com.aoindustries.aoserv.client.schema.Type;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayReader;
import java.io.Console;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <code>AOSH</code> is a command interpreter and scripting language
 * based on the Bourne shell.  It may be used to control the
 * <code>AOServ Client</code> utilities.
 *
 * @author  AO Industries, Inc.
 */
public final class AOSH extends ShellInterpreter {

	private static final Logger logger = Logger.getLogger(AOSH.class.getName());

	private static final Reader nullInput=new CharArrayReader(new char[0]);

	private final AOServConnector connector;

	public AOSH(AOServConnector connector, Reader in, TerminalWriter out, TerminalWriter err) {
		super(in, out, err);
		this.connector=connector;
	}

	public AOSH(AOServConnector connector, Reader in, TerminalWriter out, TerminalWriter err, String ... args) {
		super(in, out, err, args);
		this.connector=connector;
	}

	public static boolean checkMinParamCount(String function, String[] args, int minCount, PrintWriter err) {
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

	public static boolean checkParamCount(String function, String[] args, int requiredCount, PrintWriter err) {
		return checkRangeParamCount(function, args, requiredCount, requiredCount, err);
	}

	public static boolean checkRangeParamCount(String function, String[] args, int minCount, int maxCount, PrintWriter err) {
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

	public static String executeCommand(AOServConnector connector, String[] args) throws IOException, SQLException {
		StringWriter buff = new StringWriter();
		TerminalWriter out=new TerminalWriter(buff);
		out.setEnabled(false);
		AOSH sh=new AOSH(connector, nullInput, out, out);
		sh.handleCommand(args);
		out.flush();
		return buff.toString();
	}

	@Override
	protected String getName() {
		return "aosh";
	}

	@Override
	protected String getPrompt() throws SQLException, IOException {
		return '['+connector.getCurrentAdministrator().toString()+'@'+connector.getHostname()+"]$ ";
	}

	/** Avoid repeated array copies. */
	private static final int numTables = Table.TableID.values().length;

	/**
	 * Processes one command and returns.
	 *
	 * @param  args  the command and argments to process
	 *
	 * @return  <code>true</code> if more commands should be processed
	 */
	@Override
	public boolean handleCommand(String[] args) throws IOException, SQLException {
		int argCount=args.length;
		if(argCount>0) {
			String command=args[0];
			if(Command.EXIT.equalsIgnoreCase(command)) {
				if(argCount!=1) {
					err.println("aosh: "+Command.EXIT+": too many parameters");
					err.flush();
				} else return false;
			} else {
				if(Command.CLEAR.equalsIgnoreCase(command)) clear(args);
				else if(Command.ECHO.equalsIgnoreCase(command)) echo(args);
				else if(Command.INVALIDATE.equalsIgnoreCase(command)) invalidate(args);
				else if(Command.JOBS.equalsIgnoreCase(command)) jobs(args);
				else if(Command.PING.equalsIgnoreCase(command)) ping(args);
				else if(Command.REPEAT.equalsIgnoreCase(command)) repeat(args);
				else if(Command.SLEEP.equalsIgnoreCase(command)) sleep(args);
				else if(Command.SU.equalsIgnoreCase(command)) su(args);
				else if(Command.TIME.equalsIgnoreCase(command)) time(args);
				else if(Command.WHOAMI.equalsIgnoreCase(command)) whoami(args);
				else {
					boolean done=false;
					CommandTable commandTable = connector.getAosh().getCommand();
					Command aoshCommand = commandTable.get(command);
					if(aoshCommand == null) {
						// Case-insensitive search
						for(Command com : commandTable.getRows()) {
							if(com.getCommand().equalsIgnoreCase(command)) {
								aoshCommand = com;
								break;
							}
						}
					}
					if(aoshCommand!=null) {
						AOServTable<?, ?> table = aoshCommand.getTable(connector).getAOServTable(connector);
						done=table.handleCommand(args, in, out, err, isInteractive());
						if(!done) throw new RuntimeException("AOSHCommand found, but command not processed.  command='"+command+"', table='"+table.getTableName()+'\'');
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
		if(checkRangeParamCount(Command.INVALIDATE, args, 1, 2, err)) {
			String tableName=args[1];
			TableTable schemaTableTable=connector.getSchema().getTable();
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
				err.print("aosh: "+Command.INVALIDATE+": unable to find table: ");
				err.println(tableName);
				err.flush();
			}
		}
	}

	@SuppressWarnings({"UseOfSystemOutOrSystemErr", "UseSpecificCatch", "BroadCatchBlock", "TooBroadCatch"})
	public static void main(String[] args) {
		TerminalWriter out=new TerminalWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
		TerminalWriter err=System.out==System.err ? out : new TerminalWriter(new BufferedWriter(new OutputStreamWriter(System.err)));
		try {
			User.Name username = getConfigUsername(System.in, err);
			String password = getConfigPassword(System.in, err);
			AOServConnector connector = AOServConnector.getConnector(username, password);
			AOSH aosh=new AOSH(connector, new BufferedReader(new InputStreamReader(System.in)), out, err, args);
			aosh.run();
			if(aosh.isInteractive()) {
				out.println();
				out.flush();
			}
		} catch(Throwable t) {
			logger.log(Level.FINE, null, t);
			err.println("aosh: unable to connect: " + t.getMessage());
			err.flush();
			System.exit(SysExits.getSysExit(t)); // TODO: Review other main methods
		}
	}

	public static User.Name getConfigUsername(InputStream in, TerminalWriter err) throws ConfigurationException, IOException {
		User.Name username = AOServClientConfiguration.getUsername();
		if(username == null) {
			try {
				// Prompt for the username
				String prompt = "Username: ";
				Console console = System.console();
				if(console == null) {
					err.print(prompt);
					err.flush();
					username = User.Name.valueOf(readLine(in));
					err.flush();
				} else {
					username = User.Name.valueOf(console.readLine(prompt));
					if(username == null) throw new EOFException("End-of-file reading username");
				}
			} catch(ValidationException e) {
				throw new IOException(e);
			}
		}
		return username;
	}

	public static String getConfigPassword(InputStream in, TerminalWriter err) throws ConfigurationException, IOException {
		String password = AOServClientConfiguration.getPassword();
		if(password == null || password.isEmpty()) {
			// Prompt for the password
			String prompt = "Password: ";
			Console console = System.console();
			if(console == null) {
				err.print(prompt);
				err.flush();
				password = readLine(in);
				err.flush();
			} else {
				char[] pwchars = console.readPassword(prompt);
				if(pwchars == null) throw new EOFException("End-of-file reading password");
				password = new String(pwchars);
			}
		}
		return password;
	}

	@Override
	protected AOSH newShellInterpreter(Reader in, TerminalWriter out, TerminalWriter err, String[] args) {
		return new AOSH(connector, in, out, err, args);
	}

	public static Account.Name parseAccountingCode(String s, String field) {
		try {
			return Account.Name.valueOf(s);
		} catch(ValidationException err) {
			throw new IllegalArgumentException("Invalid argument for accounting (" + field + "): " + s, err);
		}
	}

	public static BigDecimal parseBigDecimal(String s, String field) {
		try {
			return new BigDecimal(s);
		} catch(NumberFormatException err) {
			throw new IllegalArgumentException("Invalid argument for big_decimal (" + field + "): " + s, err);
		}
	}

	public static boolean parseBoolean(String s, String field) {
		if(
			s.equalsIgnoreCase("true")
			|| s.equalsIgnoreCase("t")
			|| s.equalsIgnoreCase("yes")
			|| s.equalsIgnoreCase("y")
			|| s.equalsIgnoreCase("vang")
			|| s.equalsIgnoreCase("da")
			|| s.equalsIgnoreCase("si")
			|| s.equalsIgnoreCase("oui")
			|| s.equalsIgnoreCase("ja")
			|| s.equalsIgnoreCase("nam")
		) return true;
		else if(
			s.equalsIgnoreCase("false")
			|| s.equalsIgnoreCase("f")
			|| s.equalsIgnoreCase("no")
			|| s.equalsIgnoreCase("n")
			|| s.equalsIgnoreCase("khong")
			|| s.equalsIgnoreCase("nyet")
			|| s.equalsIgnoreCase("non")
			|| s.equalsIgnoreCase("nien")
			|| s.equalsIgnoreCase("la")
		) return false;
		else throw new IllegalArgumentException("Invalid argument for boolean (" + field + "): " + s);
	}

	/**
	 * @see  SQLUtility#parseDate(java.lang.String, java.util.TimeZone)
	 * @see  Type#DATE_TIME_ZONE
	 */
	public static Date parseDate(String s, String field) {
		try {
			return SQLUtility.parseDate(s, Type.DATE_TIME_ZONE);
		} catch(NumberFormatException err) {
			throw new IllegalArgumentException("Invalid argument for date (" + field + "): " + s, err);
		}
	}

	public static FirewallZone.Name parseFirewalldZoneName(String s, String field) {
		try {
			return FirewallZone.Name.valueOf(s);
		} catch(ValidationException err) {
			throw new IllegalArgumentException("Invalid argument for firewalld zone (" + field + "): " + s, err);
		}
	}

	public static HostAddress parseHostAddress(String s, String field) {
		try {
			return HostAddress.valueOf(s);
		} catch(ValidationException err) {
			throw new IllegalArgumentException("Invalid argument for host address (" + field + "): " + s, err);
		}
	}

	public static int parseInt(String s, String field) {
		try {
			return Integer.parseInt(s);
		} catch(NumberFormatException err) {
			throw new IllegalArgumentException("Invalid argument for int (" + field + "): " + s, err);
		}
	}

	public static float parseFloat(String s, String field) {
		try {
			return Float.parseFloat(s);
		} catch(NumberFormatException err) {
			throw new IllegalArgumentException("Invalid argument for float (" + field + "): " + s, err);
		}
	}

	public static long parseLong(String s, String field) {
		try {
			return Long.parseLong(s);
		} catch(NumberFormatException err) {
			throw new IllegalArgumentException("Invalid argument for long (" + field + "): " + s, err);
		}
	}

	/**
	 * @see  SQLUtility#parseDecimal3(java.lang.String)
	 */
	public static int parseDecimal3(String s, String field) {
		try {
			return SQLUtility.parseDecimal3(s);
		} catch(NumberFormatException err) {
			throw new IllegalArgumentException("Invalid argument for decimal (" + field + "): " + s, err);
		}
	}

	public static int parseOctalInt(String s, String field) {
		try {
			return Integer.parseInt(s, 8);
		} catch(NumberFormatException err) {
			throw new IllegalArgumentException("Invalid argument for octal int (" + field + "): " + s, err);
		}
	}

	public static long parseOctalLong(String s, String field) {
		try {
			return Long.parseLong(s, 8);
		} catch(NumberFormatException err) {
			throw new IllegalArgumentException("Invalid argument for octal long (" + field + "): " + s, err);
		}
	}

	/**
	 * @see  SQLUtility#parseDecimal2(java.lang.String)
	 */
	public static int parseDecimal2(String s, String field) {
		try {
			return SQLUtility.parseDecimal2(s);
		} catch(NumberFormatException err) {
			throw new IllegalArgumentException("Invalid argument for decimal (" + field + "): " + s, err);
		}
	}

	public static short parseShort(String s, String field) {
		try {
			return Short.parseShort(s);
		} catch(NumberFormatException err) {
			throw new IllegalArgumentException("Invalid argument for short (" + field + "): " + s, err);
		}
	}

	public static DomainName parseDomainName(String s, String field) {
		try {
			return DomainName.valueOf(s);
		} catch(ValidationException err) {
			throw new IllegalArgumentException("Invalid argument for domain_name (" + field + "): " + s, err);
		}
	}

	public static Email parseEmail(String s, String field) {
		try {
			return Email.valueOf(s);
		} catch(ValidationException err) {
			throw new IllegalArgumentException("Invalid argument for email address (" + field + "): " + s, err);
		}
	}

	public static Gecos parseGecos(String s, String field) {
		try {
			return Gecos.valueOf(s);
		} catch(ValidationException err) {
			throw new IllegalArgumentException("Invalid argument for gecos (" + field + "): " + s, err);
		}
	}

	public static Group.Name parseGroupName(String s, String field) {
		try {
			return Group.Name.valueOf(s);
		} catch(ValidationException err) {
			throw new IllegalArgumentException("Invalid argument for group (" + field + "): " + s, err);
		}
	}

	public static InetAddress parseInetAddress(String s, String field) {
		try {
			return InetAddress.valueOf(s);
		} catch(ValidationException err) {
			throw new IllegalArgumentException("Invalid argument for ip_address (" + field + "): " + s, err);
		}
	}

	public static com.aoindustries.aoserv.client.linux.User.Name parseLinuxUserName(String s, String field) {
		try {
			return com.aoindustries.aoserv.client.linux.User.Name.valueOf(s);
		} catch(ValidationException err) {
			throw new IllegalArgumentException("Invalid argument for Linux username (" + field + "): " + s, err);
		}
	}

	public static com.aoindustries.aoserv.client.mysql.Database.Name parseMySQLDatabaseName(String s, String field) {
		try {
			return com.aoindustries.aoserv.client.mysql.Database.Name.valueOf(s);
		} catch(ValidationException err) {
			throw new IllegalArgumentException("Invalid argument for MySQL database name (" + field + "): " + s, err);
		}
	}

	public static com.aoindustries.aoserv.client.mysql.Server.Name parseMySQLServerName(String s, String field) {
		try {
			return com.aoindustries.aoserv.client.mysql.Server.Name.valueOf(s);
		} catch(ValidationException err) {
			throw new IllegalArgumentException("Invalid argument for MySQL server name (" + field + "): " + s, err);
		}
	}

	public static com.aoindustries.aoserv.client.mysql.User.Name parseMySQLUserName(String s, String field) {
		try {
			return com.aoindustries.aoserv.client.mysql.User.Name.valueOf(s);
		} catch(ValidationException err) {
			throw new IllegalArgumentException("Invalid argument for MySQL username (" + field + "): " + s, err);
		}
	}

	public static Port parsePort(
		String port,
		String portField,
		String protocol,
		String protocolField
	) {
		int portInt = parseInt(port, portField);
		com.aoapps.net.Protocol protocolObj;
		try {
			protocolObj = com.aoapps.net.Protocol.valueOf(protocol.toUpperCase(Locale.ROOT));
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid argument for protocol ("+protocolField+"): "+protocol, e);
		}
		try {
			return Port.valueOf(
				portInt,
				protocolObj
			);
		} catch(ValidationException err) {
			throw new IllegalArgumentException("Invalid argument for port ("+portField+"): "+port, err);
		}
	}

	public static com.aoindustries.aoserv.client.postgresql.Database.Name parsePostgresDatabaseName(String s, String field) {
		try {
			return com.aoindustries.aoserv.client.postgresql.Database.Name.valueOf(s);
		} catch(ValidationException err) {
			throw new IllegalArgumentException("Invalid argument for PostgreSQL database name (" + field + "): " + s, err);
		}
	}

	public static com.aoindustries.aoserv.client.postgresql.Server.Name parsePostgresServerName(String s, String field) {
		try {
			return com.aoindustries.aoserv.client.postgresql.Server.Name.valueOf(s);
		} catch(ValidationException err) {
			throw new IllegalArgumentException("Invalid argument for PostgreSQL server name (" + field + "): " + s, err);
		}
	}

	public static com.aoindustries.aoserv.client.postgresql.User.Name parsePostgresUserName(String s, String field) {
		try {
			return com.aoindustries.aoserv.client.postgresql.User.Name.valueOf(s);
		} catch(ValidationException err) {
			throw new IllegalArgumentException("Invalid argument for PostgreSQL username (" + field + "): " + s, err);
		}
	}

	public static PosixPath parseUnixPath(String s, String field) {
		try {
			return PosixPath.valueOf(s);
		} catch(ValidationException err) {
			throw new IllegalArgumentException("Invalid argument for POSIX path (" + field + "): " + s, err);
		}
	}

	public static User.Name parseUserName(String s, String field) {
		try {
			return User.Name.valueOf(s);
		} catch(ValidationException err) {
			throw new IllegalArgumentException("Invalid argument for username (" + field + "): " + s, err);
		}
	}

	private void ping(String[] args) throws IOException, SQLException {
		if(checkParamCount(Command.PING, args, 0, err)) {
			out.print(connector.getSimpleAOClient().ping());
			out.println(" ms");
			out.flush();
		}
	}

	public static String readLine(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		readLine(in, sb);
		return sb.toString();
	}

	public static void readLine(InputStream in, StringBuilder sb) throws IOException {
		sb.setLength(0);
		int ch;
		while((ch = in.read()) != -1 && ch != '\n') {
			if(ch != '\r') sb.append((char)ch);
		}
	}

	private void repeat(String[] args) throws IOException, SQLException {
		int argCount = args.length;
		if(argCount > 2) {
			try {
				int count = Integer.parseInt(args[1]);
				if(count >= 0) {
					String[] newArgs = new String[argCount-2];
					System.arraycopy(args, 2, newArgs, 0, argCount - 2);

					for(int c = 0; c < count; c++) {
						handleCommand(newArgs);
					}
				} else {
					err.print("aosh: " + Command.REPEAT + ": invalid loop count: ");
					err.println(count);
					err.flush();
				}
			} catch(NumberFormatException nfe) {
				err.print("aosh: " + Command.REPEAT + ": invalid loop count: ");
				err.println(args[1]);
				err.flush();
			}
		} else {
			err.println("aosh: " + Command.REPEAT + ": not enough parameters");
			err.flush();
		}
	}

	@SuppressWarnings("SleepWhileInLoop")
	private void sleep(String[] args) {
		if(args.length > 1) {
			try {
				for(int c = 1; c < args.length; c++) {
					try {
						long time = 1000L * Integer.parseInt(args[c]);
						if(time < 0) {
							err.println("aosh: " + Command.SLEEP + ": invalid time interval: " + args[c]);
							err.flush();
						} else {
							Thread.sleep(time);
						}
					} catch(NumberFormatException nfe) {
						err.println("aosh: " + Command.SLEEP + ": invalid time interval: " + args[c]);
						err.flush();
					}
				}
			} catch(InterruptedException ie) {
				status = "Interrupted";
				err.println("aosh: " + Command.SLEEP + ": interrupted");
				err.flush();
				// Restore the interrupted status
				Thread.currentThread().interrupt();
			}
		} else {
			err.println("aosh: " + Command.SLEEP + ": too few arguments");
			err.flush();
		}
	}

	private void su(String[] args) throws IOException {
		int argCount=args.length;
		if(argCount>=2) {
			try {
				String[] newArgs=new String[argCount+(isInteractive()?0:-1)];
				int pos=0;
				if(isInteractive()) newArgs[pos++]="-i";
				newArgs[pos++]="--";
				System.arraycopy(args, 2, newArgs, pos, argCount-2);
				new AOSH(
					connector.switchUsers(User.Name.valueOf(args[1])
					),
					in,
					out,
					err,
					newArgs
				).run();
			} catch(ValidationException e) {
				err.println("aosh: "+Command.SU+": " + e.getResult().toString());
				err.flush();
			}
		} else {
			err.println("aosh: "+Command.SU+": not enough parameters");
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
				out.print(SQLUtility.formatDecimal3(secs));
				out.println('s');
				out.flush();
			}
		} else {
			err.println("aosh: "+Command.TIME+": not enough parameters");
			err.flush();
		}
	}

	private void whoami(String[] args) throws SQLException, IOException {
		if(args.length==1) {
			out.println(connector.getCurrentAdministrator().getUsername().getUsername());
			out.flush();
		} else {
			err.println("aosh: "+Command.WHOAMI+": too many parameters");
			err.flush();
		}
	}
}
