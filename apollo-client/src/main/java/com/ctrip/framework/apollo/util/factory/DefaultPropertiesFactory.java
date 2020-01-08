package com.ctrip.framework.apollo.util.factory;

import com.ctrip.framework.apollo.util.OrderedProperties;
import com.google.common.base.Strings;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default PropertiesFactory implementation.
 *
 * @author songdragon@zts.io
 */
public class DefaultPropertiesFactory implements PropertiesFactory {

  private static final Logger logger = LoggerFactory.getLogger(DefaultPropertiesFactory.class);

  protected boolean isOrderEnabled = false;

  public DefaultPropertiesFactory() {
    initOrderEnabled();
  }

  private void initOrderEnabled() {
    String customizedOrderEnable = System.getProperty(APOLLO_PROPERTY_ORDER_ENABLE);
    if (!Strings.isNullOrEmpty(customizedOrderEnable)) {
      try {
        isOrderEnabled = Boolean.parseBoolean(customizedOrderEnable);
      } catch (Throwable ex) {
        logger.warn("Config for {} is invalid: {}, set default value: false",
            APOLLO_PROPERTY_ORDER_ENABLE, customizedOrderEnable);
      }
    }
  }

  @Override
  public Properties getPropertiesInstance() {
    if (isOrderEnabled) {
      return new OrderedProperties();
    } else {
      return new Properties();
    }
  }
}
