package tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation

import tools.aqua.stars.logic.kcmftbl.dsl.CCB
import tools.aqua.stars.logic.kcmftbl.dsl.Variable

class EvaluationInstance {

  private var nextID = 0;

  /**
   * Captures variables introduced through bindings.
   * Primarily used such that [WitnessEvalNode] representing bound variables are interpreted as [ConstEvalNode].
   * TODO: Needs further design if nested formulas are introduced.
   */
  private val boundVariables = mutableSetOf<CCB<*>>()

  /** Captures the ids of the object from the top-level predicate definition. */
  val predicateIds = mutableMapOf<CCB<*>, Int>()

  private val variableToSMTName = mutableMapOf<CCB<*>, String>()

  fun generateID(): Int = nextID++

  /** See description of [boundVariables]. */
  fun hasBoundBaseVariable(variable: Variable<*>): Boolean {
    return boundVariables.contains(variable.callContext.base())
  }

  /** See description of [boundVariables]. */
  fun addBoundBaseVariable(ccb: CCB<*>, smtName: String) {
    boundVariables.add(ccb)
    variableToSMTName[ccb] = smtName
  }

}
