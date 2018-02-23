package org.jboss.as.quickstarts.datagrid.conflict.management;

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

import java.io.Console;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.lookup.GenericTransactionManagerLookup;

/**
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
public class Main {
   private static final String msgEnterKey = "Enter key: ";

   private final Console con;
   private final DefaultCacheManager cacheManager;
   private Cache<String, String> cache;

   public Main(Console con) {
      this.con = con;
      

      final GlobalConfiguration glob = new GlobalConfigurationBuilder().clusteredDefault() // Builds a default clustered
            .transport().addProperty("configurationFile", "jgroups-app.xml")
            .clusterName("ConflictManagementApp")
            .globalJmxStatistics().enable() // This method enables the jmx statistics of the global configuration
            .build(); // Builds the GlobalConfiguration object
      
      final Configuration loc = new ConfigurationBuilder().jmxStatistics().enable() // Enable JMX statistics
               .clustering().cacheMode(CacheMode.DIST_SYNC).hash().numOwners(2).stateTransfer().chunkSize(512).fetchInMemoryState(true)
               .transaction().transactionManagerLookup(new GenericTransactionManagerLookup())
               .build();
      
      cacheManager = new DefaultCacheManager(glob, loc, true);

      cache = cacheManager.getCache();
   }

   public void add() {
      String key = con.readLine(msgEnterKey);
      String value = con.readLine("Enter value: ");

      String oldValue = cache.put(key, value);

      if (oldValue != null) {
         con.printf("   Replace old value : %s\n", oldValue);
      }
   }

   public void generateEntries() {
      int start = 0;
      int num = 0;
      
      try {
         start = Integer.parseInt(con.readLine("Start number : "));
         num = Integer.parseInt(con.readLine("Number of enries : "));
      } catch (NumberFormatException e) {
         con.printf("Is not a number!\n");
      }
      
      for (int i = start ; i < start+num; i++) {
         cache.put(String.valueOf(i), "Value#"+i);
      }
   }

   public void checkGenerateEntries() {
      int start = 0;
      int num = 0;
      
      try {
         start = Integer.parseInt(con.readLine("Start number : "));
         num = Integer.parseInt(con.readLine("Number of enries : "));
      } catch (NumberFormatException e) {
         con.printf("Is not a number!\n");
      }
      
      int missing=0, wrong=0;
      for (int i = start ; i < start+num; i++) {
         String key = String.valueOf(i);
         String value = cache.get(key);
         if(value==null) {
            con.printf("Entry for key=%s not found!\n", key);
            missing++;
         }else if(!("Value#"+i).equals(value)) {
            con.printf("Value for key=%s wrong : %s!\n", key, value);
            wrong++;
         }
      }
      if(missing != 0) con.printf(" %d entries not found\n", missing);
      if(missing != 0) con.printf(" %d entries with unexpected value\n", wrong);
   }

   public void get() {
      String key = con.readLine(msgEnterKey);

      if (cache.containsKey(key)) {
         con.printf("  value : %s\n", cache.get(key));
      } else {
         con.printf("   No entry for key found!\n");
      }
   }

   public void remove() {
      String key = con.readLine(msgEnterKey);

      if (cache.containsKey(key)) {
         cache.remove(key);
      } else {
         con.printf("   No entry for key found!\n");
      }
   }

   public void list() {
      for (String key : cache.keySet()) {
         con.printf("  Entry  %s : %s\n", key, cache.get(key));
      }
   }

   public void size() {
      con.printf("  Cache size is %d\n", cache.size());
   }

   public void stop() {
      cacheManager.stop();
   }

   private void inputLoop() {
      while (true) {
         String action = con.readLine(">");
         if ("add".equals(action)) {
            add();
         } else if ("rm".equals(action)) {
            remove();
         } else if ("get".equals(action)) {
            get();
         } else if ("list".equals(action)) {
            list();
         } else if ("size".equals(action)) {
            size();
         } else if ("generate".equals(action)) {
            generateEntries();
         } else if ("check".equals(action)) {
            checkGenerateEntries();
         } else if ("q".equals(action)) {
            break;
         }
      }
   }

   private void printConsoleHelp() {
      con.printf("Choose:\n" + "============= \n" + "add  -  add an entry\n" + "rm   -  remove an entry\n" + "get  -  print a value for key\n"
            + "list -  list all entries which are store local\n"
            + "size -  local size of cache\n"
            + "q    -  quit\n");
   }
   
   public static void main(String[] args) {
      Console con = System.console();

      int argc = 0;
      while (argc < args.length) {
        if (args[argc].equals("-owner")) {
            argc++;
            argc++;
         } else {
            con.printf("option '%s' unknown\n", args[argc]);
            System.exit(1);
         }
      }

      Main main = new Main(con);
      
      main.printConsoleHelp();

      main.inputLoop();

      main.stop();
   }
}