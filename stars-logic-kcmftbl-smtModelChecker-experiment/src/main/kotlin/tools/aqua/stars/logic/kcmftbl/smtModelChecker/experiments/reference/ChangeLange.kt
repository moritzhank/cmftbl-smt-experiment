package tools.aqua.stars.logic.kcmftbl.smtModelChecker.experiments.reference

import tools.aqua.stars.carla.experiments.changedLane
import tools.aqua.stars.core.evaluation.PredicateContext
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.ExperimentLoader

fun main() {
  val segs = ExperimentLoader.loadTestSegments("10HD", "1")
  val holds = segs.indexOfFirst { changedLane.holds(PredicateContext(it)) }
  println(segs[holds].ticks.keys.first())
}
