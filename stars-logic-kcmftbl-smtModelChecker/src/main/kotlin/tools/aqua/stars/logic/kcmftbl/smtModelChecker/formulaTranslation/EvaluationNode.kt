package tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation

import tools.aqua.stars.logic.kcmftbl.dsl.Evaluable
import tools.aqua.stars.logic.kcmftbl.dsl.str
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.misc.TreeVisualizationNode

/**
 * Abstraction of witness creation & universal instantiation. Also contains hints on how to
 * translate to SMT-LIB.
 */
data class EvaluationNode(
    /** Reference to the term or formula. */
    val evaluable: Evaluable,
    /** How are the children evaluated? */
    val evaluationType: EvaluationType,
    override val children: MutableList<EvaluationNode> = mutableListOf<EvaluationNode>(),
    /** Holds interval if [evaluationType] is WITNESS. Defines **relative** search "radius". */
    val interval: Pair<Int, Int>? = null,
    /** Holds tickID if [evaluationType] is EVALUATE. Defines which tick is evaluated. */
    val evalTickID: Int? = null,
    /** Holds tickID of witness for top-most WITNESS node. */
    val emittedFindingTID: String? = null,
    /**
     * The preconditions describe constraints on the evaluated interval: precon_1 & ... & precon_N
     * => phi This is needed, because certain constraints on the evaluation interval are not known
     * before the evaluation.
     */
    val tickPrecons: List<TickPrecondition> = listOf<TickPrecondition>(),
    /** Holds ID of SMT-LIB constant if one is emitted. */
    val emittedID: String? = null,
    /** Specifies what is emitted. */
    val emissionType: EmissionType = EmissionType.NONE,
    /** Holds debug info that is used for the TVN-representation. */
    val debugInfo: String = ""
) : TreeVisualizationNode {

  override fun getTVNContent(): String {
    val firstLine =
        evaluable::class.simpleName + (if (debugInfo.isNotEmpty()) " ($debugInfo)" else "") + "\\n"
    val secondLine =
        when (evaluationType) {
          EvaluationType.EVALUATE -> "EVAL at $evalTickID"
          EvaluationType.WITNESS -> "WITNESS in ${interval.str()}"
          EvaluationType.UNIV_INST -> "UNIV_INST for ${interval.str()}"
          EvaluationType.CONSTANT -> "CONSTANT"
        } + "\\n"
    val thirdLine = if (emittedFindingTID != null) "WITNESS TID: $emittedFindingTID\\n" else ""
    val fourthLine = if (tickPrecons.isNotEmpty()) "Tick-Precons: $tickPrecons\\n" else ""
    val fifthLine =
        when (emissionType) {
          EmissionType.NONE -> ""
          EmissionType.ASSERTION -> "Emits ASSERTION\\n"
          EmissionType.DECLARE_CONST -> "Emits DEC_CONST with ID: $emittedID\\n"
          EmissionType.TERM -> "Emits TERM\\n"
        }
    return firstLine + secondLine + thirdLine + fourthLine + fifthLine
  }
}