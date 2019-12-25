package com.ctrip.framework.apollo.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

/**
 * An OrderedProperties instance will keep apperance order in config file.
 * @see https://github.com/etiennestuder/java-ordered-properties/blob/master/src/main/java/nu/studer/java/util/OrderedProperties.java
 */
public class OrderedProperties extends Properties {

  private static final long serialVersionUID = -5958921339581782524L;

  private transient Map<String, String> properties;
  private transient boolean suppressDate;

  /**
   * Creates a new instance that will keep the properties in the order they have been added. Other than
   * the ordering of the keys, this instance behaves like an instance of the {@link Properties} class.
   */
  public OrderedProperties() {
    this(new LinkedHashMap<String, String>(), false);
  }

  private OrderedProperties(Map<String, String> properties, boolean suppressDate) {
    this.properties = properties;
    this.suppressDate = suppressDate;
  }

  /**
   * See {@link Properties#getProperty(String)}.
   */
  @Override
  public String getProperty(String key) {
    return properties.get(key);
  }

  /**
   * See {@link Properties#getProperty(String, String)}.
   */
  @Override
  public String getProperty(String key, String defaultValue) {
    String value = properties.get(key);
    return (value == null) ? defaultValue : value;
  }


  /**
   * See {@link Properties#setProperty(String, String)}.
   */
  @Override
  public String setProperty(String key, String value) {
    return properties.put(key, value);
  }

  /**
   * Removes the property with the specified key, if it is present. Returns
   * the value of the property, or <tt>null</tt> if there was no property with
   * the specified key.
   *
   * @param key the key of the property to remove
   * @return the previous value of the property, or <tt>null</tt> if there was no property with the specified key
   */
  public String removeProperty(String key) {
    return properties.remove(key);
  }

  /**
   * Returns <tt>true</tt> if there is a property with the specified key.
   *
   * @param key the key whose presence is to be tested
   */
  public boolean containsProperty(String key) {
    return properties.containsKey(key);
  }

  /**
   * See {@link Properties#size()}.
   */
  @Override
  public int size() {
    return properties.size();
  }

  /**
   * See {@link Properties#isEmpty()}.
   */
  @Override
  public boolean isEmpty() {
    return properties.isEmpty();
  }

  /**
   * See {@link Properties#containsKey(Object)} and {@link java.util.Hashtable#containsKey(Object)}.
   * Override to avoid invocation on Properties's containsKey.
   */
  @Override
  public boolean containsKey(Object key) {
    return properties.containsKey(key);
  }

  /**
   * See {@link Properties#containsValue(Object)} and {@link java.util.Hashtable#containsValue(Object)}.
   * Override to avoid invocation on Properties's containsValue.
   */
  @Override
  public boolean containsValue(Object value) {
    return properties.containsValue(value);
  }

  /**
   * See {@link Properties#get(Object)} and {@link java.util.Hashtable#get(Object)}.
   * Override due to invocation on Properties's get.
   */
  @Override
  public synchronized Object get(Object key) {
    return properties.get(key);
  }

  /**
   * See {@link Properties#contains(Object)} and {@link java.util.Hashtable#contains(Object)}.
   *  Override due to invocation on Properties's get.
   */
  @Override
  public synchronized boolean contains(Object key) {
    return this.containsKey(key);
  }

  /**
   * See {@link Properties#put(Object, Object)} and {@link java.util.Hashtable#put(Object, Object)}.
   * Override due to invocation on Properties's put.
   */
  @Override
  public synchronized Object put(Object key, Object value) {
    // Make sure the value is not null
    if (value == null) {
      throw new NullPointerException();
    }

    // Makes sure the key is not already in the hashtable.
    return properties.put((String)key,(String)value);
  }

  /**
   * See {@link Properties#propertyNames()}.
   */
  @Override
  public Enumeration<String> propertyNames() {
    return new Vector<String>(properties.keySet()).elements();
  }

