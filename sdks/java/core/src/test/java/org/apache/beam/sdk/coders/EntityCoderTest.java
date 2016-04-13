/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.coders;

import static com.google.api.services.datastore.client.DatastoreHelper.makeKey;
import static com.google.api.services.datastore.client.DatastoreHelper.makeProperty;
import static com.google.api.services.datastore.client.DatastoreHelper.makeValue;

import org.apache.beam.sdk.testing.CoderProperties;
import org.apache.beam.sdk.util.CoderUtils;

import com.google.api.services.datastore.DatastoreV1.Entity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;

/**
 * Test case for {@link EntityCoder}.
 */
@RunWith(JUnit4.class)
public class EntityCoderTest {

  private static final Coder<Entity> TEST_CODER = EntityCoder.of();

  // Presumably if anything works, everything works,
  // as actual serialization is fully delegated to
  // autogenerated code from a well-tested library.
  private static final List<Entity> TEST_VALUES = Arrays.<Entity>asList(
      Entity.newBuilder()
          .setKey(makeKey("TestKind", "emptyEntity"))
          .build(),
      Entity.newBuilder()
          .setKey(makeKey("TestKind", "testSimpleProperties"))
          .addProperty(makeProperty("trueProperty", makeValue(true)))
          .addProperty(makeProperty("falseProperty", makeValue(false)))
          .addProperty(makeProperty("stringProperty", makeValue("hello")))
          .addProperty(makeProperty("integerProperty", makeValue(3)))
          .addProperty(makeProperty("doubleProperty", makeValue(-1.583257)))
          .build(),
      Entity.newBuilder()
          .setKey(makeKey("TestKind", "testNestedEntity"))
          .addProperty(makeProperty("entityProperty",
              makeValue(Entity.newBuilder()
                  .addProperty(makeProperty("stringProperty", makeValue("goodbye"))))))
          .build());

  @Test
  public void testDecodeEncodeEqual() throws Exception {
    for (Entity value : TEST_VALUES) {
      CoderProperties.coderDecodeEncodeEqual(TEST_CODER, value);
    }
  }

  // If this changes, it implies the binary format has changed.
  private static final String EXPECTED_ENCODING_ID = "";

  @Test
  public void testEncodingId() throws Exception {
    CoderProperties.coderHasEncodingId(TEST_CODER, EXPECTED_ENCODING_ID);
  }

  /**
   * Generated data to check that the wire format has not changed. To regenerate, see
   * {@link org.apache.beam.sdk.coders.PrintBase64Encodings}.
   */
  private static final List<String> TEST_ENCODINGS = Arrays.asList(
      "AAAAGwoZEhcKCFRlc3RLaW5kGgtlbXB0eUVudGl0eQ",
      "AAAAnQoiEiAKCFRlc3RLaW5kGhR0ZXN0U2ltcGxlUHJvcGVydGllcxISCgx0cnVlUHJvcGVydHkiAggBEhMKDWZhbHNl"
          + "UHJvcGVydHkiAggAEhoKDnN0cmluZ1Byb3BlcnR5IgiKAQVoZWxsbxIVCg9pbnRlZ2VyUHJvcGVydHkiAhADEh"
          + "sKDmRvdWJsZVByb3BlcnR5IgkZ8ZvCSgVV-b8",
      "AAAAVAoeEhwKCFRlc3RLaW5kGhB0ZXN0TmVzdGVkRW50aXR5EjIKDmVudGl0eVByb3BlcnR5IiAyHhIcCg5zdHJpbmdQ"
          + "cm9wZXJ0eSIKigEHZ29vZGJ5ZQ");

  @Test
  public void testWireFormatEncode() throws Exception {
      CoderProperties.coderEncodesBase64(TEST_CODER, TEST_VALUES, TEST_ENCODINGS);
  }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void encodeNullThrowsCoderException() throws Exception {
    thrown.expect(CoderException.class);
    thrown.expectMessage("cannot encode a null Entity");

    CoderUtils.encodeToBase64(TEST_CODER, null);
  }
}
