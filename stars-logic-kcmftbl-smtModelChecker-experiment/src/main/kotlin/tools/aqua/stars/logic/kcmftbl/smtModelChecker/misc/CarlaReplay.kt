package tools.aqua.stars.logic.kcmftbl.smtModelChecker.misc

import tools.aqua.stars.logic.kcmftbl.smtModelChecker.requirePathToCarlaData
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.requirePathToCarlaReplayPy
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.scripts.PythonCommandLineWrapper
import java.io.File
import kotlin.io.path.Path

private fun setupCarlaVenv(path: String, carlaVersion: String = "0.9.14", reinstall: Boolean = false): String {
  // Preparation
  val execEnding = if (System.getProperty("os.name").lowercase().contains("windows")) ".exe" else ""
  val carlaVenvPath = Path(path, "/carlaVenv/").toString().replace('\\', '/').trimEnd('/')
  if (File(carlaVenvPath).exists() && !reinstall) {
    return "$carlaVenvPath/Scripts/python$execEnding"
  }
  val py = PythonCommandLineWrapper.pythonBaseCmd()
  val carlaVenvRequirements = "carla~=$carlaVersion\n" +
          "numpy\n" +
          "dataclass-wizard\n" +
          "opencv-python\n" +
          "scipy"
  // Installation
  File(carlaVenvPath).deleteRecursively()
  runCommand("$py -m pip install --user --upgrade pip", path)
  runCommand("$py -m pip install --user virtualenv", path)
  runCommand("$py -m venv carlaVenv", path)
  runCommand("$carlaVenvPath/Scripts/python$execEnding -m pip install --upgrade pip", path)
  File("$carlaVenvPath/requirements.txt").writeText(carlaVenvRequirements)
  runCommand("$carlaVenvPath/Scripts/pip$execEnding install -r $carlaVenvPath/requirements.txt", path)
  return "$carlaVenvPath/Scripts/python$execEnding"
}

private fun runCommand(command: String, dir: String) {
  println("Run \"$command\" ...")
  val p = ProcessBuilder(command.split(" ")).directory(File(dir)).start().apply { waitFor() }
  println(p.inputReader().readText())
  val error = p.errorReader().readText()
  if (error.isNotEmpty()) {
    println(error)
  }
  require(p.exitValue() == 0)
}

fun carlaReplay(
  town: String,
  seed: String,
  start: Double,
  duration: Double,
  id: Int,
  speed: Double,
  venvPath: String
) {
  val replayData = requirePathToCarlaData().replace('\\', '/').trimEnd('/')
  val replayPy = requirePathToCarlaReplayPy()
  val venvPy = setupCarlaVenv(venvPath)
  val pathToFile = "$replayData/records/_Game_Carla_Maps_Town$town/_Game_Carla_Maps_Town${town}_seed$seed.log"
  runCommand("$venvPy $replayPy -x $speed -c $id -s $start -d $duration -f $pathToFile", venvPath)
}
