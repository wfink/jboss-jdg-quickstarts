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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.InitialContext;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.jboss.logging.Logger;

/**
 * <p>The main bean called by the standalone client.</p>
 * <p>The sub applications, deployed in different servers are called direct or via indirect naming to hide the lookup name and use
 * a configured name via comp/env environment.</p>
 * 
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
@Stateless
public class CacheAccessBean implements CacheAccess {
    private static final Logger LOGGER = Logger.getLogger(CacheAccessBean.class);
    @Resource
    SessionContext context;

    @Inject
    DefaultCacheManager cacheManager;

  /**
   * The context to invoke foreign EJB's as the SessionContext can not be used for that.
   */
    private InitialContext iCtx;

//    @EJB(lookup = "ejb:jboss-ejb-multi-server-app-one/ejb//AppOneBean!org.jboss.as.quickstarts.ejb.multi.server.app.AppOne")
//    AppOne appOneProxy;
//    @EJB(lookup = "ejb:jboss-ejb-multi-server-app-two/ejb//AppTwoBean!org.jboss.as.quickstarts.ejb.multi.server.app.AppTwo")
//    AppTwo appTwoProxy;

  /**
   * Initialize and store the context for the EJB invocations.
   */
    @PostConstruct
    public void init() {
    }

    @Override
    public void addToCache(String key, String value) {
        LOGGER.info("addTo progCache ("+key+","+value+")");
        Cache<String,String> cache = cacheManager.getCache("progCache");
        cache.put(key, value);
    }

    @Override
    public String getFromCache(String key) {
        LOGGER.info("getFrom progCache("+key+")");
        Cache<String,String> cache = cacheManager.getCache("progCache");
        final String value = cache.get(key);
        LOGGER.info("value=" + value);
        
        final String nodeName = System.getProperty("jboss.node.name");
        return "Read progCache for key=" + key + " at server '" + nodeName + "' and get " + (value == null? "no value" : "value="+value);
    }
}
