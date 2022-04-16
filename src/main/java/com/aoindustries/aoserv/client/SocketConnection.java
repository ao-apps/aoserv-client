/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.AutoCloseables;
import com.aoapps.lang.Throwables;
import com.aoapps.security.Identifier;
import com.aoapps.security.SecurityStreamables;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A <code>SocketConnection</code> is a single, persistent, plaintext
 * connection to the server.
 *
 * @see  TCPConnector
 *
 * @author  AO Industries, Inc.
 */
public final class SocketConnection extends AOServConnection {

	/**
	 * Keeps a flag of the connection status.
	 */
	private final AtomicBoolean isClosed = new AtomicBoolean(true);

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

	@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
	SocketConnection(TCPConnector connector) throws InterruptedIOException, IOException {
		super(connector);
		socket = connector.getSocket();
		try {
			this.isClosed.set(false);
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
					SecurityStreamables.writeNullIdentifier(null, out);
					out.flush();
					if(Thread.currentThread().isInterrupted()) throw new InterruptedIOException();
					if(!in.readBoolean()) throw new IOException(in.readUTF());
					connectorId = SecurityStreamables.readIdentifier(in);
					connector.id = connectorId;
					hadConnectorId = false;
				} else {
					hadConnectorId = true;
				}
			}
			if(hadConnectorId) {
				// Finish connecting outside the idLock when already have a connector ID
				SecurityStreamables.writeNullIdentifier(connectorId, out);
				out.flush();
				if(Thread.currentThread().isInterrupted()) throw new InterruptedIOException();
				if(!in.readBoolean()) throw new IOException(in.readUTF());
			}
			final long startSeq = in.readLong();
			this.seq = new AtomicLong(startSeq);
		} catch(Throwable t) {
			throw Throwables.wrap(abort(t), IOException.class, IOException::new);
		}
	}

	/**
	 * Forces connection closed, adding any new throwables to {@code t0} via
	 * {@link Throwables#addSuppressed(java.lang.Throwable, java.lang.Throwable)}.
	 */
	@Override
	@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
	Throwable abort(Throwable t0) {
		if(!isClosed.getAndSet(true)) {
			try {
				out.writeCompressedInt(AoservProtocol.CommandID.QUIT.ordinal());
				out.flush();
			} catch(Throwable t) {
				t0 = Throwables.addSuppressed(t0, t);
			}
			t0 = AutoCloseables.closeAndCatch(t0, in, out, socket);
		}
		return t0;
	}

	InetAddress getLocalInetAddress() {
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

	/**
	 * Determines if this connection has been closed.
	 */
	boolean isClosed() {
		return isClosed.get();
	}
}
