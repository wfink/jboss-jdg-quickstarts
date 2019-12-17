/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
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
package org.jboss.as.quickstarts.datagrid.remotetasks;

import java.io.Console;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.infinispan.client.hotrod.DataFormat;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.jboss.as.quickstarts.datagrid.remotetasks.domain.ItemKey;
import org.jboss.as.quickstarts.datagrid.remotetasks.domain.ItemKeyMarshaller;
import org.jboss.as.quickstarts.datagrid.remotetasks.tasks.SimpleObjectsTask;

import static org.infinispan.commons.dataconversion.MediaType.APPLICATION_JBOSS_MARSHALLING;
/**
 * Class managing the user input. The user's input is processed and the corresponding cache operation is done.
 *
 * @author Wolf Dieter Fink
 */
public class CompanyManager {

    private static final String JDG_HOST = "jdg.host";
    private static final String HOTROD_PORT = "jdg.hotrod.port";
    private static final String PROPERTIES_FILE = "jdg.properties";

    public static final String CACHE_NAME = "default";
    private static final String msgEnterId = "Please enter id: ";
    private static final String msgEnterCompanyName = "Please enter company: ";

    private static final String initialPrompt = "Choose action:\n" + "============= \n"
            + "ac  -  add a company\n"
            + "rc  -  remove a company\n"
            + "p   -  print all companies\n"
            + "h   - shows the all available options\n"
            + "q   -  quit\n";
   private static final String PROTOBUF_DEFINITION_RESOURCE = "/itemkey.proto";


    private Console con;
    private RemoteCacheManager cacheManager;
    private RemoteCache<ItemKey, String> cache;

    public CompanyManager(Console con) throws IOException {
        this.con = con;
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer()
                .host(jdgProperty(JDG_HOST))
                .port(Integer.parseInt(jdgProperty(HOTROD_PORT)))
                //.addJavaSerialWhiteList("java.util.*")
                .protocolVersion("2.6")
                ;
        builder.marshaller(new ProtoStreamMarshaller());
        cacheManager = new RemoteCacheManager(builder.build());
        cache = cacheManager.getCache(CACHE_NAME);

        registerSchemasAndMarshallers();
}
    
    /**
     * Register the Protobuf schemas and marshallers with the client and then register the schemas with the server too.
     */
    private void registerSchemasAndMarshallers() throws IOException {
       // Register entity marshallers on the client side ProtoStreamMarshaller instance associated with the remote cache manager.
       SerializationContext ctx = ProtoStreamMarshaller.getSerializationContext(cacheManager);
       ctx.registerProtoFiles(FileDescriptorSource.fromResources(PROTOBUF_DEFINITION_RESOURCE));
       ctx.registerMarshaller(new ItemKeyMarshaller());

       // register the schemas with the server too
       RemoteCache<String, String> metadataCache = cacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
       metadataCache.put(PROTOBUF_DEFINITION_RESOURCE, readResource(PROTOBUF_DEFINITION_RESOURCE));
       String errors = metadataCache.get(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX);
       if (errors != null) {
          throw new IllegalStateException("Some Protobuf schema files contain errors:\n" + errors);
       }
    }


    /**
     * Adds a book into the cache based on the user input.
     */
    public void addCompany() {
        String name = con.readLine(msgEnterCompanyName);
        int id = Integer.parseInt(con.readLine(msgEnterId));

        ItemKey item = new ItemKey(id, name);

        //putting the created object into the cache
        cache.put(item, item.getCompany() + "   " + new Date());
    }

    /**
     * Removes the book from the cache based on the user input. The book object removal is performed via {{BooksRemovingTask}}
     * remote task execution.
     */
    public void removeCompany() {
        String name = con.readLine(msgEnterCompanyName);

        Map<String, String> parameters = new HashMap<>();
        if (name != null && !name.isEmpty())
            parameters.put("name", name);

        //Removing the object from the cache by executing the remote task {{BooksRemovingTask}}.
        System.out.println("before");
      Object numberOfDeletedObjects = cache.execute(SimpleObjectsTask.TASK_NAME, parameters);
//        Object numberOfDeletedObjects = cacheManager.getCache(CACHE_NAME).withDataFormat(DataFormat.builder().valueType(APPLICATION_JBOSS_MARSHALLING).build()).execute(SimpleObjectsTask.TASK_NAME, parameters);
        System.out.println("after delete " + numberOfDeletedObjects);


//        if (numberOfDeletedObjects.size() == 0) {
//            System.out.println("No objects were found with specified parameters.");
//        } else {
//            System.out.println("Success! " + numberOfDeletedObjects + " object/s have been deleted.");
//        }
    }

    private void printList() {
        System.out.println("----- Available List -----");

        for (ItemKey ik : cache.keySet()) {
            System.out.printf("%s  -> %s\n",ik, cache.get(ik));
        }
    }

    /**
     * Stops the remote cache client.
     */
    public void stop() {
        cacheManager.stop();
    }

    public static void main(String[] args) throws IOException {
        Console con = System.console();
        CompanyManager manager = new CompanyManager(con);
        con.printf(initialPrompt);

        while (true) {
            String action = con.readLine(">");
            if ("ac".equals(action)) {
                manager.addCompany();
            } else if ("rc".equals(action)) {
                manager.removeCompany();
            } else if ("p".equals(action)) {
                manager.printList();
            } else if ("h".equals(action)) {
                con.printf(initialPrompt);
            } else if ("q".equals(action)) {
                manager.stop();
                break;
            }
        }
    }

    /**
     * Loads the property file with JDG connection properties and returns the one requested.
     * @param name      the name of the property which value should be returned.
     * @return          the property value.
     */
    private static String jdgProperty(String name) {
        Properties props = new Properties();
        try {
            props.load(CompanyManager.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return props.getProperty(name);
    }

    private String readResource(String resourcePath) throws IOException {
       try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
          Reader reader = new InputStreamReader(is, "UTF-8");
          StringWriter writer = new StringWriter();
          char[] buf = new char[1024];
          int len;
          while ((len = reader.read(buf)) != -1) {
             writer.write(buf, 0, len);
          }
          return writer.toString();
       }
    }
}
