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

import java.net.URI;

import junit.framework.TestCase;

/**
 * Tests for {@link PostgresStrategy}
 * @author ogolberg@vecna.com
 */
public class PostgresStrategyTest extends TestCase {
  /**
   * Tests for {@link PostgresStrategy#parse(URI)}.
   */
  public void testParseUrl() throws Exception {
    PostgresStrategy pgs = new PostgresStrategy();
    PostgresDbInfo info = pgs.parse(URI.create("postgresql://localhost:7777/db1"));
    assertEquals("db1", info.getDbName());
    assertEquals("jdbc:postgresql://localhost:7777/postgres", info.getConnectUrl());

    info = pgs.parse(URI.create("postgresql://localhost/db2"));

    assertEquals("db2", info.getDbName());
    assertEquals("jdbc:postgresql://localhost/postgres", info.getConnectUrl());
  }
}
