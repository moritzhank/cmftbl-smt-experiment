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

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import tools.aqua.stars.data.av.dataclasses.Segment
import tools.aqua.stars.importer.carla.CarlaSimulationRunsWrapper
import tools.aqua.stars.importer.carla.loadSegments
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.absolute

object ExperimentLoader {

  private var setup: Boolean = false
  lateinit var carlaSeeds: CarlaSeeds
  lateinit var pathToCarlaData: String

  fun loadTestSegment(town: String = "01", seed: String = "2"): Segment {
    return loadTestSegments(town, seed).first()
  }

  fun loadTestSegments(town: String = "01", seed: String = "2"): List<Segment> {
    init()
    val dynamic = Path("$pathToCarlaData/_Game_Carla_Maps_Town$town/dynamic_data__Game_Carla_Maps_Town${town}_seed$seed.json")
    val static = Path("$pathToCarlaData/_Game_Carla_Maps_Town$town/static_data__Game_Carla_Maps_Town${town}.json")
    val wrapper = CarlaSimulationRunsWrapper(static, listOf(dynamic))
    return loadSegments(listOf(wrapper), false, 10, true).toList()
  }

  fun getSeeds(town: String): Array<Int> {
    init()
    return when(town) {
      "01" -> carlaSeeds.town01
      "02" -> carlaSeeds.town02
      "10HD" -> carlaSeeds.town10HD
      else -> throw IllegalArgumentException()
    }
  }

  private fun init() {
    if(!setup) {
      val pathToCarlaSeedsJson = getPathToResource("/data/carla_seeds.json").toString()
      val fileContent = File(pathToCarlaSeedsJson).readText()
      carlaSeeds = Json.decodeFromString(fileContent)
      pathToCarlaData = requirePathToCarlaData().trimEnd('/', '\\').replace('\\', '/')
      setup = true
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

@Serializable
data class CarlaSeeds(val town01: Array<Int>, val town02: Array<Int>, val town10HD: Array<Int>)

private fun requirePathToCarlaData(): String {
  val settings = DataSettings.load()
  requireNotNull(settings) {
    DataSettings.generateTemplate()
    "The file dataSettings.json must be specified."
  }
  val dataPath = settings.pathToCarlaData
  require(File(dataPath).exists()) {
    "The specified dictionary (at \"$dataPath\") does not exist."
  }
  return dataPath
}