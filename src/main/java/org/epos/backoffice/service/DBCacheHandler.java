package org.epos.backoffice.service;

import org.epos.handler.dbapi.util.LoadCache;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class DBCacheHandler {

    @Async
    public void loadCache() {
        LoadCache.loadCache();
    }
}
