package tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation

import tools.aqua.stars.logic.kcmftbl.dsl.CCB
import tools.aqua.stars.logic.kcmftbl.dsl.Variable

class EvaluationInstance {

  private var nextID = 0;

  private val boundVariables = mutableSetOf<CCB<*>>()

  fun generateID(): Int = nextID++

  fun isBoundVariable(variable: Variable<*>): Boolean {
    return boundVariables.contains(variable.callContext.base())
  }

  fun addBoundVariable(ccb: CCB<*>) {
    boundVariables.add(ccb)
  }
}
