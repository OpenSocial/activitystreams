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
package com.ibm.common.activitystreams.actions;

import static com.ibm.common.activitystreams.Makers.object;

import com.ibm.common.activitystreams.ASObject;

/**
 */
public final class ActionMakers {

  public static final String TARGET_NONE = "NONE";
  public static final String TARGET_DEFAULT = "DEFAULT";
  public static final String TARGET_NEW = "NEW";
  public static final String TARGET_CURRENT = "CURRENT";
  public static final String TARGET_TAB = "TAB";
  public static final String TARGET_DIALOG = "DIALOG";
  public static final String TARGET_MODALDIALOG = "MODALDIALOG";
  public static final String TARGET_FLOAT = "FLOAT";
  public static final String TARGET_SIDEBAR = "SIDEBAR";
  
  public static final String METHOD_GET    = "GET";
  public static final String METHOD_POST   = "POST";
  public static final String METHOD_PUT    = "PUT";
  public static final String METHOD_DELETE = "DELETE";
  public static final String METHOD_PATCH  = "PATCH";
  
  private ActionMakers() {}
  
  /**
   * Method application.
   * @return ASObject.Builder
   */
  public static ASObject.Builder application() {
    return object("application");
  }
  
  /**
   * Method service.
   * @return ASObject.Builder
   */
  public static ASObject.Builder service() {
    return object("service");
  }
  
  /**
   * Method styles.
   * @return StylesValue.Builder
   */
  public static StylesValue.Builder styles() {
    return StylesValue.make();
  }
  
  /**
   * Method styles.
   * @param media String
   * @return StylesValue.Builder
   */
  public static StylesValue.Builder styles(String media) {
    return styles().media(media);
  }
  
  /**
   * Method httpAction.
   * @return HttpActionHandler.Builder
   */
  public static HttpActionHandler.Builder httpAction() {
    return HttpActionHandler.makeHttpActionHandler();
  }
  
  /**
   * Method httpAction.
   * @param url String
   * @return HttpActionHandler.Builder
   */
  public static HttpActionHandler.Builder httpAction(String url) {
    return httpAction().url(url);
  }
  
  /**
   * Method httpAction.
   * @param url String
   * @param method String
   * @return HttpActionHandler.Builder
   */
  public static HttpActionHandler.Builder httpAction(
    String url, 
    String method) {
      return httpAction()
        .url(url)
        .method(method);
  }
  
  /**
   * Method embedAction.
   * @return EmbedActionHandler.Builder
   */
  public static EmbedActionHandler.Builder embedAction() {
    return EmbedActionHandler.makeEmbedActionHandler();
  }
  
  /**
   * Method embedAction.
   * @param url String
   * @return EmbedActionHandler.Builder
   */
  public static EmbedActionHandler.Builder embedAction(String url) {
    return embedAction().url(url);
  }
  
  /**
   * Method embedAction.
   * @param url String
   * @param mediaType String
   * @return EmbedActionHandler.Builder
   */
  public static EmbedActionHandler.Builder embedAction(
    String url, 
    String mediaType) {
    return embedAction(url).mediaType(mediaType);
  }
  
  /**
   * Method staticEmbedAction.
   * @param mediaType String
   * @param content String
   * @return EmbedActionHandler.Builder
   */
  public static EmbedActionHandler.Builder staticEmbedAction(
    String mediaType,
    String content) {
    return embedAction().mediaType(mediaType).content(content);
  }
 
  /**
   * Method intentAction.
   * @return IntentActionHandler.Builder
   */
  public static IntentActionHandler.Builder intentAction() {
    return IntentActionHandler.makeIntentActionHandler();
  }
  
  /**
   * Method intentAction.
   * @param url String
   * @return IntentActionHandler.Builder
   */
  public static IntentActionHandler.Builder intentAction(String url) {
    return intentAction().url(url);
  }
  
  /**
   * Method htmlForm.
   * @return HtmlForm.Builder
   */
  public static HtmlForm.Builder htmlForm() {
    return HtmlForm.makeHtmlForm();
  }
  
  /**
   * Method typedPayload.
   * @param mediaType String
   * @return TypedPayload.Builder
   */
  public static TypedPayload.Builder typedPayload(String mediaType) {
    return TypedPayload.makeTypedPayload(mediaType);
  }
  
  public static TypedPayload.Builder typedPayload() {
    return TypedPayload.make();
  }
  
  /**
   * Method urlTemplate.
   * @return UrlTemplate.Builder
   */
  public static UrlTemplate.Builder urlTemplate() {
    return UrlTemplate.makeUrlTemplate();
  }
  
  /**
   * Method urlTemplate.
   * @param template String
   * @return UrlTemplate.Builder
   */
  public static UrlTemplate.Builder urlTemplate(String template) {
    return urlTemplate().template(template);
  }
  
  /**
   * Method parameter.
   * @return Parameter.Builder
   */
  public static Parameter.Builder parameter() {
    return Parameter.makeParameter();
  }
  
  /**
   * Method parameter.
   * @param id String
   * @return Parameter.Builder
   */
  public static Parameter.Builder parameter(String id) {
    return parameter().id(id);
  }
}
