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

@file:Suppress("unused", "UndocumentedPublicProperty")

package tools.aqua.stars.logic.kcmftbl.smtModelChecker

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.misc.getAbsolutePathFromProjectDir
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/** Captures all supported SMT-Solver. */
enum class SmtSolver(val solverName: String) {
  CVC5("cvc5"),
  Z3("z3"),
  YICES("yices")
}

/** Save the SMT-program [program] in a file. */
fun saveSmtFile(program: String, solver: SmtSolver = SmtSolver.CVC5) {
  val smtTmpDirPath = getAbsolutePathFromProjectDir("_smtTmp")
  File(smtTmpDirPath).mkdir()
  val smt2FilePath = "$smtTmpDirPath${File.separator}${UUID.randomUUID()}.smt2"
  val smt2File = File(smt2FilePath).apply { writeText(program) }
}

/** Run a local SMT-Solver instance. This requires a correct setup of "smtSolverSettings.json". */
fun runSmtSolver(
    program: String,
    solver: SmtSolver = SmtSolver.CVC5,
    removeSmt2File: Boolean = true,
    vararg solverArgs: String,
    yicesTimeoutInSeconds: Int = 120,
    memoryProfilerCallback: ((Long) -> Unit)? = null,
): String? {
  val solverBinPath = requireSolverBinPath(solver)
  val smtTmpDirPath = getAbsolutePathFromProjectDir("_smtTmp")
  File(smtTmpDirPath).mkdir()
  val smt2FilePath = "$smtTmpDirPath${File.separator}${UUID.randomUUID()}.smt2"
  val smt2File = File(smt2FilePath).apply { writeText(program) }
  val proc = ProcessBuilder(solverBinPath, smt2FilePath, *solverArgs).start()
  // MemoryProfiler should run async
  GlobalScope.launch {
    memoryProfilerCallback?.invoke(proc.pid())
  }
  // Handle timeout for Yices2
  if (solver == SmtSolver.YICES) {
    proc.waitFor(yicesTimeoutInSeconds.toLong(), TimeUnit.SECONDS)
    if (proc.isAlive) {
      proc.destroyForcibly().waitFor()
    }
  } else {
    proc.waitFor()
  }
  val exitCode = proc.exitValue()
  // Has run into timeout/error of Yices2
  if (solver == SmtSolver.YICES && exitCode != 0) {
    smt2File.delete()
    return null
  }
  // Has run into timeout/error of CVC5
  if (solver == SmtSolver.CVC5 && exitCode != 0) {
    smt2File.delete()
    return null
  }
  require(exitCode == 0)
  val result = proc.inputReader().readText() + proc.errorReader().readText()
  // Has run into timeout of Z3
  if (solver == SmtSolver.Z3 && result.startsWith("timeout", true)) {
    return null
  }
  if (removeSmt2File) {
    smt2File.delete()
  }
  return result
}

/**
 * Run a local SMT-Solver instance with the option to show the version. This requires a correct
 * setup of "smtSolverSettings.json".
 */
fun smtSolverVersion(solver: SmtSolver): String {
  val solverBinPath = requireSolverBinPath(solver)
  val versionOption =
      when (solver) {
        SmtSolver.CVC5 -> "--version"
        SmtSolver.Z3 -> "--version"
        SmtSolver.YICES -> "--version"
      }
  val proc = ProcessBuilder(solverBinPath, versionOption).start().apply { waitFor() }
  val result = proc.inputReader().readText() + proc.errorReader().readText()
  return when (solver) {
    SmtSolver.CVC5 -> {
      result.lines().first().removePrefix("This is ").dropLastWhile { it != '[' }.dropLast(2).removePrefix("cvc5 version ")
    }
    SmtSolver.Z3 -> result.dropLastWhile { it != '-' }.dropLast(2).removePrefix("Z3 version ")
    SmtSolver.YICES -> result.lines().first().removePrefix("Yices ")
  }
}

private fun requireSolverBinPath(solver: SmtSolver): String {
  val settings = SmtSolverSettings.load()
  requireNotNull(settings) {
    SmtSolverSettings.generateTemplate()
    "The file smtSolverSettings.json must be specified."
  }
  val solverBinPath = settings.getPathToSolverBin(solver)
  require(File(solverBinPath).exists()) {
    "The specified binary (at \"$solverBinPath\") for ${solver.solverName} does not exist."
  }
  return solverBinPath
}
