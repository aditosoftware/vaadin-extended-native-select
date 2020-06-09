package de.aditosoftware.vaadin.addon.extendednativeselect.client;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Implements an wrapper element for a {@link InnerSelectWidget}. This will delegate
 * most available operations to the underlying {@link InnerSelectWidget} widget.
 */
public class ExtendedNativeSelectWidget
    extends SimplePanel
    implements HasEnabled, Focusable, HasAllFocusHandlers {
  private final InnerSelectWidget select;

  public ExtendedNativeSelectWidget () {
    // Create the InnerSelectWidget element and select it as widget for this panel.
    select = new InnerSelectWidget();
    setWidget(select);

    // Set the primary style name for this wrapper widget.
    //noinspection GWTStyleCheck
    setStylePrimaryName("v-extended-native-select");
  }

  /**
   * Will return the {@link InnerSelectWidget} which has been wrapped by this widget.
   * This can never be null.
   *
   * @return The VSelect of this widget. Never null.
   */
  InnerSelectWidget getSelect () {
    return select;
  }

  @Override
  public HandlerRegistration addBlurHandler (BlurHandler handler) {
    return getSelect().addBlurHandler(handler);
  }

  @Override
  public HandlerRegistration addFocusHandler (FocusHandler handler) {
    return getSelect().addFocusHandler(handler);
  }

  @Override
  public int getTabIndex () {
    return getSelect().getTabIndex();
  }

  @Override
  public void setAccessKey (char key) {
    getSelect().setAccessKey(key);
  }

  @Override
  public void setFocus (boolean focused) {
    getSelect().setFocus(focused);
  }

  @Override
  public void setTabIndex (int index) {
    getSelect().setTabIndex(index);
  }

  @Override
  public boolean isEnabled () {
    return getSelect().isEnabled();
  }

  @Override
  public void setEnabled (boolean enabled) {
    getSelect().setEnabled(enabled);
  }
}
