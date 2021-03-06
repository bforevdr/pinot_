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

package com.linkedin.pinot.core.segment.store;

import com.linkedin.pinot.core.indexsegment.generator.SegmentVersion;
import com.linkedin.pinot.core.segment.index.SegmentMetadataImpl;
import com.linkedin.pinot.core.segment.memory.PinotDataBuffer;
import java.io.IOException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ColumnIndexDirectoryTestHelper {
  private static Logger LOGGER = LoggerFactory.getLogger(ColumnIndexDirectoryTestHelper.class);

  static ColumnIndexType[] indexTypes = ColumnIndexType.values();

  static PinotDataBuffer newIndexBuffer(ColumnIndexDirectory columnDirectory, String column, int size, int index)
      throws IOException {
    String columnName = column + Integer.toString(index);
    ColumnIndexType indexType = indexTypes[index % indexTypes.length];
    PinotDataBuffer buf = null;
    switch (indexType) {
      case DICTIONARY:
        buf = columnDirectory.newDictionaryBuffer(columnName, size);
        break;
      case FORWARD_INDEX:
        buf = columnDirectory.newForwardIndexBuffer(columnName, size);
        break;
      case INVERTED_INDEX:
        buf = columnDirectory.newInvertedIndexBuffer(columnName, size);
        break;
    }
    return buf;
  }

  static PinotDataBuffer getIndexBuffer(ColumnIndexDirectory columnDirectory, String column, int index)
      throws IOException {
    String columnName = column + Integer.toString(index);
    ColumnIndexType indexType = indexTypes[index % indexTypes.length];
    PinotDataBuffer buf = null;
    switch (indexType) {
      case DICTIONARY:
        buf = columnDirectory.getDictionaryBufferFor(columnName);
        break;
      case FORWARD_INDEX:
        buf = columnDirectory.getForwardIndexBufferFor(columnName);
        break;
      case INVERTED_INDEX:
        buf = columnDirectory.getInvertedIndexBufferFor(columnName);
        break;
    }
    return buf;
  }


  static void verifyMultipleReads(ColumnIndexDirectory columnDirectory, String column, int numIter)
      throws Exception {
    for (int ii = 0; ii < numIter; ii++) {
      PinotDataBuffer buf = null;
      try {
        buf = ColumnIndexDirectoryTestHelper.getIndexBuffer(columnDirectory, column, ii);
        int numValues = (int) (buf.size() / 4);
        for (int j = 0; j < numValues; ++j) {
          Assert.assertEquals(buf.getInt(j * 4), j, "Inconsistent value at index: " + j);
        }
      } finally {
        if (buf != null) {
          buf.close();
        }
      }
    }
  }

  static void performMultipleWrites(ColumnIndexDirectory columnDirectory, String column, int size, int numIter)
      throws Exception {
    for (int i = 0; i < numIter; i++) {
      PinotDataBuffer buf = null;
      try {
        buf = ColumnIndexDirectoryTestHelper.newIndexBuffer(columnDirectory, column, size, i);
        int numValues = size / 4;
        for (int j = 0; j < numValues; j++) {
          buf.putInt(j * 4, j);
        }
      } finally {
        if (buf != null) {
          buf.close();
        }
      }
    }
  }

  static SegmentMetadataImpl writeMetadata(SegmentVersion version) {
    SegmentMetadataImpl meta = mock(SegmentMetadataImpl.class);
    when(meta.getVersion()).thenReturn(version.toString());
    when(meta.getDictionaryFileName(anyString(), anyString()))
        .thenAnswer(new Answer<String>() {
          @Override
          public String answer(InvocationOnMock invocationOnMock)
              throws Throwable {
            return invocationOnMock.getArguments()[0] + ".dictionary";
          }
        });
    when(meta.getForwardIndexFileName(anyString(), anyString()))
        .thenAnswer(new Answer<String>() {
          @Override
          public String answer(InvocationOnMock invocationOnMock)
              throws Throwable {
            return invocationOnMock.getArguments()[0] + ".fwd.index";
          }
        });

    when(meta.getBitmapInvertedIndexFileName(anyString(), anyString()))
        .thenAnswer(new Answer<String>() {
          @Override
          public String answer(InvocationOnMock invocationOnMock)
              throws Throwable {
            return invocationOnMock.getArguments()[0] + ".ii";
          }
        });
    return meta;
  }
}
