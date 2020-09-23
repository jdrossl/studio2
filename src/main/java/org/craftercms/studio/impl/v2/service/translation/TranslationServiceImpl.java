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
package org.craftercms.studio.impl.v2.service.translation;

import org.craftercms.studio.api.v2.dal.translation.LocalizedItem;
import org.craftercms.studio.api.v2.service.translation.TranslationService;
import org.craftercms.studio.api.v2.service.translation.internal.TranslationServiceInternal;

import java.util.List;

/**
 * @author joseross
 * @since
 */
public class TranslationServiceImpl implements TranslationService {

    protected TranslationServiceInternal translationServiceInternal;

    public TranslationServiceImpl(TranslationServiceInternal translationServiceInternal) {
        this.translationServiceInternal = translationServiceInternal;
    }

    @Override
    public int countItems(String siteId, String path, String locale, boolean markedForTranslation,
                          boolean notTranslated, boolean translationOutOfDate) {
        return translationServiceInternal.countItems(siteId, path, locale, markedForTranslation, notTranslated,
                translationOutOfDate);
    }

    @Override
    public List<LocalizedItem> getItems(String siteId, String path, String locale, boolean markedForTranslation,
                                        boolean notTranslated, boolean translationOutOfDate, int offset, int limit) {
        return translationServiceInternal.getItems(siteId, path, locale, markedForTranslation, notTranslated,
                translationOutOfDate, offset, limit);
    }

    @Override
    public void markById(String siteId, List<Long> ids, List<String> locales) {
        translationServiceInternal.markById(siteId, ids, locales);
    }

}
