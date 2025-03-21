/*
 * Copyright 2021-2025 The STARS Project Authors
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//import com.diffplug.gradle.spotless.KotlinExtension
//import com.diffplug.gradle.spotless.KotlinGradleExtension
import tools.aqua.GlobalMavenMetadataExtension
//import tools.aqua.defaultFormat
import tools.aqua.destabilizesVersion

plugins {
  id("com.dorongold.task-tree")
  id("com.github.ben-manes.versions")
  //id("com.diffplug.spotless")
  id("io.github.gradle-nexus.publish-plugin")
  id("me.qoomon.git-versioning")
}

group = rootProject.group

version = rootProject.version

repositories { mavenCentral() }

tasks.dependencyUpdates {
  gradleReleaseChannel = "stable"
  rejectVersionIf(destabilizesVersion)
}


//spotless {
//  kotlinGradle { defaultFormat(rootProject) }
//
//  format("kotlinBuildSrc", KotlinExtension::class.java) {
//    target("buildSrc/src/*/kotlin/**/*.kt")
//    defaultFormat(rootProject)
//  }
//  format("kotlinGradleBuildSrc", KotlinGradleExtension::class.java) {
//    target("buildSrc/*.gradle.kts", "buildSrc/src/*/kotlin/**/*.gradle.kts")
//    defaultFormat(rootProject)
//  }
//}

val mavenMetadata = extensions.create<GlobalMavenMetadataExtension>("mavenMetadata")

gitVersioning.apply {
  describeTagPattern = "v(?<version>.*)"
  refs {
    considerTagsOnBranches = true
    tag("v(?<version>.*)") { version = "\${ref.version}" }
    branch("((?!main).*|main.+|)") { // everything but main
      version =
          "\${describe.tag.version}-\${ref.slug}-\${describe.distance}-\${commit.short}-SNAPSHOT"
    }
    branch("main") {
      version = "\${describe.tag.version}-\${describe.distance}-\${commit.short}-SNAPSHOT"
    }
  }
}

val printVersion by tasks.registering { doFirst { logger.error(version.toString()) } }

nexusPublishing { repositories { sonatype() } }
