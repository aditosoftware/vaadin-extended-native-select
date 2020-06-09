package de.aditosoftware.client;

import com.vaadin.shared.ui.AbstractSingleSelectState;

public class ExtendedNativeSelectState extends AbstractSingleSelectState {
  /**
   * The placeholder for the component.
   */
  public String placeholder;

  /**
   * If there is an empty option to reset the current selection.
   */
  public boolean emptySelectionAllowed;
}
