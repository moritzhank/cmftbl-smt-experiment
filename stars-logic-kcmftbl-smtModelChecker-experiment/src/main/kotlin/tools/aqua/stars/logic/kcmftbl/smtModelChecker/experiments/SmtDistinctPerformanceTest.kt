package tools.aqua.stars.logic.kcmftbl.smtModelChecker.experiments

import tools.aqua.stars.logic.kcmftbl.smtModelChecker.SmtSolver
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.misc.MemoryProfiler
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.scripts.linSpaceArr

class SmtDistinctPerformanceSetup(override val x: Int) : PerfExperimentSetup {

  override fun toString(): String {
    return "SmtDistinctPerf with $x distinct individuals"
  }

}

class SmtDistinctPerformanceTest(): PerfExperiment("SmtDistinctPerf") {

  override val memoryProfilerWorkingCond: (MemoryProfiler) -> Boolean = { memProfiler ->
    memProfiler.maxProcMemUsageBytes != -1L &&
            memProfiler.maxSysMemUsagePercent != -1.0 &&
            memProfiler.numSamples > 10
  }

  override fun generateSmtLib(
    exp: PerfExperimentSetup,
    solver: SmtSolver,
    logic: String
  ): String {
    val result = StringBuilder()
    result.appendLine("(set-logic $logic)")
    result.appendLine("(declare-sort TestSort 0)")
    for (i in 1..exp.x) {
      result.appendLine("(declare-const ind_$i TestSort)")
    }
    result.append("(assert (distinct ")
    for (i in 1..exp.x) {
      result.append("ind_$i ")
    }
    result.appendLine("))")
    result.appendLine("(check-sat)")
    return result.toString()
  }

}

fun main() {
  val rangeOfDistinctStatements = linSpaceArr(10_000, 100_000, 30).map { SmtDistinctPerformanceSetup(it) }
  val res = SmtDistinctPerformanceTest().runExperiment(
    rangeOfDistinctStatements,
    SmtSolver.Z3,
    "UF",
    1,
    "#034B7B",
    "Z3 v4.13.4 (avg of 3x)",
    { arr -> (arr.fold(0L) { acc, elem -> acc + elem } / arr.size).toLong().toString() },
    { arr -> (arr.fold(0L) { acc, elem -> acc + elem } / arr.size).toLong().toString() }
  )
  println(res)
}