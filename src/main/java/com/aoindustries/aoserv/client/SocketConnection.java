/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2015, 2016, 2017, 2018, 2019, 2020  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.io.stream.StreamableInput;
import com.aoindustries.io.stream.StreamableOutput;
import com.aoindustries.lang.AutoCloseables;
import com.aoindustries.lang.Throwables;
import com.aoindustries.security.Identifier;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;
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
	private final StreamableOutput out;

	/**
	 * The input stream from the server.
	 */
	private final StreamableInput in;

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
			out = new StreamableOutput(new BufferedOutputStream(socket.getOutputStream()));
			in = new StreamableInput(new BufferedInputStream(socket.getInputStream()));

			out.writeUTF(AoservProtocol.Version.CURRENT_VERSION.getVersion());
			out.writeNullUTF(Objects.toString(connector.daemonServer, null));
			out.writeUTF(connector.connectAs.toString());
			out.writeUTF(connector.authenticateAs.toString());
			out.writeUTF(connector.password);
			boolean hadConnectorId;
			Identifier connectorId;
			synchronized(connector.idLock) {
				connectorId = connector.id;
				if(connectorId == null) {
					// Hold the idLock when need a connector ID
					out.writeNullIdentifier(null);
					out.flush();
					if(Thread.interrupted()) throw new InterruptedIOException();
					if(!in.readBoolean()) throw new IOException(in.readUTF());
					connectorId = in.readIdentifier();
					connector.id = connectorId;
					hadConnectorId = false;
				} else {
					hadConnectorId = true;
				}
			}
			if(hadConnectorId) {
				// Finish connecting outside the idLock when already have a connector ID
				out.writeNullIdentifier(connectorId);
				out.flush();
				if(Thread.interrupted()) throw new InterruptedIOException();
				if(!in.readBoolean()) throw new IOException(in.readUTF());
			}
			final long startSeq = in.readLong();
			this.seq = new AtomicLong(startSeq);
			successful = true;
		} finally {
			if(!successful) abort();
		}
	}

	@Override
	@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
	void abort() {
		Throwable t1 = null;
		try {
			out.writeCompressedInt(AoservProtocol.CommandID.QUIT.ordinal());
			out.flush();
		} catch(Throwable t) {
			t1 = Throwables.addSuppressed(t1, t);
		}
		t1 = AutoCloseables.close(t1, in, out, socket);
		if(t1 != null) {
			connector.getLogger().log(
				// Normal when the socket is already closed
				(t1 instanceof SocketException)
					? Level.FINE
					: Level.WARNING,
				null,
				t1
			);
		}
		isClosed = true;
	}

	InetAddress getLocalInetAddress() throws IOException {
		return socket.getLocalAddress();
	}

	private long currentSeq;

	@Override
	StreamableOutput getRequestOut(AoservProtocol.CommandID commID) throws IOException {
		currentSeq = seq.getAndIncrement();
		out.writeLong(currentSeq);
		out.writeCompressedInt(commID.ordinal());
		return out;
	}

	@Override
	StreamableInput getResponseIn() throws IOException {
		// Verify server sends matching sequence
		long serverSeq = in.readLong();
		if(serverSeq != currentSeq) throw new IOException("Sequence mismatch: " + serverSeq + " != " + currentSeq);
		return in;
	}

	boolean isClosed() {
		return isClosed;
	}
}
