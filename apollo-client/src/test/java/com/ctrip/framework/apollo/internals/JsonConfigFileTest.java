package com.ctrip.framework.apollo.internals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.ctrip.framework.apollo.enums.ConfigSourceType;

import java.util.LinkedHashMap;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonConfigFileTest {
  private String someNamespace;
  @Mock
  private ConfigRepository configRepository;

  private ConfigSourceType someSourceType;

  @Before
  public void setUp() throws Exception {
    someNamespace = "someName";
  }

  @Test
  public void testWhenHasContent() throws Exception {
    Properties someProperties = new Properties();
    String key = ConfigConsts.CONFIG_FILE_CONTENT_KEY;
    String someValue = "someValue";
    someProperties.setProperty(key, someValue);

    someSourceType = ConfigSourceType.LOCAL;

    mockConfigRepository(someProperties);
    when(configRepository.getSourceType()).thenReturn(someSourceType);

    JsonConfigFile configFile = new JsonConfigFile(someNamespace, configRepository);

    assertEquals(ConfigFileFormat.JSON, configFile.getConfigFileFormat());
    assertEquals(someNamespace, configFile.getNamespace());
    assertTrue(configFile.hasContent());
    assertEquals(someValue, configFile.getContent());
    assertEquals(someSourceType, configFile.getSourceType());
  }

  @Test
  public void testWhenHasNoContent() throws Exception {
    mockConfigRepository(null);

    JsonConfigFile configFile = new JsonConfigFile(someNamespace, configRepository);

    assertFalse(configFile.hasContent());
    assertNull(configFile.getContent());
  }

  @Test
  public void testWhenConfigRepositoryHasError() throws Exception {
//    when(configRepository.getConfig()).thenThrow(new RuntimeException("someError"));
    when(configRepository.getSequenceConfig()).thenThrow(new RuntimeException("someError"));

    JsonConfigFile configFile = new JsonConfigFile(someNamespace, configRepository);

    assertFalse(configFile.hasContent());
    assertNull(configFile.getContent());
    assertEquals(ConfigSourceType.NONE, configFile.getSourceType());
  }

  @Test
  public void testOnRepositoryChange() throws Exception {
    Properties someProperties = new Properties();
    String key = ConfigConsts.CONFIG_FILE_CONTENT_KEY;
    String someValue = "someValue";
    String anotherValue = "anotherValue";
    someProperties.setProperty(key, someValue);

    someSourceType = ConfigSourceType.LOCAL;

    mockConfigRepository(someProperties);
    when(configRepository.getSourceType()).thenReturn(someSourceType);

    JsonConfigFile configFile = new JsonConfigFile(someNamespace, configRepository);

    assertEquals(someValue, configFile.getContent());
    assertEquals(someSourceType, configFile.getSourceType());

    Properties anotherProperties = new Properties();
    anotherProperties.setProperty(key, anotherValue);

    ConfigSourceType anotherSourceType = ConfigSourceType.REMOTE;
    when(configRepository.getSourceType()).thenReturn(anotherSourceType);

    configFile.onRepositoryChange(someNamespace, anotherProperties);

    assertEquals(anotherValue, configFile.getContent());
    assertEquals(anotherSourceType, configFile.getSourceType());
  }

  private void mockConfigRepository(Properties someProperties) {
    when(configRepository.getConfig()).thenReturn(someProperties);

    if(someProperties!=null) {

      LinkedHashMap linkedHashMap = new LinkedHashMap();
      linkedHashMap.putAll(someProperties);
      when(configRepository.getSequenceConfig()).thenReturn(linkedHashMap);
    }
    else{
      when(configRepository.getSequenceConfig()).thenReturn(null);
    }

  }

  @Test
  public void testWhenConfigRepositoryHasErrorAndThenRecovered() throws Exception {
    Properties someProperties = new Properties();
    String key = ConfigConsts.CONFIG_FILE_CONTENT_KEY;
    String someValue = "someValue";
    someProperties.setProperty(key, someValue);

    someSourceType = ConfigSourceType.LOCAL;

    when(configRepository.getSequenceConfig()).thenThrow(new RuntimeException("someError"));
    when(configRepository.getSourceType()).thenReturn(someSourceType);

    JsonConfigFile configFile = new JsonConfigFile(someNamespace, configRepository);

    assertFalse(configFile.hasContent());
    assertNull(configFile.getContent());
    assertEquals(ConfigSourceType.NONE, configFile.getSourceType());

    configFile.onRepositoryChange(someNamespace, someProperties);

    assertTrue(configFile.hasContent());
    assertEquals(someValue, configFile.getContent());
    assertEquals(someSourceType, configFile.getSourceType());
  }
}
