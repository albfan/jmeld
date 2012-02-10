package org.jmeld.util.conf;

import org.jmeld.util.*;

import java.io.*;
import java.util.*;

import javax.xml.bind.*;

public class ConfigurationPersister
{
  // class variables:
  private static ConfigurationPersister instance = new ConfigurationPersister();

  private ConfigurationPersister()
  {
  }

  public static ConfigurationPersister getInstance()
  {
    return instance;
  }

  /** Load a configuration of type 'clazz' from a file.
   */
  public <T extends AbstractConfiguration> T load(Class<T> clazz, File file)
      throws FileNotFoundException
  {
    T configuration;

    try
    {
      configuration = JaxbPersister.getInstance().load(clazz, file);
    }
    catch (Exception ex)
    {
      return null;
    }

    configuration.init();

    return configuration;
  }

  /** Save a configuration to a file.
   */
  public void save(AbstractConfiguration configuration, File file)
      throws JAXBException, IOException
  {
    JaxbPersister.getInstance().save(configuration, file);
  }
}
