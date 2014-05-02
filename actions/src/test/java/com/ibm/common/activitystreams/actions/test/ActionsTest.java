package com.ibm.common.activitystreams.actions.test;

import static com.google.common.collect.Iterables.getFirst;
import static com.ibm.common.activitystreams.actions.ActionMakers.embedAction;
import static com.ibm.common.activitystreams.actions.ActionMakers.htmlForm;
import static com.ibm.common.activitystreams.actions.ActionMakers.httpAction;
import static com.ibm.common.activitystreams.actions.ActionMakers.intentAction;
import static com.ibm.common.activitystreams.actions.ActionMakers.io;
import static com.ibm.common.activitystreams.actions.ActionMakers.parameter;
import static com.ibm.common.activitystreams.actions.ActionMakers.styles;
import static com.ibm.common.activitystreams.actions.ActionMakers.typedPayload;
import static com.ibm.common.activitystreams.actions.ActionMakers.urlTemplate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import com.google.common.base.Throwables;
import com.google.common.net.MediaType;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.LinkValue;
import com.ibm.common.activitystreams.LinkValue.SimpleLinkValue;
import com.ibm.common.activitystreams.actions.EmbedActionHandler;
import com.ibm.common.activitystreams.actions.HtmlForm;
import com.ibm.common.activitystreams.actions.HttpActionHandler;
import com.ibm.common.activitystreams.actions.IntentActionHandler;
import com.ibm.common.activitystreams.actions.Parameter;
import com.ibm.common.activitystreams.actions.ParametersValue;
import com.ibm.common.activitystreams.actions.StylesValue;
import com.ibm.common.activitystreams.actions.TypedPayload;
import com.ibm.common.activitystreams.actions.UrlTemplate;

public final class ActionsTest {

  @Test
  public void roundtripHttpActionHandler2() {
    
    HttpActionHandler hah1 =
      httpAction()
        .url("http://example.org")
        .method("GET")
        .get();
    
    HttpActionHandler hah2 = 
      roundTrip2(hah1);
    
    assertEquals("GET", hah2.method());
    
    Iterable<LinkValue> i = hah2.url();
    LinkValue lv = getFirst(i,null);
    assertNotNull(lv);
    assertTrue(lv instanceof SimpleLinkValue);
    SimpleLinkValue slv = (SimpleLinkValue) lv;
    assertEquals("http://example.org", slv.url());
    
  }
  
  @Test
  public void roundtripIntentActionHandler2() {
    
    IntentActionHandler iah1 =
      intentAction()
        .id("urn:example:foo:1")
        .get();
    
    IntentActionHandler iah2 =
      roundTrip2(iah1);
    
    assertEquals("urn:example:foo:1", iah2.id());
    
  }
  
  @Test
  public void roundtripEmbedActionHandler2() {
    
    EmbedActionHandler eah1 = 
      embedAction()
        .style(
          styles()
            .set("width", "100px")
        )
        .get();
    
    EmbedActionHandler eah2 =
      roundTrip2(eah1);
    
    Iterable<StylesValue> styles = 
      eah2.styles();
    StylesValue style = 
      getFirst(styles, null);
    assertNotNull(style);
    assertTrue(style.has("width"));
    assertEquals("100px", style.get("width"));
  }
  
  @Test
  public void roundtripHtmlForm2() {
    HtmlForm htmlForm1 = 
      htmlForm()
        .parameter("foo", parameter().optional())
        .get();
    
    HtmlForm htmlForm2 =
      roundTrip2(htmlForm1);
    
    ParametersValue params = 
      htmlForm2.parameters();
    assertTrue(params.has("foo"));
    
    Parameter param = params.get("foo");
    assertFalse(param.required());
  }
  
  @Test
  public void roundtripTypedValue2() {
    TypedPayload typedPayload1 =
      typedPayload()
        .mediaType(MediaType.create("application", "json"))
        .get();
    
    TypedPayload typedPayload2 = 
      roundTrip2(typedPayload1);
    
    assertEquals(
      typedPayload1.mediaType(),
      typedPayload2.mediaType());
  }
  
