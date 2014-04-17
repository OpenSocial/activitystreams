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

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Test;

import com.google.common.collect.Iterables;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.ActionsValue;
import com.ibm.common.activitystreams.Activity;
import com.ibm.common.activitystreams.IO;
import com.ibm.common.activitystreams.LinkValue;
import com.ibm.common.activitystreams.LinkValue.SimpleLinkValue;
import com.ibm.common.activitystreams.Makers;
import com.ibm.common.activitystreams.NLV;
import com.ibm.common.activitystreams.NLV.MapNLV;
import com.ibm.common.activitystreams.ValueType;

public final class TestBasics {

  @Test
  public void testObject() {
    
    ASObject obj = 
      Makers.object()
        .id("urn:example:test")
        .title("This is a test")
        .action("like", "http://example.org/actions/like")
        .get();
    
    assertNotNull(obj);
    
    assertEquals("urn:example:test", obj.id());
    assertEquals("This is a test", obj.titleString());
    
    ActionsValue actions = obj.actions();
    Iterable<LinkValue> lvs = actions.get("like");
    LinkValue lv = Iterables.getFirst(lvs, null);
    assertEquals(ValueType.SIMPLE, lv.valueType());
    SimpleLinkValue slv = (SimpleLinkValue) lv;
    assertEquals("http://example.org/actions/like", slv.url());
    
  }
  
  @Test
  public void testLanguageContext() {
    ASObject obj = 
      Makers.object()
        .language("en")
        .title("en", "Foo")
        .title("fr", "Bar")
        .get();
    assertEquals("Foo", obj.titleString("en"));
    assertEquals("Bar", obj.titleString("fr"));
    
    NLV nlv = obj.title();
    assertEquals(ValueType.OBJECT, nlv.valueType());
    
    MapNLV map = (MapNLV) nlv;
    assertTrue(map.has("en"));
    assertTrue(map.has("fr"));
    assertEquals("Foo", map.value("en"));
    assertEquals("Bar", map.value("fr"));
  }
  
  @Test
  public void testDateTimes() {
    DateTime now = DateTime.now();
    ASObject obj = 
      Makers.object()
        .updated(now)
        .published(now)
        .startTime(now.minus(Period.days(1)))
        .endTime(now.plus(Period.days(1)))
        .get();
    assertNotNull(obj.updated());
    assertNotNull(obj.published());
    assertNotNull(obj.startTime());
    assertNotNull(obj.endTime());
    assertEquals(now, obj.updated());
    assertEquals(now, obj.published());
    assertTrue(now.isAfter(obj.startTime()));
    assertTrue(now.isBefore(obj.endTime()));
  }
  
  @Test
  public void testSerialization() 
    throws IOException, 
           ClassNotFoundException {
    
    ASObject obj = 
      Makers.object()
        .id("urn:example:test")
        .title("This is a test")
        .action("like", "http://example.org/actions/like")
        .get();
    
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(out);
    oos.writeObject(obj);
    
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(in);
    obj = (ASObject) ois.readObject();
  
    assertNotNull(obj);
    
    assertEquals("urn:example:test", obj.id());
    assertEquals("This is a test", obj.titleString());
    
    ActionsValue actions = obj.actions();
    Iterable<LinkValue> lvs = actions.get("like");
    LinkValue lv = Iterables.getFirst(lvs, null);
    assertEquals(ValueType.SIMPLE, lv.valueType());
    SimpleLinkValue slv = (SimpleLinkValue) lv;
    assertEquals("http://example.org/actions/like", slv.url());
    
  }
  
  @Test
  public void testRoundTrip() {
    Activity activity = 
      Makers.activity()
        .verb("post")
        .actor("acct:joe@example.org")
        .object("http://example.org/1", "http://example.org/2")
        .get();
    ByteArrayOutputStream out = 
      new ByteArrayOutputStream();
    activity.writeTo(out);
    ByteArrayInputStream in =
      new ByteArrayInputStream(out.toByteArray());
    activity = IO.makeDefault().readAsActivity(in);
    
    assertEquals("post", activity.verb().id());
    assertEquals("acct:joe@example.org", ((SimpleLinkValue)activity.firstActor()).url());
    
    Iterable<LinkValue> lvs = activity.object();
    
    LinkValue lv = Iterables.get(lvs, 0);
    assertEquals("http://example.org/1", ((SimpleLinkValue)lv).url());
    
    lv = Iterables.get(lvs, 1);
    assertEquals("http://example.org/2", ((SimpleLinkValue)lv).url());
  }
  
  
}
