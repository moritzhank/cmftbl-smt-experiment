package tools.aqua.stars.logic.kcmftbl.smtModelChecker.experiments

import kotlin.time.measureTime
import kotlinx.serialization.modules.EmptySerializersModule
import tools.aqua.stars.data.av.dataclasses.Segment
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.ExperimentLoader
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.SmtSolver
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.dataTranslation.SmtDataTranslationWrapper
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.dataTranslation.SmtIntermediateRepresentation
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.dataTranslation.generateSmtLib
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.dataTranslation.getSmtIntermediateRepresentation
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.runSmtSolver
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.saveSmtFile

fun main() {
  // Options
  val solver = SmtSolver.YICES
  val removeSmt2File = false

  val t: Segment = ExperimentLoader.loadTestSegment()
  println("Finished reading.")
  val serializersModule = EmptySerializersModule()
  var intermediateRepresentation: List<SmtIntermediateRepresentation>
  val intermediateRepresentationTime = measureTime {
    intermediateRepresentation = getSmtIntermediateRepresentation(serializersModule, t)
  }
  println("Duration of generation of intermediate representation: $intermediateRepresentationTime")
  println("Size of intermediate representation: ${intermediateRepresentation.size}")
  var translationWrapper: SmtDataTranslationWrapper
  val translationWrapperTime = measureTime {
    translationWrapper =
        SmtDataTranslationWrapper(intermediateRepresentation, t.tickData.toTypedArray())
  }
  println("Duration of generation of SmtDataTranslationWrapper: $translationWrapperTime")
  var smtLib: String
  val smtLibTime = measureTime { smtLib = generateSmtLib(translationWrapper, solver) }
  smtLib += "(check-sat)"
  smtLib = ";Town_01, seed 2, segment 1" + System.lineSeparator() + smtLib
  println("Duration of generation of SMT-LIB: $smtLibTime")
  println("Generated SmtLib lines: ${smtLib.lines().size}")

  saveSmtFile(smtLib, solver)
  return // TODO: remove

  val statsOption = if (solver == SmtSolver.Z3) "-st" else "--stats"
  println("Running solver ...")
  println("========[ Result of the solver ]========")
  println(runSmtSolver(smtLib, solver, removeSmt2File, statsOption))
  println("========================================")
}
