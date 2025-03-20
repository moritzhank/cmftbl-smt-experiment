package tools.aqua.stars.logic.kcmftbl.smtModelChecker.experiments

import kotlinx.serialization.modules.EmptySerializersModule
import tools.aqua.stars.data.av.dataclasses.Segment
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.ExperimentLoader
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.dataTranslation.SmtIntermediateRepresentation
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.dataTranslation.getSmtIntermediateRepresentation
import kotlin.time.measureTime

fun main() {
  val t: Segment = ExperimentLoader.loadTestSegment()
  println("Finished reading.")
  val serializersModule = EmptySerializersModule()
  var intermediateRepresentation: List<SmtIntermediateRepresentation>
  val intermediateRepresentationTime = measureTime {
    intermediateRepresentation = getSmtIntermediateRepresentation(serializersModule, t)
  }
  println("Duration of generation of intermediate representation: $intermediateRepresentationTime")
  println("Size of intermediate representation: ${intermediateRepresentation.size}")
}