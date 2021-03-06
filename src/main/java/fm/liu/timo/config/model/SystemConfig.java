/*
 * Copyright 2015 Liu Huanting.
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
package fm.liu.timo.config.model;

import fm.liu.timo.config.Isolations;

/**
 * @author Liu Huanting 2015年5月9日
 * 系统级参数配置信息
 */
public class SystemConfig {
    private static final int     DEFAULT_PORT                       = 8066;
    private static final int     DEFAULT_MANAGER_PORT               = 9066;
    private static final String  DEFAULT_CHARSET                    = "UTF-8";
    private static final int     DEFAULT_PROCESSORS                 =
            Runtime.getRuntime().availableProcessors();
    private static final long    DEFAULT_IDLE_TIMEOUT               = 8 * 3600 * 1000L;
    private static final long    DEFAULT_PROCESSOR_CHECK_PERIOD     = 10 * 1000L;
    private static final long    DEFAULT_DATANODE_IDLE_CHECK_PERIOD = 60 * 1000L;
    private static final int     DEFAULT_PARSER_COMMENT_VERSION     = 50148;
    private static final int     DEFAULT_HEARTBEAT_PERIOD           = 1000;
    private static final int     DEFAULT_HEARTBEAT_TIMEOUT          = 300;
    private static final int     DEFAULT_QUERY_TIMEOUT              = 300;
    private static final int     DEFAULT_SQL_RECORD_COUNT           = 10;
    private static final boolean DEFAULT_ENABLE_XA                  = false;
    private static final boolean DEFAUKT_AUTOINCREMENT              = false;

    private int     serverPort;
    private int     managerPort;
    private String  charset;
    private int     processors;
    private int     processorHandler;
    private int     processorExecutor;
    private int     initExecutor;
    private int     timerExecutor;
    private int     managerExecutor;
    private long    idleTimeout;
    private long    processorCheckPeriod;
    private long    dataNodeIdleCheckPeriod;
    private int     txIsolation;
    private int     parserCommentVersion;
    private String  url;
    private String  username;
    private String  password;
    private int     heartbeatPeriod;
    private int     heartbeatTimeout;
    private int     queryTimeout;
    private int     sqlRecordCount;
    private boolean enableXA;
    private boolean autoIncrement;

    public SystemConfig() {
        this.serverPort = DEFAULT_PORT;
        this.managerPort = DEFAULT_MANAGER_PORT;
        this.charset = DEFAULT_CHARSET;
        this.processors = DEFAULT_PROCESSORS;
        this.processorHandler = DEFAULT_PROCESSORS;
        this.processorExecutor = DEFAULT_PROCESSORS;
        this.managerExecutor = DEFAULT_PROCESSORS;
        this.timerExecutor = DEFAULT_PROCESSORS;
        this.initExecutor = DEFAULT_PROCESSORS;
        this.idleTimeout = DEFAULT_IDLE_TIMEOUT;
        this.processorCheckPeriod = DEFAULT_PROCESSOR_CHECK_PERIOD;
        this.dataNodeIdleCheckPeriod = DEFAULT_DATANODE_IDLE_CHECK_PERIOD;
        this.txIsolation = Isolations.REPEATED_READ;
        this.parserCommentVersion = DEFAULT_PARSER_COMMENT_VERSION;
        this.heartbeatPeriod = DEFAULT_HEARTBEAT_PERIOD;
        this.heartbeatTimeout = DEFAULT_HEARTBEAT_TIMEOUT;
        this.queryTimeout = DEFAULT_QUERY_TIMEOUT;
        this.sqlRecordCount = DEFAULT_SQL_RECORD_COUNT;
        this.enableXA = DEFAULT_ENABLE_XA;
        this.autoIncrement = DEFAUKT_AUTOINCREMENT;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getManagerPort() {
        return managerPort;
    }

    public void setManagerPort(int managerPort) {
        this.managerPort = managerPort;
    }

    public int getProcessors() {
        return processors;
    }

    public void setProcessors(int processors) {
        this.processors = processors;
    }

    public int getProcessorHandler() {
        return processorHandler;
    }

    public void setProcessorHandler(int processorExecutor) {
        this.processorHandler = processorExecutor;
    }

    public int getProcessorExecutor() {
        return processorExecutor;
    }

    public void setProcessorExecutor(int processorExecutor) {
        this.processorExecutor = processorExecutor;
    }

    public int getManagerExecutor() {
        return managerExecutor;
    }

    public void setManagerExecutor(int managerExecutor) {
        this.managerExecutor = managerExecutor;
    }

    public int getTimerExecutor() {
        return timerExecutor;
    }

    public void setTimerExecutor(int timerExecutor) {
        this.timerExecutor = timerExecutor;
    }

    public int getInitExecutor() {
        return initExecutor;
    }

    public void setInitExecutor(int initExecutor) {
        this.initExecutor = initExecutor;
    }

    public long getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public long getProcessorCheckPeriod() {
        return processorCheckPeriod;
    }

    public void setProcessorCheckPeriod(long processorCheckPeriod) {
        this.processorCheckPeriod = processorCheckPeriod;
    }

    public long getDataNodeIdleCheckPeriod() {
        return dataNodeIdleCheckPeriod;
    }

    public void setDataNodeIdleCheckPeriod(long dataNodeIdleCheckPeriod) {
        this.dataNodeIdleCheckPeriod = dataNodeIdleCheckPeriod;
    }

    public int getTxIsolation() {
        return txIsolation;
    }

    public void setTxIsolation(int txIsolation) {
        this.txIsolation = txIsolation;
    }

    public int getParserCommentVersion() {
        return parserCommentVersion;
    }

    public void setParserCommentVersion(int parserCommentVersion) {
        this.parserCommentVersion = parserCommentVersion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getHeartbeatPeriod() {
        return heartbeatPeriod;
    }

    public void setHeartbeatPeriod(int heartbeatPeriod) {
        this.heartbeatPeriod = heartbeatPeriod;
    }

    public int getHeartbeatTimeout() {
        return heartbeatTimeout;
    }

    public void setHeartbeatTimeout(int heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }

    public int getQueryTimeout() {
        return queryTimeout * 1000;
    }

    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public int getSqlRecordCount() {
        return sqlRecordCount;
    }

    public void setSqlRecordCount(int sqlRecordCount) {
        this.sqlRecordCount = sqlRecordCount;
    }

    public boolean isEnableXA() {
        return enableXA;
    }

    public void setEnableXA(boolean enableXA) {
        this.enableXA = enableXA;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

}
