/*
 * Copyright 2024-2025 The STARS Project Authors
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

package tools.aqua.stars.logic.kcmftbl.smtModelChecker

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.absolute
import tools.aqua.stars.data.av.dataclasses.Segment
import tools.aqua.stars.importer.carla.CarlaSimulationRunsWrapper
import tools.aqua.stars.importer.carla.loadSegments

object ExperimentLoader {

  fun loadTestSegment(town: String = "01", seed: String = "2"): Segment {
    return loadTestSegments(town, seed).first()
  }

  fun loadTestSegments(town: String = "01", seed: String = "2"): List<Segment> {
    val dynamic = getPathToResource("/data/_Game_Carla_Maps_Town$town/dynamic_data__Game_Carla_Maps_Town${town}_seed$seed.json")
    val static = getPathToResource("/data/_Game_Carla_Maps_Town$town/static_data__Game_Carla_Maps_Town${town}.json")
    val wrapper = CarlaSimulationRunsWrapper(static, listOf(dynamic))
    return loadSegments(listOf(wrapper), false, 10, true).toList()
  }

  fun getSeeds(town: String): Array<Int> {
    return when(town) {
      "01" -> arrayOf(2, 7, 10, 15, 18, 19, 22, 25, 26, 29, 32, 33, 36, 42, 47, 52, 53, 55, 58, 59, 61, 62, 64, 67, 69, 73, 78, 86, 88, 94)
      "02" -> arrayOf(0, 4, 5, 8, 12, 13, 16, 17, 21, 23, 24, 27, 30, 31, 34, 35, 38, 39, 41, 43, 45, 46, 48, 49, 50, 54, 57, 60, 65, 66, 68, 70, 76, 77, 79, 80, 81, 82, 83, 84, 93, 96, 97, 99, 100)
      "10HD" -> arrayOf(1, 3, 6, 9, 11, 14, 20, 28, 37, 40, 44, 51, 56, 72, 74, 75, 85, 87, 89, 90, 91, 92, 95, 98)
      else -> throw IllegalArgumentException()
    }
  }

  fun getPathToResource(name: String): Path {
    val uri = ExperimentLoader::class.java.getResource(name)!!.toURI()
    return try {
      Paths.get(uri)
    } catch (_: Exception) {
      Paths.get(name.trimStart('/', '\\')).absolute()
    }
  }
}
