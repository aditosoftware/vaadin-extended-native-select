package de.aditosoftware.vaadin.addon.extendednativeselect.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasEnabled;
import de.aditosoftware.vaadin.addon.extendednativeselect.client.util.KeyValueOption;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

// Extend any GWT Widget
public class ExtendedNativeSelectWidget extends FocusWidget implements HasEnabled {
  // Statics.
  private static final String PLACEHOLDER_VALUE = "$placeholder";
  private static final String EMPTY_VALUE = "$empty";

  // Configuration properties.
  private boolean emptySelectionAllowed = false;
  private String placeholder = null;

  // Current values.
  private List<KeyValueOption> currentOptions = null;
  private Map<String, KeyValueOption> currentOptionsMapping = null;
  private String currentValue = PLACEHOLDER_VALUE;

  private final Element selectElement;

  public ExtendedNativeSelectWidget () {
    super(DOM.createDiv());
    setStylePrimaryName("v-extended-native-select");

    selectElement = DOM.createSelect();
    selectElement.addClassName("v-extended-native-select-select");
    getElement().appendChild(selectElement);

    // Set the "required" attribute on the select element to provide the
    // ":invalid" pseudo-class for css.
    getSelectElement().setAttribute("required", "");

    // Register a listener on the element to detect value changes.
    addDomHandler(this::onSelectionChange, ChangeEvent.getType());
    addDomHandler(this::onKeyDown, KeyDownEvent.getType());
  }

  /**
   * Will set the {@link KeyValueOption} to use for the current selection. This
   * will trigger a refresh for the select element and will also reset the
   * current value. By default, the value will be reset to the placeholder.
   *
   * @param entries The new option entries.
   */
  public void setOptions (List<KeyValueOption> entries, boolean forceReset) {
    currentOptions = entries;
    currentOptionsMapping = entries.stream()
        .collect(Collectors.toMap(it -> "#" + it.getKey(), it -> it));

    // Reset the current value to the placeholder value if we need a force
    // reset or the current value is no longer available.
    if (forceReset || !currentOptionsMapping.containsKey(currentValue))
      currentValue = PLACEHOLDER_VALUE;

    // Refresh the component to apply the new options.
    refresh();
  }

  /**
   * Will set the given {@link KeyValueOption} as selected item. If the item
   * does not exist on this widget, this will just do nothing.
   *
   * @param option The option to set as selected.
   */
  public void setCurrentValue (KeyValueOption option) {
    if (currentOptions == null || !currentOptions.contains(option))
      return;

    currentValue = "#" + option.getKey();
    getSelectElement().setValue("#" + option.getKey());
  }

  /**
   * Will set if the selection element allows an empty selection. This will
   * trigger a refresh on the select element.
   *
   * @param emptySelectionAllowed If empty selection is allowed.
   */
  public void setEmptySelectionAllowed (boolean emptySelectionAllowed) {
    this.emptySelectionAllowed = emptySelectionAllowed;
    refresh();
  }

  /**
   * If the selection element allows an empty selection.
   *
   * @return If empty selection is allowed.
   */
  public boolean isEmptySelectionAllowed () {
    return this.emptySelectionAllowed;
  }

  /**
   * Will set the placeholder for the selection element. This will trigger
   * a refresh on the select element.
   *
   * @param placeholder The placeholder.
   */
  public void setPlaceholder (String placeholder) {
    this.placeholder = placeholder;
    refresh();
  }

  /**
   * Will return the current placeholder for this selection element.
   *
   * @return The placeholder.
   */
  public String getPlaceholder () {
    return this.placeholder;
  }

  /**
   * Will add an listener, which will be triggered wenn the selection this
   * select element changes. This will wrap the actual change event and
   * resolve the user selected option correctly.
   *
   * @param listener The consumer to trigger when the selection changes.
   * @return The registration for the event, which allows to remove the handler.
   */
  public HandlerRegistration addChangeListener (Consumer<String> listener) {
    return addDomHandler(
        event -> {
          String value = getSelectElement().getValue();
          String convertedValue = null;
          if (value != null
              && !value.equals(PLACEHOLDER_VALUE)
              && !value.equals(EMPTY_VALUE)
              && currentOptionsMapping.containsKey(value)) {
            convertedValue = currentOptionsMapping.get(value).getKey();
          }

          listener.accept(convertedValue);
        },
        ChangeEvent.getType());
  }

