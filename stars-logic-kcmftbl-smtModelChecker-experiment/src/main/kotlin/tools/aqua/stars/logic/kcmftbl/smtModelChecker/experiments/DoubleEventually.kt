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

package tools.aqua.stars.logic.kcmftbl.smtModelChecker.experiments

import tools.aqua.stars.logic.kcmftbl.dsl.times


/*
val changedLane =
          predicate(Vehicle::class) { _, v ->
            eventually(v) { v0 ->
              eventually(v0) { v1 -> v0.lane.road == v1.lane.road && v0.lane != v1.lane }
            }
          }

          ∃v.◊(velocity(v)=0∧◊(velocity(v)=0))

 */

/*
val hasMidTrafficDensity = formula { v: CCB<Vehicle> ->
  registerFunction(TickData::vehiclesInBlock, vehiclesInBlock)
  minPrevalence(0.6) {
    val block = v * Vehicle::lane * Lane::road * Road::block
    val numVehicles =
      term(
        (v * Vehicle::tickData * TickData::vehiclesInBlock).withParam(block) *
                List<Vehicle>::size)
    const(6) leq numVehicles and (numVehicles leq const(15))
  }
}
*/
