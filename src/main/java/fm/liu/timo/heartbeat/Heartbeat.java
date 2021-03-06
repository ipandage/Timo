package fm.liu.timo.heartbeat;

import java.util.concurrent.atomic.AtomicInteger;
import org.pmw.tinylog.Logger;
import fm.liu.timo.backend.Node;
import fm.liu.timo.backend.Source;
import fm.liu.timo.net.connection.BackendConnection;
import fm.liu.timo.server.session.handler.HeartbeatHandler;
import fm.liu.timo.server.session.handler.HeartbeatInitHandler;
import fm.liu.timo.server.session.handler.ResultHandler;
import fm.liu.timo.util.TimeUtil;

public class Heartbeat {

    private static final int           RETRY   = 3;
    private static final String        SQL     = "REPLACE INTO timo_heartbeat SET id=1";
    private final Source               source;
    private final int                  period;
    private volatile HeartbeatStatus   status;
    private volatile BackendConnection connection;
    private volatile long              lastActiveTime;
    private AtomicInteger              counter = new AtomicInteger(0);
    private Node                       node;
    private HeartbeatHandler           handler;

    public enum HeartbeatStatus {
        CHECKING, OK, PAUSED, STOPED
    }

    public Heartbeat(Source source, int period) {
        this.source = source;
        this.period = period;
        this.handler = new HeartbeatHandler(this);
    }

    public void heartbeat(Node node) {
        this.node = node;
        long now = TimeUtil.currentTimeMillis();
        if ((now - lastActiveTime) < period) {
            return;
        }
        if (this.status == HeartbeatStatus.CHECKING) {
            if (counter.incrementAndGet() > RETRY) {
                handover();
            } else {
                this.updateStatus(HeartbeatStatus.OK);
                this.connection.close("heartbeat timeout");
                this.connection = null;
                connect();
            }
        } else {
            check();
        }
    }

    private void check() {
        if (this.connection == null) {
            if (counter.incrementAndGet() > RETRY) {
                handover();
            } else {
                connect();
            }
        } else {
            this.updateStatus(HeartbeatStatus.CHECKING);
            this.connection.query(SQL, handler);
        }
    }

    private void handover() {
        this.stop();
        if (this.connection != null) {
            this.connection.close("heartbeat error");
            this.connection = null;
        }
        if (source != node.getSource()) {
            source.getConfig().ban();
            source.clear("clear datasource due to heartbeat error");
            return;
        }
        if (node.handover(false)) {
            Logger.info("datanode {} handover datasource{} to '{}' due to heartbeat error!",
                    node.getID(), source.getConfig(), node.getSource().getConfig());
        } else {
            Logger.error("datanode {} handover datasource:{} failed!", node.getID(),
                    source.getConfig());
        }
    }

    private void connect() {
        ResultHandler initHandler = new HeartbeatInitHandler(this);
        try {
            source.query("SET sql_log_bin=0", initHandler);
        } catch (Throwable e) {
            counter.incrementAndGet();
            Logger.error("datasource {} error due to {}", source.getConfig().getID(), e);
            return;
        }
    }

    public void pause() {
        this.updateStatus(HeartbeatStatus.PAUSED);
    }

    public void updateStatus(HeartbeatStatus status) {
        this.status = status;
    }

    public void updateConnection(BackendConnection connection) {
        this.connection = connection;
    }

    public Source getSource() {
        return source;
    }

    public boolean isStoped() {
        return status == HeartbeatStatus.STOPED;
    }

    public void update() {
        this.lastActiveTime = TimeUtil.currentTimeMillis();
        this.counter.set(0);
        this.status = HeartbeatStatus.OK;
    }

    public int getErrorCount() {
        return counter.intValue();
    }

    public HeartbeatStatus getStatus() {
        return status;
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

    public void stop() {
        this.status = HeartbeatStatus.STOPED;
    }
}
