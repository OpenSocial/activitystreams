package com.ibm.common.activitystreams.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Test;

import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.Activity;
import com.ibm.common.activitystreams.Collection;
import com.ibm.common.activitystreams.IO;
import com.ibm.common.activitystreams.internal.Schema;

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
    assertTrue(obj.get("bar") instanceof DateTime);
  }
}
