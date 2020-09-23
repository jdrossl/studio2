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
package org.craftercms.studio.api.v2.dal.translation;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author joseross
 * @since
 */
public interface TranslationDAO {

    int countItems(@Param("siteId") String siteId, @Param("path") String path, @Param("locale") String locale,
                   @Param("markedForTranslation") boolean markedForTranslation,
                   @Param("markedForTranslationMask") long markedForTranslationMask,
                   @Param("notTranslated") boolean notTranslated, //TODO: Implement this filter
                   @Param("translationOutOfDate") boolean translationOutOfDate); //TODO: Implement this filter

    List<LocalizedItem> getItems(@Param("siteId") String siteId, @Param("path") String path,
                                 @Param("locale") String locale,
                                 @Param("markedForTranslation") boolean markedForTranslation,
                                 @Param("markedForTranslationMask") long markedForTranslationMask,
                                 @Param("notTranslated") boolean notTranslated,
                                 @Param("translationOutOfDate") boolean translationOutOfDate,
                                 @Param("offset") int offset,
                                 @Param("limit") int limit);

    void insertTranslationRequests(@Param("siteId") String siteId, @Param("ids") List<Long> ids,
                   @Param("locales") List<String> locales);

}
