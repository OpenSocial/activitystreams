package com.ibm.common.activitystreams.actions;

import static com.ibm.common.activitystreams.Makers.object;
import static com.ibm.common.activitystreams.Makers.type;

import java.io.ObjectStreamException;

import com.google.common.base.Supplier;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.TypeValue;

/**
 */
public final class HtmlForm 
  extends ASObject {

  /**
   * Method makeHtmlForm.
   * @return Builder
   */
  public static Builder makeHtmlForm() {
    return new Builder();
  }
  
  /**
   */
  public static final class Builder 
    extends ASObject.AbstractBuilder<HtmlForm, Builder> {

    private final ParametersValue.Builder params = 
      ParametersValue.make();
    
    private Builder() {
      objectType("HtmlForm");
      mediaType("application/x-www-form-urlencoded");
    }
    
    /**
     * Method parameter.
     * @param name String
     * @param iri String
     * @return Builder
     */
    public Builder parameter(
      String name, 
      String iri) {
      params.set(name, type(iri));
      return this;
    }
    
    /**
     * Method parameter.
     * @param name String
     * @param iri String
     * @param required boolean
     * @return Builder
     */
    public Builder parameter(
      String name, 
      String iri, 
      boolean required) {
      return parameter(
        name, 
          object()
          .id(iri)
          .set("required", required));
    }
    
    /**
     * Method parameter.
     * @param name String
     * @param iri String
     * @param required boolean
     * @param value Object
     * @return Builder
     */
    public Builder parameter(
      String name, 
      String iri, 
      boolean required, 
      Object value) {
      return parameter(
        name,
        object()
          .id(iri)
          .set("required", required)
          .set("value", value));
    }
    
    /**
     * Method parameter.
     * @param name String
     * @param lv TypeValue
     * @return Builder
     */
    public Builder parameter(
      String name, 
      TypeValue lv) {
      params.set(name, lv);
      return this;
    }
    
    /**
     * Method parameter.
     * @param name String
     * @param lv Supplier<? extends TypeValue>
     * @return Builder
     */
    public Builder parameter(
      String name, 
      Supplier<? extends TypeValue> lv) {
      return parameter(name, lv.get());
    }
    
    /**
     * Method get.
     * @return HtmlForm
     * @see com.google.common.base.Supplier#get()
     */
    public HtmlForm get() {
      if (params.notEmpty())
        set("parameters", params.get());
      return new HtmlForm(this);
    }
    
  }
  
  /**
   * Constructor for HtmlForm.
   * @param builder Builder
   */
  private HtmlForm(Builder builder) {
    super(builder);
  }
  
  /**
   * Method parameters.
   * @return ParametersValue
   */
  public ParametersValue parameters() {
    return this.<ParametersValue>get("parameters");
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<HtmlForm> {
    private static final long serialVersionUID = -2060301713159936285L;
    protected SerializedForm(HtmlForm obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
    protected HtmlForm.Builder builder() {
      return ActionMakers.htmlForm();
    }
  }
}
