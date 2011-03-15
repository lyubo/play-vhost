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
  private List<String>        fqdns;
  private DataSource          dataSource;
  Map<String, String>         config;
  private Map<String, Object> properties;
  Set<VirtualHostListener>    listeners;

  static public VirtualHost current()
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

  public VirtualHost(List<String> pFqdns, Map<String, String> pConfig, DataSource ds)
  {
    if (pFqdns == null || pFqdns.size() == 0) throw new IllegalArgumentException("Unexpected parameter values");
    fqdns = pFqdns;
    properties = new HashMap<String, Object>();
    config = new HashMap<String, String>();
    listeners = new HashSet<VirtualHostListener>();
    for (String key : pConfig.keySet()) {
      if (!(key.equals("fqdn") || key.startsWith("db."))) config.putAll(pConfig);
    }
    this.dataSource = ds;
  }

  public String getName()
  {
    return fqdns.get(0);
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
    return properties.put(pName, pValue);
  }

  public void addListener(VirtualHostListener pListener)
  {
    if (pListener != null) listeners.add(pListener);
  }

  public DataSource getDataSource()
  {
    return dataSource;
  }

  public String cacheKey(String pKey)
  {
    return String.format("%d:%s", fqdns.get(0).hashCode(), pKey);
  }

  public <T> T cacheGet(String pKey, Class<T> pClass)
  {
    return Cache.get(cacheKey(pKey), pClass);
  }

}