  @Test
  public void roundtripUrlTemplate2() {
    UrlTemplate template1 = 
      urlTemplate()
        .template("http://example.org{/foo}")
        .parameter("foo", "bar")
        .get();
    UrlTemplate template2 = 
      roundTrip2(template1);
    assertEquals(
      template1.template(), 
      template2.template());
    ParametersValue value = 
      template2.parameters();
    assertTrue(value.has("foo"));   
  }
  
  
  
  
  
  @Test
  public void roundtripHttpActionHandler() {
    
    HttpActionHandler hah1 =
      httpAction()
        .url("http://example.org")
        .method("GET")
        .get();
    
    HttpActionHandler hah2 = 
      roundTrip(hah1);
    
    assertEquals("GET", hah2.method());
    
    Iterable<LinkValue> i = hah2.url();
    LinkValue lv = getFirst(i,null);
    assertNotNull(lv);
    assertTrue(lv instanceof SimpleLinkValue);
    SimpleLinkValue slv = (SimpleLinkValue) lv;
    assertEquals("http://example.org", slv.url());
    
  }
  
  @Test
  public void roundtripIntentActionHandler() {
    
    IntentActionHandler iah1 =
      intentAction()
        .id("urn:example:foo:1")
        .get();
    
    IntentActionHandler iah2 =
      roundTrip(iah1);
    
    assertEquals("urn:example:foo:1", iah2.id());
    
  }
  
  @Test
  public void roundtripEmbedActionHandler() {
    
    EmbedActionHandler eah1 = 
      embedAction()
        .style(
          styles()
            .set("width", "100px")
        )
        .get();
    
    EmbedActionHandler eah2 =
      roundTrip(eah1);
    
    Iterable<StylesValue> styles = 
      eah2.styles();
    StylesValue style = 
      getFirst(styles, null);
    assertNotNull(style);
    assertTrue(style.has("width"));
    assertEquals("100px", style.get("width"));
  }
  
  @Test
  public void roundtripHtmlForm() {
    HtmlForm htmlForm1 = 
      htmlForm()
        .parameter("foo", parameter().optional())
        .get();
    
    HtmlForm htmlForm2 =
      roundTrip(htmlForm1);
    
    ParametersValue params = 
      htmlForm2.parameters();
    assertTrue(params.has("foo"));
    
    Parameter param = params.get("foo");
    assertFalse(param.required());
  }
  
  @Test
  public void roundtripTypedValue() {
    TypedPayload typedPayload1 =
      typedPayload()
        .mediaType(MediaType.create("application", "json"))
        .get();
    
    TypedPayload typedPayload2 = 
      roundTrip(typedPayload1);
    
    assertEquals(
      typedPayload1.mediaType(),
      typedPayload2.mediaType());
  }
  
  @Test
  public void roundtripUrlTemplate() {
    UrlTemplate template1 = 
      urlTemplate()
        .template("http://example.org{/foo}")
        .parameter("foo", "bar")
        .get();
    UrlTemplate template2 = 
      roundTrip(template1);
    assertEquals(
      template1.template(), 
      template2.template());
    ParametersValue value = 
      template2.parameters();
    assertTrue(value.has("foo"));   
  }
  
  private <T extends ASObject>T roundTrip(T writable) {
    ByteArrayOutputStream out = 
      new ByteArrayOutputStream();
    writable.writeTo(out,io);
    ByteArrayInputStream in =
      new ByteArrayInputStream(out.toByteArray());
    return io.readAs(in);
  }
  
  @SuppressWarnings("unchecked")
  private <T extends ASObject>T roundTrip2(T writable){
    try {
      ByteArrayOutputStream out = 
        new ByteArrayOutputStream();
      ObjectOutputStream oos =
        new ObjectOutputStream(out);
      oos.writeObject(writable);
      ByteArrayInputStream in =
        new ByteArrayInputStream(out.toByteArray());
      ObjectInputStream ois = 
        new ObjectInputStream(in);
      return (T)ois.readObject();
    } catch (Throwable t) {
      throw Throwables.propagate(t);
    }
  }
}
