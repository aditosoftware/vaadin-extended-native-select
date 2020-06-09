package de.aditosoftware.vaadin.addon.extendednativeselect.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.StateChangeEvent;
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
public class ExtendedNativeSelectConnector extends AbstractSingleSelectConnector<ExtendedNativeSelectWidget> {
  // Listener registrations.
  private HandlerRegistration handlerRegistration;
  private Registration dataSourceChangeRegistration;

  // RPC registrations.
  private final SelectionServerRpc selectionRpc =
      getRpcProxy(SelectionServerRpc.class);

  @Override
  protected void init () {
    super.init();
    getWidget().addChangeListener(key -> {
      selectionRpc.select(key);
    });
  }

  // We must implement getWidget() to cast to correct type
  // (this will automatically create the correct widget type)
  @Override
  public ExtendedNativeSelectWidget getWidget () {
    return super.getWidget();
  }

  // We must implement getState() to cast to correct type
  @Override
  public ExtendedNativeSelectState getState () {
    return (ExtendedNativeSelectState) super.getState();
  }

  // Whenever the state changes in the server-side, this method is called
  @Override
  public void onStateChanged (StateChangeEvent stateChangeEvent) {
    super.onStateChanged(stateChangeEvent);

    if (stateChangeEvent.hasPropertyChanged("selectedItemKey")) {
      GWT.log("### " + getState().selectedItemKey);
      getWidget().setCurrentValue(new KeyValueOption(getState().selectedItemKey, null));
    }
  }

  @OnStateChange({"emptySelectionAllowed", "placeholder"})
  private void onEmptySelectionChange () {
    getWidget().setEmptySelectionAllowed(getState().emptySelectionAllowed);
    getWidget().setPlaceholder(getState().placeholder);
  }

  @Override
  public void setDataSource (DataSource<JsonObject> dataSource) {
    // Remove the previous listener.
    if (dataSourceChangeRegistration != null)
      dataSourceChangeRegistration.remove();

    // Add a change listener on the new DataSource.
    dataSourceChangeRegistration = dataSource.addDataChangeHandler(this::onDataChange);
    super.setDataSource(dataSource);
  }

  /**
   * Will process the current {@link DataSource} (provided by {@link this#getDataSource()}.
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

    getWidget().setOptions(entries, false);
  }
}
