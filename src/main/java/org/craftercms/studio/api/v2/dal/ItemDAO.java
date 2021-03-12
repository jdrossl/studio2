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

package org.craftercms.studio.api.v2.dal;

import org.apache.ibatis.annotations.Param;

import java.time.ZonedDateTime;
import java.util.List;

import static org.craftercms.studio.api.v2.dal.QueryParameterNames.COMMIT_ID;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.CONTENT_TYPE;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.DATE_FROM;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.DATE_TO;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.ENTRIES;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.EXCLUDES;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.FOLDER_PATH;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.ID;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.IGNORE_NAMES;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.IN_PROGRESS_MASK;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.ITEM_IDS;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.KEYWORD;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.LEVEL_DESCRIPTOR_NAME;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.LEVEL_DESCRIPTOR_PATH;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.LIKE_PATH;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.LIMIT;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.LOCALE_CODE;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.MODIFIED_MASK;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.MODIFIER;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.NEW_MASK;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.NEW_PATH;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.NEW_PREVIEW_URL;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.NON_CONTENT_ITEM_TYPES;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.OFFSET;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.OFF_STATES_BIT_MAP;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.OLD_PATH;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.OLD_PREVIEW_URL;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.ON_STATES_BIT_MAP;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.ORDER;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.PARENTS;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.PARENT_ID;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.PATH;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.PATHS;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.POSSIBLE_PARENTS;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.PREVIOUS_PATH;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.ROOT_PATH;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.SITE_ID;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.SORT_STRATEGY;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.STATE;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.STATES_BIT_MAP;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.SUBMITTED_MASK;

public interface ItemDAO {

    /**
     * Get total number of children for given path
     *
     * @param siteId site identifier
     * @param path path to get children for
     * @param ldName level descriptor name
     * @param localeCode local code
     * @param keyword filter by keyword
     * @param excludes exclude items by regular expression patterns
     * @param ignoreNames ignore children
     * @return total number of children
     */
    int getChildrenByPathTotal(@Param(SITE_ID) Long siteId, @Param(PATH) String path,
                               @Param(LEVEL_DESCRIPTOR_NAME) String ldName, @Param(LOCALE_CODE) String localeCode,
                               @Param(KEYWORD) String keyword, @Param(EXCLUDES) List<String> excludes,
                               @Param(IGNORE_NAMES) List<String> ignoreNames);

    /**
     * Get children for given path from database
     *
     * @param siteId site identifier
     * @param ldPath level descriptor path
     * @param ldName level descriptor name
     * @param path path to get children for
     * @param localeCode locale code
     * @param keyword filter by keyword
     * @param excludes exclude items by regular expression patterns
     * @param ignoreNames ignore children
     * @param sortStrategy sort strategy
     * @param order order of children
     * @param offset offset of the first record to return
     * @param limit number of children to return
     *
     * @return list of items (parent, level descriptor, children)
     */

    List<Item> getChildrenByPath(@Param(SITE_ID) Long siteId, @Param(LEVEL_DESCRIPTOR_PATH) String ldPath,
                                 @Param(LEVEL_DESCRIPTOR_NAME) String ldName, @Param(PATH) String path,
                                 @Param(LOCALE_CODE) String localeCode, @Param(KEYWORD) String keyword,
                                 @Param(EXCLUDES) List<String> excludes, @Param(IGNORE_NAMES) List<String> ignoreNames,
                                 @Param(SORT_STRATEGY) String sortStrategy, @Param(ORDER) String order,
                                 @Param(OFFSET) int offset, @Param(LIMIT) int limit);



