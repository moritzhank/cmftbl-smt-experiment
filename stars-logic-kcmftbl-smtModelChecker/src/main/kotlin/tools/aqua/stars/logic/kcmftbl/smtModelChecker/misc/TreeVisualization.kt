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

import java.io.File
import java.net.URI
import java.net.URLEncoder
import java.util.UUID

interface TreeVisualizationNode {
  fun getTVNContent(): String

  val children: List<TreeVisualizationNode>
}

fun TreeVisualizationNode.generateGraphVizCode(): String {
  val result = StringBuilder()
  result.append("digraph G {")
  result.append("node [shape=box];")
  val queue =
      ArrayDeque<Pair<Int, TreeVisualizationNode>>().apply {
        add(Pair(0, this@generateGraphVizCode))
      }
  var nextId = 1
  while (queue.isNotEmpty()) {
    val node = queue.removeFirst()
    result.append("n${node.first} [label=\"${node.second.getTVNContent()}\"];")
    node.second.children.forEach {
      val childID = nextId++
      queue.add(Pair(childID, it))
      result.append("n${node.first} -> n$childID;")
    }
  }
  result.append("}")
  return result.toString()
}

/** Generate an SVG of the input tree. */
fun renderTree(graphviz: String, deletePrevSvgs: Boolean = true) {
  val formulaImgs = getAbsolutePathFromProjectDir("_treeSvgs")
  File(formulaImgs)
      .apply {
        if (deletePrevSvgs) {
          deleteRecursively()
        }
      }
      .mkdir()
  val encodedLatex = URLEncoder.encode(graphviz, "utf-8")
  val url = URI("https://quickchart.io/graphviz?graph=$encodedLatex").toURL()
  val imageData = url.readBytes()
  val imageFilePath = "$formulaImgs${File.separator}${UUID.randomUUID()}.svg"
  File(imageFilePath).apply { writeBytes(imageData) }
}
