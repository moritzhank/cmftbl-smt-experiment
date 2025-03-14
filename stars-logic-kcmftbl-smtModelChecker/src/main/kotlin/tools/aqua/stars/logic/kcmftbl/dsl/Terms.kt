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

@file:Suppress(
    "UndocumentedPublicClass",
    "UndocumentedPublicFunction",
    "UndocumentedPublicProperty",
    "ExpressionBodySyntax")

package tools.aqua.stars.logic.kcmftbl.dsl

sealed interface Term<Type> : Evaluable

data class Constant<Type>(val value: Type) : Term<Type>

data class Variable<Type>(val callContext: CallContext<*, Type>) : Term<Type>

/** Create a deep copy of [term]. */
fun <T> copyTerm(term: Term<T>): Term<T> {
  return when (term) {
    is Constant -> Constant(term.value)
    is Variable -> Variable(term.callContext)
  }
}
