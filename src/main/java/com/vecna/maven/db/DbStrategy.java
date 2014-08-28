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

import java.io.File;
import java.net.URI;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * API for performing database operations.
 * @author ogolberg@vecna.com
 */
public interface DbStrategy {
  /**
   * Create a database.
   * @param url database URL
   * @param username username
   * @param password password
   * @throws MojoExecutionException if an unexpected build problem occurs
   * @throws MojoFailureException if an expected build problem occurs
   */
  public void createDb(URI url, String username, String password) throws MojoExecutionException, MojoFailureException;

  /**
   * Drop a database.
   * @param url database URL
   * @param username username
   * @param password password
   * @throws MojoExecutionException if an unexpected build problem occurs
   * @throws MojoFailureException if an expected build problem occurs
   */
  public void dropDb(URI url, String username, String password) throws MojoExecutionException, MojoFailureException;

  /**
   * Dump a database
   * @param url database URL
   * @param username username
   * @param password password
   * @param out output file
   * @throws MojoExecutionException if an unexpected build problem occurs
   * @throws MojoFailureException if an expected build problem occurs
   */
  public void dumpDb(URI url, String username, String password, File out) throws MojoExecutionException, MojoFailureException;
}
