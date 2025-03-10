package tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation

import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.logic.kcmftbl.dsl.*

fun <T: EntityType<*, *, *, *, *>>
        ((CallContextBase<T>) -> FormulaBuilder).generateEvaluation(holdsFor: T, name: String) : EvaluationNode {
  // TODO: Use TickData to eliminate universal quantification
  val evalInstance = EvaluationInstance()
  val ccb = CCB<T>().apply { debugInfo = name }
  val ccbSMTName = "predv_${evalInstance.generateID()}"
  evalInstance.addIntroducedBaseVariable(ccb, ccbSMTName)
  val formula = this(ccb).getPhi().first()
  val evalInnerNode = formula.generateEvaluation(evalInstance, EvaluationType.EVALUATE, null, 0, null, null)
  return OrgaEvalNode("UnaryPredicate (${ccb.debugInfo})", mutableListOf(evalInnerNode), ccbSMTName)
}

/** Generates an [EvaluationNode] from a [Term]. */
private fun <T> Term<T>.generateEvaluation(
  evalInstance: EvaluationInstance,
  evaluationType: EvaluationType,
  interval: Pair<Int, Int>?,
  evalTickID: Int?
): EvaluationNode {
  return when(this) {
    is Constant -> ConstEvalNode(this, this.str())
    is Variable -> {
      if (evalInstance.hasBoundBaseVariable(this)) {
        ConstEvalNode(this, this.str())
      } else {
        when (evaluationType) {
          EvaluationType.EVALUATE -> {
            EvalNode(this, mutableListOf(), evalTickID, null, null, EmissionType.NONE, this.str())
          }
          EvaluationType.WITNESS -> {
            WitnessEvalNode(this, mutableListOf(), interval, null, "wtns_${evalInstance.generateID()}",
              EmissionType.DECLARE_CONST, this.str())
          }
          EvaluationType.UNIV_INST -> error("This path should not be reached.")
        }
      }
    }
  }
}

/** Generates an [EvaluationNode] from a [Formula]. */
private fun Formula.generateEvaluation(
    evalInstance: EvaluationInstance,
    evaluationType: EvaluationType,
    interval: Pair<Int, Int>?,
    evalTickID: Int?,
    emittedFindingTID: String?,
    precond: TickPrecondition?
): EvaluationNode {
  return when (this) {
    is EvaluableRelation<*> -> {
      generateEvaluationForEvaluableRelation(this, evalInstance, evaluationType, interval, evalTickID,
        emittedFindingTID, precond)
    }
    is Until -> {
      generateEvaluationForUntil(this, evalInstance, evaluationType, interval, evalTickID, emittedFindingTID, precond)
    }
    is TT -> ConstEvalNode(this)
    is Binding<*> -> {
      generateEvaluationForBinding(this, evalInstance, evaluationType, interval, evalTickID,
        emittedFindingTID, precond)
    }
    else -> error("The generation is not yet available for the formula type \"${this::class.simpleName}\".")
  }
}

private fun generateEvaluationForEvaluableRelation(
  evalRelation: EvaluableRelation<*>,
  evalInstance: EvaluationInstance,
  evaluationType: EvaluationType,
  interval: Pair<Int, Int>?,
  evalTickID: Int?,
  emittedFindingTID: String?,
  precond: TickPrecondition?
): EvaluationNode {
  return when (evaluationType) {
    EvaluationType.EVALUATE -> {
      val evalNodeLhs = evalRelation.lhs.generateEvaluation(evalInstance, evaluationType, interval, evalTickID)
      val evalNodeRhs = evalRelation.rhs.generateEvaluation(evalInstance, evaluationType, interval, evalTickID)
      EvalNode(evalRelation, mutableListOf(evalNodeLhs, evalNodeRhs), evalTickID, precond, null, EmissionType.NONE)
    }
    EvaluationType.WITNESS -> {
      val evalNodeLhs = evalRelation.lhs.generateEvaluation(evalInstance, evaluationType, interval, evalTickID)
      val evalNodeRhs = evalRelation.rhs.generateEvaluation(evalInstance, evaluationType, interval, evalTickID)
      WitnessEvalNode(evalRelation, mutableListOf(evalNodeLhs, evalNodeRhs), interval, emittedFindingTID, null,
        EmissionType.ASSERTION)
    }
    EvaluationType.UNIV_INST -> UniversalEvalNode(evalRelation, interval, precond)
  }
}

private fun generateEvaluationForUntil(
  until: Until,
  evalInstance: EvaluationInstance,
  evaluationType: EvaluationType,
  interval: Pair<Int, Int>?, // Needed for WITNESS evaluation
  evalTickID: Int?,
  emittedFindingTID: String?, // Needed for WITNESS evaluation
  precond: TickPrecondition?
): EvaluationNode {
  return when (evaluationType) {
    EvaluationType.EVALUATE -> {
      val tid = "twtns_${evalInstance.generateID()}"
      val evalNodeRhs = until.rhs.generateEvaluation(evalInstance, EvaluationType.WITNESS, until.interval, null, tid,
        null)
      val evalNodeLhs = until.lhs.generateEvaluation(evalInstance, EvaluationType.UNIV_INST, until.interval, null,
        null, TickPrecondition(evalNodeRhs as WitnessEvalNode, Relation.Lt))
      EvalNode(until, mutableListOf(evalNodeLhs, evalNodeRhs), evalTickID, precond, null, EmissionType.NONE)
    }
    else -> error("Nested evaluations with Until are not supported yet.")
  }
}

private fun generateEvaluationForBinding(
  binding: Binding<*>,
  evalInstance: EvaluationInstance,
  evaluationType: EvaluationType,
  interval: Pair<Int, Int>?, // Needed for WITNESS evaluation
  evalTickID: Int?,
  emittedFindingTID: String?, // Needed for WITNESS evaluation
  precond: TickPrecondition?
): EvaluationNode {
  return when (evaluationType) {
    EvaluationType.EVALUATE -> {
      val boundVarID = "bnd_${evalInstance.generateID()}"
      val evalTerm = binding.bindTerm.generateEvaluation(evalInstance, EvaluationType.EVALUATE, null, evalTickID)
      evalInstance.addBoundBaseVariable(binding.ccb, boundVarID)
      val evalNode = binding.inner.generateEvaluation(evalInstance, EvaluationType.EVALUATE, null, evalTickID, null,
        null)
      EvalNode(binding, mutableListOf(evalTerm, evalNode), evalTickID, precond, boundVarID, EmissionType.DECLARE_CONST,
        binding.ccb.debugInfo ?: ""
      )
    }
    else -> error("Evaluating a binding in anything other than EVALUATE mode is not yet supported.")
  }
}