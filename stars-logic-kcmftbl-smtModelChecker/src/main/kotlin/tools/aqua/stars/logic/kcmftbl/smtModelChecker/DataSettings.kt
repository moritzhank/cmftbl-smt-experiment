/*
 * Copyright 2025 The STARS Project Authors
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

import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.misc.getAbsolutePathFromProjectDir

/** Contains all relevant settings for the data of experiments. */
@Serializable
data class DataSettings(var pathToCarlaData: String) {

  companion object {

    private val settingsFilePath = getAbsolutePathFromProjectDir("dataSettings.json")

    /** Loads the [DataSettings] from "dataSettings.json". */
    fun load(): DataSettings? {
      val settingsFile = File(settingsFilePath)
      if (!settingsFile.exists()) {
        return null
      }
      return Json.decodeFromString(settingsFile.readText())
    }

    /** Generate empty "dataSettings.json". */
    fun generateTemplate() {
      File(settingsFilePath).writeText(Json.encodeToString(DataSettings("")))
    }
  }
}
