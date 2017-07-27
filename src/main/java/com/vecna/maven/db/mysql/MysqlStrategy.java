/**
 * Copyright 2017 Vecna Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.vecna.maven.db.mysql;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils.StringStreamConsumer;
import org.codehaus.plexus.util.cli.Commandline;

import com.google.common.collect.Lists;
import com.vecna.maven.db.DbStrategy;

/**
 * MySQL strategy for creating/dropping/dumping databases.
 * @author jyoung@vecna.com
 */
public class MysqlStrategy implements DbStrategy {
  private static final String DEFAULT_DB = "mysql";
  private static final String JDBC = "jdbc:";
  private static final String DUMP_COMMAND = "mysqldump";
  private static final String PASSWORD_ENV = "MYSQLPASSWORD";
  private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

  /**
   * Create an instance of the MySQL strategy.
   * @throws IllegalStateException if the JDBC driver cannot be loaded.
   */
  public MysqlStrategy() {
    try {
      Class.forName(JDBC_DRIVER);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new IllegalStateException("cannot load the MySQL JDBC driver", classNotFoundException);
    }
  }

  /**
   * run a SQL statement
   * @param url the url to execute the sql on.
   * @param username username of the user that will perform the sql.
   * @param password password of the user that will perform the sql.
   * @param sql the sql string to execute.
   * @throws SQLException if the sql fails to run.
   */
  private void execute(String url, String username, String password, String sql) throws SQLException {
    Connection conn = DriverManager.getConnection(url, username, password);
    try {
      conn.createStatement().execute(sql);
    } finally {
      conn.close();
    }
  }

  /**
   * Parse {@link MysqlDbInfo} out of a jdbc URL.
   * @param uri {@link URI} representation of the jdbc URL
   * @return a {@link MysqlDbInfo}
   * @throws MojoExecutionException if the URL cannot be parsed
   */
  protected MysqlDbInfo parse(URI uri) throws MojoExecutionException {
    URI connectUri;
    try {
      connectUri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(),
          "/" + DEFAULT_DB, uri.getQuery(), uri.getFragment());
    } catch (URISyntaxException uriSyntaxException) {
      throw new MojoExecutionException("cannot construct the connection URL", uriSyntaxException);
    }

    return new MysqlDbInfo(uri.getPath().replaceAll("^/", ""), JDBC + connectUri.toString());
  }

  @Override
  public void createDb(URI uri, String username, String password) throws MojoExecutionException {
    MysqlDbInfo dbInfo = parse(uri);
    try {
      execute(dbInfo.getConnectUrl(), username, password, "CREATE DATABASE " + dbInfo.getDbName());
    } catch (SQLException sqlException) {
      throw new MojoExecutionException("cannot create the db", sqlException);
    }
  }

  @Override
  public void dropDb(URI uri, String username, String password) throws MojoExecutionException {
    MysqlDbInfo dbInfo = parse(uri);
    try {
      execute(dbInfo.getConnectUrl(), username, password, "DROP DATABASE IF EXISTS " + dbInfo.getDbName());
    } catch (SQLException sqlException) {
      throw new MojoExecutionException("cannot drop the db", sqlException);
    }
  }

  @Override
  public void dumpDb(URI uri, String username, String password, File out) throws MojoExecutionException {
    MysqlDbInfo dbInfo = parse(uri);

    Commandline cmdLine = new Commandline();
    cmdLine.setExecutable(DUMP_COMMAND);

    List<String> args = Lists.newArrayList("-h", uri.getHost(), "-U", username, "-f", out.getPath());
    if (uri.getPort() != -1) {
      args.add("-p");
      args.add(String.valueOf(uri.getPort()));
    }
    args.add("--add-drop-database");
    args.add(dbInfo.getDbName());
    cmdLine.addArguments(args.toArray(new String[0]));
    cmdLine.addEnvironment(PASSWORD_ENV, password);

    StringStreamConsumer stdout = new StringStreamConsumer();
    StringStreamConsumer stderr = new StringStreamConsumer();

    int result;
    try {
      result = CommandLineUtils.executeCommandLine(cmdLine, stdout, stderr);
    } catch (CommandLineException commandLineException) {
      throw new MojoExecutionException("couldn't execute " + DUMP_COMMAND, commandLineException);
    }

    if (result != 0) {
      throw new MojoExecutionException(DUMP_COMMAND + " returned " + result
                                       + "\n<stdout>:\n" + stdout.getOutput()
                                       + "\n<stderr>:\n" + stderr.getOutput());
    }
  }
}
