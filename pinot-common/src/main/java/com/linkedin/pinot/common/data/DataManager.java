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
package com.linkedin.pinot.common.data;

import com.linkedin.pinot.common.config.AbstractTableConfig;
import com.linkedin.pinot.common.segment.SegmentMetadata;
import com.linkedin.pinot.common.segment.SegmentMetadataLoader;
import org.apache.commons.configuration.Configuration;


public interface DataManager {
  void init(Configuration dataManagerConfig);

  void start();

  void addSegment(SegmentMetadata segmentMetadata, AbstractTableConfig tableConfig) throws Exception;

  void removeSegment(String segmentName);

  void refreshSegment(String oldSegmentName, SegmentMetadata newSegmentMetadata);

  void shutDown();

  String getSegmentDataDirectory();

  String getSegmentFileDirectory();

  SegmentMetadataLoader getSegmentMetadataLoader();

  SegmentMetadata getSegmentMetadata(String tableName, String segmentName);

  boolean isStarted();
}
