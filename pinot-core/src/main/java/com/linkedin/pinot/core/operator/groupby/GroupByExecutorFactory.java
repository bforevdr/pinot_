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
package com.linkedin.pinot.core.operator.groupby;

import com.linkedin.pinot.common.request.AggregationInfo;
import com.linkedin.pinot.common.request.GroupBy;
import com.linkedin.pinot.core.common.DataSource;
import com.linkedin.pinot.core.common.DataSourceMetadata;
import com.linkedin.pinot.core.indexsegment.IndexSegment;
import java.util.List;


/**
 * Factory class for creating GroupByExecutor of different types.
 */
public class GroupByExecutorFactory {
  /**
   * Returns the appropriate implementation of GroupByExecutor based on
   * pre-defined criteria.
   *
   * Returns SingleValueGroupByExecutor if all group by columns are single-valued,
   * returns MultiValueGroupByExecutor otherwise.
   *
   * @param indexSegment
   * @param aggregationInfoList
   * @param groupBy
   * @return
   */
  public static GroupByExecutor getGroupByExecutor(IndexSegment indexSegment, List<AggregationInfo> aggregationInfoList,
      GroupBy groupBy) {

    if (hasMultiValueGroupByColumns(indexSegment, groupBy)) {
      return new MultiValueGroupByExecutor(indexSegment, aggregationInfoList, groupBy);
    } else {
      int maxNumGroupKeys = computeMaxNumGroupKeys(indexSegment, groupBy);
      return new SingleValueGroupByExecutor(indexSegment, aggregationInfoList, groupBy, maxNumGroupKeys);
    }
  }

  /**
   * Returns true if any of the group-by columns are multi-valued, false otherwise.
   *
   * @param indexSegment
   * @param groupBy
   * @return
   */
  private static boolean hasMultiValueGroupByColumns(IndexSegment indexSegment, GroupBy groupBy) {
    for (String column : groupBy.getColumns()) {
      DataSource dataSource = indexSegment.getDataSource(column);
      DataSourceMetadata dataSourceMetadata = dataSource.getDataSourceMetadata();

      // For multi-valued group by columns, return max.
      if (!dataSourceMetadata.isSingleValue()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Computes and returns the product of cardinality of all group-by columns.
   * If product cannot fit into an integer, or if any of the group-by columns
   * is a multi-valued column, returns Integer.MAX_VALUE.
   *
   * @param indexSegment
   * @param groupBy
   * @return
   */
  private static int computeMaxNumGroupKeys(IndexSegment indexSegment, GroupBy groupBy) {
    int maxGroupKeys = 1;

    for (String column : groupBy.getColumns()) {
      DataSource dataSource = indexSegment.getDataSource(column);
      DataSourceMetadata dataSourceMetadata = dataSource.getDataSourceMetadata();

      // For multi-valued group by columns, return max.
      if (!dataSourceMetadata.isSingleValue()) {
        return Integer.MAX_VALUE;
      }

      // Protecting against overflow.
      int cardinality = dataSourceMetadata.cardinality();
      if (maxGroupKeys > (Integer.MAX_VALUE / cardinality)) {
        return Integer.MAX_VALUE;
      }
      maxGroupKeys *= dataSourceMetadata.cardinality();
    }

    return maxGroupKeys;
  }
}
