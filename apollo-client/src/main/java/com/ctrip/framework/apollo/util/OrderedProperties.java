package com.ctrip.framework.apollo.util;

import java.util.*;
import java.util.Map.Entry;

/**
 * An OrderedProperties instance will keep apperance order in config file.
 */
public class OrderedProperties extends Properties {

  private static final long serialVersionUID = -1741073539526213291L;
  private final Set<String> propertyNames;

  public OrderedProperties() {
    propertyNames = Collections.synchronizedSet(new LinkedHashSet<String>());
  }

  @Override
  public synchronized Object put(Object key, Object value) {
    addPropertyName(key);
    return super.put(key, value);
  }

  @Override
  public Set<String> stringPropertyNames() {
    return propertyNames;
  }

  @Override
  public Enumeration<?> propertyNames() {
    return Collections.enumeration(propertyNames);
  }

  @Override
  public synchronized Enumeration<Object> keys() {
    return new Enumeration<Object>() {
      private final Iterator<String> i = propertyNames.iterator();

      @Override
      public boolean hasMoreElements() {
        return i.hasNext();
      }
      @Override
      public Object nextElement() {
        return i.next();
      }
    };
  }

  @Override
  public Set<Object> keySet() {
    return new LinkedHashSet<Object>(propertyNames);
  }


  @Override
  public Set<Entry<Object, Object>> entrySet() {
    Set<Entry<Object, Object>> original = super.entrySet();
    LinkedHashMap<Object, Entry<Object, Object>> entryMap = new LinkedHashMap<>();
    for (String propertyName : propertyNames) {
      entryMap.put(propertyName, null);
    }

    for (Entry<Object, Object> entry : original) {
      entryMap.put(entry.getKey(), entry);
    }

    return new LinkedHashSet<>(entryMap.values());
  }

  private void addPropertyName(Object key) {
    if (key instanceof String) {
      propertyNames.add((String) key);
    }
  }

  @Override
  public synchronized void putAll(Map<?, ?> t) {
    for(Entry entry:t.entrySet()){
      put(entry.getKey(),entry.getValue());
    }

  }

  @Override
  public synchronized void clear() {
    super.clear();
    this.propertyNames.clear();
  }

  @Override
  public synchronized Object remove(Object key) {
    this.propertyNames.remove(key);
    return super.remove(key);
  }

}
