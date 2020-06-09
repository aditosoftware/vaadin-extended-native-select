package de.aditosoftware.demo;

import com.sun.tools.javac.util.List;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import de.aditosoftware.ExtendedNativeSelect;

import javax.servlet.annotation.WebServlet;

@Theme("demo")
@Title("MyComponent Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

  @WebServlet(value = "/*", asyncSupported = true)
  @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
  public static class Servlet extends VaadinServlet {
  }

  @Override
  protected void init (VaadinRequest request) {

    ListDataProvider<String> dataProvider = new ListDataProvider<>(List.of("first", "second", "third", "fourth"));

    // Initialize our new UI component
    final ExtendedNativeSelect<String> component = new ExtendedNativeSelect<>();
    component.setEmptySelectionAllowed(true);
    component.setPlaceholder("Eigenschaften");
    component.addSelectionListener(event -> System.out.println(event.getSelectedItem()));
//    component.setEnabled(false);
    component.setValue("first");
    component.addStyleName("test-test");

    component.setDataProvider(dataProvider);

    setContent(component);

    // Show it in the middle of the screen
    /*final VerticalLayout layout = new VerticalLayout();
    layout.setStyleName("demoContentLayout");
    layout.setSizeFull();
    layout.setMargin(false);
    layout.setSpacing(false);
    layout.addComponent(component);
    layout.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
    setContent(layout);*/
  }
}
