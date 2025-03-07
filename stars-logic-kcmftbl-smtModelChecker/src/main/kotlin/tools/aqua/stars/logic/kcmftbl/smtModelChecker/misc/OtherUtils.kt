/*
 * Copyright 2024-2025 The STARS Project Authors
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

@file:Suppress("ExpressionBodySyntax")

package tools.aqua.stars.logic.kcmftbl.smtModelChecker.misc

/** Replaces first character with a lower-case one. */
fun String.firstCharLower(): String = this.replaceFirstChar { it.lowercaseChar() }

/** Converts given primitive to SmtLib format. */
fun Any.toSmtLibPrimitiveFormat(smtLibTermForNegativeNumbers: (Number) -> String): String {
  return when (this) {
    is String -> "\"$this\""
    is Number ->
        if (!this.isNegative()) this._toSmtLibPrimitiveFormat()
        else smtLibTermForNegativeNumbers(this)
    else -> this.toString()
  }
}

/** Converts number to SmtLib format. */
fun Number._toSmtLibPrimitiveFormat(): String {
  return when (this) {
    is Int -> this.toString()
    is Long -> this.toString()
    is Float -> this.toBigDecimal().toPlainString()
    is Double -> this.toBigDecimal().toPlainString()
    else -> this.toString()
  }
}

/** Generate ITE-structure for SMT-LIB. */
fun <T> generateEqualsITEStructure(
    elements: Collection<T>,
    comparisonVarName: String,
    ifStr: (T) -> String,
    thenStr: (T) -> String,
    defaultValue: String? = null
): String {
  val iteStructureFront = StringBuilder("")
  var bracketsNeeded = 0
  val firstElem = elements.first()
  elements.forEachIndexed { index, elem ->
    // Skip first element if no default is given
    val skip = defaultValue == null && index == 0
    if (!skip) {
      iteStructureFront.append("(ite (= $comparisonVarName ${ifStr(elem)}) ${thenStr(elem)} ")
      bracketsNeeded++
    }
  }
  if (defaultValue == null) {
    iteStructureFront.append("${thenStr(firstElem)}${")".repeat(bracketsNeeded)}")
  } else {
    iteStructureFront.append("$defaultValue${")".repeat(bracketsNeeded)}")
  }
  return iteStructureFront.toString()
}

/** Negate a number. */
fun Number.negate(): Number {
  require(this != Int.MIN_VALUE) { "Int.MIN_VALUE cannot be negated." }
  return when (this) {
    is Int -> -this
    is Long -> -this
    is Float -> -this
    is Double -> -this
    else -> throw IllegalArgumentException("Unsupported number type: ${this::class.simpleName}")
  }
}

/** Is number smaller than zero? */
fun Number.isNegative(): Boolean {
  return when (this) {
    is Int -> this < 0
    is Long -> this < 0
    is Float -> this < 0
    is Double -> this < 0
    else -> throw IllegalArgumentException("Unsupported number type: ${this::class.simpleName}")
  }
}
