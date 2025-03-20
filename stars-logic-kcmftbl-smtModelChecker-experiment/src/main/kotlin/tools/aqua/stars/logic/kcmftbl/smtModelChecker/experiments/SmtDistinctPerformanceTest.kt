package tools.aqua.stars.logic.kcmftbl.smtModelChecker.experiments

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.SmtSolver
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.misc.MemoryProfiler
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.scripts.LegendPosition
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.scripts.getDateTimeString
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.scripts.linSpaceArr
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.scripts.plotPerf
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.smtSolverVersion

class SmtDistinctPerformanceSetup(override val x: Int) : PerfExperimentSetup {

  override fun toString(): String {
    return "SmtDistinctPerf with $x distinct individuals"
  }

}

class SmtDistinctPerformanceTest(useMemProfiler: Boolean = true): PerfExperiment("SmtDistinctPerf") {

  init {
    memoryProfilerSampleRateMs = 10
    useMemoryProfiler = useMemProfiler
  }

  override val memoryProfilerWorkingCond: (MemoryProfiler) -> Boolean = { memProfiler ->
    memProfiler.maxProcMemUsageBytes != -1L &&
            memProfiler.maxSysMemUsagePercent != -1.0 &&
            memProfiler.numSamples > 5
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

fun runSmtDistinctPerformanceTest(useMemProfiler: Boolean = true) {
  val resMaxSolverMemUsageGBLambda: (List<Long>) -> String = { list ->
    var numberMinusOne = list.size
    var acc = 0L
    list.forEach {
      if (it != -1L) {
        acc += it
        numberMinusOne--
      }
    }
    if (numberMinusOne > 0) {
      "-1"
    } else {
      val resBytes = ((1.0 * acc) / list.size).toLong()
      val resGB = MemoryProfiler.bytesToGB(resBytes)
      resGB.toString()
    }
  }
  // Setup
  val rangeOfDistinctStatementsCVC5 = linSpaceArr(2, 2_000, 10).map { SmtDistinctPerformanceSetup(it) }
  val rangeOfDistinctStatementsZ3 = rangeOfDistinctStatementsCVC5.toMutableList().apply {
    addAll(linSpaceArr(2_500, 100_000, 20).map { SmtDistinctPerformanceSetup(it) })
  }
  val rangeOfDistinctStatementsYices = rangeOfDistinctStatementsZ3.toMutableList().apply {
    addAll(linSpaceArr(110_000, 500_000, 10).map { SmtDistinctPerformanceSetup(it) })
  }

  // CVC5
  val cvc5Version = smtSolverVersion(SmtSolver.CVC5)
  val resCVC5 = SmtDistinctPerformanceTest(useMemProfiler).runExperiment(
    rangeOfDistinctStatementsCVC5,
    SmtSolver.CVC5,
    "UF",
    3,
    "#808080",
    "CVC5 v$cvc5Version (avg. 3x)",
    { arr -> (arr.fold(0L) { acc, elem -> acc + elem } / arr.size).toString() },
    resMaxSolverMemUsageGBLambda
  )

  // Z3
  val z3Version = smtSolverVersion(SmtSolver.Z3)
  val resZ3 = SmtDistinctPerformanceTest(useMemProfiler).runExperiment(
    rangeOfDistinctStatementsZ3,
    SmtSolver.Z3,
    "UF",
    3,
    "#034B7B",
    "Z3 v$z3Version (avg. 3x)",
    { arr -> (arr.fold(0L) { acc, elem -> acc + elem } / arr.size).toLong().toString() },
    resMaxSolverMemUsageGBLambda
  )

  // YICES
  val yicesVersion = smtSolverVersion(SmtSolver.YICES)
  val resYices = SmtDistinctPerformanceTest(useMemProfiler).runExperiment(
    rangeOfDistinctStatementsYices,
    SmtSolver.YICES,
    "UF",
    3,
    "#44B7C2",
    "Yices v$yicesVersion (avg. 3x)",
    { arr -> (arr.fold(0L) { acc, elem -> acc + elem } / arr.size).toString() },
    resMaxSolverMemUsageGBLambda
  )

  val outputFile = "${SmtDistinctPerformanceTest().expFolderPath}/graph_${getDateTimeString()}.png"
  plotPerf(resZ3, resYices, resCVC5, title = "Distinct Experiment", xLabel = "Unterschiedliche Individuen",
    legendPosition = LegendPosition.BEST, outputFile = outputFile, rmMemPlot = !useMemProfiler)
}

class SmtDistinctPerformanceArgs(parser: ArgParser) {
  val disableMemoryProfiler by parser.flagging("-D", "--disable_memory_profiler", help = "Disable memory profiler")
}

fun main(args: Array<String>) = mainBody {
  ArgParser(args).parseInto(::SmtDistinctPerformanceArgs).run {
    runSmtDistinctPerformanceTest(!disableMemoryProfiler)
  }
}