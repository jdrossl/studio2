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
package org.craftercms.studio.impl.v2.service.translation.internal;

import org.craftercms.studio.api.v2.dal.ItemDAO;
import org.craftercms.studio.api.v2.dal.ItemState;
import org.craftercms.studio.api.v2.dal.translation.LocalizedItem;
import org.craftercms.studio.api.v2.dal.translation.TranslationDAO;
import org.craftercms.studio.api.v2.service.translation.internal.TranslationServiceInternal;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author joseross
 * @since
 */
public class TranslationServiceInternalImpl implements TranslationServiceInternal {

    protected TranslationDAO translationDAO;

    protected ItemDAO itemDAO;

    public TranslationServiceInternalImpl(TranslationDAO translationDAO, ItemDAO itemDAO) {
        this.translationDAO = translationDAO;
        this.itemDAO = itemDAO;
    }

    @Override
    public int countItems(String siteId, String path, String locale, boolean markedForTranslation,
                          boolean notTranslated, boolean translationOutOfDate) {
        return translationDAO.countItems(siteId, path, locale, markedForTranslation,
                ItemState.TRANSLATION_PENDING.value, notTranslated, translationOutOfDate);
    }

    @Override
    public List<LocalizedItem> getItems(String siteId, String path, String locale, boolean markedForTranslation,
                                        boolean notTranslated, boolean translationOutOfDate, int offset, int limit) {
        return translationDAO.getItems(siteId, path, locale, markedForTranslation, ItemState.TRANSLATION_PENDING.value,
                notTranslated, translationOutOfDate, offset, limit);
    }

    @Override
    @Transactional
    public void markById(String siteId, List<Long> ids, List<String> locales) {
        // TODO: Validate that all locales are supported by the site
        translationDAO.insertTranslationRequests(siteId, ids, locales);
        itemDAO.setStatesByIdBulk(ids, ItemState.TRANSLATION_PENDING.value);
    }

}
