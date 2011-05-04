package play.modules.vhost;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import play.mvc.Http;
import play.mvc.Http.Request;
import play.templates.BaseTemplate;

public class VirtualHost
{
  final static String         CFG_HOME_DIR = "homedir";

  private List<String>        fqdns;
  private DataSource          dataSource;
  private Map<String, Object> properties;
  Map<String, String>         config;
  Map<String, BaseTemplate>   templateCache;
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
    templateCache = new HashMap<String, BaseTemplate>();
    for (String key : pConfig.keySet()) {
      if (!key.toLowerCase().equals("fqdns") && !key.toLowerCase().startsWith("db.")) {
        config.put(key.toLowerCase(), pConfig.get(key));
      }
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

  String config(String pName, String pDefault)
  {
    String result = config.get(pName);
    return result != null ? result : pDefault;
  }
  
}
