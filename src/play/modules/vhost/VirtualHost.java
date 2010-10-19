package play.modules.vhost;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import play.cache.Cache;
import play.mvc.Http;
import play.mvc.Http.Request;

public class VirtualHost
{
  private String                homeDir;
  private List<String>          fqdns;
  private VirtualHostDataSource dataSource;
  private Map<String, String>   config;
  private Map<String, Object>   properties;
  Set<VirtualHostListener>      listeners;

  static public VirtualHost getCurrent()
  {
    Request currentRequest = Http.Request.current();
    return currentRequest != null ? VirtualHostsPlugin.findHost(currentRequest.domain) : null;
  }

  static public VirtualHost find(String domain)
  {
    return domain != null ? VirtualHostsPlugin.findHost(domain) : null;
  }

  public static VirtualHost[] getAll()
  {
    return VirtualHostsPlugin.getAllHosts();
  }

  public VirtualHost(String pHomeDir, List<String> pFqdns, Map<String, String> pConfig, VirtualHostDataSource ds)
  {
    if (pHomeDir == null || pFqdns == null) throw new IllegalArgumentException("Unexpected parameter values");
    homeDir = pHomeDir;
    fqdns = pFqdns;
    properties = new HashMap<String, Object>();
    config = new HashMap<String, String>();
    listeners = new HashSet<VirtualHostListener>();
    config.putAll(pConfig);
    this.dataSource = ds;
  }

  public String getHomeDir()
  {
    return homeDir;
  }

  public String getName()
  {
    return fqdns.get(0);
  }

  public String getTitle()
  {
    return config.containsKey("title") ? config.get("title") : fqdns.get(0);
  }

  public List<String> getFqdns()
  {
    return fqdns;
  }

  public String getConfigProperty(String pName, String pDefault)
  {
    String result = config.get(pName);
    return result != null ? result : pDefault;
  }

  public Object getProperty(String pName)
  {
    return properties.get(pName);
  }

  public void clearProperty(String pName)
  {
    properties.remove(pName);
  }

  public Object setProperty(String pName, Object pValue)
  {
    return setProperty(pName, pValue, null);
  }

  public Object setProperty(String pName, Object pValue, VirtualHostListener pListener)
  {
    if (pListener != null) listeners.add(pListener);
    return properties.put(pName, pValue);
  }

  public DataSource getDataSource()
  {
    return dataSource;
  }

  public String cacheKey(String pKey)
  {
    return String.format("%d:%s", homeDir.hashCode(), pKey);
  }

  public <T> T cacheGet(String pKey, Class<T> pClass)
  {
    return Cache.get(cacheKey(pKey), pClass);
  }

}
