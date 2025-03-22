package tools.aqua.stars.logic.kcmftbl.smtModelChecker.scripts

import java.io.IOException
import kotlin.io.path.absolutePathString
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.ExperimentLoader

internal class PythonCommandLineWrapper {

  companion object {

    private var pythonBaseCmdCache: String? = null

    private fun pythonInstalled(name: String): Boolean =
        try {
          ProcessBuilder(name, "--version").start().apply { waitFor() }.exitValue() == 0
        } catch (err: IOException) {
          false
        }

    fun pythonBaseCmd(): String {
      val pythonBaseCmdCache = this.pythonBaseCmdCache
      if (pythonBaseCmdCache != null) {
        return pythonBaseCmdCache
      }
      val pythonBaseCmd =
          if (pythonInstalled("python")) {
            "python"
          } else if (pythonInstalled("python3")) {
            "python3"
          } else {
            error("No python installation could be found.")
          }
      this.pythonBaseCmdCache = pythonBaseCmd
      return pythonBaseCmd
    }

    fun runScript(name: String, vararg args: String): Process {
      val scriptPath = ExperimentLoader.getPathToResource("/scripts/$name").absolutePathString()
      return ProcessBuilder(pythonBaseCmd(), scriptPath, *args).start().apply { waitFor() }
    }
  }
}
