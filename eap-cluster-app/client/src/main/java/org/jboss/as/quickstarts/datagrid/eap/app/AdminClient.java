/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.datagrid.eap.app;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.jboss.ejb.client.ContextSelector;
import org.jboss.ejb.client.EJBClientConfiguration;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;

/**
 * <p>
 * A simple standalone application which uses the JBoss API to invoke the MainApp demonstration
 * Bean.
 * </p>
 * <p>
 * With the boolean property <i>UseScopedContext</i> the basic example or the example with the
 * scoped-environment will be called.
 * </p>
 * 
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
public class AdminClient {

   /**
    * @param args
    *           no args needed
    * @throws Exception
    */
   public static void main(String[] args) throws Exception {
      // suppress output of client messages
      Logger.getLogger("org.jboss").setLevel(Level.OFF);
      Logger.getLogger("org.xnio").setLevel(Level.OFF);

      Properties p = new Properties();
      p.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
      p.put("remote.connections", "admin,appOne");
      p.put("remote.connection.admin.port", "4447");
      p.put("remote.connection.admin.host", "localhost");
      //        p.put("remote.connection.admin.username", "quickuser");
      //        p.put("remote.connection.admin.password", "quick-123");
      p.put("remote.connection.appOne.port", "4547");
      p.put("remote.connection.appOne.host", "localhost");
      //        p.put("remote.connection.appOne.username", "quickuser");
      //        p.put("remote.connection.appOne.password", "quick-123");

      EJBClientConfiguration cc = new PropertiesBasedEJBClientConfiguration(p);
      ContextSelector<EJBClientContext> selector = new ConfigBasedEJBClientContextSelector(cc);
      EJBClientContext.setSelector(selector);

      Properties props = new Properties();
      props.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
      InitialContext context = new InitialContext(props);

      final String adminLookup = "ejb:jboss-eap-application-adminApp/ejb//CacheAdminBean!" + CacheAdmin.class.getName();
      final CacheAdmin admin = (CacheAdmin) context.lookup(adminLookup);
      admin.addToApp1Cache("App1One", "The App1 One entry");
      // check that the Admin has the correct key entry local
      admin.verifyApp1Cache("App1One", "The App1 One entry");

      final String appOneLookup = "ejb:jboss-eap-application-AppOne/ejb//CacheAccessBean!"
            + AppOneCacheAccess.class.getName();
      final AppOneCacheAccess appOne = (AppOneCacheAccess) context.lookup(appOneLookup);
      // check that the App1 cache has the correct replicated key entry
      appOne.verifyApp1Cache("App1One", "The App1 One entry");

      admin.addToApp2Cache("One", "The App2 One entry");
      admin.verifyApp2Cache("One", "The App2 One entry");

      // check that the cache is transactional
      try {
         admin.removeFromApp2Cache("SouldNeverExist");
         admin.addToApp2Cache("SouldNeverExist", "This value should not available as the transaction fail", true);
         if (admin.containsApp2Key("SouldNeverExist")) {
            System.out.println("   ERROR the cache App2 is not transactional");
         } else {
            System.out.println("   INFO  the cache App2 is transactional");
         }
      } catch (RuntimeException e) {
         System.out.println("   ERROR add throws RuntimeException!");
         e.printStackTrace();
      }
   }

}
