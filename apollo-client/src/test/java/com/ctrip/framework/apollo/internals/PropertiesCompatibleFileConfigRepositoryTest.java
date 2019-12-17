package com.ctrip.framework.apollo.internals;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ctrip.framework.apollo.PropertiesCompatibleConfigFile;
import com.ctrip.framework.apollo.enums.ConfigSourceType;
import com.ctrip.framework.apollo.model.ConfigFileChangeEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PropertiesCompatibleFileConfigRepositoryTest {

  @Mock
  private PropertiesCompatibleConfigFile configFile;

  private String someNamespaceName;

  @Mock
  private Properties someProperties;

  @Mock
  private LinkedHashMap someLinkedHashMap;

  @Before
  public void setUp() throws Exception {
    someNamespaceName = "someNamespaceName";
    when(configFile.getNamespace()).thenReturn(someNamespaceName);
    when(configFile.asProperties()).thenReturn(someProperties);
    when(configFile.asSequenceProperties()).thenReturn(someLinkedHashMap);
  }

  @Test
  public void testGetConfig() throws Exception {
    PropertiesCompatibleFileConfigRepository configFileRepository = new PropertiesCompatibleFileConfigRepository(
        configFile);

    assertSame(someLinkedHashMap, configFileRepository.getSequenceConfig());

    //TOFIX: TODO:assertEquals should be remove
    //assertEquals(someProperties, configFileRepository.getConfig());
    verify(configFile, times(1)).addChangeListener(configFileRepository);
  }

  @Test
  public void testGetConfigFailedAndThenRecovered() throws Exception {
    RuntimeException someException = new RuntimeException("some exception");

    when(configFile.asProperties()).thenThrow(someException);
    when(configFile.asSequenceProperties()).thenThrow(someException);

    PropertiesCompatibleFileConfigRepository configFileRepository = new PropertiesCompatibleFileConfigRepository(
        configFile);

    Throwable exceptionThrown = null;
    try {
      configFileRepository.getConfig();
    } catch (Throwable ex) {
      exceptionThrown = ex;
    }

    assertSame(someException, exceptionThrown);

    // recovered
    reset(configFile);

    Properties someProperties = mock(Properties.class);
    LinkedHashMap someLinkedHashMap = mock(LinkedHashMap.class);

    when(configFile.asProperties()).thenReturn(someProperties);
    when(configFile.asSequenceProperties()).thenReturn(someLinkedHashMap);

    assertSame(someLinkedHashMap, configFileRepository.getSequenceConfig());
    //TOFIX: TODO: should not check same
    //assertSame(someProperties, configFileRepository.getConfig());
  }

  @Test(expected = IllegalStateException.class)
  public void testGetConfigWithConfigFileReturnNullProperties() throws Exception {
    when(configFile.asProperties()).thenReturn(null);
    when(configFile.asSequenceProperties()).thenReturn(null);

    PropertiesCompatibleFileConfigRepository configFileRepository = new PropertiesCompatibleFileConfigRepository(
        configFile);

    configFileRepository.getConfig();
  }

  @Test
  public void testGetSourceType() throws Exception {
    ConfigSourceType someType = ConfigSourceType.REMOTE;

    when(configFile.getSourceType()).thenReturn(someType);

    PropertiesCompatibleFileConfigRepository configFileRepository = new PropertiesCompatibleFileConfigRepository(
        configFile);

    assertSame(someType, configFileRepository.getSourceType());
  }

  @Test
  public void testOnChange() throws Exception {
    Properties anotherProperties = mock(Properties.class);
    LinkedHashMap antoherLinkedHashMap = mock(LinkedHashMap.class);
    ConfigFileChangeEvent someChangeEvent = mock(ConfigFileChangeEvent.class);

    RepositoryChangeListener someListener = mock(RepositoryChangeListener.class);

    PropertiesCompatibleFileConfigRepository configFileRepository = new PropertiesCompatibleFileConfigRepository(
        configFile);

    configFileRepository.addChangeListener(someListener);

    //TOFIX: TODO:assertEquals should be remove
    //assertEquals(someProperties, configFileRepository.getConfig());
    assertSame(someLinkedHashMap, configFileRepository.getSequenceConfig());

    when(configFile.asProperties()).thenReturn(anotherProperties);
    when(configFile.asSequenceProperties()).thenReturn(antoherLinkedHashMap);

    configFileRepository.onChange(someChangeEvent);

    assertSame(antoherLinkedHashMap, configFileRepository.getSequenceConfig());
    //TOFIX: TODO:assertEquals should be remove
    //assertEquals(anotherProperties, configFileRepository.getConfig());
    verify(someListener, times(0)).onRepositoryChange(someNamespaceName, anotherProperties);
    verify(someListener, times(1)).onRepositoryChange(someNamespaceName, (Map)antoherLinkedHashMap);
  }
}
