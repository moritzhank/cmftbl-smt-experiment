package tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation

import tools.aqua.stars.logic.kcmftbl.dsl.Relation

data class TickPrecondition(val witness: WitnessEvalNode, val operation: Relation) {

  init {
    requireNotNull(witness.emittedFindingTID)
  }

  override fun toString(): String {
    return "$operation time(${witness.emittedFindingTID})"
  }

}
