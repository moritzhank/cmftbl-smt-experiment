package tools.aqua.stars.logic.kcmftbl.smtModelChecker.experiments

import tools.aqua.stars.data.av.dataclasses.Lane
import tools.aqua.stars.data.av.dataclasses.Vector3D
import tools.aqua.stars.data.av.dataclasses.Vehicle
import tools.aqua.stars.logic.kcmftbl.dsl.Binding
import tools.aqua.stars.logic.kcmftbl.dsl.CCB
import tools.aqua.stars.logic.kcmftbl.dsl.FormulaBuilder.Companion.formula
import tools.aqua.stars.logic.kcmftbl.dsl.Ne
import tools.aqua.stars.logic.kcmftbl.dsl.Until
import tools.aqua.stars.logic.kcmftbl.dsl.formulaToLatex
import tools.aqua.stars.logic.kcmftbl.dsl.renderLatexFormula
import tools.aqua.stars.logic.kcmftbl.dsl.times
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation.EvaluationInstance
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation.EvaluationType
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation.convert
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation.toReducedSyntax
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
    until(Pair(2, 10)) {
      term(v * Vehicle::id) ne const(10)
      term(v * Vehicle::lane) ne term(l)
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
  val pred = changedLanePred(CCB<Vehicle>().apply { debugInfo = "v" })
  val reducedPred = toReducedSyntax(pred.getPhi().first())
  val evalInstance = EvaluationInstance()

  val binding = reducedPred as Binding<*>
  val until = binding.inner as Until
  val ne = until.rhs as Ne<*>
  val term1 = ne.lhs
  val term2 = ne.rhs

  evalInstance.addBoundVariable(binding.ccb)

  val untilPred_ = untilPred(CCB<Vehicle>().apply { debugInfo = "v" })
  val n = (untilPred_.getPhi().first() as Binding<*>).inner.convert(evalInstance, EvaluationType.EVALUATE, null, 10)

  val nestedUntilPred_ = toReducedSyntax(nestedUntilPred(CCB<Vehicle>().apply { debugInfo = "v" }).getPhi().first())

  renderTree(n.generateGraphVizCode())
  renderLatexFormula(formulaToLatex(untilPred_))
}
