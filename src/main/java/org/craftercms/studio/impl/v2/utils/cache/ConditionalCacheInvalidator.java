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
package org.craftercms.studio.impl.v2.utils.cache;

import com.google.common.cache.Cache;
import org.craftercms.studio.api.v2.utils.cache.CacheInvalidator;

/**
 * @author joseross
 * @since
 */
public class ConditionalCacheInvalidator implements CacheInvalidator {

    protected String pattern;

    protected CacheInvalidator actualCacheInvalidator;

    public ConditionalCacheInvalidator(String pattern, CacheInvalidator actualCacheInvalidator) {
        this.pattern = pattern;
        this.actualCacheInvalidator = actualCacheInvalidator;
    }

    @Override
    public void invalidate(Cache<String, Object> cache, String key) {
        if (key.matches(pattern)) {
            actualCacheInvalidator.invalidate(cache, key);
        }
    }

}
