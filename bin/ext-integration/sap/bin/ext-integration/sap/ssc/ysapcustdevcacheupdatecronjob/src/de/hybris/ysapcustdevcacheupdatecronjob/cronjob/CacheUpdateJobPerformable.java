package de.hybris.ysapcustdevcacheupdatecronjob.cronjob;

import com.sap.custdev.projects.fbs.slc.cml.util.CacheUtil;
import com.sap.custdev.projects.fbs.slc.kbo.util.KBOCache;
import com.sap.sce.kbrt.imp.kb_descriptor_seq_imp;
import com.sap.sce.kbrt.imp.kb_finder;
import com.sap.sce.kbrt.kb_descriptor;
import com.sap.sce.kbrt.kb_descriptor_seq;
import com.sap.util.cache.CacheFacade;
import com.sap.util.cache.CacheRegion;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.ysapcustdevcacheupdatecronjob.model.CacheUpdateJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This is a CronJob which synchronizes the kb descriptors presents in the cache
 * with the ones present in the database.
 */
public class CacheUpdateJobPerformable extends AbstractJobPerformable<CacheUpdateJobModel>{
    private static final Logger LOG = LoggerFactory.getLogger(CacheUpdateJobPerformable.class.getName());

    static {
        //This is added to avoid classnotfound error.
        System.getProperties().setProperty("vmc.runtimeInformationImpl", "com.sap.custdev.projects.fbs.slc.cfg.RuntimeInformationLocalImpl");
    }

    //TODO replace the contents of the following method to KBOCache.getDescriptorCache
    //when that method has public visibility
    public static kb_descriptor_seq getDescriptorCache() {
        CacheRegion region = CacheUtil.getCacheRegion("/AP/CFG/Knowledgebases");

        if (region != null) {
            CacheFacade facade = region.getCacheFacade();
            Object kbDescCacheObject = facade.get("KB_DESC");

            if (kbDescCacheObject != null) {
                return (kb_descriptor_seq_imp) kbDescCacheObject;
            }
        }

        return null;
    }

    @Override
    public PerformResult perform(CacheUpdateJobModel cacheUpdateJobModel) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Starting update kb descriptor cache job.");
        }
        updateCache();
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    public void updateCache() {
        long startTime = System.currentTimeMillis();
        kb_descriptor_seq kb_desc_from_db = null;
  		try
  		{
  			kb_desc_from_db = kb_finder.kb_get_loadable_kbs();
  		}
  		catch (final NullPointerException ex)
  		{
  			LOG.error("NullPointerException was caught in the catch block as Knowledge Bases Configuration is not yet Initialized.");
  		}

          if (LOG.isDebugEnabled()) {
              LOG.debug("KB descriptors loaded from the database: " + kb_desc_from_db + " in " +
                      (System.currentTimeMillis() - startTime) + " milliseconds.");
          }

          Map<Integer, kb_descriptor> kbDescFromDbMap = new HashMap<>();
    		try
    		{
    			kbDescFromDbMap = converSequenceToMap(kb_desc_from_db);
    		}
  		catch (final NullPointerException ex)
  		{
  			LOG.error("NullPointerException was caught in the catch block as Knowledge Bases Configuration is not yet Initialized.");
  		}
          
  		final kb_descriptor_seq kb_desc_from_cache = getDescriptorCache();
          if (kb_desc_from_cache != null) {
              if (LOG.isDebugEnabled()) {
                  LOG.debug("KB descriptors loaded from the cache: " + kb_desc_from_cache);
              }
              
              HashMap<Integer, kb_descriptor> kbDescFromCacheMap = converSequenceToMap(kb_desc_from_cache);

            List<kb_descriptor> toBeAdded = new ArrayList<kb_descriptor>();
            List<kb_descriptor> toBeRemoved = new ArrayList<kb_descriptor>();
            for (Map.Entry<Integer, kb_descriptor> kbDescDbEntry : kbDescFromDbMap.entrySet()) {
                kb_descriptor kbDescCache = kbDescFromCacheMap.get(kbDescDbEntry.getKey());
                kb_descriptor kbDescDb = kbDescDbEntry.getValue();

                if (kbDescCache == null) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Did not find kb descriptor : " + kbDescDb + " in the cache.");
                    }
                    toBeAdded.add(kbDescDb);
                } else {
                    if (!kbDescCache.equals(kbDescDb) || !kbDescCache.kb_build().equals(kbDescDb.kb_build())) {
                        if (LOG.isInfoEnabled()) {
                            LOG.info("Difference in KB found, kb descriptor(Database): [" + kbDescDb + "], kb descriptor(Cache): [" + kbDescCache + "]");
                        }

                        toBeAdded.add(kbDescDb);
                        toBeRemoved.add(kbDescCache);
                    }
                }
            }

            Set<Integer> kbIdsFromCache = kbDescFromCacheMap.keySet();
            kbIdsFromCache.removeAll(kbDescFromDbMap.keySet());

            if (kbIdsFromCache != null && kbIdsFromCache.size() > 0) {
                for (Integer kbId : kbIdsFromCache) {
                    toBeRemoved.add(kbDescFromCacheMap.get(kbId));
                }
            }

            if (toBeAdded.size() > 0 || toBeRemoved.size() > 0) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("KB descriptors added into the Cache: " + toBeAdded);
                    LOG.info("KB descriptors removed from the Cache: " + toBeRemoved);
                }
                kb_descriptor_seq cache_seq = getDescriptorCache();
                synchronized (cache_seq) {
                    for (kb_descriptor kbDesc : toBeAdded) {
                        KBOCache.push_kb_desc(kbDesc, true);
                    }

                    for (kb_descriptor kbDesc : toBeRemoved) {
                        KBOCache.remove_kb(kbDesc.kb_cache_key(), true);
                    }
                }
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("KB descriptors cache after synchronization:" + getDescriptorCache());
            }
            long endTime = System.currentTimeMillis();
            if (LOG.isInfoEnabled()) {
                LOG.info("Update kb descriptor cache job took " + (endTime - startTime) + " milliseconds.");
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("KB descriptors cache after synchronization:" + getDescriptorCache());
            }
        } else {
            if (LOG.isInfoEnabled()) {
                LOG.info("KB Desc cache is not initialized.");
            }
        }
    }


    private HashMap<Integer, kb_descriptor> converSequenceToMap(kb_descriptor_seq kb_desc_seq) {
        HashMap<Integer, kb_descriptor> kbDescMap = new HashMap<Integer, kb_descriptor>();
        Enumeration elements = kb_desc_seq.elements();
        while (elements.hasMoreElements()) {
            kb_descriptor  kb_desc = (kb_descriptor)elements.nextElement();
            kbDescMap.put(kb_desc.kb_id(), kb_desc);
        }
        return kbDescMap;
    }
}
