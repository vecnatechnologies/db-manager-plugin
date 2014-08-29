/**
 * Copyright 2014 Vecna Technologies, Inc.
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

package com.vecna.maven.db.pg;

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
 * PostgreSQL strategy for creating/dropping/dumping databases.
 * @author ogolberg@vecna.com
 */
public class PostgresStrategy implements DbStrategy {
  private static final String CONNECT_DB = "/postgres";
  private static final String JDBC = "jdbc:";
  private static final String DUMP_COMMAND = "pg_dump";
  private static final String PASSWORD_ENV = "PGPASSWORD";
  private static final String JDBC_DRIVER = "org.postgresql.Driver";

  /**
   * Create an instance of the PostgreSQL strategy.
   * @throws IllegalStateException if the JDBC driver cannot be loaded.
   */
  public PostgresStrategy() {
    try {
      Class.forName(JDBC_DRIVER);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("cannot load the PostgreSQL JDBC driver", e);
    }
  }

  /**
   * run a SQL statement
   */
  private void execute(String url, String username, String password, String sql) throws SQLException {
    try (Connection conn = DriverManager.getConnection(url, username, password)) {
      conn.createStatement().execute(sql);
    }
  }

  /**
   * Parse {@link PostgresDbInfo} out of a jdbc URL.
   * @param uri {@link URI} representation of the jdbc URL
   * @return a {@link PostgresDbInfo}
   * @throws MojoExecutionException if the URL cannot be parsed
   */
  protected PostgresDbInfo parse(URI uri) throws MojoExecutionException {
    URI connectUri;
    try {
      connectUri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(),
                           CONNECT_DB, uri.getQuery(), uri.getFragment());
    } catch (URISyntaxException e) {
      throw new MojoExecutionException("cannot construct the connection URL", e);
    }

    return new PostgresDbInfo(uri.getPath().replaceAll("^/", ""), JDBC + connectUri.toString());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createDb(URI uri, String username, String password) throws MojoExecutionException {
    PostgresDbInfo dbInfo = parse(uri);
    try {
      execute(dbInfo.getConnectUrl(), username, password, "create database \"" + dbInfo.getDbName() + "\"");
    } catch (SQLException e) {
      throw new MojoExecutionException("cannot created the db", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dropDb(URI uri, String username, String password) throws MojoExecutionException {
    PostgresDbInfo dbInfo = parse(uri);
    try {
      execute(dbInfo.getConnectUrl(), username, password, "drop database if exists \"" + dbInfo.getDbName() + "\"");
    } catch (SQLException e) {
      throw new MojoExecutionException("cannot drop the db", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dumpDb(URI uri, String username, String password, File out) throws MojoExecutionException {
    PostgresDbInfo dbInfo = parse(uri);

    Commandline cmdLine = new Commandline();
    cmdLine.setExecutable(DUMP_COMMAND);

    List<String> args = Lists.newArrayList("-h", uri.getHost(), "-U", username, "-f", out.getPath());
    if (uri.getPort() != -1) {
      args.add("-p");
      args.add(String.valueOf(uri.getPort()));
    }
    args.add(dbInfo.getDbName());
    cmdLine.addArguments(args.toArray(new String[0]));
    cmdLine.addEnvironment(PASSWORD_ENV, password);

    StringStreamConsumer stdout = new StringStreamConsumer();
    StringStreamConsumer stderr = new StringStreamConsumer();

    int result;
    try {
      result = CommandLineUtils.executeCommandLine(cmdLine, stdout, stderr);
    } catch (CommandLineException e) {
      throw new MojoExecutionException("couldn't execute " + DUMP_COMMAND, e);
    }

    if (result != 0) {
      throw new MojoExecutionException(DUMP_COMMAND + " returned " + result
                                       + "\n<stdout>:\n" + stdout.getOutput()
                                       + "\n<stderr>:\n" + stderr.getOutput());
    }
  }
}
