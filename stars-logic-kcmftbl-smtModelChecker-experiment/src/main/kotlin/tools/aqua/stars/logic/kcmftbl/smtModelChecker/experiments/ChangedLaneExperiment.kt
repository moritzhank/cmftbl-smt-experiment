package tools.aqua.stars.logic.kcmftbl.smtModelChecker.experiments

import tools.aqua.stars.data.av.dataclasses.Lane
import tools.aqua.stars.data.av.dataclasses.Vector3D
import tools.aqua.stars.data.av.dataclasses.Vehicle
import tools.aqua.stars.logic.kcmftbl.dsl.CCB
import tools.aqua.stars.logic.kcmftbl.dsl.FormulaBuilder.Companion.formula
import tools.aqua.stars.logic.kcmftbl.dsl.formulaToLatex
import tools.aqua.stars.logic.kcmftbl.dsl.renderLatexFormula
import tools.aqua.stars.logic.kcmftbl.dsl.times

val changedLanePred = formula { v: CCB<Vehicle> ->
  binding(term(v * Vehicle::lane)) { l ->
        eventually(Pair(2, 10)) { term(v * Vehicle::lane) ne term(l) }
      }
      .apply { ccb.debugInfo = "l" }
}

val changedLaneAndHadSpeedBefore = formula { v: CCB<Vehicle> ->
  binding(term(v * Vehicle::lane)) { l ->
    until(Pair(1, 3)) {
      term(v * Vehicle::effVelocityInKmPH) gt const(0.0)
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
  //val evalNode = changedLaneAndHadSpeedBefore.generateEvaluation(emptyVehicle(id = 1), "v", arrayOf(1.0, 2.0, 3.0, 4.0, 5.5))
  //renderTree(evalNode.generateGraphVizCode())
  //renderLatexFormula(formulaToLatex(changedLaneAndHadSpeedBefore(CCB<Vehicle>().apply { debugInfo = "v" })))
  renderLatexFormula(formulaToLatex(changedLanePred(CCB<Vehicle>().apply { debugInfo = "v" })))
}
