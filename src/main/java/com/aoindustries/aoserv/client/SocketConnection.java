/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2015, 2016, 2017  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.lang.ObjectUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

/**
 * A <code>SocketConnection</code> is a single, persistent, plaintext
 * connection to the server.
 *
 * @see  TCPConnector
 *
 * @author  AO Industries, Inc.
 */
final public class SocketConnection extends AOServConnection {

	/**
	 * Keeps a flag of the connection status.
	 */
	private boolean isClosed=true;

	/**
	 * The socket to the server.
	 */
	private final Socket socket;

	/**
	 * The output stream to the server.
	 */
	private final CompressedDataOutputStream out;

	/**
	 * The input stream from the server.
	 */
	private final CompressedDataInputStream in;

	/**
	 * The first command sequence for this connection.
	 */
	//private final long startSeq;

	/**
	 * The next command sequence that will be sent.
	 */
	private final AtomicLong seq;

	SocketConnection(TCPConnector connector) throws InterruptedIOException, IOException {
		super(connector);
		socket = connector.getSocket();
		boolean successful = false;
		try {
			isClosed = false;
			out = new CompressedDataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			in = new CompressedDataInputStream(new BufferedInputStream(socket.getInputStream()));

			out.writeUTF(AOServProtocol.Version.CURRENT_VERSION.getVersion());
			out.writeNullUTF(ObjectUtils.toString(connector.daemonServer));
			out.writeUTF(connector.connectAs.toString());
			out.writeUTF(connector.authenticateAs.toString());
			out.writeUTF(connector.password);
			boolean hadConnectorId;
			long connectorId;
			synchronized(connector.idLock) {
				connectorId = connector.id;
				if(connectorId == -1) {
					// Hold the idLock when need a connector ID
					out.writeLong(-1);
					out.flush();
					if(Thread.interrupted()) throw new InterruptedIOException();
					if(!in.readBoolean()) throw new IOException(in.readUTF());
					connectorId = in.readLong();
					connector.id = connectorId;
					hadConnectorId = false;
				} else {
					hadConnectorId = true;
				}
			}
			if(hadConnectorId) {
				// Finish connecting outside the idLock when already have a connector ID
				out.writeLong(connectorId);
				out.flush();
				if(Thread.interrupted()) throw new InterruptedIOException();
				if(!in.readBoolean()) throw new IOException(in.readUTF());
			}
			final long startSeq = in.readLong();
			this.seq = new AtomicLong(startSeq);
			successful = true;
		} finally {
			if(!successful) close();
		}
	}

	@Override
	void close() {
		if(in != null) {
			try {
				in.close();
			} catch(IOException err) {
				connector.logger.log(Level.WARNING, null, err);
			}
		}
		if(out != null) {
			try {
				out.writeCompressedInt(AOServProtocol.CommandID.QUIT.ordinal());
				out.flush();
			} catch(SocketException err) {
				// Normal when the other side has terminated the connection
			} catch(IOException err) {
				connector.logger.log(Level.WARNING, null, err);
			}
			try {
				out.close();
			} catch(SocketException err) {
				// Normal when the socket is already closed
			} catch(IOException err) {
				connector.logger.log(Level.WARNING, null, err);
			}
		}
		if(socket != null) {
			try {
				socket.close();
			} catch(IOException err) {
				connector.logger.log(Level.WARNING, null, err);
			}
		}
		isClosed=true;
	}

	InetAddress getLocalInetAddress() throws IOException {
		return socket.getLocalAddress();
	}

	private long currentSeq;

	@Override
	CompressedDataOutputStream getRequestOut(AOServProtocol.CommandID commID) throws IOException {
		currentSeq = seq.getAndIncrement();
		out.writeLong(currentSeq);
		out.writeCompressedInt(commID.ordinal());
		return out;
	}

	@Override
	CompressedDataInputStream getResponseIn() throws IOException {
		// Verify server sends matching sequence
		long serverSeq = in.readLong();
		if(serverSeq != currentSeq) throw new IOException("Sequence mismatch: " + serverSeq + " != " + currentSeq);
		return in;
	}

	boolean isClosed() {
		return isClosed;
	}
}
