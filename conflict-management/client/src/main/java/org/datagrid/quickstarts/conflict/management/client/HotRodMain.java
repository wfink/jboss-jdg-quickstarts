package org.datagrid.quickstarts.conflict.management.client;

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

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

/**
 * Simple HotRod client which uses the hotrod-client.properties file to set the remote client configuration.
 * The client is used to play with the partition handling configured on server side to see the effects.
 * 
 * @author Wolf-Dieter Fink
 */
public class HotRodMain {
   private static final String msgEnterKey = "Enter key: ";

   private final Console con;

   private RemoteCacheManager remoteCacheManager;
   private RemoteCache<String, String> cache;

   private HotRodMain(Console con, String host, int port) {
      this.con = con;

      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host(host).port(port);

      remoteCacheManager = new RemoteCacheManager(builder.build(),true);
      cache = remoteCacheManager.getCache("PartitionHandlingCache");
   }

   public void put() {
      String key = con.readLine(msgEnterKey);
      String value = con.readLine("Enter value: ");

      con.printf("     Insert key=%s value=%s", key,value);
      String oldValue = cache.put(key, value);
      con.printf("  Done\n");

      if (oldValue != null) {
         con.printf("   Replace old value : %s\n", oldValue);
      }
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

   private void inputLoop() {
      while (true) {
         try {
            String action = con.readLine(">");
            if ("put".equals(action)) {
               put();
            } else if ("rm".equals(action)) {
               remove();
            } else if ("get".equals(action)) {
               get();
            } else if ("list".equals(action)) {
               list();
            } else if ("size".equals(action)) {
               size();
            } else if ("q".equals(action)) {
               break;
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   private void printConsoleHelp() {
      con.printf("Choose:\n" + "============= \n" + "put   -  put an entry\n" + "rm    -  remove an entry\n" + "get   -  print a value for key\n"
            + "list  -  list all entries which are store local\n"
            + "size  -  local size of cache\n"
            + "merge -  invoke conflict manager\n"
            + "q     -  quit\n");
   }

   private void stop() {
      remoteCacheManager.stop();
   }

   public static void main(String[] args) {
      final Console con = System.console();
      String host = "127.0.0.1";
      int port = 11222;
      
      int argc = 0;
      while (argc < args.length) {
         if (args[argc].equals("-host")) {
            argc++;
            host = args[argc];
            argc++;
         }else if (args[argc].equals("-port")) {
               argc++;
               port = Integer.valueOf(args[argc]);
               argc++;
          } else {
             con.printf("option '%s' unknown\n", args[argc]);
             System.exit(1);
          }
       }

      
      HotRodMain client = new HotRodMain(con,host,port);
      
      client.printConsoleHelp();

      client.inputLoop();

      client.stop();
   }

}
