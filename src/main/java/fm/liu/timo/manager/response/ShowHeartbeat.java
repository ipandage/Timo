/*
 * Copyright 1999-2012 Alibaba Group.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package fm.liu.timo.manager.response;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import fm.liu.timo.TimoConfig;
import fm.liu.timo.TimoServer;
import fm.liu.timo.config.Fields;
import fm.liu.timo.manager.ManagerConnection;
import fm.liu.timo.mysql.PacketUtil;
import fm.liu.timo.net.backend.Node;
import fm.liu.timo.net.mysql.EOFPacket;
import fm.liu.timo.net.mysql.FieldPacket;
import fm.liu.timo.net.mysql.ResultSetHeaderPacket;
import fm.liu.timo.net.mysql.RowDataPacket;

/**
 * @author xianmao.hexm
 */
public class ShowHeartbeat {

    private static final int                   FIELD_COUNT = 11;
    private static final ResultSetHeaderPacket header      = PacketUtil.getHeader(FIELD_COUNT);
    private static final FieldPacket[]         fields      = new FieldPacket[FIELD_COUNT];
    private static final EOFPacket             eof         = new EOFPacket();

    static {
        int i = 0;
        byte packetId = 0;
        header.packetId = ++packetId;

        fields[i] = PacketUtil.getField("NAME", Fields.FIELD_TYPE_VAR_STRING);
        fields[i++].packetId = ++packetId;

        fields[i] = PacketUtil.getField("TYPE", Fields.FIELD_TYPE_VAR_STRING);
        fields[i++].packetId = ++packetId;

        fields[i] = PacketUtil.getField("HOST", Fields.FIELD_TYPE_VAR_STRING);
        fields[i++].packetId = ++packetId;

        fields[i] = PacketUtil.getField("PORT", Fields.FIELD_TYPE_LONG);
        fields[i++].packetId = ++packetId;

        fields[i] = PacketUtil.getField("RS_CODE", Fields.FIELD_TYPE_LONG);
        fields[i++].packetId = ++packetId;

        fields[i] = PacketUtil.getField("RETRY", Fields.FIELD_TYPE_LONG);
        fields[i++].packetId = ++packetId;

        fields[i] = PacketUtil.getField("STATUS", Fields.FIELD_TYPE_VAR_STRING);
        fields[i++].packetId = ++packetId;

        fields[i] = PacketUtil.getField("TIMEOUT", Fields.FIELD_TYPE_LONGLONG);
        fields[i++].packetId = ++packetId;

        fields[i] = PacketUtil.getField("EXECUTE_TIME", Fields.FIELD_TYPE_VAR_STRING);
        fields[i++].packetId = ++packetId;

        fields[i] = PacketUtil.getField("LAST_ACTIVE_TIME", Fields.FIELD_TYPE_DATETIME);
        fields[i++].packetId = ++packetId;

        fields[i] = PacketUtil.getField("STOP", Fields.FIELD_TYPE_VAR_STRING);
        fields[i++].packetId = ++packetId;

        eof.packetId = ++packetId;
    }

    public static void response(ManagerConnection c) {
        ByteBuffer buffer = c.allocate();

        // write header
        buffer = header.write(buffer, c);

        // write fields
        for (FieldPacket field : fields) {
            buffer = field.write(buffer, c);
        }

        // write eof
        buffer = eof.write(buffer, c);

        // write rows
        byte packetId = eof.packetId;
        for (RowDataPacket row : getRows()) {
            row.packetId = ++packetId;
            buffer = row.write(buffer, c);
        }

        // write last eof
        EOFPacket lastEof = new EOFPacket();
        lastEof.packetId = ++packetId;
        buffer = lastEof.write(buffer, c);

        // post write
        c.write(buffer);
    }

    private static List<RowDataPacket> getRows() {
        List<RowDataPacket> list = new LinkedList<RowDataPacket>();
        TimoConfig conf = TimoServer.getInstance().getConfig();

        // data nodes
        Map<Integer, Node> dataNodes = conf.getNodes();
        List<Integer> dataNodeKeys = new ArrayList<Integer>(dataNodes.size());
        dataNodeKeys.addAll(dataNodes.keySet());
        Collections.sort(dataNodeKeys);
        for (Integer key : dataNodeKeys) {
            Node node = dataNodes.get(key);
            if (node != null) {
                // MySQLHeartbeat hb = node.getHeartbeat();
                RowDataPacket row = new RowDataPacket(FIELD_COUNT);
                row.add((node.getID() + "").getBytes());
                row.add("MYSQL".getBytes());
                // if (hb != null) {
                // row.add(hb.getSource().getConfig().getHost().getBytes());
                // row.add(IntegerUtil.toBytes(hb.getSource().getConfig().getPort()));
                // row.add(IntegerUtil.toBytes(hb.getStatus()));
                // row.add(IntegerUtil.toBytes(hb.getErrorCount()));
                // row.add(hb.isChecking() ? "checking".getBytes() : "idle".getBytes());
                // row.add(LongUtil.toBytes(hb.getTimeout()));
                // row.add(hb.getRecorder().get().getBytes());
                // String lat = hb.getLastActiveTime();
                // row.add(lat == null ? null : lat.getBytes());
                // row.add(hb.isStop() ? "true".getBytes() : "false".getBytes());
                // } else {
                row.add(null);
                row.add(null);
                row.add(null);
                row.add(null);
                row.add(null);
                row.add(null);
                row.add(null);
                row.add(null);
                row.add(null);
                // }
                list.add(row);
            }
        }
        return list;
    }

}