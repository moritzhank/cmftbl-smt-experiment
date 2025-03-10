package tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation

import tools.aqua.stars.logic.kcmftbl.dsl.Formula

fun instantiateUniversalQuantification(evalInstance: EvaluationInstance, evalNode: EvaluationNode, currentTick: Int,
                                       ticks: Array<Double>): List<EvaluationNode> {
  require(evalNode.evaluationType == EvaluationType.UNIV_INST)
  require(evalNode.evaluable is Formula) { "This should be a formula." }
  val ticksInInterval = getTicksInInterval(currentTick, ticks, evalNode.interval)
  val result = mutableListOf<EvaluationNode>()
  ticksInInterval.forEach {
    val newNode = evalNode.evaluable.convert(evalInstance, EvaluationType.EVALUATE, null, it.first,
      evalNode.emittedFindingTID, evalNode.tickPrecons)
    result.add(newNode)
  }
  return result
}
/*
private fun applyUniversalInstantiation(evalNode: EvaluationNode, currentTick: Int, topMost: Boolean = true): EvaluationNode {
  val children = evalNode.children.map { applyUniversalInstantiation(it, currentTick, false) }
  val newEmittedFindingId = null
  val newEmittedID = evalNode.emittedID // Has to be generated new or?
  val emissionType = EmissionType.NONE // Has to be again calculated?
  return evalNode.copy(evalNode.evaluable, EvaluationType.EVALUATE, children.toMutableList(), null, currentTick,
    newEmittedFindingId, evalNode.tickPrecons, newEmittedID, emissionType)
}
 */

private fun getTicksInInterval(currentTick: Int, ticks: Array<Double>, interval: Pair<Int, Int>?): List<Pair<Int, Double>> {
  val listOfIndexedTicks = ticks.mapIndexed { index, tick -> Pair(index, tick) }.toMutableList()
  listOfIndexedTicks.removeIf {
    val diff = it.second - ticks[currentTick]
    diff < 0 || ((interval != null) && (diff < interval.first || diff > interval.second))
  }
  return listOfIndexedTicks
}

private fun testsForGetTicksInInterval() {
  val ticks = arrayOf(1.0, 2.0, 3.0, 4.0, 5.5)
  var result: List<Pair<Int, Double>>
  result = getTicksInInterval(0, ticks, null)
  require("[(0, 1.0), (1, 2.0), (2, 3.0), (3, 4.0), (4, 5.5)]" == result.toString())
  result = getTicksInInterval(2, ticks, null)
  require("[(2, 3.0), (3, 4.0), (4, 5.5)]" == result.toString())
  result = getTicksInInterval(1, ticks, Pair(1, 3))
  require("[(2, 3.0), (3, 4.0)]" == result.toString())
  result = getTicksInInterval(1, ticks, Pair(4, 6))
  require("[]" == result.toString())
}