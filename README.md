# Extended Native Select

This add-on provides a native select with placeholder and empty selection capabilities. This is mostly inspired by the [NativeSelect](https://vaadin.com/docs/v8/framework/components/components-nativeselect.html) provided by the Vaadin framework.

## Download release

Official releases of this add-on are available at Vaadin Directory. For Maven instructions, download and reviews, go to https://vaadin.com/addon/extended-native-select

## Getting started

Here is a simple example on how to try out the add-on

```java
ExtendedNativeSelect<String> component = new ExtendedNativeSelect<>();

// Allow empty selection.
component.setEmptySelectinAllowed(true);

// Set the placeholder.
component.setPlaceholder("Eigenschaften");

// Set your DataProvider etc
```

# Building and running demo

- git clone https://github.com/aditosoftware/vaadin-extended-native-select
- mvn clean install
- cd extended-native-select-demo
- mvn jetty:run

To see the demo, navigate to http://localhost:8080/
