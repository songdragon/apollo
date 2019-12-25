package com.ctrip.framework.apollo.util;

import static org.junit.Assert.*;

import java.util.Collection;
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

  @Test
  public void testClear(){
    orderedProperties.clear();
    assertEquals(0,orderedProperties.size());
    assertTrue(orderedProperties.isEmpty());
  }

  @Test
  public void testClone(){
    OrderedProperties clone= (OrderedProperties) orderedProperties.clone();

    assertNotSame(clone, orderedProperties);
    assertEquals(orderedProperties,clone);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testElements(){
    orderedProperties.elements();
  }

  @Test
  public void testRemove(){
    Object value1 = orderedProperties.remove("key1");
    assertEquals("value1",value1);

    value1=orderedProperties.remove("key1");
    assertNull(value1);
  }

  @Test
  public void testValues(){
    Collection<Object> values = orderedProperties.values();
    assertEquals(2, values.size());
    assertTrue(values.contains("value1"));
    assertTrue(values.contains("value2"));
  }

  /**
   * Test cases for JDK1.8
   */
  /*
  @Test
  public void testGetOrDefault(){
    String value1 = (String) orderedProperties.getOrDefault("key1", "keyNotExisted");
    assertEquals("value1",value1);

    String defaultValue=(String) orderedProperties.getOrDefault("key3","");
    assertEquals("",defaultValue);

    String defaultNullValue=(String)orderedProperties.getOrDefault("key3",null);
    assertNull(defaultNullValue);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testKeys(){
    orderedProperties.keys();
  }

  @Test
  public void testPutIfAbsent(){
    Object oldValue3 = orderedProperties.putIfAbsent("key3", "value3");
    assertNull(oldValue3);

    Object oldValue2 = orderedProperties.putIfAbsent("key2", "value3");
    assertEquals("value2",oldValue2);

    assertEquals("value2",orderedProperties.getProperty("key2"));
  }



  @Test
  public void testRemoveObjectWithRetBoolean(){
    assertTrue(orderedProperties.remove("key1","value1"));
    assertFalse(orderedProperties.remove("key2","value1"));
    assertFalse(orderedProperties.remove("key3",null));
  }

  @Test
  public void testReplace(){
    String preValue1= (String) orderedProperties.replace("key1","value11");
    assertEquals("value1",preValue1);

    String preValue3 =(String) orderedProperties.replace("key3", "value3");
    assertNull(preValue3);
    assertFalse(orderedProperties.containsKey("key3"));
  }

  @Test
  public void testReplaceWithRetBoolean(){

    assertFalse(orderedProperties.replace("key1", "value11", "value111"));
    assertTrue(orderedProperties.replace("key1","value1","value11"));
    assertEquals("value11",orderedProperties.get("key1"));

    assertFalse(orderedProperties.replace("key3",null,null));
    assertFalse(orderedProperties.containsKey("key3"));
  }

   */
  

}
