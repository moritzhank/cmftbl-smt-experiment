package tools.aqua.stars.logic.kcmftbl.smtModelChecker

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.misc.getAbsolutePathFromProjectDir
import java.io.File

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