  /**
   * See {@link Properties#stringPropertyNames()}.
   */
  @Override
  public Set<String> stringPropertyNames() {
    return new LinkedHashSet<String>(properties.keySet());
  }

  /**
   * See {@link Properties#entrySet()}.
   * @return
   */
  @Override
  public Set<Map.Entry<Object, Object>> entrySet() {
    return new LinkedHashSet<Map.Entry<Object, Object>>((Set) properties.entrySet());
  }

  /**
   * See {@link Properties#load(InputStream)}.
   */
  @Override
  public void load(InputStream stream) throws IOException {
    CustomProperties customProperties = new CustomProperties(this.properties);
    customProperties.load(stream);
  }

  /**
   * See {@link Properties#load(Reader)}.
   */
  @Override
  public void load(Reader reader) throws IOException {
    CustomProperties customProperties = new CustomProperties(this.properties);
    customProperties.load(reader);
  }

  /**
   * See {@link Properties#loadFromXML(InputStream)}.
   */
  @SuppressWarnings("DuplicateThrows")
  @Override
  public void loadFromXML(InputStream stream) throws IOException, InvalidPropertiesFormatException {
    CustomProperties customProperties = new CustomProperties(this.properties);
    customProperties.loadFromXML(stream);
  }

  /**
   * See {@link Properties#store(OutputStream, String)}.
   */
  @Override
  public void store(OutputStream stream, String comments) throws IOException {
    CustomProperties customProperties = new CustomProperties(this.properties);
    if (suppressDate) {
      customProperties.store(new DateSuppressingPropertiesBufferedWriter(new OutputStreamWriter(stream, "8859_1")), comments);
    } else {
      customProperties.store(stream, comments);
    }
  }

  /**
   * See {@link Properties#store(Writer, String)}.
   */
  @Override
  public void store(Writer writer, String comments) throws IOException {
    CustomProperties customProperties = new CustomProperties(this.properties);
    if (suppressDate) {
      customProperties.store(new DateSuppressingPropertiesBufferedWriter(writer), comments);
    } else {
      customProperties.store(writer, comments);
    }
  }

  /**
   * See {@link Properties#storeToXML(OutputStream, String)}.
   */
  @Override
  public void storeToXML(OutputStream stream, String comment) throws IOException {
    CustomProperties customProperties = new CustomProperties(this.properties);
    customProperties.storeToXML(stream, comment);
  }

  /**
   * See {@link Properties#storeToXML(OutputStream, String, String)}.
   */
  @Override
  public void storeToXML(OutputStream stream, String comment, String encoding) throws IOException {
    CustomProperties customProperties = new CustomProperties(this.properties);
    customProperties.storeToXML(stream, comment, encoding);
  }

  /**
   * See {@link Properties#list(PrintStream)}.
   */
  @Override
  public void list(PrintStream stream) {
    CustomProperties customProperties = new CustomProperties(this.properties);
    customProperties.list(stream);
  }

  /**
   * See {@link Properties#list(PrintWriter)}.
   */
  @Override
  public void list(PrintWriter writer) {
    CustomProperties customProperties = new CustomProperties(this.properties);
    customProperties.list(writer);
  }

