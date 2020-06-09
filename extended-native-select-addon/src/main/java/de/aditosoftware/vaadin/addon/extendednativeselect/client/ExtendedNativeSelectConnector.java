package de.aditosoftware.vaadin.addon.extendednativeselect.client;

import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.connectors.AbstractSingleSelectConnector;
import com.vaadin.client.data.DataSource;
import com.vaadin.shared.Range;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.data.selection.SelectionServerRpc;
import com.vaadin.shared.ui.Connect;
import de.aditosoftware.vaadin.addon.extendednativeselect.ExtendedNativeSelect;
import de.aditosoftware.vaadin.addon.extendednativeselect.client.util.KeyValueOption;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

import java.util.ArrayList;
import java.util.List;

@Connect(ExtendedNativeSelect.class)
public class ExtendedNativeSelectConnector
    extends AbstractSingleSelectConnector<ExtendedNativeSelectWidget> {

  // Listener registrations.
  private Registration dataSourceChangeRegistration;

  // RPC registrations.
  private final SelectionServerRpc selectionRpc =
      getRpcProxy(SelectionServerRpc.class);

  @Override
  protected void init () {
    super.init();

    // Add a change listener, which will be called when the value changes on
    // the client-side.
    getWidget().getSelect().addChangeListener(selectionRpc::select);
  }

  @Override
  public ExtendedNativeSelectState getState () {
    return (ExtendedNativeSelectState) super.getState();
  }

  /**
   * Will update the current {@link InnerSelectWidget} when the "emptySelectionAllowed"
   * property changes. This will simply delegate the new value of th property
   * to the widget.
   */
  @OnStateChange({"emptySelectionAllowed", "placeholder"})
  private void onEmptySelectionChange () {
    getWidget().getSelect().setEmptySelectionAllowed(getState().emptySelectionAllowed);
  }

  /**
   * Will update the current {@link InnerSelectWidget} when the "placeholder" property
   * changes. This will simply delegate the new value of the property to the
   * widget.
   */
  @OnStateChange({"placeholder"})
  private void onPlaceholderChange () {
    getWidget().getSelect().setPlaceholder(getState().placeholder);
  }

  /**
   * Will update the currently selected item on the current {@link InnerSelectWidget}
   * when the "selectedItemItemKey" property changes. This will simply delegate
   * the new value of the property to the widget.
   */
  @OnStateChange({"selectedItemKey"})
  private void onSelectedItemKeyChange () {
    String key = getState().selectedItemKey;

    // If null is given, this means that no value (aka placeholder) shall be
    // selected.
    if (key == null)
      getWidget().getSelect().setCurrentValue(null);
    else
      getWidget().getSelect().setCurrentValue(getState().selectedItemKey);
  }

  /**
   * Will unregister the current {@link DataSource}, if there have been any
   * and register listeners on the given one and will continue to use the
   * given one.
   *
   * @param dataSource The new DataSource to use for this connector.
   */
  @Override
  public void setDataSource (DataSource<JsonObject> dataSource) {
    // Remove the previous listener if given.
    if (dataSourceChangeRegistration != null)
      dataSourceChangeRegistration.remove();

    // Add a change listener on the new DataSource.
    dataSourceChangeRegistration = dataSource.addDataChangeHandler(this::onDataChange);

    super.setDataSource(dataSource);
  }

  /**
   * Will process the current {@link DataSource} (provided by {@link this#getDataSource()}.
   * This will also update the currently selected value.
   *
   * @param range The new range for the data.
   */
  private void onDataChange (Range range) {
    List<KeyValueOption> entries = new ArrayList<>();
    for (int i = range.getStart(); i < range.getEnd(); i++) {
      JsonObject rowData = getDataSource().getRow(i);

      JsonValue keyValue = rowData.get(DataCommunicatorConstants.KEY);
      JsonValue dataValue = rowData.get(DataCommunicatorConstants.DATA);

      if (keyValue != null && dataValue != null)
        entries.add(new KeyValueOption(keyValue.asString(), dataValue.asString()));
    }

    // Set the generated options and also update the selected item if available.
    getWidget().getSelect().setOptions(entries, false);
    onSelectedItemKeyChange();
  }
}
