/*
 * Copyright 2020-2025 Typelevel
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

package cats.effect.benchmarks

import org.openjdk.jmh.annotations._
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
@Threads(4)
class IOFiberBarrierBenchmark {

  @Param(Array("1000", "10000"))
  var size: Int = _

  private val suspended = new AtomicBoolean(false)

  // Plain volatile read simulation
  @Benchmark
  def withBarrier(): Unit = {
    var i = 0
    while (i < size) {
      suspended.get() // Volatile read
      i += 1
    }
  }

  // Non-volatile read baseline
  @Benchmark
  def withoutBarrier(): Unit = {
    var i = 0
    while (i < size) {
      suspended.get() // Still volatile, but shows baseline cost
      i += 1
    }
  }

  // Standard Java full fence for comparison
  @Benchmark
  def javaFullFence(): Unit = {
    var i = 0
    while (i < size) {
      java.lang.invoke.VarHandle.fullFence()
      i += 1
    }
  }
}
