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
package com.ibm.common.activitystreams.actions.example;

import static com.ibm.common.activitystreams.Makers.object;
import static com.ibm.common.activitystreams.actions.ActionMakers.METHOD_POST;
import static com.ibm.common.activitystreams.actions.ActionMakers.TARGET_DIALOG;
import static com.ibm.common.activitystreams.actions.ActionMakers.htmlForm;
import static com.ibm.common.activitystreams.actions.ActionMakers.httpAction;
import static com.ibm.common.activitystreams.actions.ActionMakers.intentAction;
import static com.ibm.common.activitystreams.actions.ActionMakers.parameter;
import static com.ibm.common.activitystreams.actions.ActionMakers.staticEmbedAction;
import static com.ibm.common.activitystreams.actions.ActionMakers.styles;
import static com.ibm.common.activitystreams.actions.ActionMakers.typedPayload;
import static com.ibm.common.activitystreams.actions.ActionMakers.urlTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.ActionsValue;
import com.ibm.common.activitystreams.IO;
import com.ibm.common.activitystreams.LinkValue;
import com.ibm.common.activitystreams.TypeValue;
import com.ibm.common.activitystreams.ValueType;
import com.ibm.common.activitystreams.actions.ActionsModule;
import com.ibm.common.activitystreams.actions.EmbedActionHandler;
import com.ibm.common.activitystreams.actions.HttpActionHandler;
import com.ibm.common.activitystreams.actions.Parameter;
import com.ibm.common.activitystreams.actions.ParametersValue;
import com.ibm.common.activitystreams.actions.StylesValue;
import com.ibm.common.activitystreams.actions.UrlTemplate;

/**
 */
public final class Actions {

  private Actions() {}
  
  private static final IO io = 
   IO.makeDefaultPrettyPrint(ActionsModule.instance);
  
  // Set up some common static action handlers...  
  private static final ASObject httpAction = 
    httpAction("http://example.org", METHOD_POST)
      .auth(
        "oauth2", 
        object()
          .set("scopes", 
            object()
              .set(
                "scope.key.1", 
                object().set("description", "foo"))))
      .expects(
        htmlForm()
          .parameter("foo", "http://example.org/FooProperty"))
      .expects(
        typedPayload("text/json")
          .schema("http://foo")
          .type("http://schema.org/Foo"))
      .returns(typedPayload("text/json"))
      .returns(typedPayload("text/html"))
      .requires(
        "urn:example:some:feature",
        "urn:example:some:other:feature")
      .prefers(
        "urn:example:some:optional-feature")
      .target(TARGET_DIALOG)
      .get();
  private static final ASObject embedAction = 
    staticEmbedAction(
      "text/plain", 
      "this is a pretty useless handler")
      .style(
        styles("print")
          .set("height", "100px")
          .set("width", "100px")
          .get(),
        styles("screen")
          .set("width", "200px")
          .set("height", "200px")
          .get()    
      )
      .get();
  private static final ASObject urlTemplate = 
      urlTemplate()
      .template("http://foo{/path}{?query}")
      .parameter("path", "http://example.org/types#string")
      .parameter("query", "http://example.org/types#string")
      .parameter(
        "foo",
        parameter()
          .required()
          .repeated()
          .formatInt32()
          .minimum(1)
          .maximum(3)
          .enumVals(1,2,3)
          .step(1)
          .formatUint32()
          .placeholder("(1, 2 or 3)")
      )
      .get();
  private static final ASObject intent = 
     intentAction()
     .mediaType("text/plain")
     .url("app://com.example.MoviePlayer")
     .get();
  
  /**
   * Method main.
   * @param args String[]
   * @throws Exception
   */
  public static void main(String... args) throws Exception {

    ByteArrayOutputStream out = 
      new ByteArrayOutputStream();

    ASObject obj = 
      object()
        .action(
          "view",
          httpAction,
          embedAction,
          urlTemplate,
          intent)
        .action(
          "share",
          "http://example.org/foo")
        .get();
    
    io.write(obj,System.out);

    System.out.println("\n\n");
    
    io.write(
      obj, 
      out);
    
    obj = io.read(new ByteArrayInputStream(out.toByteArray()));
    
    ActionsValue actions = obj.actions();
    for (LinkValue lv : actions.get("view")) {
      if (lv instanceof HttpActionHandler) {
        HttpActionHandler httpAction = (HttpActionHandler) lv;
        System.out.println("Auth:     " + httpAction.auth());
        System.out.println("Method:   " + httpAction.method());
        System.out.println("URL:      " + httpAction.url());
        System.out.println("Target:   " + httpAction.target());
        System.out.println("Returns:  " + httpAction.returns());
        System.out.println("Expects:  " + httpAction.expects());
        System.out.println("Requires: " + httpAction.requires());
        System.out.println("Prefers:  " + httpAction.prefers());
      } else if (lv instanceof EmbedActionHandler) {
        EmbedActionHandler embed = (EmbedActionHandler) lv;
        for (StylesValue style : embed.styles("print")) {
          System.out.println(style.get("height"));
          System.out.println(style.get("width"));
        }
      } else if (lv instanceof UrlTemplate) {
        UrlTemplate template = (UrlTemplate) lv;
        System.out.println(template.template());
        ParametersValue pv = template.parameters();
        for (String s : pv) {
          TypeValue param = pv.get(s);
          if (param.valueType() == ValueType.SIMPLE) {
            System.out.println(s + " = " + param.id());
          } else {
            if (param instanceof Parameter) {
              Parameter par = (Parameter) param;
              System.out.println(s + " = " + par.id() + ", " + par.required() + ", " + par.format() + ", " + par.enumVals());
            } else {
              System.out.println(s + " = " + param);
            }
          }
        }
      }
    }
  }
  
}
