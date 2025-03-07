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

import tools.aqua.stars.data.av.dataclasses.Lane
import tools.aqua.stars.data.av.dataclasses.Vehicle
import tools.aqua.stars.logic.kcmftbl.dsl.CCB
import tools.aqua.stars.logic.kcmftbl.dsl.FormulaBuilder.Companion.formula
import tools.aqua.stars.logic.kcmftbl.dsl.formulaToLatex
import tools.aqua.stars.logic.kcmftbl.dsl.renderLatexFormula
import tools.aqua.stars.logic.kcmftbl.dsl.times

val changedLane = formula { v: CCB<Vehicle> ->
  binding(term(v * Vehicle::lane)) { l -> eventually { term<Lane>(v * Vehicle::lane) ne term(l) } }
      .apply { ccb.debugInfo = "l" }
}

fun main() {
  val x = formulaToLatex(changedLane(CCB<Vehicle>().apply { debugInfo = "v" }))
  renderLatexFormula(x)
}
