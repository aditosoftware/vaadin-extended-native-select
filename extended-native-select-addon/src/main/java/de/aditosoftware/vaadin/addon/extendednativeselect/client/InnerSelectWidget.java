package de.aditosoftware.vaadin.addon.extendednativeselect.client;

import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import de.aditosoftware.vaadin.addon.extendednativeselect.client.util.KeyValueOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Implements a native `select` element. This is capable of having a
 * placeholder and an empty selection.
 */
class InnerSelectWidget extends FocusWidget implements HasEnabled, Focusable {
  // Statics.
  /**
   * The placeholder has to be empty, to correctly set the invalid state.
   */
  private static final String PLACEHOLDER_VALUE = "";
  private static final String EMPTY_VALUE = "$empty";
  private static final String ATTR_DISABLED = "disabled";

  // Configuration properties.
  private boolean emptySelectionAllowed = false;
  private String placeholder = null;

  // Current values.
  private List<KeyValueOption> currentOptions = null;
  private KeyValueOption currentValue = null;

  private final List<Consumer<String>> changeListeners = new ArrayList<>();

  public InnerSelectWidget () {
    super(DOM.createSelect());

    // Set the primary style on the select element.
    setStylePrimaryName("v-extended-native-select-select");

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
    currentOptions = new ArrayList<>(entries);

    // Reset the current value to the placeholder value if we need a force
    // reset or the current value is no longer available.
    if (forceReset)
      currentValue = null;

    // Refresh the component to apply the new options.
    refresh();
  }

  /**
   * Will set the given {@link KeyValueOption} as selected item. If the item
   * does not exist on this widget, this will just do nothing.
   *
   * @param key The option to set as selected.
   */
  public void setCurrentValue (String key) {
    if (key == null) {
      currentValue = null;
      getSelectElement().setValue(PLACEHOLDER_VALUE);
    } else {
      KeyValueOption resolvedOption = getOptionByKey(key);

      // Return if there are currently no options or the key does not exist
      // in the current options.
      if (currentOptions == null || resolvedOption == null)
        return;

      // Save the current value and set the value of the select element.
      currentValue = resolvedOption;
      getSelectElement().setValue(getExposedOptionID(resolvedOption));
    }
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

  @Override
  public void setEnabled (boolean enabled) {
    if (!enabled)
      getSelectElement().setAttribute(ATTR_DISABLED, "");
    else
      getSelectElement().removeAttribute(ATTR_DISABLED);
  }

  @Override
  public boolean isEnabled () {
    return !getSelectElement().getPropertyBoolean(ATTR_DISABLED);
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
  public void addChangeListener (Consumer<String> listener) {
    changeListeners.add(listener);
  }

  /**
   * Will return the element of this widget casted to an {@link SelectElement}.
   *
   * @return The current element as {@link SelectElement}.
   */
  public SelectElement getSelectElement () {
    return getElement().cast();
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

      // Apply the current value.
      getSelectElement().setValue(currentValue == null
          ? PLACEHOLDER_VALUE
          : getExposedOptionID(currentValue));
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

    el.setValue("#" + key);
    el.setInnerSafeHtml(SafeHtmlUtils.fromString(value));

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
    el.setValue("");
    el.setInnerSafeHtml(SafeHtmlUtils.fromString(placeholder != null ? placeholder : ""));

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
    if (emptySelectionAllowed && getSelectElement().getSelectedIndex() == 1)
      getSelectElement().setValue(PLACEHOLDER_VALUE);

    String value = getSelectElement().getValue();
    if (value.equals(PLACEHOLDER_VALUE) || value.equals(EMPTY_VALUE)) {
      currentValue = null;
    } else {
      String internalOptionID = getInternalOptionID(value);
      if (internalOptionID == null) {
        // If the exposed value could not be parsed, just reset it and refresh.
        currentValue = null;
        refresh();
      } else {
        currentValue = getOptionByKey(internalOptionID);
      }
    }

    changeListeners.forEach(it -> it.accept(currentValue.getKey()));
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
   * Will return the {@link KeyValueOption} by the given key. If no option
   * exists with the given key, null will be returned.
   *
   * @param key The key to search for in the current options.
   * @return The {@link KeyValueOption} or null if non exists.
   */
  private KeyValueOption getOptionByKey (String key) {
    if (key == null)
      return null;

    return currentOptions.stream()
        .filter(element -> Objects.equals(element.getKey(), key))
        .findFirst().orElse(null);
  }

  /**
   * Will create the ID which is used in the select element. This is basically
   * just the key of the given option prefixed with an hashtag (#). If null is
   * given for the option, null will be returned.
   *
   * @param option The option for which the exposed ID is needed.
   * @return The exposed ID or null.
   */
  private String getExposedOptionID (KeyValueOption option) {
    if (option == null)
      return null;

    return "#" + option.getKey();
  }

  /**
   * Will convert the given ID (-> exposed ID) into an internal usable option ID.
   * This will simply remove the prefixed hashtag (#) of the given exposed
   * option ID.
   *
   * @param input The exposed option ID which shall be converted into an internal option ID.
   * @return The converted internal option ID.
   */
  private String getInternalOptionID (String input) {
    if (!input.startsWith("#"))
      return null;

    return input.substring(1);
  }
}
