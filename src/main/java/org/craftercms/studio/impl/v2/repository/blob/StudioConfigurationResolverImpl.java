/*
 * Copyright (C) 2007-2021 Crafter Software Corporation. All Rights Reserved.
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
import org.craftercms.commons.config.ConfigurationResolverImpl;
import org.craftercms.commons.config.EncryptionAwareConfigurationReader;
import org.craftercms.studio.api.v1.log.Logger;
import org.craftercms.studio.api.v1.log.LoggerFactory;
import org.craftercms.studio.api.v2.service.config.ConfigurationService;

import java.util.concurrent.ExecutionException;

/**
 * @author joseross
 * @since
 */
public class StudioConfigurationResolverImpl extends ConfigurationResolverImpl {

    private static final Logger logger = LoggerFactory.getLogger(StudioConfigurationResolverImpl.class);

    protected Cache<String, Object> configurationCache;

    protected ConfigurationService configurationService;

    public StudioConfigurationResolverImpl(String environment, String basePath, String envPath,
                                           EncryptionAwareConfigurationReader configurationReader) {
        super(environment, basePath, envPath, configurationReader);
    }

    public void setConfigurationCache(Cache<String, Object> configurationCache) {
        this.configurationCache = configurationCache;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public HierarchicalConfiguration<?> getXmlConfiguration(String module, String path, ConfigurationProvider provider)
            throws ConfigurationException {
        var studioProvider = (StudioBlobStoreResolverImpl.ConfigurationProviderImpl) provider;

        try {
            var cacheKey = configurationService.getCacheKey(studioProvider.getSite(), module, path, environment);
            return (HierarchicalConfiguration<?>) configurationCache.get(cacheKey, () -> {
                logger.debug("CACHE MISS: {0}", cacheKey);
                return super.getXmlConfiguration(module, path, provider);
            });
        } catch (ExecutionException e) {
            throw new ConfigurationException("Error reading configuration", e);
        }
    }
}
