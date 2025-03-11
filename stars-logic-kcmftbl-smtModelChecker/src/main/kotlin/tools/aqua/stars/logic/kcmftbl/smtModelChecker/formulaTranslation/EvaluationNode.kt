package tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation

import tools.aqua.stars.logic.kcmftbl.dsl.CCB
import tools.aqua.stars.logic.kcmftbl.dsl.Evaluable
import tools.aqua.stars.logic.kcmftbl.dsl.str
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.misc.TreeVisualizationNode

/**
 * Abstraction of witness creation & universal instantiation. Also contains information on how to
 * translate to SMT-LIB.
 */
interface EvaluationNode: TreeVisualizationNode {
  override val children: MutableList<EvaluationNode>
  /** Holds ID of SMT-LIB constant if one is emitted. */
  val emittedID: String?
  /** Specifies what is emitted. */
  val emissionType: EmissionType
  /** Holds debug info that is used for the TVN-representation. */
  val debugInfo: String
}

data class EvalNode(
  /** Reference to the term or formula. */
  val evaluable: Evaluable,
  override val children: MutableList<EvaluationNode>,
  /** Defines which tick is evaluated. */
  val evalTickID: Int,
  /**
   * The precondition describes a constraint on the evaluated interval: precon => phi.
   * This is needed, because certain constraints on the evaluation interval are not known before the evaluation.
   */
  val tickPrecondition: TickPrecondition?,
  override val emittedID: String?,
  override val emissionType: EmissionType,
  override val debugInfo: String = ""
): EvaluationNode {

  override fun getTVNContent(): String {
    val line1 = evaluable::class.simpleName + (if (debugInfo.isNotEmpty()) " ($debugInfo)" else "") + "\\n"
    val line2 = "EVAL at $evalTickID\\n"
    val line3 = if (tickPrecondition != null) "Tick-Precond: $tickPrecondition\\n" else ""
    val line4 = when (emissionType) {
      EmissionType.ASSERTION -> "Emits ASSERTION\\n"
      EmissionType.DECLARE_CONST -> "Emits DEC_CONST with ID: $emittedID\\n"
      EmissionType.NONE -> ""
    }
    return line1 + line2 + line3 + line4
  }

}

data class WitnessEvalNode(
  /** Reference to the term or formula. */
  val evaluable: Evaluable,
  override val children: MutableList<EvaluationNode>,
  /** Defines **relative** search "radius". */
  val interval: Pair<Int, Int>?,
  /** Holds tickID of witness for top-most WitnessEvalNode. */
  val emittedFindingTID: String?,
  override val emittedID: String?,
  override val emissionType: EmissionType,
  override val debugInfo: String = ""
) : EvaluationNode {

  override fun getTVNContent(): String {
    val line1 = evaluable::class.simpleName + (if (debugInfo.isNotEmpty()) " ($debugInfo)" else "") + "\\n"
    val line2 = "WITNESS in ${interval.str()}\\n"
    val line3 = if (emittedFindingTID != null) "WITNESS TID: $emittedFindingTID\\n" else ""
    val line4 = when (emissionType) {
      EmissionType.ASSERTION -> "Emits ASSERTION\\n"
      EmissionType.DECLARE_CONST -> "Emits DEC_CONST with ID: $emittedID\\n"
      EmissionType.NONE -> ""
    }
    return line1 + line2 + line3 + line4
  }

}

data class OrgaEvalNode(
  override val debugInfo: String,
  override val children: MutableList<EvaluationNode>,
  override val emittedID: String?,
  val referenceCCB: CCB<*>? = null
): EvaluationNode {

  override val emissionType: EmissionType = if(emittedID == null) EmissionType.NONE else EmissionType.DECLARE_CONST

  override fun getTVNContent(): String {
    return "ORGA ($debugInfo)\\n" + if (emittedID != null) "Emits DEC_CONST with ID: $emittedID\\n" else ""
  }

}

/** Is used as an intermediate step. */
data class UniversalEvalNode(
  /** Reference to the term or formula. */
  val evaluable: Evaluable,
  /** Defines **relative** search "radius". */
  val interval: Pair<Int, Int>?,
  /**
   * The precondition describes a constraint on the evaluated interval: precon => phi.
   * This is needed, because certain constraints on the evaluation interval are not known before the evaluation.
   */
  val tickPrecondition: TickPrecondition?,
  override val debugInfo: String = ""
) : EvaluationNode {

  override val children: MutableList<EvaluationNode> = mutableListOf()
  override val emittedID: String? = null
  override val emissionType: EmissionType = EmissionType.NONE

  override fun getTVNContent(): String {
    val line1 = evaluable::class.simpleName + (if (debugInfo.isNotEmpty()) " ($debugInfo)" else "") + "\\n"
    val line2 = "UNIV_INST for ${interval.str()}\\n"
    val line3 = if (tickPrecondition != null) "Tick-Precond: $tickPrecondition\\n" else ""
    return line1 + line2 + line3
  }

}