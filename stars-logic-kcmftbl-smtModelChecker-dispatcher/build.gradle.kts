/*
 * Copyright 2024 The STARS Project Authors
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

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/*
 * Copyright 2024 The STARS Project Authors
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

plugins {
  id("tools.aqua.stars.library-conventions")
  id("com.gradleup.shadow") version "8.3.2"
}

dependencies {
  implementation("io.ktor:ktor-server-netty:2.3.12")
  implementation("io.ktor:ktor-network:2.3.12")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}

tasks.compileKotlin { compilerOptions { jvmTarget.set(JvmTarget.JVM_1_8) } }

tasks.compileJava {
  sourceCompatibility = "1.8"
  targetCompatibility = "1.8"
}

tasks.jar { manifest { attributes["Main-Class"] = "MainKt" } }

tasks.shadowJar {
  archiveBaseName.set("dispatcher")
  archiveClassifier.set("")
  archiveVersion.set("")
}