  /**
   * Convert this instance to a {@link Properties} instance.
   *
   * @return the {@link Properties} instance
   */
  public Properties toJdkProperties() {
    Properties jdkProperties = new Properties();
    for (Map.Entry<Object, Object> entry : this.entrySet()) {
      jdkProperties.put(entry.getKey(), entry.getValue());
    }
    return jdkProperties;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (other == null || getClass() != other.getClass()) {
      return false;
    }

    OrderedProperties that = (OrderedProperties) other;
    return Arrays.equals(properties.entrySet().toArray(), that.properties.entrySet().toArray());
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(properties.entrySet().toArray());
  }

  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    stream.writeObject(properties);
    stream.writeBoolean(suppressDate);
  }

  @SuppressWarnings("unchecked")
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    properties = (Map<String, String>) stream.readObject();
    suppressDate = stream.readBoolean();
  }

  private void readObjectNoData() throws InvalidObjectException {
    throw new InvalidObjectException("Stream data required");
  }

  /**
   * See {@link Properties#toString()}.
   */
  @Override
  public String toString() {
    return properties.toString();
  }

  /**
   * Copies all of the mappings from the specified map to this hashtable.
   * These mappings will replace any mappings that this hashtable had for any
   * of the keys currently in the specified map.
   *

   */
  @Override
  public synchronized void putAll(Map<? extends Object, ? extends Object> t) {
    for (Map.Entry<? extends Object, ? extends Object> e : t.entrySet()) {
      properties.put((String)e.getKey(),(String) e.getValue());
    }
  }

  @Override
  public Set<Object> keySet() {
    return (Set)properties.keySet();
  }

  /**
   * @see {@link Hashtable#clear()}
   * TODO: to test
   */
  @Override
  public synchronized void clear() {
    properties.clear();
  }

  /**
   * Clone a new OrderedProperties by deep copy
   * @see {@link Hashtable#clone()}
   * TODO: to test
   * @return
   */
  @Override
  public synchronized Object clone() {
    OrderedProperties orderedProperties= (OrderedProperties) super.clone();
    orderedProperties.properties=new LinkedHashMap<>(properties);
    return orderedProperties;
  }

  /**
   * return value elements
   * @see {@link Hashtable#elements()}
   * TODO: to test
   * @return
   */
  @Override
  public synchronized Enumeration<Object> elements() {
    return new Vector<Object>(properties.values()).elements();
  }

  /**
   * @see {@link Hashtable#getOrDefault(Object, Object)}
   * @param key
   * @param defaultValue
   * @return
   * @since 1.8
   */
//  public synchronized Object getOrDefault(Object key, Object defaultValue) {
//    return this.getProperty((String)key, (String)defaultValue);
//  }

  /**
   * @see {@link Hashtable#keys}
   * @return
   */
  @Override
  public synchronized Enumeration<Object> keys() {
    return new Vector<Object>(properties.keySet()).elements();
  }

  /**
   * @see {@link Hashtable#remove(Object)}
   * @param key
   * @return
   * TODO: to test
   */
  @Override
  public synchronized Object remove(Object key) {
    return properties.remove(key);
  }

  /**
   * @see {@link Hashtable#remove(Object, Object)}
   * @param key
   * @param value
   * @return
   *  @since 1.8
   */
//  @Override
//  public synchronized boolean remove(Object key, Object value) {
//    return properties.remove((String)key, (String)value);
//  }

  /**
   * @see {@link Hashtable#replace(Object, Object, Object)}
   * @param key
   * @param oldValue
   * @param newValue
   * @return
   *  \@since 1.8
   */
//  @Override
//  public synchronized boolean replace(Object key, Object oldValue, Object newValue) {
//    return properties.replace((String)key, (String)oldValue, (String)newValue);
//  }


  /**
   * @see {@link Hashtable#replace(Object, Object)}
   * @param key
   * @param value
   * @return
   *  @since 1.8
   */
//  @Override
//  public synchronized Object replace(Object key, Object value) {
//    return properties.replace((String)key, (String)value);
//  }

  /**
   * @see {@link Hashtable#replaceAll(BiFunction)}
   * @param function
   *  @since 1.8
   */
//  @Override
//  public synchronized void replaceAll(BiFunction<? super Object, ? super Object, ?> function) {
//    throw new UnsupportedOperationException();
//  }

  /**
   * see {@link Hashtable#values()}
   * @return
   * TODO: to test
   */
  @Override
  public Collection<Object> values() {
    return (Collection)properties.values();
  }

  /**
   * see {@link Hashtable#putIfAbsent(Object, Object)}
   * @param key
   * @param value
   * @return
   *  @since 1.8
   */
