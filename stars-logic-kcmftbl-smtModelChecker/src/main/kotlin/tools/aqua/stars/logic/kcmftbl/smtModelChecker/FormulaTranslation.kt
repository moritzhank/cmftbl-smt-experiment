package tools.aqua.stars.logic.kcmftbl.smtModelChecker

import tools.aqua.stars.logic.kcmftbl.dsl.Variable
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.dataTranslation.SmtIntermediateRepresentation

fun <T> translateVariable(variable: Variable<T>, intermediateRep: List<SmtIntermediateRepresentation>) {
  println(variable.toString())
}