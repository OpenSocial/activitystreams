package com.ibm.common.activitystreams.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Locale;

public final class Util {

  private Util() {}
  
  /**
   * Method convLocale.
   * @param locale Locale
   * @return String 
   **/
  public static String convLocale(Locale locale) {
    return checkNotNull(locale).toString().replaceAll("_", "-");
  }
  
  public static final String DEFAULT_LOCALE = 
    convLocale(Locale.getDefault());
  
}
