/*
 * Copyright 2025 The STARS Project Authors
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tools.aqua.stars.logic.kcmftbl.smtModelChecker.experiments.reference

import kotlin.time.measureTime
import tools.aqua.stars.carla.experiments.rightOvertaking
import tools.aqua.stars.core.evaluation.PredicateContext
import tools.aqua.stars.core.evaluation.UnaryPredicate.Companion.predicate
import tools.aqua.stars.data.av.dataclasses.Vehicle
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.ExperimentLoader

val hasV0EverBeenOvertakenFromRight =
    predicate(Vehicle::class) { ctx, v0 ->
      v0.tickData.vehicles.any { v1 -> v0 != v1 && rightOvertaking.holds(ctx, v1, v0) }
    }

fun main() {
  ExperimentLoader.getSeeds("10HD").forEach { seed ->
    val segs = ExperimentLoader.loadTestSegments("10HD", "$seed")
    segs.first().vehicleIds.forEach { vehicle ->
      segs.forEachIndexed { i, seg ->
        val segStart = seg.tickData.first().currentTick
        val segDuration = seg.tickData.last().currentTick.minus(segStart)
        val segStartF = String.format("%.1f", segStart.tickSeconds).replace(",", ".")
        val segDurationF = String.format("%.1f", segDuration.differenceSeconds).replace(",", ".")
        println("$seed|Seg_$i: -c $vehicle -s $segStartF -d $segDurationF")
        val vehicle = seg.tickData.first().vehicles.find { it.id == vehicle }!!
        // Does vehicle exists in all other ticks?
        assert(!seg.tickData.any { !it.vehicles.contains(vehicle) })
        val predHolds: Boolean
        val time = measureTime {
          predHolds = hasV0EverBeenOvertakenFromRight.holds(PredicateContext(seg), vehicle)
        }
        println("PredHolds: $predHolds; $time")
        if (predHolds) {
          return
        }
        println()
      }
    }
  }
}
