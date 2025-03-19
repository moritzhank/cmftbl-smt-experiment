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

package tools.aqua.stars.logic.kcmftbl.smtModelChecker.misc

import kotlin.math.max
import kotlin.math.min
import oshi.SystemInfo
import oshi.software.os.OSProcess
import kotlin.math.pow

/**
 * Profile the memory used by the system and a particular process.
 * NOTE: Memory in swap is not recorded! (See [oshi.software.os.OSProcess.getResidentSetSize])
 */
class MemoryProfiler private constructor(private val pid: Int, val sampleRate: Int) {

  val maxSysMemUsagePercent: Double
  val maxProcMemUsageBytes: Long
  val numSamples: Int
  val elapsedMillis: Long
  val delayMillis: Long

  init {
    var minSysMemAvailable: Long = -1
    var maxProcMemUsageBytes: Long = -1
    var numSamples: Int = 0
    val startMillis = System.currentTimeMillis()
    var sysInfo = SystemInfo()
    val totalMem = sysInfo.hardware.memory.total
    var proc: OSProcess? = sysInfo.operatingSystem.getProcess(pid)
    delayMillis = System.currentTimeMillis() - startMillis
    var t1 = System.currentTimeMillis()
    while (proc != null) {
      if ((System.currentTimeMillis() - t1) < sampleRate) {
        continue
      }
      t1 = System.currentTimeMillis()
      minSysMemAvailable = if (minSysMemAvailable == -1L) {
        sysInfo.hardware.memory.available
      } else  {
        min(sysInfo.hardware.memory.available, minSysMemAvailable)
      }
      maxProcMemUsageBytes = max(proc.residentSetSize, maxProcMemUsageBytes)
      numSamples++
      try {
        require(proc.updateAttributes())
        sysInfo = SystemInfo()
      } catch (_: Exception) {
        break
      }
    }
    this.maxSysMemUsagePercent = if(minSysMemAvailable == -1L) {
      -1.0
    } else {
      (totalMem - minSysMemAvailable).toDouble() / totalMem
    }
    this.numSamples = numSamples
    this.maxProcMemUsageBytes = maxProcMemUsageBytes
    elapsedMillis = System.currentTimeMillis() - startMillis
  }

  companion object {
    fun start(pid: Int, sampleRate: Int = 100) = MemoryProfiler(pid, sampleRate)
    fun bytesToGB(bytes: Long) = bytes * 10.0.pow(9)
  }
}
