/*
 * MIT License
 *
 * Copyright (c) 2020 Choko (choko@curioswitch.org)
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
package org.curioswitch.gradle.plugins.terraform;

import org.curioswitch.gradle.helpers.immutables.ExtensionStyle;
import org.gradle.api.Project;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.reflect.HasPublicType;
import org.gradle.api.reflect.TypeOf;
import org.immutables.value.Value.Modifiable;

@Modifiable
@ExtensionStyle
public interface TerraformExtension extends HasPublicType {

  String NAME = "terraform";

  static TerraformExtension create(Project project) {
    var objects = project.getObjects();

    return project
        .getExtensions()
        .create(NAME, ModifiableTerraformExtension.class)
        .setDependencies(objects.listProperty(String.class).empty());
  }

  default TerraformExtension dependency(String path) {
    getDependencies().add(path);
    return this;
  }

  /**
   * Paths to projects that are dependencies of this one. The configurations for dependencies will
   * be built and copied into the build directory of this one before running commands. The
   * dependency will be copied in with the same name as the project - use a descriptive project name
   * if needed.
   */
  ListProperty<String> getDependencies();

  @Override
  default TypeOf<?> getPublicType() {
    return TypeOf.typeOf(TerraformExtension.class);
  }
}
