package org.jboss.as.quickstarts.datagrid.remotetasks.tasks;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.infinispan.Cache;
import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.ProtobufUtil;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.tasks.ServerTask;
import org.infinispan.tasks.TaskContext;
import org.infinispan.tasks.TaskExecutionMode;
import org.jboss.as.quickstarts.datagrid.remotetasks.domain.ItemKey;
import org.jboss.as.quickstarts.datagrid.remotetasks.domain.ItemKeyMarshaller;
import org.jboss.logging.Logger;

public class SimpleObjectsTask implements ServerTask<String>, Serializable {
   public static final String TASK_NAME = "SimpleTask";
   private final static Logger log = Logger.getLogger(SimpleObjectsTask.class.getName());
   /**
    *
    */
   private static final long serialVersionUID = 2207872563747428886L;

   private static final String CACHE_NAME = "default";

   private TaskContext ctx;

   private SerializationContext serializationContext;

   @Override
   public void setTaskContext(TaskContext ctx) {
      this.ctx = ctx;

      this.serializationContext = ProtobufUtil.newSerializationContext();

   }

   @Override
   public  TaskExecutionMode	getExecutionMode() {
      return TaskExecutionMode.ALL_NODES;
   }

   @Override
   public String call() throws Exception {

      try {
         serializationContext.registerProtoFiles(FileDescriptorSource.fromResources(SimpleObjectsTask.class.getClassLoader(), "itemkey.proto"));
      } catch (IOException e) {
         e.printStackTrace();
      }
      serializationContext.registerMarshaller(new ItemKeyMarshaller());

      String name = (String) ctx.getParameters().get().get("name");
      log.infof("Company Name '%s' received in execute task ", name);
      log.infof("HEX %s", String.format("%040x", new BigInteger(1, name.getBytes("UTF-8"))));
      log.infof("manager %s", ctx.getCacheManager());
      log.infof("cache %s", ctx.getCache());
      if(ctx.getCacheManager() != null) {
         log.infof("cache %s", ctx.getCacheManager().getCache(CACHE_NAME));
      }
      
      log.info("returning from Task Execution");
      return "Task Completed";
   }

   @Override
   public String getName() {
      return TASK_NAME;
   }

}