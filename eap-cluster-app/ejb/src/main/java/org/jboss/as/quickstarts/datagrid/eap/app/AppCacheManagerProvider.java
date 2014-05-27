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
package org.jboss.as.quickstarts.datagrid.eap.app;

import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

/**
 * Creates a DefaultCacheManager which is configured programmatically. Infinispan's libraries need to be bundled with the
 * application.
 * 
 * @author Wolf-Dieter Fink
 */
@ApplicationScoped
public class AppCacheManagerProvider {
    private static final Logger log = Logger.getLogger(AppCacheManagerProvider.class.getName());
    private DefaultCacheManager manager;

    public DefaultCacheManager getCacheManager() {
        if (manager == null) {
            log.info("\n\n DefaultCacheManager does not exist - constructing a new one\n\n");

//            GlobalConfiguration glob = new GlobalConfigurationBuilder().clusteredDefault() // Builds a default clustered
            GlobalConfiguration glob = new GlobalConfigurationBuilder().nonClusteredDefault() // Builds a default non clustered configuration
                    //.transport().addProperty("configurationFile", "jgroups-udp.xml") // provide a specific JGroups configuration
                    .globalJmxStatistics().allowDuplicateDomains(true).enable() // This method enables the jmx statistics of
                    // the global configuration and allows for duplicate JMX domains
                    .build(); // Builds the GlobalConfiguration object
            Configuration loc = new ConfigurationBuilder().jmxStatistics().enable() // Enable JMX statistics
//                    .clustering().cacheMode(CacheMode.DIST_SYNC) // Set Cache mode to DISTRIBUTED with SYNCHRONOUS replication
//                    .hash().numOwners(2) // Keeps two copies of each key/value pair
                    // the lifespan parameter) and are removed from the cache (cluster-wide).
                    .build();
            manager = new DefaultCacheManager(glob, loc, true);

            Configuration cache1 = new ConfigurationBuilder().build();
            manager.defineConfiguration("cache1", cache1);
        }
        return manager;
    }

    @PreDestroy
    public void cleanUp() {
        manager.stop();
        manager = null;
    }

}
