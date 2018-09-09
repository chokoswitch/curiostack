/*
 * MIT License
 *
 * Copyright (c) 2018 Choko (choko@curioswitch.org)
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

package org.curioswitch.gradle.conda;

import org.curioswitch.gradle.helpers.immutables.ExtensionStyle;
import org.gradle.api.Named;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.reflect.HasPublicType;
import org.gradle.api.reflect.TypeOf;
import org.immutables.value.Value.Modifiable;

@Modifiable
@ExtensionStyle
public interface CondaExtension extends Named, HasPublicType {

  static ModifiableCondaExtension create(String name, ObjectFactory objects) {
    var extension = objects.newInstance(ModifiableCondaExtension.class)
        .setName(name)
        .setVersion(objects.property(String.class))
        .setPackages(objects.listProperty(String.class))
        .setPythonPackages(objects.listProperty(String.class));

    return extension;
  }

  /**
   * The conda version to install. Should be a full name like Miniconda2-4.5.11.
   */
  Property<String> getVersion();

  /**
   * Conda packages to install into the build environment.
   */
  ListProperty<String> getPackages();

  /**
   * Python packages to install into the build environment.
   */
  ListProperty<String> getPythonPackages();

  @Override
  default TypeOf<?> getPublicType() {
    return TypeOf.typeOf(CondaExtension.class);
  }
}
