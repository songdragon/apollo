package com.ctrip.framework.apollo.internals;

import com.ctrip.framework.apollo.util.ExceptionUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.framework.apollo.PropertiesCompatibleConfigFile;
import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.ctrip.framework.apollo.exceptions.ApolloConfigException;
import com.ctrip.framework.apollo.tracer.Tracer;
import com.ctrip.framework.apollo.util.yaml.YamlParser;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class YamlConfigFile extends PlainTextConfigFile implements PropertiesCompatibleConfigFile {
  private static final Logger logger = LoggerFactory.getLogger(YamlConfigFile.class);
  private volatile LinkedHashMap cachedProperties;

  public YamlConfigFile(String namespace, ConfigRepository configRepository) {
    super(namespace, configRepository);
    tryTransformToProperties();
  }

  @Override
  public ConfigFileFormat getConfigFileFormat() {
    return ConfigFileFormat.YAML;
  }

  @Override
  @Deprecated
  protected void update(Properties newProperties) {
    update((Map)newProperties);
  }

  @Override
  protected void update(Map newProperties) {
    super.update(newProperties);
    tryTransformToProperties();
  }

  @Override
  @Deprecated
  public Properties asProperties() {
    LinkedHashMap properties = asSequenceProperties();
    Properties decorated=new Properties();
    decorated.putAll(properties);
    return decorated;
  }

  /**
   * @return the properties form of the config file with original order in the file
   * @throws RuntimeException if the content could not be transformed to @{link java.util.LinkedHashMap}
   */
  @Override
  public LinkedHashMap asSequenceProperties() {
    if (cachedProperties == null) {
      transformToProperties();
    }
    return cachedProperties;
  }

  private boolean tryTransformToProperties() {
    try {
      transformToProperties();
      return true;
    } catch (Throwable ex) {
      Tracer.logEvent("ApolloConfigException", ExceptionUtil.getDetailMessage(ex));
      logger.warn("yaml to properties failed, reason: {}", ExceptionUtil.getDetailMessage(ex));
    }
    return false;
  }

  private synchronized void transformToProperties() {
    cachedProperties = toProperties();
  }

  private LinkedHashMap toProperties() {
    if (!this.hasContent()) {
      return new LinkedHashMap();
    }

    try {
      return ApolloInjector.getInstance(YamlParser.class).yamlToSequenceProperties(getContent());
    } catch (Throwable ex) {
      ApolloConfigException exception = new ApolloConfigException(
          "Parse yaml file content failed for namespace: " + m_namespace, ex);
      Tracer.logError(exception);
      throw exception;
    }
  }
}
