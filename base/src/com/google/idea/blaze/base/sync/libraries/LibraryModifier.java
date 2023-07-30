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
package com.google.idea.blaze.base.sync.libraries;

import com.google.idea.blaze.base.io.VirtualFileSystemProvider;
import com.google.idea.blaze.base.model.BlazeProjectData;
import com.google.idea.blaze.base.model.LibraryFilesProvider;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.externalSystem.service.project.IdeModifiableModelsProvider;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.Library.ModifiableModel;
import com.intellij.openapi.roots.ui.configuration.JavaVfsSourceRootDetectionUtil;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.openapi.vfs.StandardFileSystems;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.io.URLUtil;
import java.io.File;
import java.util.Collections;
import java.util.List;

/** Modifies {@link Library} content in {@link Library.ModifiableModel}. */
public class LibraryModifier {
  private static final Logger logger = Logger.getInstance(LibraryModifier.class);
  private final LibraryFilesProvider libraryFilesProvider;
  private final Library.ModifiableModel modifiableModel;

  public LibraryModifier(
      LibraryFilesProvider libraryFilesProvider, IdeModifiableModelsProvider modelsProvider) {
    this.libraryFilesProvider = libraryFilesProvider;
    modifiableModel = getLibraryModifiableModel(modelsProvider, libraryFilesProvider.getName());
  }

  public Library.ModifiableModel getModifiableModel() {
    return modifiableModel;
  }

  /** Writes the library content to its {@link Library.ModifiableModel}. */
  public void updateModifiableModel(BlazeProjectData blazeProjectData) {
    removeAllContents();
    for (String classFileUrl : libraryFilesProvider.getClassFilesUrls(blazeProjectData)) {
      addRoot(classFileUrl, OrderRootType.CLASSES);
    }

    for (String sourceFileUrl : libraryFilesProvider.getSourceFilesUrls(blazeProjectData)) {
      addRoot(sourceFileUrl, OrderRootType.SOURCES);
    }
  }

  private ModifiableModel getLibraryModifiableModel(
      IdeModifiableModelsProvider modelsProvider, String libraryName) {
    Library library = modelsProvider.getLibraryByName(libraryName);
    boolean libraryExists = library != null;
    if (!libraryExists) {
      library = modelsProvider.createLibrary(libraryName);
    }
    return modelsProvider.getModifiableLibraryModel(library);
  }

  private void addRoot(String fileUrl, OrderRootType orderRootType) {
    VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl(fileUrl);
    if (virtualFile == null || !virtualFile.exists()) {
      logger.warn("No local file found for " + fileUrl);
      return;
    }
    modifiableModel.addRoot(fileUrl, orderRootType);
  }

  private void removeAllContents() {
    for (String url : modifiableModel.getUrls(OrderRootType.CLASSES)) {
      modifiableModel.removeRoot(url, OrderRootType.CLASSES);
    }
    for (String url : modifiableModel.getUrls(OrderRootType.SOURCES)) {
      modifiableModel.removeRoot(url, OrderRootType.SOURCES);
    }
  }
}
