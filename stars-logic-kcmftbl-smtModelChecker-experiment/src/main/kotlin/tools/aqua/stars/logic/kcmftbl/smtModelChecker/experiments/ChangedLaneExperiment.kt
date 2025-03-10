package tools.aqua.stars.logic.kcmftbl.smtModelChecker.experiments

import tools.aqua.stars.data.av.dataclasses.Vector3D
import tools.aqua.stars.data.av.dataclasses.Vehicle
import tools.aqua.stars.logic.kcmftbl.dsl.Binding
import tools.aqua.stars.logic.kcmftbl.dsl.CCB
import tools.aqua.stars.logic.kcmftbl.dsl.FormulaBuilder.Companion.formula
import tools.aqua.stars.logic.kcmftbl.dsl.TT
import tools.aqua.stars.logic.kcmftbl.dsl.formulaToLatex
import tools.aqua.stars.logic.kcmftbl.dsl.renderLatexFormula
import tools.aqua.stars.logic.kcmftbl.dsl.times
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation.EvaluationInstance
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation.EvaluationNode
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation.EvaluationType
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation.convert
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation.instantiateUniversalQuantification
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
  val evalInstance = EvaluationInstance()
  val untilPred_ = untilPred(CCB<Vehicle>().apply { debugInfo = "v" })
  val bindingF = untilPred_.getPhi().first() as Binding<*>
  evalInstance.addBoundVariable(bindingF.ccb)
  val evalNode = bindingF.inner.convert(evalInstance, EvaluationType.EVALUATE, null, 0)

  val x = instantiateUniversalQuantification(evalInstance, evalNode.children.first(), 0, arrayOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0))

  val tmp = EvaluationNode(TT, EvaluationType.UNIV_INST, x.toMutableList())
  evalNode.children.apply { removeFirst() }.add(0, tmp)

  renderTree(evalNode.generateGraphVizCode())
  renderLatexFormula(formulaToLatex(untilPred_))
}
