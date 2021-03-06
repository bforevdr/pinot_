/**
 * Copyright (C) 2014-2015 LinkedIn Corp. (pinot-core@linkedin.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.linkedin.pinot.core.operator.aggregation.function;

import com.linkedin.pinot.core.operator.groupby.ResultHolder;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;


/**
 * Interface for Aggregation functions.
 */
public interface AggregationFunction {

  /**
   * Performs aggregation on the input array of values.
   *
   * @param values
   * @return
   */
  double aggregate(double[] values);

  /**
   * Perform a group-by
   * @param length
   * @param valueArrayIndexToGroupKeys
   * @param resultHolder
   * @param valueArray
   */
  void apply(int length, Int2ObjectOpenHashMap valueArrayIndexToGroupKeys, ResultHolder resultHolder,
      double[] valueArray);

  /**
   * Reduce the aggregation. For example, in case of avg/range being performed
   * for blocks of docIds, the aggregate() function may be maintaining a pair of values
   * (eg, sum and total count). Use the reduce interface to compute the final aggregation
   * value from intermediate data.
   *
   * @return
   */
  double reduce();

  /**
   * Return default value for the aggregation function.
   * @return
   */
  double getDefaultValue();

  void apply(int length, int[] docIdToGroupKey, ResultHolder resultHolder, double[]... valueArray);
}
