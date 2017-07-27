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
import java.io.IOException;
import java.net.URI;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

/**
 * Dumps a database into a file.
 *
 * @author ogolberg@vecna.com
 */
@Mojo(name = "dump", threadSafe = true)
public class DumpDbMojo extends AbstractDbMojo {
  /**
   * Output file
   */
  @Parameter
  private String output;

  /**
   * {@inheritDoc}
   */
  @Override
  public void executeIfEnabled() throws MojoExecutionException, MojoFailureException {
    final URI uri = getURI();
    File file = new File(output);
    File parent = file.getParentFile();
    if (parent != null) {
      try {
        FileUtils.forceMkdir(parent);
      } catch (IOException ioException) {
        throw new MojoExecutionException("failed to create " + parent, ioException);
      }
    }
    lookupStrategy(uri.getScheme()).dumpDb(uri, username, password, file);
  }
}
