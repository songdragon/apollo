package com.ctrip.framework.apollo.internals;

import java.util.Map;
import java.util.Properties;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface RepositoryChangeListener {
  /**
   * Invoked when config repository changes.
   * @param namespace the namespace of this repository change
   * @param newProperties the properties after change
   */
  @Deprecated
  public void onRepositoryChange(String namespace, Properties newProperties);

  /**
   * Invoked when config repository changes.
   * @param namespace the namespace of this repository change
   * @param newProperties the properties after change
   */
  public void onRepositoryChange(String namespace, Map newProperties);
}
