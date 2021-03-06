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
package fm.liu.timo.config.loader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import fm.liu.timo.config.model.Database;
import fm.liu.timo.config.model.Datanode;
import fm.liu.timo.config.model.Datasource;
import fm.liu.timo.config.model.Function;
import fm.liu.timo.config.model.Rule;
import fm.liu.timo.config.model.Table;
import fm.liu.timo.config.model.User;
import fm.liu.timo.route.function.AutoFunction;
import fm.liu.timo.route.function.HashFunction;
import fm.liu.timo.route.function.MatchFunction;
import fm.liu.timo.route.function.Range;
import fm.liu.timo.route.function.RangeFunction;

/**
 * @author Liu Huanting 2015年5月9日
 * 配置库配置加载器
 */
public class ServerConfigLoader {
    private final String                           url;
    private final String                           username;
    private final String                           password;
    private final Map<Integer, Datasource>         datasources;
    private final Map<Integer, Datanode>           datanodes;
    private final Map<Integer, ArrayList<Integer>> handovers;
    private final Map<String, Database>            databases;
    private final Map<Integer, Rule>               rules;
    private final Map<Integer, Function>           functions;
    private final Map<String, User>                users;

    public ServerConfigLoader(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.datasources = new HashMap<Integer, Datasource>();
        this.datanodes = new HashMap<Integer, Datanode>();
        this.handovers = new HashMap<Integer, ArrayList<Integer>>();
        this.databases = new HashMap<String, Database>();
        this.rules = new HashMap<Integer, Rule>();
        this.functions = new HashMap<Integer, Function>();
        this.users = new HashMap<String, User>();
        try {
            load();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void load() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection(url, username, password);
        loadFunctions(con);
        loadRules(con);
        loadDatasources(con);
        loadDatanodes(con);
        loadHandovers(con);
        loadDatabase(con);
        loadUsers(con);
        con.close();
    }

    /**
     * 加载拆分函数
     */
    private void loadFunctions(Connection con) throws SQLException {
        String sql = "SELECT `id`,`type` FROM `functions`";
        ResultSet result = con.createStatement().executeQuery(sql);
        int defaultNode = 1;
        while (result.next()) {
            int id = result.getInt("id");
            String type = result.getString("type").toUpperCase();
            Function function = null;
            sql = "SELECT `args`,`datanode_id` FROM `function_args` WHERE `function_id`=" + id;
            ResultSet r = con.createStatement().executeQuery(sql);
            switch (type) {
                case "AUTO":
                    while (r.next()) {
                        int count = Integer.parseInt(r.getString("args"));
                        function = new AutoFunction(count);
                    }
                    break;
                case "HASH":
                    Set<Range> hash = new HashSet<Range>();
                    while (r.next()) {
                        int node = r.getInt("datanode_id");
                        String[] value = r.getString("args").split(":");
                        Range range = new Range(node, Long.decode(value[0]), Long.decode(value[1]));
                        hash.add(range);
                    }
                    function = new HashFunction(hash);
                    break;
                case "MATCH":
                    Map<Object, Integer> match = new HashMap<Object, Integer>();

                    while (r.next()) {
                        int node = r.getInt("datanode_id");
                        if (node != 0) {
                            Object val = r.getObject("args");
                            match.put(val, node);
                        } else {
                            defaultNode = r.getInt("args");
                        }
                    }
                    function = new MatchFunction(match, defaultNode);
                    break;
                case "RANGE":
                    Set<Range> ranges = new HashSet<Range>();
                    while (r.next()) {
                        int node = r.getInt("datanode_id");
                        if (node != 0) {
                            String[] value = r.getString("args").split(":");
                            Range range =
                                    new Range(node, Long.decode(value[0]), Long.decode(value[1]));
                            ranges.add(range);
                        } else {
                            defaultNode = r.getInt("args");
                        }
                    }
                    function = new RangeFunction(ranges, defaultNode);
                    break;
            }
            functions.put(id, function);
        }
    }

    /**
     * 加载拆分规则
     */
    private void loadRules(Connection con) throws SQLException {
        String sql = "SELECT `id`,`column_name`,`function_id` FROM `rules`";
        ResultSet result = con.createStatement().executeQuery(sql);
        while (result.next()) {
            int id = result.getInt("id");
            String column = result.getString("column_name").toUpperCase();
            int functionId = result.getInt("function_id");
            Function function = functions.get(functionId);
            if (function == null) {

            }
            Rule rule = new Rule(column, function);
            rules.put(id, rule);
        }
    }

    /**
     * 加载数据源
     */
    private void loadDatasources(Connection con) throws SQLException {
        String sql =
                "SELECT `id`,`datanode_id`,`host`,`port`,`username`,`password`,`db`,`datasource_type`,`datasource_status`,`character_type`,`init_con`,`max_con`,`min_idle`,`max_idle`,`idle_check_period` FROM `datasources`";
        ResultSet result = con.createStatement().executeQuery(sql);
        while (result.next()) {
            int id = result.getInt("id");
            int datanodeID = result.getInt("datanode_id");
            String host = result.getString("host");
            int port = result.getInt("port");
            String username = result.getString("username");
            String password = result.getString("password");
            String db = result.getString("db");
            int type = result.getInt("datasource_type");
            int status = result.getInt("datasource_status");
            String charset = result.getString("character_type");
            int initCon = result.getInt("init_con");
            int maxCon = result.getInt("max_con");
            int minIdle = result.getInt("min_idle");
            int maxIdle = result.getInt("max_idle");
            int idleCheckPeriod = result.getInt("idle_check_period");
            Datasource datasource = new Datasource(id, datanodeID, host, port, username, password,
                    db, type, status, charset, initCon, maxCon, minIdle, maxIdle, idleCheckPeriod);
            datasources.put(id, datasource);
        }
    }

    /**
     * 加载数据节点
     */
    private void loadDatanodes(Connection con) throws SQLException {
        String sql = "SELECT `id`,`strategy` FROM `datanodes`";
        ResultSet result = con.createStatement().executeQuery(sql);
        while (result.next()) {
            int id = result.getInt("id");
            int strategy = result.getInt("strategy");
            List<Integer> ds = new ArrayList<Integer>();
            sql = "SELECT `id` FROM `datasources` WHERE `datanode_id`=" + id
                    + " ORDER BY `priority` ASC";
            ResultSet rs = con.createStatement().executeQuery(sql);
            while (rs.next()) {
                ds.add(rs.getInt("id"));
            }
            Datanode datanode = new Datanode(id, strategy, ds);
            datanodes.put(id, datanode);
        }
    }

    /**
     * 加载节点切换信息
     */
    private void loadHandovers(Connection con) throws SQLException {
        String sql = "SELECT DISTINCT `datasource_id` FROM `handovers`";
        ResultSet result = con.createStatement().executeQuery(sql);
        while (result.next()) {
            int id = result.getInt("datasource_id");
            sql = "SELECT `handover_id` FROM `handovers` WHERE `datasource_id`=" + id
                    + " ORDER BY `priority`";
            ResultSet rs = con.createStatement().executeQuery(sql);
            ArrayList<Integer> sources = new ArrayList<>();
            while (rs.next()) {
                sources.add(rs.getInt("handover_id"));
            }
            handovers.put(id, sources);
        }
    }

    /**
     * 加载逻辑数据库
     */
    private void loadDatabase(Connection con) throws SQLException {
        String sql = "SELECT `id`,`name` FROM `dbs`";
        ResultSet result = con.createStatement().executeQuery(sql);
        while (result.next()) {
            int id = result.getInt("id");
            String name = result.getString("name").toUpperCase();
            Database database = new Database(id, name, loadTables(id, con));
            databases.put(name, database);
        }
    }

    /**
     * 加载逻辑表
     */
    private Map<String, Table> loadTables(int id, Connection con) throws SQLException {
        String sql = "SELECT `name`,`type`,`datanodes`,`rule_id` FROM `tables` WHERE db_id=" + id;
        Map<String, Table> tables = new HashMap<String, Table>();
        ResultSet result = con.createStatement().executeQuery(sql);
        while (result.next()) {
            String name = result.getString("name").toUpperCase();
            int type = result.getInt("type");
            String datanodes = result.getString("datanodes");
            String[] datanode = datanodes.split(",");
            List<Integer> nodes = new ArrayList<Integer>();
            for (String node : datanode) {
                nodes.add(Integer.parseInt(node));
            }
            int ruleID = result.getInt("rule_id");
            Rule rule = rules.get(ruleID);
            Table table = new Table(id, name, type, rule, nodes);
            tables.put(name, table);
        }
        return tables;
    }

    /**
     * 加载用户信息
     */
    private void loadUsers(Connection con) throws SQLException {
        String sql = "SELECT `username`,`password`,`dbs`,`hosts` FROM `users`";
        ResultSet result = con.createStatement().executeQuery(sql);
        while (result.next()) {
            String username = result.getString("username");
            String password = result.getString("password");
            String dbs = result.getString("dbs");
            String[] db = dbs.split(",");
            Set<String> databases = new HashSet<String>();
            for (String d : db) {
                databases.add(d);
            }
            String host = result.getString("hosts");
            String[] hos = host.split(",");
            Set<String> hosts = new HashSet<String>();
            for (String h : hos) {
                if (!h.equals("")) {
                    hosts.add(h);
                }
            }
            User user = new User(username, password, databases, hosts);
            users.put(username, user);
        }
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public Map<String, Database> getDatabases() {
        return databases;
    }

    public Map<Integer, Datanode> getDatanodes() {
        return datanodes;
    }

    public Map<Integer, Datasource> getDatasources() {
        return datasources;
    }

    public Map<Integer, ArrayList<Integer>> getHandovers() {
        return handovers;
    }
}
