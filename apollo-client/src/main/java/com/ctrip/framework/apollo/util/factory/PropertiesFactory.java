package com.ctrip.framework.apollo.util.factory;

import com.ctrip.framework.apollo.util.OrderedProperties;
import java.util.Properties;

public class PropertiesFactory {

  public static Properties getPropertiesObject(){
    return new OrderedProperties();
  }
}
