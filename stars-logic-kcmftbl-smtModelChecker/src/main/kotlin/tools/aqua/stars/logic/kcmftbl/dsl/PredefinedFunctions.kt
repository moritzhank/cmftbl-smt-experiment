@file:Suppress("Unused")

package tools.aqua.stars.logic.kcmftbl.dsl

import tools.aqua.stars.logic.kcmftbl.dsl.TFunctionBuilder.Companion.function

/** Contains commonly used predefined functions. */
object PredefinedFunctions {

  val IntSign = function { int: CCB<Int> ->
    branch {
          lt {
            wrap(int)
            const(0)
          }
        }
        .satisfied { const(-1) }
        .otherwise { const(1) }
  }
}
