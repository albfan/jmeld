package org.jmeld.util.conf;

import javax.xml.bind.*;

import java.io.*;
import java.util.*;

public class ConfigurationPersister
{
  // class variables:
  private static ConfigurationPersister instance = new ConfigurationPersister();

  // instance variables:
  private Map<Class, Context> contexts = new HashMap<Class, Context>();

  private ConfigurationPersister()
  {
  }

  public static ConfigurationPersister getInstance()
  {
    return instance;
  }

  /** Load a configuration of type 'clazz' from a file.
   */
  public <T extends AbstractConfiguration> T load(
    Class<T> clazz,
    File     file)
    throws FileNotFoundException
  {
    InputStream is;
    Context     context;
    T           configuration;

    is = new FileInputStream(file);

    try
    {
      context = getContext(clazz);
      synchronized (context)
      {
        configuration = (T) context.unmarshal(is);

        // Initialize the root of the configuration. 
        // This can be used to prepare some transient maps, lists
        //   to speed up access (comparable with indexes in database)
        configuration.init();
        return configuration;
      }
    }
    catch (Exception ex)
    {
      //ex.printStackTrace();
    }

    return null;
  }

  /** Save a configuration to a file.
   */
  public void save(
    AbstractConfiguration configuration,
    File                  file)
    throws JAXBException, IOException
  {
    save(
      configuration,
      new FileOutputStream(file));
  }

  /** Save a configuration to a outputstream.
   */
  public void save(
    AbstractConfiguration configuration,
    OutputStream          os)
    throws JAXBException, IOException
  {
    Writer writer;
    Context                                context;

    writer = new OutputStreamWriter(os);

    context = getContext(configuration.getClass());
    synchronized (context)
    {
      context.marshal(configuration, writer);
    }

    os.close();
  }

  /** Print debug info if validation fails.
   *    Sometimes jaxb will not print any useful information. So
   *    always use this validation event handler.
   */
  private ValidationEventHandler getValidationEventHandler()
  {
    return new ValidationEventHandler()
      {
        public boolean handleEvent(ValidationEvent event)
        {
          ValidationEventLocator locator;

          locator = event.getLocator();
          System.out.println(locator.getLineNumber() + ": "
            + event.getMessage());
          return false;
        }
      };
  }

  /** Each class has it's own context to marshal and unmarshal.
   *   The context contains a jaxbcontext.
   */
  private Context getContext(Class clazz)
  {
    Context c;

    synchronized (contexts)
    {
      c = contexts.get(clazz);
      if (c == null)
      {
        c = new Context(clazz);
        contexts.put(clazz, c);
      }
    }

    return c;
  }

  class Context
  {
    private JAXBContext  jaxbContext;
    private Marshaller   marshaller;
    private Unmarshaller unmarshaller;

    Context(Class clazz)
    {
      try
      {
        jaxbContext = JAXBContext.newInstance(clazz);

        marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setEventHandler(getValidationEventHandler());

        unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setEventHandler(getValidationEventHandler());
      }
      catch (JAXBException e)
      {
        e.printStackTrace();
      }
    }

    public void marshal(
      Object configuration,
      Writer writer)
      throws JAXBException
    {
      marshaller.marshal(configuration, writer);
    }

    public Object unmarshal(InputStream is)
      throws JAXBException
    {
      return unmarshaller.unmarshal(is);
    }
  }
}
