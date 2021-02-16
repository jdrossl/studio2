/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.studio.impl.v2.repository.blob;

import com.google.common.cache.Cache;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.craftercms.commons.config.ConfigurationException;
import org.craftercms.commons.config.ConfigurationProvider;
import org.craftercms.commons.file.blob.BlobStore;
import org.craftercms.commons.file.blob.impl.BlobStoreResolverImpl;
import org.craftercms.studio.api.v1.exception.ServiceLayerException;
import org.craftercms.studio.api.v1.repository.ContentRepository;
import org.craftercms.studio.api.v2.repository.blob.StudioBlobStoreResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static org.craftercms.commons.file.blob.BlobStore.CONFIG_KEY_PATTERN;

/**
 * Implementation of {@link StudioBlobStoreResolver}
 *
 * @author joseross
 * @since 3.1.6
 */
@SuppressWarnings("rawtypes")
public class StudioBlobStoreResolverImpl extends BlobStoreResolverImpl implements StudioBlobStoreResolver {

    public static final String CACHE_KEY_STORE = "blob-store-";

    protected ContentRepository contentRepository;

    protected Cache<String, Object> configurationCache;

    public void setContentRepository(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public void setConfigurationCache(Cache<String, Object> configurationCache) {
        this.configurationCache = configurationCache;
    }

    @Override
    public BlobStore getByPaths(String site, String... paths)
            throws ServiceLayerException, ConfigurationException {
        logger.debug("Looking blob store for paths {} for site {}", Arrays.toString(paths), site);
        HierarchicalConfiguration config = getConfiguration(new ConfigurationProviderImpl(site));
        if (config != null) {
            String storeId = findStoreId(config, store -> paths[0].matches(store.getString(CONFIG_KEY_PATTERN)));
            BlobStore blobStore;
            logger.debug("Checking cache for blob store {}", storeId);
            var cacheKey = CACHE_KEY_STORE + storeId + site;
            try {
                blobStore = (BlobStore) configurationCache.get(cacheKey, () -> {
                    logger.debug("Blob store {} not found in cache", storeId);
                    return getById(config, storeId);
                });
            } catch (ExecutionException e) {
                throw new ConfigurationException("Error loading blob store", e);
            }

            // We have to compare each one to know if the exception should be thrown
            if (blobStore != null && !Stream.of(paths).allMatch(blobStore::isCompatible)) {
                throw new ServiceLayerException("Unsupported operation for paths " + Arrays.toString(paths));
            }
            return blobStore;
        }
        return null;
    }

    /**
     * Internal class to provide access to configuration files
     */
    public class ConfigurationProviderImpl implements ConfigurationProvider {

        private String site;

        public ConfigurationProviderImpl(String site) {
            this.site = site;
        }

        public String getSite() {
            return site;
        }

        @Override
        public boolean configExists(String path) {
            return StudioBlobStoreResolverImpl.this.contentRepository.contentExists(site, path);
        }

        @Override
        public InputStream getConfig(String path) throws IOException {
            try {
                return StudioBlobStoreResolverImpl.this.contentRepository.getContent(site, path);
            } catch (Exception e) {
                throw new IOException("Error reading file", e);
            }
        }

    }

}
