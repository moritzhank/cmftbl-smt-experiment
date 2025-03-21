package tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation

import tools.aqua.stars.logic.kcmftbl.dsl.CCB
import tools.aqua.stars.logic.kcmftbl.dsl.Formula
import tools.aqua.stars.logic.kcmftbl.dsl.Variable

fun eliminateUniversalQuantification(
  evalInstance: EvaluationInstance,
  rootNode: EvaluationNode,
  ticks: Array<Double>
) {
  while(true) {
    val listOfNodes = rootNode.iterator().asSequence().toList()
    val universalEvalNode = listOfNodes.find { it is UniversalEvalNode } as? UniversalEvalNode
    if (universalEvalNode == null) {
      break
    }
    val parent = listOfNodes.find { it.children.contains(universalEvalNode) } as EvalNode
    instantiateUniversalQuantification(evalInstance, universalEvalNode, parent, ticks)
  }
}

private fun instantiateUniversalQuantification(
  evalInstance: EvaluationInstance,
  node: UniversalEvalNode,
  parent: EvalNode,
  ticks: Array<Double>
) {
  val currentTick = parent.evalTickID
  val ticksInInterval = getTicksInInterval(currentTick, ticks, node.interval)
  val instantiatedNodes = mutableListOf<EvaluationNode>()
  ticksInInterval.forEach {
    val f = (node.evaluable as Formula)
    var newNode = f.generateEvaluation(evalInstance, EvaluationType.EVALUATE, null, it.first, null,
      node.tickPrecondition)
    getUsedUnboundVariables(evalInstance, newNode).forEach {
      val predID = evalInstance.predicateIds[it]!!
      newNode = VarIntroNode(mutableListOf(newNode), "uinst_${evalInstance.generateID()}", it, predID)
    }
    instantiatedNodes.add(newNode)
  }
  val childIndex = parent.children.indexOf(node)
  parent.children.removeAt(childIndex)
  parent.children.add(childIndex, OrgaEvalNode("UNIV_INST", instantiatedNodes))
}

private fun getUsedUnboundVariables(evalInstance: EvaluationInstance, node: EvaluationNode): Set<CCB<*>> {
  val usedUnboundVariables = mutableSetOf<CCB<*>>()
  node.iterator().asSequence().forEach { elem ->
    val n = elem as EvaluationNode
    // TODO: What is with internal binding terms??!
    if (n is EvalNode && n.evaluable is Variable<*> && !evalInstance.hasBoundBaseVariable(n.evaluable)) {
      usedUnboundVariables.add(n.evaluable.callContext.base())
    }
  }
  return usedUnboundVariables
}

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
