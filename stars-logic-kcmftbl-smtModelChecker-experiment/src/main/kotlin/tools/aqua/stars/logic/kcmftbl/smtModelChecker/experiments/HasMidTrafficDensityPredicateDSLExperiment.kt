package tools.aqua.stars.logic.kcmftbl.smtModelChecker.experiments

import tools.aqua.stars.data.av.dataclasses.*
import tools.aqua.stars.logic.kcmftbl.dsl.CCB
import tools.aqua.stars.logic.kcmftbl.dsl.FormulaBuilder.Companion.formula
import tools.aqua.stars.logic.kcmftbl.dsl.TFunctionBuilder.Companion.function
import tools.aqua.stars.logic.kcmftbl.dsl.formulaToLatex
import tools.aqua.stars.logic.kcmftbl.dsl.renderLatexFormula
import tools.aqua.stars.logic.kcmftbl.dsl.times

fun main() {
  /* Original:
  // The [Block] of [Vehicle] v has between 6 and 15 vehicles in it.
  val hasMidTrafficDensity =
    predicate(Vehicle::class) { _, v ->
      minPrevalence(v, 0.6) { v -> v.tickData.vehiclesInBlock(v.lane.road.block).size in 6..15 }
    }

  /** Returns all [Vehicle]s in given [Block]. */
  fun vehiclesInBlock(block: Block): List<Vehicle> = vehicles.filter { it.lane.road.block == block }
  */
  val vehiclesInBlock = function { t: CCB<TickData>, b: CCB<Block> ->
    filter(t * TickData::vehicles) { v: CCB<Vehicle> ->
      eq {
        wrap(v * Vehicle::lane * Lane::road * Road::block)
        wrap(b)
      }
    }
  }

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

  val ast = hasMidTrafficDensity(CCB<Vehicle>().apply { debugInfo = "v" })
  renderLatexFormula(formulaToLatex(ast))
}
