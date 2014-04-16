package com.ibm.common.activitystreams.internal;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

/**
 * @author james
 * @version $Revision: 1.0 $
 */
public abstract class Adapter<T>
  implements JsonSerializer<T>, JsonDeserializer<T> {

}
