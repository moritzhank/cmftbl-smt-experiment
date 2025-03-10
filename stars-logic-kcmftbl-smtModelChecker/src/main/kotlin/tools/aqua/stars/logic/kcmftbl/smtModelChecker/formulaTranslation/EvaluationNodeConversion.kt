package tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation

import tools.aqua.stars.logic.kcmftbl.dsl.*

/** Generates an [EvaluationNode] from a [Term]. */
fun <T> Term<T>.convert(
    evalInstance: EvaluationInstance,
    evaluationType: EvaluationType,
    interval: Pair<Int, Int>?,
    evalTickID: Int? = null
): EvaluationNode {
  return when(this) {
    is Constant -> {
      EvaluationNode(this, EvaluationType.CONSTANT, mutableListOf(), null, null, null, listOf(), null,
        EmissionType.TERM, this.str())
    }
    is Variable -> {
      val bound = evalInstance.isBoundVariable(this)
      val emittedID = if (evaluationType == EvaluationType.WITNESS && !bound) "wtns_${evalInstance.generateID()}" else null
      val emissionType = when (evaluationType) {
        EvaluationType.EVALUATE -> EmissionType.TERM
        EvaluationType.WITNESS -> if (!bound) EmissionType.DECLARE_CONST else EmissionType.TERM
        EvaluationType.UNIV_INST -> EmissionType.NONE
        EvaluationType.CONSTANT -> error("This path should not be reached.")
      }
      EvaluationNode(this, evaluationType, mutableListOf(), interval, evalTickID, null, listOf(), emittedID,
        emissionType, this.str().trimEnd('.'))
    }
  }
}

/** Generates an [EvaluationNode] from a [Formula]. */
fun Formula.convert(
    evalInstance: EvaluationInstance,
    evaluationType: EvaluationType,
    interval: Pair<Int, Int>?,
    evalTickID: Int? = null,
    emittedFindingTID: String? = null,
    preconditions: List<TickPrecondition> = listOf()
): EvaluationNode {
  return when (this) {
    is EvaluableRelation<*> -> convertEvaluableRelation(this, evalInstance, evaluationType, interval, evalTickID,
      emittedFindingTID, preconditions)
    is TT -> EvaluationNode(this, EvaluationType.CONSTANT, mutableListOf(), null, null, null, listOf(), null,
      EmissionType.NONE)
    is Until -> convertUntil(this, evalInstance, evaluationType, interval, evalTickID,
      emittedFindingTID, preconditions)
    else -> error("The conversion is not yet available for the formula type \"${this::class.simpleName}\".")
  }
}

private fun convertEvaluableRelation(
  evalRelation: EvaluableRelation<*>,
  evalInstance: EvaluationInstance,
  evaluationType: EvaluationType,
  interval: Pair<Int, Int>?,
  evalTickID: Int? = null,
  emittedFindingTID: String? = null,
  preconditions: List<TickPrecondition> = listOf()
): EvaluationNode {
  val evalNodeLhs = evalRelation.lhs.convert(evalInstance, evaluationType, interval, evalTickID)
  val evalNodeRhs = evalRelation.rhs.convert(evalInstance, evaluationType, interval, evalTickID)
  return EvaluationNode(evalRelation, evaluationType, mutableListOf(evalNodeLhs, evalNodeRhs), interval, evalTickID,
    emittedFindingTID, preconditions, null, EmissionType.ASSERTION)
}

private fun convertUntil(
  u: Until,
  evalInstance: EvaluationInstance,
  evaluationType: EvaluationType,
  interval: Pair<Int, Int>?,
  evalTickID: Int? = null,
  emittedFindingTID: String? = null,
  preconditions: List<TickPrecondition> = listOf()
): EvaluationNode {
  return when (evaluationType) {
    EvaluationType.EVALUATE -> {
      val tid = "twtns_${evalInstance.generateID()}"
      val evalNodeLhs = u.lhs.convert(evalInstance, EvaluationType.UNIV_INST, u.interval, evalTickID, null,
        preconditions.toMutableList().apply { add(TickPrecondition(tid, Relation.Lt)) })
      val evalNodeRhs = u.rhs.convert(evalInstance, EvaluationType.WITNESS, u.interval, evalTickID, tid,
        preconditions)
      EvaluationNode(u, evaluationType, mutableListOf(evalNodeLhs, evalNodeRhs), interval, evalTickID,
        emittedFindingTID, preconditions, null, EmissionType.NONE)
    }
    else -> error("Nested evaluations are not supported yet.")
  }
}