    /**
     * Get total number of children for given path
     *
     * @param siteId site identifier
     * @param parentId item id to get children for
     * @param ldName level descriptor name
     * @param localeCode local code
     * @param keyword filter by keyword
     * @param excludes exclude items by regular expression patterns
     * @param ignoreNames ignore children
     *
     * @return total number of children
     */
    int getChildrenByIdTotal(@Param(SITE_ID) Long siteId, @Param(PARENT_ID) String parentId,
                             @Param(LEVEL_DESCRIPTOR_NAME) String ldName, @Param(LOCALE_CODE) String localeCode,
                             @Param(KEYWORD) String keyword, @Param(EXCLUDES) List<String> excludes,
                             @Param(IGNORE_NAMES) List<String> ignoreNames);
    /**
     * Get children for given id from database
     * @param siteId site identifier
     * @param parentId parent identifier
     * @param ldName level descriptor name
     * @param localeCode locale code
     * @param keyword filter by keyword
     * @param excludes exclude items by regular expression patterns
     * @param ignoreNames ignore  children
     * @param sortStrategy sort strategy
     * @param order order of children
     * @param offset offset of the first record to return
     * @param limit number of children to return
     * @return list of items (parent, level descriptor, children)
     */
    List<Item> getChildrenById(@Param(SITE_ID) Long siteId, @Param(PARENT_ID) String parentId,
                               @Param(LEVEL_DESCRIPTOR_NAME) String ldName, @Param(LOCALE_CODE) String localeCode,
                               @Param(KEYWORD) String keyword, @Param(EXCLUDES) List<String> excludes,
                               @Param(IGNORE_NAMES) List<String> ignoreNames, @Param(SORT_STRATEGY) String sortStrategy,
                               @Param(ORDER) String order, @Param(OFFSET) int offset, @Param(LIMIT) int limit);

    /**
     * Update parent ID for site
     * @param siteId site identifier
     */
    void updateParentIdForSite(@Param(SITE_ID) long siteId, @Param(ROOT_PATH) String rootPath);

    /**
     * Insert or update items
     *
     * @param entries list of items to insert
     */
    void upsertEntries(@Param(ENTRIES) List<Item> entries);

    /**
     * Get item by id
     *
     * @param id item id
     * @return item identified by given id
     */
    Item getItemById(@Param(ID) long id);

    /**
     * Get item by id with prefer content option
     *
     * @param id item id
     * @return item identified by given id
     */
    Item getItemByIdPreferContent(@Param(ID) long id);

    /**
     * Get item for given site and path
     * @param siteId site identifier
     * @param path path of the item
     * @return item for given site and path
     */
    Item getItemBySiteIdAndPath(@Param(SITE_ID) long siteId, @Param(PATH) String path);

    /**
     * Get item with prefer content option for given site and path
     * @param siteId site identifier
     * @param path path of the item
     * @return item for given site and path
     */
    Item getItemBySiteIdAndPathPreferContent(@Param(SITE_ID) long siteId, @Param(PATH) String path);

    /**
     * Update item
     * @param item item to update
     */
    void updateItem(Item item);

    /**
     * Delete item
     * @param id id of the item to delete
     */
    void deleteById(@Param(ID) long id);

    /**
     * Delete item
     * @param siteId site identifier
     * @param path path of item to delete
     */
    void deleteBySiteAndPath(@Param(SITE_ID) long siteId, @Param(PATH) String path);

    /**
     * Set items state
     * @param siteId site identifier
     * @param paths paths of items
     * @param statesBitMap states bit map to be set
     */
    void setStatesBySiteAndPathBulk(@Param(SITE_ID) long siteId, @Param(PATHS) List<String> paths,
                                    @Param(STATES_BIT_MAP) long statesBitMap);

    /**
     * Set items state
     * @param itemIds ids of items
     * @param statesBitMap states bit map to be set
     */
    void setStatesByIdBulk(@Param(ITEM_IDS) List<Long> itemIds, @Param(STATES_BIT_MAP) long statesBitMap);

    /**
     * Reset items state
     * @param siteId site identifier
     * @param paths paths of items
     * @param statesBitMap states bit map to be reset
     */
    void resetStatesBySiteAndPathBulk(@Param(SITE_ID) long siteId, @Param(PATHS) List<String> paths,
                                    @Param(STATES_BIT_MAP) long statesBitMap);

    /**
     * Reset items state
     * @param itemIds ids of items
     * @param statesBitMap states bit map to be reset
     */
    void resetStatesByIdBulk(@Param(ITEM_IDS) List<Long> itemIds, @Param(STATES_BIT_MAP) long statesBitMap);

    /**
     * Update states to flip on list off states and flip off another list of states for items
     *
     * @param siteId site identifier
     * @param paths list of paths to update states for
     * @param onStatesBitMap state bitmap to flip on
     * @param offStatesBitMap state bitmap to flip off
     */
    void updateStatesBySiteAndPathBulk(@Param(SITE_ID) long siteId, @Param(PATHS) List<String> paths,
                                   @Param(ON_STATES_BIT_MAP) long onStatesBitMap,
                                   @Param(OFF_STATES_BIT_MAP) long offStatesBitMap);

