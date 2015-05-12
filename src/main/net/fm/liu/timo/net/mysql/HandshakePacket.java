/*
 * Copyright (c) 2013, OpenCloudDB/HotDB and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software;Designed and Developed mainly by many Chinese 
 * opensource volunteers. you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Any questions about this component can be directed to it's project Web address 
 * https://code.google.com/p/opencloudb/.
 *
 */
package fm.liu.timo.net.mysql;

import java.nio.ByteBuffer;

import fm.liu.timo.mysql.BufferUtil;
import fm.liu.timo.mysql.MySQLMessage;

/**
 * From server to client during initial handshake.
 * 
 * <pre>
 * Bytes                        Name
 * -----                        ----
 * 1                            protocol_version
 * n (Null-Terminated String)   server_version
 * 4                            thread_id
 * 8                            scramble_buff
 * 1                            (filler) always 0x00
 * 2                            server_capabilities
 * 1                            server_language
 * 2                            server_status
 * 13                           (filler) always 0x00 ...
 * 13                           rest of scramble_buff (4.1)
 * 
 * @see http://forge.mysql.com/wiki/MySQL_Internals_ClientServer_Protocol#Handshake_Initialization_Packet
 * </pre>
 * 
 * @author hotdb
 */
public class HandshakePacket extends MySQLServerPacket {
	private static final byte[] FILLER_13 = new byte[] { 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0 };

	public byte protocolVersion;
	public byte[] serverVersion;
	public long threadId;
	public byte[] seed;
	public int serverCapabilities;
	public byte serverCharsetIndex;
	public int serverStatus;
	public byte[] restOfScrambleBuff;

	@Override
	protected void readBody(MySQLMessage mm){
		protocolVersion = mm.read();
		serverVersion = mm.readBytesWithNull();
		threadId = mm.readUB4();
		seed = mm.readBytesWithNull();
		serverCapabilities = mm.readUB2();
		serverCharsetIndex = mm.read();
		serverStatus = mm.readUB2();
		mm.move(13);
		restOfScrambleBuff = mm.readBytesWithNull();
	}

	@Override
	public int calcPacketSize() {
		int size = 1;
		size += serverVersion.length;// n
		size += 5;// 1+4
		size += seed.length;// 8
		size += 19;// 1+2+1+2+13
		size += restOfScrambleBuff.length;// 12
		size += 1;// 1
		return size;
	}

	@Override
	protected String getPacketInfo() {
		return "MySQL Handshake Packet";
	}

	@Override
	protected void writeBody(ByteBuffer buffer) {
		buffer.put(protocolVersion);
		BufferUtil.writeWithNull(buffer, serverVersion);
		BufferUtil.writeUB4(buffer, threadId);
		BufferUtil.writeWithNull(buffer, seed);
		BufferUtil.writeUB2(buffer, serverCapabilities);
		buffer.put(serverCharsetIndex);
		BufferUtil.writeUB2(buffer, serverStatus);
		buffer.put(FILLER_13);
		// buffer.position(buffer.position() + 13);
		BufferUtil.writeWithNull(buffer, restOfScrambleBuff);
	}

}