package uk.ac.manchester.cs.owl.explanation;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Matthew Horridge
 * Stanford University
 * Bio-Medical Informatics Research Group
 * Date: 20/03/2012
 */
public class JustificationCacheManager {

    private Map<JustificationType, JustificationCache> caches = new HashMap<>();

    public JustificationCacheManager() {
        for(JustificationType type : JustificationType.values()) {
            caches.put(type, new JustificationCache());
        }
    }

    public JustificationCache getJustificationCache(JustificationType justificationType) {
        return caches.get(justificationType);
    }

    public void clear() {
        for(JustificationCache cache : caches.values()) {
            cache.clear();
        }
    }
}
