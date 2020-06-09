package de.aditosoftware.vaadin.addon.extendednativeselect.client.util;

/**
 * Represents an option which contains a key and a value.
 */
public class KeyValueOption {
  private final String key;
  private final String value;

  public KeyValueOption (String key, String value) {
    this.key = key;
    this.value = value;
  }

  public String getKey () {
    return key;
  }

  public String getValue () {
    return value;
  }

  @Override
  public boolean equals (Object o) {
    if (this == o) return true;
    if (!(o instanceof KeyValueOption)) return false;

    KeyValueOption that = (KeyValueOption) o;

    return getKey() != null ? getKey().equals(that.getKey()) : that.getKey() == null;
  }

  @Override
  public int hashCode () {
    return getKey() != null ? getKey().hashCode() : 0;
  }
}
