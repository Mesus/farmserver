package com.cbd5;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import redis.clients.jedis.Jedis;

@ApplicationPath("/api")
public class Application extends ResourceConfig{
	public static PostgresCommon pc = null;
	public static HashMap tab = new HashMap();
	public static Jedis jedis = null;
    public Application() {
        packages("com.cbd5.resource");
        
        register(JacksonJsonProvider.class);
        register(LoggingFilter.class);
        
        pc = new PostgresCommon();
        pc.conn();
        
//		JedisUtil ju = JedisUtil.getInstance();
//		String jip = pc.prop.getProperty("jedis_ip");
//		int jport = Integer.parseInt(pc.prop.getProperty("jedis_port") );
//		jedis = ju.getJedis(jip, jport);
        
        PutTableMap();
        
    }
    
    private void PutTableMap(){
    	Properties prop = new Properties();
		try {
			InputStream in = getClass().getResourceAsStream("TableMap.properties");
			prop.load(new InputStreamReader(in, "UTF-8")); // /加载属性列表
			Iterator<String> it = prop.stringPropertyNames().iterator();
			while (it.hasNext()) {
				String key = it.next();
				tab.put(key, prop.getProperty(key));
				System.out.println(key + ":" + prop.getProperty(key));
			}
			in.close();

		} catch (Exception e) {
			System.out.println(e);
		}
    }
}
