package tools.aqua.stars.logic.kcmftbl.smtModelChecker.experiments

import kotlinx.serialization.modules.EmptySerializersModule
import tools.aqua.stars.data.av.dataclasses.Block
import tools.aqua.stars.data.av.dataclasses.Lane
import tools.aqua.stars.data.av.dataclasses.Road
import tools.aqua.stars.data.av.dataclasses.Segment
import tools.aqua.stars.data.av.dataclasses.TickData
import tools.aqua.stars.data.av.dataclasses.Vehicle
import tools.aqua.stars.logic.kcmftbl.dsl.And
import tools.aqua.stars.logic.kcmftbl.dsl.CCB
import tools.aqua.stars.logic.kcmftbl.dsl.FormulaBuilder.Companion.formula
import tools.aqua.stars.logic.kcmftbl.dsl.Leq
import tools.aqua.stars.logic.kcmftbl.dsl.MinPrevalence
import tools.aqua.stars.logic.kcmftbl.dsl.TFunctionBuilder.Companion.function
import tools.aqua.stars.logic.kcmftbl.dsl.Variable
import tools.aqua.stars.logic.kcmftbl.dsl.times
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.ExperimentLoader
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.dataTranslation.SmtDataTranslationWrapper
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.dataTranslation.getSmtIntermediateRepresentation
import tools.aqua.stars.logic.kcmftbl.smtModelChecker.translateVariable

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

fun main() {
  val seg: Segment = ExperimentLoader.loadTestSegment()
  //val primaryEntity = seg.tickData.first().vehicles.find { it.id == seg.primaryEntityId }
  val serializersModule = EmptySerializersModule()
  var intermediateRepresentation = getSmtIntermediateRepresentation(serializersModule, seg)
  val translationWrapper = SmtDataTranslationWrapper(intermediateRepresentation)


  val y = intermediateRepresentation.find { it.ref is Vehicle && (it.ref as Vehicle).id == seg.primaryEntityId }!!.ref.getSmtID()
  val z = translationWrapper.smtIDToExternalID[y]


  val x = (((ast.getPhi().first() as MinPrevalence).inner as And).lhs as Leq<*>).rhs as Variable<Int>
  translateVariable(x, intermediateRepresentation)
}