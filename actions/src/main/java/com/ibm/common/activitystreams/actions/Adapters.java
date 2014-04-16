package com.ibm.common.activitystreams.actions;

import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.TypeValue;
import com.ibm.common.activitystreams.actions.StylesValue.Builder;
import com.ibm.common.activitystreams.internal.Adapter;
import com.ibm.common.activitystreams.util.AbstractDictionaryObjectAdapter;

final class Adapters {

  private Adapters() {}
  
  static final Adapter<Authentication> AUTH =
      new AbstractDictionaryObjectAdapter
      <ASObject,
       Authentication,
       Authentication.Builder>(ASObject.class) {
      @Override
      protected Authentication.Builder builder() {
        return Authentication.make();
      }
    };
    
    static final Adapter<ParametersValue> PARAMETERS =
      new AbstractDictionaryObjectAdapter
       <TypeValue,
        ParametersValue,
        ParametersValue.Builder>(TypeValue.class) {
      @Override
      protected ParametersValue.Builder builder() {
        return ParametersValue.make();
      }
    };
    
    static final Adapter<StylesValue> STYLES  =
      new AbstractDictionaryObjectAdapter
        <String,
         StylesValue,
         StylesValue.Builder>(String.class) {  
      @Override
      protected Builder builder() {
        return StylesValue.make();
      }
    };
    
}