//  @Override
//  public synchronized Object putIfAbsent(Object key, Object value) {
//    return properties.putIfAbsent((String)key, (String)value);
//  }

  /**
   * Creates a new instance that will have both the same property entries and
   * the same behavior as the given source.
   * <p/>
   * Note that the source instance and the copy instance will share the same
   * comparator instance if a custom ordering had been configured on the source.
   *
   * @param source the source to copy from
   * @return the copy
   */
  public static OrderedProperties copyOf(OrderedProperties source) {
    // create a copy that has the same behaviour
    OrderedPropertiesBuilder builder = new OrderedPropertiesBuilder();
    builder.withSuppressDateInComment(source.suppressDate);
    if (source.properties instanceof TreeMap) {
      builder.withOrdering(((TreeMap<String, String>) source.properties).comparator());
    }
    OrderedProperties result = builder.build();

    // copy the properties from the source to the target
    for (Map.Entry<Object, Object> entry : source.entrySet()) {
      result.setProperty((String)entry.getKey(), (String)entry.getValue());
    }
    return result;
  }

  /**
   * Builder for {@link OrderedProperties} instances.
   */
  public static final class OrderedPropertiesBuilder {

    private Comparator<? super String> comparator;
    private boolean suppressDate;

    /**
     * Use a custom ordering of the keys.
     *
     * @param comparator the ordering to apply on the keys
     * @return the builder
     */
    public OrderedPropertiesBuilder withOrdering(Comparator<? super String> comparator) {
      this.comparator = comparator;
      return this;
    }

    /**
     * Suppress the comment that contains the current date when storing the properties.
     *
     * @param suppressDate whether to suppress the comment that contains the current date
     * @return the builder
     */
    public OrderedPropertiesBuilder withSuppressDateInComment(boolean suppressDate) {
      this.suppressDate = suppressDate;
      return this;
    }

    /**
     * Builds a new {@link OrderedProperties} instance.
     *
     * @return the new instance
     */
    public OrderedProperties build() {
      Map<String, String> properties = (this.comparator != null) ?
          new TreeMap<String, String>(comparator) :
          new LinkedHashMap<String, String>();
      return new OrderedProperties(properties, suppressDate);
    }

  }

  /**
   * Custom {@link Properties} that delegates reading, writing, and enumerating properties to the
   * backing {@link OrderedProperties} instance's properties.
   */
  private static final class CustomProperties extends Properties {

    private final Map<String, String> targetProperties;

    private CustomProperties(Map<String, String> targetProperties) {
      this.targetProperties = targetProperties;
    }

    @Override
    public Object get(Object key) {
      return targetProperties.get(key);
    }

    @Override
    public Object put(Object key, Object value) {
      return targetProperties.put((String) key, (String) value);
    }

    @Override
    public String getProperty(String key) {
      return targetProperties.get(key);
    }

    @Override
    public Enumeration<Object> keys() {
      return new Vector<Object>(targetProperties.keySet()).elements();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Set<Object> keySet() {
      return new LinkedHashSet<Object>(targetProperties.keySet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
      Set entrySet = targetProperties.entrySet();
      return (Set<Map.Entry<Object, Object>>) entrySet;
    }

  }

  /**
   * Custom {@link BufferedWriter} for storing properties that will write all leading lines of comments except
   * the last comment line. Using the JDK Properties class to store properties, the last comment
   * line always contains the current date which is what we want to filter out.
   */
  private static final class DateSuppressingPropertiesBufferedWriter extends BufferedWriter {

    private final String LINE_SEPARATOR = System.getProperty("line.separator");

    private StringBuilder currentComment;
    private String previousComment;

    private DateSuppressingPropertiesBufferedWriter(Writer out) {
      super(out);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void write(String string) throws IOException {
      if (currentComment != null) {
        currentComment.append(string);
        if (string.endsWith(LINE_SEPARATOR)) {
          if (previousComment != null) {
            super.write(previousComment);
          }

          previousComment = currentComment.toString();
          currentComment = null;
        }
      } else if (string.startsWith("#")) {
        currentComment = new StringBuilder(string);
      } else {
        super.write(string);
      }
    }

  }
}