    /**
     * Update states to flip on list off states and flip off another list of states for items
     *
     * @param itemIds list of item identifiers
     * @param onStatesBitMap state bitmap to flip on
     * @param offStatesBitMap state bitmap to flip off
     */
    void updateStatesByIdBulk(@Param(ITEM_IDS) List<Long> itemIds,
                                   @Param(ON_STATES_BIT_MAP) long onStatesBitMap,
                                   @Param(OFF_STATES_BIT_MAP) long offStatesBitMap);

    /**
     * Delete all items for site
     * @param siteId site id
     */
    void deleteItemsForSite(@Param(SITE_ID) long siteId);

    /**
     * Delete items by id
     * @param itemIds item ids
     */
    void deleteItemsById(@Param(ITEM_IDS) List<Long> itemIds);

    /**
     * Delete items for site and paths
     * @param siteId site id
     * @param paths paths of the items
     */
    void deleteItemsForSiteAndPath(@Param(SITE_ID) long siteId, @Param(PATHS) List<String> paths);

    /**
     * Delete items for site and folder path
     * @param siteId site id
     * @param path path of the folder
     */
    void deleteBySiteAndPathForFolder(@Param(SITE_ID) long siteId, @Param(FOLDER_PATH) String path);

    /**
     * Get total number of records for content dashboard
     *
     * @param siteId site identifier
     * @param path path regular expression to apply as filter for result set
     * @param modifier filter results by user
     * @param contentType filter results by content type
     * @param state filter results by state
     * @param dateFrom lower boundary for modified date
     * @param dateTo upper boundary for modified date
     * @return total number of records in result set
     */
    int getContentDashboardTotal(@Param(SITE_ID) String siteId, @Param(PATH) String path,
                                 @Param(MODIFIER) String modifier, @Param(CONTENT_TYPE) String contentType,
                                 @Param(STATE) long state, @Param(DATE_FROM) ZonedDateTime dateFrom,
                                 @Param(DATE_TO) ZonedDateTime dateTo);

    /**
     * Get result set for content dashboard
     *
     * @param siteId site identifier
     * @param path path regular expression to apply as filter for result set
     * @param modifier filter results by user
     * @param contentType filter results by content type
     * @param state filter results by state
     * @param dateFrom lower boundary for modified date
     * @param dateTo upper boundary for modified date
     * @param sort sort results by column
     * @param order order of results
     * @param offset offset of the first record in result set
     * @param limit number of records to return
     * @return list of item metadata records
     */
    List<Item> getContentDashboard(@Param(SITE_ID) String siteId, @Param(PATH) String path,
                                   @Param(MODIFIER) String modifier, @Param(CONTENT_TYPE) String contentType,
                                   @Param(STATE) long state, @Param(DATE_FROM) ZonedDateTime dateFrom,
                                   @Param(DATE_TO) ZonedDateTime dateTo, @Param(SORT_STRATEGY) String sort,
                                   @Param(ORDER) String order, @Param(OFFSET) int offset, @Param(LIMIT) int limit);

    /**
     * Move item
     * @param siteId site identifier
     * @param oldPath old path
     * @param newPath new path
     */
    void moveItem(@Param(SITE_ID) String siteId, @Param(OLD_PATH) String oldPath, @Param(NEW_PATH) String newPath);

    /**
     * Get item for given path from database
     *
     * @param siteId site identifier
     * @param path path of the item
     * @return item
     */

    Item getItemByPath(@Param(SITE_ID) Long siteId, @Param(PATH) String path);
    /**
     * Get item with prefer content option for given path from database
     *
     * @param siteId site identifier
     * @param path path of the item
     * @return item
     */

    Item getItemByPathPreferContent(@Param(SITE_ID) Long siteId, @Param(PATH) String path);


    /**
     * Move item
     * @param siteId site identifier
     * @param oldPath old path
     * @param newPath new path
     * @param onStatesBitMap state bitmap to flip on
     * @param offStatesBitMap state bitmap to flip off
     */
    void moveItems(@Param(SITE_ID) String siteId, @Param(OLD_PATH) String oldPath, @Param(NEW_PATH) String newPath,
                   @Param(OLD_PREVIEW_URL) String oldPreviewUrl, @Param(NEW_PREVIEW_URL) String newPreviewUrl,
                   @Param(ON_STATES_BIT_MAP) long onStatesBitMap, @Param(OFF_STATES_BIT_MAP) long offStatesBitMap);

    /**
     * Get sandbox items for given paths with prefer content option
     * @param siteId site identifier
     * @param paths paths to get items for
     * @return list of items
     */
    List<Item> getSandboxItemsByPathPreferContent(@Param(SITE_ID) Long siteId, @Param(PATHS) List<String> paths);

