package tools.aqua.stars.logic.kcmftbl.smtModelChecker.experiments.reference

import tools.aqua.stars.core.evaluation.PredicateContext
import tools.aqua.stars.core.evaluation.UnaryPredicate.Companion.predicate
import tools.aqua.stars.data.av.dataclasses.TickDataDifferenceSeconds
import tools.aqua.stars.data.av.dataclasses.Vehicle
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.ExperimentLoader
import tools.aqua.stars.logic.kcmftbl.until

/*
val changedLaneAndHadSpeedBefore = formula { v: CCB<Vehicle> ->
binding(term(v * Vehicle::lane)) { l ->
    until(Pair(1, 3)) {
      term(v * Vehicle::effVelocityInKmPH) gt const(0.0)
      term(v * Vehicle::lane * Lane::laneId) ne term(l * Lane::laneId)
    }
  }.apply { ccb.debugInfo = "l" }
}
 */


val changedLaneAndHadSpeedBefore =
  predicate(Vehicle::class) { _, v ->
    val lane = v.lane
    until(
      v,
      TickDataDifferenceSeconds(1.0) to TickDataDifferenceSeconds(3.0),
      phi1 = { v0: Vehicle ->
        v0.effVelocityInKmPH > 0.0
      },
      phi2 = { v1: Vehicle ->
        v1.lane.laneId != lane.laneId
      }
    )
  }

fun main() {
  val town = "10HD"
  val segs = ExperimentLoader.loadTestSegmentsSortedByLength(town, ExperimentLoader.getSeeds(town)[1].toString())
  for (seg in segs) {
    println(seg.tickData.first().currentTick to seg.tickData.last().currentTick)
    val vehicle = seg.tickData.first().vehicles.find { it.id == 110 }!!
    val holds = changedLaneAndHadSpeedBefore.holds(PredicateContext(seg), vehicle)
    println(holds)
  }
}