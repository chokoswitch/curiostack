/*
 * MIT License
 *
 * Copyright (c) 2017 Choko (choko@curioswitch.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.curioswitch.gradle.plugins.curioserver;

import com.bmuschko.gradle.docker.DockerExtension;
import com.bmuschko.gradle.docker.DockerJavaApplication;
import com.bmuschko.gradle.docker.DockerJavaApplicationPlugin;
import groovy.lang.GroovyObject;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.ApplicationPluginConvention;
import org.gradle.api.plugins.BasePluginConvention;

/**
 * A simple {@link Plugin} to reduce boilerplate when defining server projects. Contains common
 * logic for building and deploying executables.
 */
public class CurioServerPlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(ApplicationPlugin.class);

    project.afterEvaluate(
        p -> {
          String archivesBaseName =
              project.getConvention().getPlugin(BasePluginConvention.class).getArchivesBaseName();
          project
              .getConvention()
              .getPlugin(ApplicationPluginConvention.class)
              .setApplicationName(archivesBaseName);

          GroovyObject docker = project.getExtensions().getByType(DockerExtension.class);
          DockerJavaApplication javaApplication =
              (DockerJavaApplication) docker.getProperty("javaApplication");
          javaApplication.setMaintainer("Choko (choko@curioswitch.org)");

          String tagVersion =
              project.getVersion().equals("unspecified")
                  ? "latest"
                  : project.getVersion().toString();
          String artifactAndVersion = (archivesBaseName + ":" + tagVersion).toLowerCase();
          String baseTag =
              project.getGroup() != null
                  ? project.getGroup() + "/" + artifactAndVersion
                  : artifactAndVersion;
          // TODO(choko): Make this prefix configurable.
          String tag = "asia.gcr.io/" + baseTag;
          javaApplication.setTag(tag);
        });
    project.getPluginManager().apply(DockerJavaApplicationPlugin.class);
  }
}
