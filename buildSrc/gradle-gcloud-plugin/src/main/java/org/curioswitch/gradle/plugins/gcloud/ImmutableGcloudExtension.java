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

package org.curioswitch.gradle.plugins.gcloud;

import java.io.File;
import java.nio.file.Paths;
import org.gradle.api.Project;
import org.immutables.value.Value;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Modifiable;
import org.immutables.value.Value.Style;

@Modifiable
@Style(create = "new", typeModifiable = "*", defaultAsDefault = true, typeAbstract = "Immutable*")
public interface ImmutableGcloudExtension {

  String NAME = "gcloud";

  @Value.Parameter
  Project project();

  default boolean download() {
    return true;
  }

  default File workDir() {
    return Paths.get(project().getProjectDir().getAbsolutePath(), ".gradle", "gcloud").toFile();
  }

  default String version() {
    return "146.0.0";
  }

  default String distBaseUrl() {
    return "https://dl.google.com/dl/cloudsdk/channels/rapid/downloads/";
  }

  @Derived
  default PlatformConfig platformConfig() {
    return PlatformConfig.fromExtension(this);
  }
}