  /**
   * Will refresh the current select element. This will basically just clear
   * the select element and add the required option elements.
   */
  private void refresh () {
    // Clear the select element.
    getSelectElement().clear();

    // If we do not even have options, we can not proceed.
    if (currentOptions != null) {
      // Always add the placeholder option.
      getSelectElement().add(createPlaceholderOption(), null);

      // If the empty selection is allowed, we just add an additional option.
      if (emptySelectionAllowed)
        getSelectElement().add(createEmptyOption(), null);

      // Add all options and set the last value.
      currentOptions.forEach(entry -> getSelectElement()
          .add(createOption(entry.getKey(), entry.getValue()), null));

      // If there is no current value, we can just fallback to the placeholder.
      if (currentValue == null)
        currentValue = PLACEHOLDER_VALUE;

      // Apply the current value.
      getSelectElement().setValue(currentValue);
    }
  }

  /**
   * Will create an option which represents a custom option. The option is
   * represented with a key and value.
   *
   * @param key   The key of the option.
   * @param value The value of the option.
   * @return The created option element.
   */
  private OptionElement createOption (String key, String value) {
    OptionElement el = DOM.createOption().cast();

    el.setLabel(value);
    el.setValue("#" + key);
    el.setAttribute("data-internal-key", key);

    return el;
  }

  /**
   * Will create an option which represents the placeholder option. The option
   * has some attributes set to make it act like an actual placeholder. If no
   * placeholder is set, an empty string will be used.
   *
   * @return The created option element.
   */
  private OptionElement createPlaceholderOption () {
    OptionElement el = DOM.createOption().cast();

    el.setDisabled(true);
    el.setAttribute("selected", "");
    el.setAttribute("hidden", "");
    el.setValue(PLACEHOLDER_VALUE);
    el.setLabel(placeholder != null ? placeholder : "");

    return el;
  }

  /**
   * Will create an option which represents the empty selection option.
   * This has an empty label and an internal identifier.
   *
   * @return The created option element.
   */
  private OptionElement createEmptyOption () {
    OptionElement el = DOM.createOption().cast();

    el.setValue(EMPTY_VALUE);
    el.setLabel("");

    return el;
  }

  /**
   * Will handle a Change event on the select element.
   *
   * @param changeEvent The event data.
   */
  private void onSelectionChange (ChangeEvent changeEvent) {
    // If empty selection is allowed and the empty option has been selected,
    // the placeholder will be selected internal.
    if (emptySelectionAllowed && getSelectElement().getSelectedIndex() == 1) {
      getSelectElement().setValue(PLACEHOLDER_VALUE);
    }

    currentValue = getSelectElement().getValue();
  }

  /**
   * Will handle a KeyDown event on the select element.
   *
   * @param keyDownEvent The event data.
   */
  private void onKeyDown (KeyDownEvent keyDownEvent) {
    // Select the first item on arrow down with the placeholder option is
    // currently selected.
    if (keyDownEvent.getNativeKeyCode() == KeyCodes.KEY_DOWN
        && getSelectElement().getSelectedIndex() == 0) {
      // If the empty selection is allowed, the first custom option is on
      // index 1, otherwise on 0.
      getSelectElement().setSelectedIndex(emptySelectionAllowed ? 1 : 0);
    }
  }

  /**
   * Will return the element of this widget casted to an {@link SelectElement}.
   *
   * @return The current element as {@link SelectElement}.
   */
  public SelectElement getSelectElement () {
    return selectElement.cast();
  }

  @Override
  public void setEnabled (boolean enabled) {
    if (!enabled)
      getSelectElement().setAttribute("disabled", "");
    else
      getSelectElement().removeAttribute("disabled");
  }

  @Override
  public boolean isEnabled () {
    return !getSelectElement().getPropertyBoolean("disabled");
  }
}