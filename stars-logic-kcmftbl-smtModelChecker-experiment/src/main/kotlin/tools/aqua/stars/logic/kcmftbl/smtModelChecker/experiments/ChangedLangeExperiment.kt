package tools.aqua.stars.logic.kcmftbl.smtModelChecker.experiments

import tools.aqua.stars.data.av.dataclasses.Lane
import tools.aqua.stars.data.av.dataclasses.Vehicle
import tools.aqua.stars.logic.kcmftbl.dsl.CCB
import tools.aqua.stars.logic.kcmftbl.dsl.FormulaBuilder.Companion.formula
import tools.aqua.stars.logic.kcmftbl.dsl.formulaToLatex
import tools.aqua.stars.logic.kcmftbl.dsl.renderLatexFormula
import tools.aqua.stars.logic.kcmftbl.dsl.times

val changedLane = formula { v: CCB<Vehicle> ->
  binding(term(v * Vehicle::lane)) { l ->
    eventually {
      term<Lane>(v * Vehicle::lane) ne term(l)
    }
  }.apply { ccb.debugInfo = "l" }
}

fun main()  {
  val x = formulaToLatex(changedLane(CCB<Vehicle>().apply { debugInfo = "v" }))
  renderLatexFormula(x)
}