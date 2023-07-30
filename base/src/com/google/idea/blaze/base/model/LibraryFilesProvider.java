/*
 * Copyright 2022 The Bazel Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.idea.blaze.base.model;

import com.google.common.collect.ImmutableList;
import com.google.idea.blaze.base.io.VirtualFileSystemProvider;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.StandardFileSystems;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.io.URLUtil;

import java.io.File;

import static com.google.common.collect.ImmutableList.toImmutableList;

/** Provides files to be updated in {@link Library.ModifiableModel}. */
public interface LibraryFilesProvider {
  /** Returns the name of library that the modifier will update. */
  String getName();

  /**
   * Returns a list of files that should be added to {@link Library.ModifiableModel} as
   * OrderRootType.CLASSES.
   */
  ImmutableList<File> getClassFiles(BlazeProjectData blazeProjectData);

  /**
   * Returns a list of files' urls that should be added to {@link Library.ModifiableModel} as
   * OrderRootType.CLASSES.
   */
  default ImmutableList<String> getClassFilesUrls(BlazeProjectData blazeProjectData) {
    return getClassFiles(blazeProjectData).stream()
            .map(this::pathToUrl)
            .collect(toImmutableList());
  };

  /**
   * Returns a list of files that should be added to {@link Library.ModifiableModel} as
   * OrderRootType.SOURCES.
   */
  ImmutableList<File> getSourceFiles(BlazeProjectData blazeProjectData);

  /**
   * Returns a list of files' urls that should be added to {@link Library.ModifiableModel} as
   * OrderRootType.SOURCES.
   */
  default ImmutableList<String> getSourceFilesUrls(BlazeProjectData blazeProjectData) {
    return getSourceFiles(blazeProjectData).stream()
            .map(this::pathToUrl)
            .collect(toImmutableList());
  }

  default boolean supportAnchors() {
    return false;
  }

  default String pathToUrl(File path) {
    String name = path.getName();
    boolean isJarFile =
            FileUtilRt.extensionEquals(name, "jar")
                    || FileUtilRt.extensionEquals(name, "srcjar")
                    || FileUtilRt.extensionEquals(name, "zip");
    // .jar files require an URL with "jar" protocol.
    String protocol =
            isJarFile
                    ? StandardFileSystems.JAR_PROTOCOL
                    : VirtualFileSystemProvider.getInstance().getSystem().getProtocol();
    String filePath = FileUtil.toSystemIndependentName(path.getPath());
    String url = VirtualFileManager.constructUrl(protocol, filePath);
    if (isJarFile) {
      url += URLUtil.JAR_SEPARATOR;
    }
    return url;
  }


}
