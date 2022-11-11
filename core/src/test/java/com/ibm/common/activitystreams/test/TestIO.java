/**
 * Copyright 2013 OpenSocial Foundation
 * Copyright 2013 International Business Machines Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Utility library for working with Activity Streams Actions
 * Requires underscorejs.
 *
 * @author James M Snell (jasnell@us.ibm.com)
 */
package com.ibm.common.activitystreams.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.Activity;
import com.ibm.common.activitystreams.Collection;
import com.ibm.common.activitystreams.IO;
import com.ibm.common.activitystreams.internal.Schema;

import java.time.ZonedDateTime;

public final class TestIO {

  @Test
  public void testIO() {
    IO.Builder iobuilder = IO.make();
    assertNotNull(iobuilder);
    IO io = iobuilder.get();
    assertNotNull(io);
  }
  
  @Test
  public void testIO2() {
    final String in = "{\"a\":1,\"b\":2}";
    final String out = "{\n  \"a\": 1,\n  \"b\": 2\n}";
    IO io = IO.makeDefaultPrettyPrint();
    assertNotNull(io);
    assertTrue(io.read(in).toString(io).equals(out));
  }
  
  @Test
  public void testIO3() {
    final String in = "{\"a\":1,\"b\":2}";
    IO io = IO.makeDefault();
    assertTrue(io.read(in) instanceof ASObject);
    assertTrue(io.readAs(in,Activity.class) instanceof Activity);
    assertTrue(io.readAs(in,Collection.class) instanceof Collection);
    assertTrue(io.readAsActivity(in) instanceof Activity);
    assertTrue(io.readAsCollection(in) instanceof Collection);
  }
  
  @Test
  public void testSchema() {
    Schema schema = 
      Schema.make()
        .map("foo", Schema.object.template().dateTime("bar"))
        .get();
    IO io = IO.make().schema(schema).get();
    ASObject obj = 
      io.read(
        "{\"objectType\":\"foo\",\"bar\":\"2013-12-12T12:12:12Z\"}");
    assertTrue(obj.get("bar") instanceof ZonedDateTime);
  }
}
