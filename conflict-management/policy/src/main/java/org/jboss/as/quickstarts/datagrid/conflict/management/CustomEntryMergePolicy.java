package org.jboss.as.quickstarts.datagrid.conflict.management;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.infinispan.conflict.EntryMergePolicy;
import org.infinispan.container.entries.CacheEntry;
import org.infinispan.container.entries.NullCacheEntry;

public class CustomEntryMergePolicy implements EntryMergePolicy<String, String> {
   private static final Logger log = Logger.getLogger(CustomEntryMergePolicy.class.getName());

   @Override
   public CacheEntry<String, String> merge(CacheEntry<String, String> preferredEntry, List<CacheEntry<String, String>> otherEntries) {
      log.info("merge preferred Entry " + preferredEntry + " number of others is " + otherEntries.size());
      
      CacheEntry<String, String> solved = (preferredEntry instanceof NullCacheEntry) ? null : preferredEntry;
      
      if(solved != null) {
         log.info("Preferred entry : key="+solved.getKey()+" value="+solved.getValue()+
               " created:"+new Date(solved.getCreated())+" ("+solved.getCreated()+")"+
               " lastUsed:"+new Date(solved.getLastUsed())+" ("+solved.getLastUsed()+")");
      }
      
      for (CacheEntry<String, String> cacheEntry : otherEntries) {
         if(cacheEntry instanceof NullCacheEntry) {
            log.info("Ignore NullCacheEntry");
         }else{
            log.info("Entry : key="+cacheEntry.getKey()+" value="+cacheEntry.getValue()+
                  " created:"+new Date(cacheEntry.getCreated())+" ("+cacheEntry.getCreated()+")"+
                  " lastUsed:"+new Date(cacheEntry.getLastUsed())+" ("+cacheEntry.getLastUsed()+")");
            if(solved == null || solved.getValue().compareTo(cacheEntry.getValue()) > -1) {
               // swap if alphabethic before
               log.info("Swap current " + cacheEntry.getValue() + " was " + (solved == null ? "NULL" : solved.getValue()));
               solved = cacheEntry;
            }else{
               log.info("Keep current " + solved.getValue() + " other is " + cacheEntry.getValue());
            }
         }
      }
      return solved;
   }

}