    /**
     * Get sandbox items for given paths
     * @param siteId site identifier
     * @param paths paths to get items for
     * @return list of items
     */
    List<Item> getSandboxItemsByPath(@Param(SITE_ID) Long siteId, @Param(PATHS) List<String> paths);

    /**
     * Get sandbox items for given ids with prefer content option
     * @param itemIds item ids
     * @return list of items
     */
    List<Item> getSandboxItemsByIdPreferContent(@Param(ITEM_IDS) List<Long> itemIds);

    /**
     * Get sandbox items for given ids
     * @param itemIds item ids
     * @return list of items
     */
    List<Item> getSandboxItemsById(@Param(ITEM_IDS) List<Long> itemIds);

    /**
     * Get mandatory parents for publishing
     * @param siteId site identifier
     * @param possibleParents possible parents
     * @param newMask states mask for detecting new items
     * @param modifiedMask states mask for detecting modified items
     * @return list of mandatory parents
     */
    List<String> getMandatoryParentsForPublishing(@Param(SITE_ID) String siteId,
                                                  @Param(POSSIBLE_PARENTS) List<String> possibleParents,
                                                  @Param(NEW_MASK) long newMask,
                                                  @Param(MODIFIED_MASK) long modifiedMask);

    List<String> getExistingRenamedChildrenOfMandatoryParentsForPublishing(@Param(SITE_ID) String siteId,
                                                                           @Param(PARENTS) List<String> parents,
                                                                           @Param(NEW_MASK) long newMask,
                                                                           @Param(MODIFIED_MASK) long modifiedMask);

    /**
     * Count all content items in the system
     * @return number of content items in the system
     */
    int countAllContentItems(@Param(NON_CONTENT_ITEM_TYPES) List<String> nonContentItemTypes);

    /**
     * Clear previous path of the content
     * @param siteId site identifier
     * @param path path of the content
     */
    void clearPreviousPath(@Param(SITE_ID) String siteId, @Param(PATH) String path);

    /**
     * Get in progress items from DB
     * @param siteId site identifier
     * @param inProgressMask in progress states mask
     * @return list of items
     */
    List<Item> getInProgressItems(@Param(SITE_ID) String siteId, @Param(IN_PROGRESS_MASK) long inProgressMask);

    /**
     * Get submitted items from DB
     * @param siteId site identifier
     * @param submittedMask mask with submitted states turned on
     * @return list of items
     */
    List<Item> getSubmittedItems(@Param(SITE_ID) String siteId, @Param(SUBMITTED_MASK) long submittedMask);

    /**
     * Count items having previous path property set to given path
     * @param siteId site identifier
     * @param previousPath path to check
     * @return number of items
     */
    int countPreviousPaths(@Param(SITE_ID) String siteId, @Param(PREVIOUS_PATH) String previousPath);

    /**
     * Update commit id for item
     * @param siteId site identifier
     * @param path path of the item
     * @param commitId commit id
     */
    void updateCommitId(@Param(SITE_ID) String siteId, @Param(PATH) String path, @Param(COMMIT_ID) String commitId);

    /**
     * Get change set for subtree
     * @param siteId site identifier
     * @param path path of subtree root
     * @param likePath like path for query
     * @param nonContentItemTypes non content item types
     * @param inProgressMask in progress state mask
     * @return list of items
     */
    List<String> getChangeSetForSubtree(@Param(SITE_ID) String siteId,
                                        @Param(PATH) String path,
                                        @Param(LIKE_PATH) String likePath,
                                        @Param(NON_CONTENT_ITEM_TYPES) List<String> nonContentItemTypes,
                                        @Param(IN_PROGRESS_MASK) long inProgressMask);

    /**
     * Get items edited on same commit id for given item
     * @param siteId site identifier
     * @param path path of content item
     * @return list of items paths
     */
    List<String> getSameCommitItems(@Param(SITE_ID) String siteId, @Param(PATH) String path);

    /**
     * Finds all items related to a given content-type
     *
     * @param siteId the id of the site
     * @param contentType the id of the content-type
     * @param scriptPath the path of the controller script
     * @return the list of items
     */
    List<Item> getContentTypeUsages(@Param(SITE_ID) String siteId, @Param(CONTENT_TYPE) String contentType,
                                    @Param("scriptPath") String scriptPath);

}
