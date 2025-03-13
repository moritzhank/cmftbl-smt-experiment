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

package tools.aqua.stars.data.av.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import tools.aqua.stars.data.av.dataclasses.*

/** Custom [KSerializer] for [Vehicle]. */
class VehicleSerializer : KSerializer<Vehicle> {

  override val descriptor: SerialDescriptor =
      buildClassSerialDescriptor("Vehicle") {
        element<Int>("id")
        element<TickData>("tickData")
        element<Double>("positionOnLane")
        element<Lane>("lane")
        element<String>("typeId")
        element<Boolean>("isEgo")
        element<Location>("location")
        element<Vector3D>("forwardVector")
        element<Rotation>("rotation")
        element<Vector3D>("velocity")
        element<Vector3D>("acceleration")
        element<Vector3D>("angularVelocity")
        element<Double>("effVelocityInKmPH")
      }

  override fun serialize(encoder: Encoder, value: Vehicle) {
    encoder.encodeStructure(descriptor) {
      encodeSerializableElement(descriptor, 0, Int.serializer(), value.id)
      encodeSerializableElement(descriptor, 1, TickData.serializer(), value.tickData)
      encodeSerializableElement(descriptor, 2, Double.serializer(), value.positionOnLane)
      encodeSerializableElement(descriptor, 3, Lane.serializer(), value.lane)
      encodeSerializableElement(descriptor, 4, String.serializer(), value.typeId)
      encodeSerializableElement(descriptor, 5, Boolean.serializer(), value.isEgo)
      encodeSerializableElement(descriptor, 6, Location.serializer(), value.location)
      encodeSerializableElement(descriptor, 7, Vector3D.serializer(), value.forwardVector)
      encodeSerializableElement(descriptor, 8, Rotation.serializer(), value.rotation)
      encodeSerializableElement(descriptor, 9, Vector3D.serializer(), value.velocity)
      encodeSerializableElement(descriptor, 10, Vector3D.serializer(), value.acceleration)
      encodeSerializableElement(descriptor, 11, Vector3D.serializer(), value.angularVelocity)
      encodeSerializableElement(descriptor, 12, Double.serializer(), value.effVelocityInKmPH)
    }
  }

  override fun deserialize(decoder: Decoder): Vehicle {
    error("This serializer does not support decoding.")
  }
}
