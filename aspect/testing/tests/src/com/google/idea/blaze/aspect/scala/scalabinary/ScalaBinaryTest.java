/*
 * Copyright 2017 The Bazel Authors. All rights reserved.
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
package com.google.idea.blaze.aspect.scala.scalabinary;

import static com.google.common.truth.Truth.assertThat;

import com.google.devtools.intellij.IntellijAspectTestFixtureOuterClass.IntellijAspectTestFixture;
import com.google.devtools.intellij.ideinfo.IntellijIdeInfo.TargetIdeInfo;
import com.google.idea.blaze.BazelIntellijAspectTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests scala_binary */
@RunWith(JUnit4.class)
public class ScalaBinaryTest extends BazelIntellijAspectTest {
  @Test
  public void testScalaBinary() throws Exception {
    IntellijAspectTestFixture testFixture = loadTestFixture(":foo_fixture");
    TargetIdeInfo binaryInfo = findTarget(testFixture, ":foo");

    assertThat(binaryInfo.getKindString()).isEqualTo("scala_binary");
    assertThat(relativePathsForArtifacts(binaryInfo.getJavaIdeInfo().getSourcesList()))
        .containsExactly(testRelative("FooMain.scala"));
    assertThat(dependenciesForTarget(binaryInfo)).contains(dep(":foolib"));

    assertThat(binaryInfo.getJavaIdeInfo().getJarsList()).hasSize(1);
    assertThat(binaryInfo.getJavaIdeInfo().getJarsList().get(0).getJar().getRelativePath())
        .isEqualTo(testRelative("foo.jar"));

    assertThat(binaryInfo.getJavaIdeInfo().getMainClass()).isEqualTo("com.google.MyMainClass");

    assertThat(getOutputGroupFiles(testFixture, "intellij-info-java"))
        .containsAtLeast(
            testRelative(intellijInfoFileName("foolib")),
            testRelative(intellijInfoFileName("foo")));
    assertThat(getOutputGroupFiles(testFixture, "intellij-resolve-java"))
        .contains(testRelative("foo.jar"));
    assertThat(getOutputGroupFiles(testFixture, "intellij-resolve-java"))
            .doesNotContain(testRelative("libfoo.jar"));
    assertThat(getOutputGroupFiles(testFixture, "intellij-compile-java"))
        .containsAtLeast(testRelative("foolib.jar"), testRelative("foo.jar"));
    assertThat(getOutputGroupFiles(testFixture, "intellij-info-generic")).isEmpty();
  }
}
