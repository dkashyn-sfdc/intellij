/*
 * Copyright 2023 The Bazel Authors. All rights reserved.
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
package com.google.idea.blaze.cpp.qsync;

import com.jetbrains.cidr.lang.workspace.OCWorkspace;

/** SDK compat class for {@link OCWorkspace.ModifiableModel} */
public class OCWorkspaceModifiableModelDisposer {

  private OCWorkspaceModifiableModelDisposer() {}

  public static void dispose(OCWorkspace.ModifiableModel modifiableModel) {
    // Nothing to do - the modifiable model does not implement dispose in 22.3
  }
}