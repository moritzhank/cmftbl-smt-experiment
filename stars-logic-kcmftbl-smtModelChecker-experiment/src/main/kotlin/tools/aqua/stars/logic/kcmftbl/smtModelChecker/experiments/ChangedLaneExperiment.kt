package tools.aqua.stars.logic.kcmftbl.smtModelChecker.experiments

import tools.aqua.stars.data.av.dataclasses.Lane
import tools.aqua.stars.data.av.dataclasses.Vector3D
import tools.aqua.stars.data.av.dataclasses.Vehicle
import tools.aqua.stars.logic.kcmftbl.dsl.CCB
import tools.aqua.stars.logic.kcmftbl.dsl.FormulaBuilder.Companion.formula
import tools.aqua.stars.logic.kcmftbl.dsl.formulaToLatex
import tools.aqua.stars.logic.kcmftbl.dsl.renderLatexFormula
import tools.aqua.stars.logic.kcmftbl.dsl.times
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation.generateEvaluation
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.misc.emptyVehicle
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.misc.generateGraphVizCode
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.misc.renderTree

val changedLanePred = formula { v: CCB<Vehicle> ->
  binding(term(v * Vehicle::lane)) { l ->
        eventually(Pair(2, 10)) { term(v * Vehicle::lane) ne term(l) }
      }
      .apply { ccb.debugInfo = "l" }
}

val untilPred = formula { v: CCB<Vehicle> ->
  binding(term(v * Vehicle::lane)) { l ->
    until(Pair(1, 3)) {
      term(v * Vehicle::id) ne const(10)
      term(v * Vehicle::lane * Lane::laneId) ne term(l * Lane::laneId)
    }
  }.apply { ccb.debugInfo = "l" }
}

val nestedUntilPred = formula { v: CCB<Vehicle> ->
  eventually {
    (term(v * Vehicle::velocity * Vector3D::x) eq const(0.0)) and eventually(Pair(2, 10)) {
      term(v * Vehicle::velocity * Vector3D::x) ne const(0.0)
    }
  }
}

fun main() {
  val evalNode = untilPred.generateEvaluation(emptyVehicle(id = 1), "v")

  renderTree(evalNode.generateGraphVizCode())
  renderLatexFormula(formulaToLatex(untilPred(CCB<Vehicle>().apply { debugInfo = "v" })))
}
