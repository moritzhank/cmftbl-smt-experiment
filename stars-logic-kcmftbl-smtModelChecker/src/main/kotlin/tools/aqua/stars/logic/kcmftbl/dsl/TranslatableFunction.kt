/*
 * Copyright 2024 The STARS Project Authors
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

@file:Suppress("unused")

package tools.aqua.stars.logic.kcmftbl.dsl

sealed interface TranslatableFunction<Return>

data class CallContextWrapper<Return>(
    val callContext: CallContext<*, Return>,
) : TranslatableFunction<Return>

data class Add<T : Number>(val lhs: TranslatableFunction<T>, val rhs: TranslatableFunction<T>) :
    TranslatableFunction<T>

data class Subtract<T : Number>(
    val lhs: TranslatableFunction<T>,
    val rhs: TranslatableFunction<T>
) : TranslatableFunction<T>

data class Multiply<T : Number>(
    val lhs: TranslatableFunction<T>,
    val rhs: TranslatableFunction<T>
) : TranslatableFunction<T>

data class Divide<T : Number>(val lhs: TranslatableFunction<T>, val rhs: TranslatableFunction<T>) :
    TranslatableFunction<T>
