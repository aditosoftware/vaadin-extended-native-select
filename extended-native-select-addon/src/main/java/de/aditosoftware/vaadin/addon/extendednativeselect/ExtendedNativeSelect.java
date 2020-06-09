package de.aditosoftware.vaadin.addon.extendednativeselect;

import com.vaadin.data.HasDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.event.FieldEvents;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.ui.AbstractSingleSelect;
import de.aditosoftware.vaadin.addon.extendednativeselect.client.ExtendedNativeSelectState;

/**
 * A native select implementation which supports placeholders and empty selections.
 */
public class ExtendedNativeSelect<T>
    extends AbstractSingleSelect<T>
    implements FieldEvents.FocusNotifier, FieldEvents.BlurNotifier, HasDataProvider<T> {
  public ExtendedNativeSelect () {
    registerRpc(new FieldEvents.FocusAndBlurServerRpcDecorator(this, this::fireEvent));
    addDataGenerator((item, json) -> {
      String caption = getItemCaptionGenerator().apply(item);
      if (caption == null)
        caption = "";
      json.put(DataCommunicatorConstants.DATA, caption);
    });

    setItemCaptionGenerator(String::valueOf);
  }

  @Override
  public Registration addFocusListener (FieldEvents.FocusListener listener) {
    return addListener(FieldEvents.FocusEvent.EVENT_ID, FieldEvents.FocusEvent.class, listener,
        FieldEvents.FocusListener.focusMethod);
  }

  @Override
  public Registration addBlurListener (FieldEvents.BlurListener listener) {
    return addListener(FieldEvents.BlurEvent.EVENT_ID, FieldEvents.BlurEvent.class, listener,
        FieldEvents.BlurListener.blurMethod);
  }

  @Override
  protected ExtendedNativeSelectState getState () {
    return (ExtendedNativeSelectState) super.getState();
  }

  @Override
  protected ExtendedNativeSelectState getState (boolean markAsDirty) {
    return (ExtendedNativeSelectState) super.getState(markAsDirty);
  }

  @Override
  public DataProvider<T, ?> getDataProvider () {
    return internalGetDataProvider();
  }

  @Override
  public void setDataProvider (DataProvider<T, ?> dataProvider) {
    internalSetDataProvider(dataProvider);
  }

  /**
   * Will set the placeholder for this component.
   *
   * @param placeholder The new placeholder.
   */
  public void setPlaceholder (String placeholder) {
    getState().placeholder = placeholder;
  }

  /**
   * Will return the current placeholder for this component.
   *
   * @return The current placeholder.
   */
  public String getPlaceholder () {
    return getState(false).placeholder;
  }

  /**
   * Will allow the selection of an empty option.
   *
   * @param allowed If an empty selection is allowed.
   */
  public void setEmptySelectionAllowed (boolean allowed) {
    getState().emptySelectionAllowed = allowed;
  }

  /**
   * Will return if an empty selection is allowed.
   *
   * @return If empty selection is allowed.
   */
  public boolean isEmptySelectionAllowed () {
    return getState(false).emptySelectionAllowed;
  }
}
