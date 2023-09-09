/*
 * Copyright 2019 The Bazel Authors. All rights reserved.
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
package com.google.idea.blaze.base.command.buildresult;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;

/** All the relevant output data for a single {@link OutputArtifact}. */
public class BepArtifactData {

  public final OutputArtifact artifact;
  /** The output groups this artifact belongs to. */
  public final ImmutableSet<String> outputGroups;
  private final Set<String> topLevelTargets;

  BepArtifactData(
      OutputArtifact artifact,
      Collection<String> outputGroups,
      Collection<String> topLevelTargets) {
    this.artifact = artifact;
    this.outputGroups = ImmutableSet.copyOf(outputGroups);
    if (topLevelTargets instanceof HashSet<String>) {
      this.topLevelTargets = (Set<String>) topLevelTargets;
    } else {
      this.topLevelTargets = new HashSet<>(topLevelTargets);
    }
  }

  @Override
  public int hashCode() {
    return artifact.getKey().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof BepArtifactData && artifact.equals(((BepArtifactData) obj).artifact);
  }

  /** Returns null if this was the only top-level target the artifact was associated with. */
  @Nullable
  public BepArtifactData removeTargetAssociation(String target) {
    Set<String> newTargets = new HashSet<>(topLevelTargets);
    newTargets.remove(target);
    return newTargets.isEmpty() ? null : new BepArtifactData(artifact, outputGroups, newTargets);
  }

  /** Combines this data with a newer version. */
  public BepArtifactData update(BepArtifactData newer) {
    Preconditions.checkState(artifact.getKey().equals(newer.artifact.getKey()));
    Set<String> mergedTargets = new HashSet<>(topLevelTargets);
    mergedTargets.addAll(newer.topLevelTargets);
    return new BepArtifactData(
        newer.artifact,
        Sets.union(outputGroups, newer.outputGroups).immutableCopy(), mergedTargets);
  }

  /** The top-level targets this artifact is transitively associated with. */
  public Set<String> getTopLevelTargets() {
    return topLevelTargets;
  }
}
