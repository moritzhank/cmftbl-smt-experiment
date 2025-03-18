package tools.aqua.stars.logic.kcmftbl.smtModelChecker.scripts

/** @param outputFile If null the plot will not be saved but displayed on screen */
fun plotPerf(
    vararg files: String,
    width: Int? = null,
    height: Int? = null,
    title: String? = null,
    xLabel: String? = null,
    outputFile: String? = null)
{
  var args = mutableListOf(*files)
  if (width != null) {
    args.add(0, "-W $width")
  }
  if (height != null) {
    args.add(0, "-H $height")
  }
  if (title != null) {
    args.add(0, "--title $title")
  }
  if (xLabel != null) {
    args.add(0, "--x_label $xLabel")
  }
  if (outputFile != null) {
    args.add(0, "-S $outputFile")
  }
  val proc = PythonCommandLineWrapper.runScript("plotPerf.py", *args.toTypedArray())
  if (proc.exitValue() != 0) {
    throw RuntimeException(proc.inputReader().readText() + proc.errorReader().readText())
  }
}