package com.ibm.common.activitystreams.legacy;

import com.ibm.common.activitystreams.ASObject;
import static com.ibm.common.activitystreams.Makers.object;

public final class LegacyMakers {
  
  private LegacyMakers() {}
  
  public static Binary.Builder binary() {
    return new Binary.Builder();
  }
  
  public static Task.Builder task() {
    return new Task.Builder();
  }
  
  public static WithImage.Builder withImage() {
    return new WithImage.Builder();
  }
  
  public static Question.Builder question() {
    return new Question.Builder();
  }
  
  public static WithImage.Builder product() {
    return new WithImage.Builder().objectType("product");
  }
  
  public static WithImage.Builder image() {
    return new WithImage.Builder().objectType("image");
  }
  
  public static File.Builder file() {
    return new File.Builder();
  }
  
  public static Bookmark.Builder bookmark() {
    return new Bookmark.Builder();
  }
  
  public static Bookmark bookmark(String targetUrl) {
    return bookmark().targetUrl(targetUrl).get();
  }
  
  public static Membership.Builder role() {
    return new Membership.Builder().objectType("role");
  }
  
  public static Membership.Builder group() {
    return new Membership.Builder().objectType("group");
  }
  
  public static Issue.Builder issue() {
    return new Issue.Builder();
  }
  
  public static Membership.Builder membership() {
    return new Membership.Builder();
  }
  
  public static Event.Builder event() {
    return new Event.Builder();
  }
  
  public static MediaLink.Builder mediaLink() {
    return new MediaLink.Builder();
  }
  
  public static MediaLink mediaLink(String url) {
    return mediaLink().url(url).get();
  }
  
  public static ASObject.Builder permission() {
    return object("permission");
  }
  
  public static AudioVisual.Builder video() {
    return new AudioVisual.Builder().objectType("video");
  }
  
  public static AudioVisual.Builder audioVisual() {
    return new AudioVisual.Builder();
  }
  
  public static AudioVisual.Builder audio() {
    return new AudioVisual.Builder().objectType("audio");
  }
  
  public static ASObject.Builder alert() {
    return object("alert");
  }
  
  public static ASObject.Builder application() {
    return object("application");
  }
  
  public static ASObject.Builder article() {
    return object("article");
  }
  
  public static ASObject.Builder badge() {
    return object("badge");
  }
  
  public static ASObject.Builder comment() {
    return object("comment");
  }
  
  public static ASObject.Builder device() {
    return object("device");
  }
  
  public static ASObject.Builder game() {
    return object("game");
  }
  
  public static ASObject.Builder job() {
    return object("job");
  }
  
  public static ASObject.Builder note() {
    return object("note");
  }
  
  public static ASObject.Builder offer() {
    return object("offer");
  }
  
  public static ASObject.Builder organization() {
    return object("organization");
  }
  
  public static ASObject.Builder page() {
    return object("page");
  }
  
  public static ASObject.Builder person() {
    return object("person");
  }
  
  public static ASObject.Builder process() {
    return object("process");
  }
  
  public static ASObject.Builder review() {
    return object("review");
  }
  
  public static ASObject.Builder service() {
    return object("service");
  }
  
  public static Membership.Builder team() {
    return new Membership.Builder().objectType("team");
  }
  
}
