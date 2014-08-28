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

/**
 * Database information extracted from a JDBC URL.
 *
 * @author ogolberg@vecna.com
 */
class PostgresDbInfo {
  private final String m_dbName;
  private final String m_connectUrl;

  /**
   * Create a new {@link PostgresDbInfo}
   * @param dbName name of the database
   * @param connectUrl a universal connection URL to run drop/create commands against
   */
  public PostgresDbInfo(String dbName, String connectUrl) {
    m_dbName = dbName;
    m_connectUrl = connectUrl;
  }

  /**
   * @return name of the database
   */
  public String getDbName() {
    return m_dbName;
  }

  /**
   * @return universal connection URL to run drop/create commands against
   */
  public String getConnectUrl() {
    return m_connectUrl;
  }
}
