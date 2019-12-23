package com.ctrip.framework.apollo.util;

import static org.junit.Assert.*;

import java.util.Properties;
import org.junit.Before;
import org.junit.Test;

public class OrderedPropertiesTest {

  private OrderedProperties orderedProperties;
  private Properties legacyProperties;

  @Before
  public void setUp(){
    orderedProperties=new OrderedProperties();
    orderedProperties.setProperty("key1","value1");
    orderedProperties.setProperty("key2","value2");
  }

  @Test
  public void testOrderedPropertiesInvokedAsLegacyProperties(){
    legacyProperties=orderedProperties;
    assertEquals(orderedProperties.size(),legacyProperties.size());

    legacyProperties.put("key3","value3");
    assertEquals(orderedProperties.size(),legacyProperties.size());

    assertEquals(orderedProperties.getProperty("key3"),legacyProperties.getProperty("key3"));
    assertEquals(orderedProperties.get("key3"),legacyProperties.get("key3"));

    assertEquals(orderedProperties.containsProperty("key2"),legacyProperties.contains("key2"));
    assertEquals(orderedProperties.containsKey("key2"),legacyProperties.containsKey("key2"));
    assertEquals(orderedProperties.containsValue("key2"),legacyProperties.containsValue("key2"));
    assertEquals(orderedProperties.containsValue("value2"),legacyProperties.containsValue("value2"));

    assertEquals(orderedProperties.entrySet(),legacyProperties.entrySet());
    assertEquals(orderedProperties.keySet(),legacyProperties.keySet());

  }
}
