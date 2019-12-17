package com.ctrip.framework.apollo.internals;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import com.ctrip.framework.apollo.ConfigFileChangeListener;
import com.ctrip.framework.apollo.PropertiesCompatibleConfigFile;
import com.ctrip.framework.apollo.enums.ConfigSourceType;
import com.ctrip.framework.apollo.model.ConfigFileChangeEvent;
import com.google.common.base.Preconditions;

public class PropertiesCompatibleFileConfigRepository extends AbstractConfigRepository implements
    ConfigFileChangeListener {
  private final PropertiesCompatibleConfigFile configFile;
  private volatile LinkedHashMap cachedProperties;

  public PropertiesCompatibleFileConfigRepository(PropertiesCompatibleConfigFile configFile) {
    this.configFile = configFile;
    this.configFile.addChangeListener(this);
    this.trySync();
  }

  @Override
  protected synchronized void sync() {
    LinkedHashMap current = configFile.asSequenceProperties();

    Preconditions.checkState(current != null, "PropertiesCompatibleConfigFile.asProperties should never return null");

    if (cachedProperties != current) {
      cachedProperties = current;
      this.fireRepositoryChange(configFile.getNamespace(), (Map)cachedProperties);
    }
  }

  @Override
  @Deprecated
  public Properties getConfig() {
    Properties properties=new Properties();
    properties.putAll(getSequenceConfig());
    return properties;
  }

  /**
   * Get the config from this repository with config origin order.
   *
   * @return config
   */
  @Override
  public LinkedHashMap getSequenceConfig() {
    if (cachedProperties == null) {
      sync();
    }
    return cachedProperties;
  }

  @Override
  public void setUpstreamRepository(ConfigRepository upstreamConfigRepository) {
    //config file is the upstream, so no need to set up extra upstream
  }

  @Override
  public ConfigSourceType getSourceType() {
    return configFile.getSourceType();
  }

  @Override
  public void onChange(ConfigFileChangeEvent changeEvent) {
    this.trySync();
  }
}
