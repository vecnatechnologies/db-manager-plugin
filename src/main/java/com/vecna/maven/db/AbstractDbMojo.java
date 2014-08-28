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

package com.vecna.maven.db;

import java.net.URI;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import com.google.common.collect.ImmutableMap;

/**
 * Base DB management plugin.
 *
 * @author ogolberg@vecna.com
 */
public abstract class AbstractDbMojo extends AbstractMojo {
  /**
   * Map of protocol - strategy class. We load strategies via reflection so that JDBC drivers
   * can be added by the projects that use this plugin.
   */
  private final Map<String, String> m_strategies = ImmutableMap.of("postgresql", "com.vecna.maven.db.pg.PostgresStrategy");

  /**
   * JDBC url
   */
  @Parameter
  protected String url;

  /**
   * JDBC password
   */
  @Parameter
  protected String password;

  /**
   * JDBC username
   */
  @Parameter
  protected String username;

  /**
   * Skip execution
   */
  @Parameter
  protected boolean skip;

  /**
   * {@inheritDoc}
   */
  @Override
  public final void execute() throws MojoExecutionException, MojoFailureException {
    if (!skip) {
      executeIfEnabled();
    } else {
      getLog().info("skipping execution");
    }
  }

  /**
   * Main plugin execution logic.
   * @throws MojoExecutionException on unexpected build failure
   * @throws MojoFailureException on expected build failure
   */
  protected abstract void executeIfEnabled() throws MojoExecutionException, MojoFailureException;

  /**
   * @return a URI representation of the jdbc connection url
   * @throws MojoFailureException if the url does't look like a valid JDBC url
   */
  protected URI getURI() throws MojoFailureException {
    if (url.startsWith("jdbc:")) {
      return URI.create(url.substring(5));
    } else {
      throw new MojoFailureException("Invalid JDBC url: " + url);
    }
  }

  /**
   * Lookup the DB-specific strategy for performing DB management operations.
   * @param protocol identifies the type of the db (jdbc:type://...)
   * @return db-specific strategy
   * @throws MojoFailureException if the protocol is not supported
   */
  protected DbStrategy lookupStrategy(String protocol) throws MojoFailureException {
    String type = m_strategies.get(protocol);
    if (type == null) {
      throw new MojoFailureException("protocol " + protocol + " is not implemented");
    } else {
      try {
        return (DbStrategy) Class.forName(type).newInstance();
      } catch (Exception e) {
        throw new IllegalStateException("cannot instantiate a strategy for protocol " + protocol);
      }
    }
  }
}
