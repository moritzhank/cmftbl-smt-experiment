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

import tools.aqua.stars.carla.experiments.changedLane
import tools.aqua.stars.core.evaluation.PredicateContext
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.ExperimentLoader

fun main() {
  val segs = ExperimentLoader.loadTestSegments("10HD", "1")
  val holds = segs.indexOfFirst { changedLane.holds(PredicateContext(it)) }
  println(segs[holds].ticks.keys.first())
}
