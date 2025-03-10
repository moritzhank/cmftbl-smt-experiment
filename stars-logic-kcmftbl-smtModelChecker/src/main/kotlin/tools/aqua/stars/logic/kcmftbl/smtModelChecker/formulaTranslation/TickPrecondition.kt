package tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation

import tools.aqua.stars.logic.kcmftbl.dsl.Relation

data class TickPrecondition(val witnessTID: String, val operation: Relation) {

  override fun toString(): String {
    return "$operation $witnessTID"
  }
}
