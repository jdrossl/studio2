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

package org.craftercms.studio.impl.v2.service.content.internal;

import org.apache.commons.collections.CollectionUtils;
import org.craftercms.studio.api.v1.exception.ServiceLayerException;
import org.craftercms.studio.api.v1.exception.security.AuthenticationException;
import org.craftercms.studio.api.v1.service.content.ContentTypeService;
import org.craftercms.studio.api.v1.service.deployment.DeploymentException;
import org.craftercms.studio.api.v1.service.security.SecurityService;
import org.craftercms.studio.api.v1.to.ContentTypeConfigTO;
import org.craftercms.studio.api.v2.dal.Item;
import org.craftercms.studio.api.v2.dal.ItemDAO;
import org.craftercms.studio.api.v2.dal.QuickCreateItem;
import org.craftercms.studio.api.v2.service.config.ConfigurationService;
import org.craftercms.studio.api.v2.service.content.ContentService;
import org.craftercms.studio.api.v2.service.content.internal.ContentTypeServiceInternal;
import org.craftercms.studio.model.contentType.ContentTypeUsage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FilenameUtils.normalize;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.craftercms.studio.api.v1.constant.StudioConstants.CONTENT_TYPE_COMPONENT;
import static org.craftercms.studio.api.v1.constant.StudioConstants.CONTENT_TYPE_PAGE;
import static org.craftercms.studio.api.v1.constant.StudioConstants.CONTENT_TYPE_SCRIPT;

public class ContentTypeServiceInternalImpl implements ContentTypeServiceInternal {

    protected final ContentTypeService contentTypeService;
    protected final SecurityService securityService;
    protected final ConfigurationService configurationService;
    protected final ItemDAO itemDao;
    protected final ContentService contentService;

    protected final String contentTypeBasePathPattern;
    protected final String contentTypeDefinitionFilename;
    protected final String templateXPath;
    protected final String controllerPattern;
    protected final String controllerFormat;

    public ContentTypeServiceInternalImpl(ContentTypeService contentTypeService, SecurityService securityService,
                                          ConfigurationService configurationService, ItemDAO itemDao,
                                          ContentService contentService, String contentTypeBasePathPattern,
                                          String contentTypeDefinitionFilename, String templateXPath,
                                          String controllerPattern, String controllerFormat) {
        this.contentTypeService = contentTypeService;
        this.securityService = securityService;
        this.configurationService = configurationService;
        this.itemDao = itemDao;
        this.contentService = contentService;
        this.contentTypeBasePathPattern = contentTypeBasePathPattern;
        this.contentTypeDefinitionFilename = contentTypeDefinitionFilename;
        this.templateXPath = templateXPath;
        this.controllerPattern = controllerPattern;
        this.controllerFormat = controllerFormat;
    }

    @Override
    public List<QuickCreateItem> getQuickCreatableContentTypes(String siteId) {
        List<QuickCreateItem> toRet = new ArrayList<QuickCreateItem>();
        List<ContentTypeConfigTO> allContentTypes = contentTypeService.getAllContentTypes(siteId, true);
        List<ContentTypeConfigTO> quickCreatable = allContentTypes.stream()
                .filter(ct -> ct.isQuickCreate()).collect(toList());
        for (ContentTypeConfigTO ctto : quickCreatable) {
            QuickCreateItem qci = new QuickCreateItem();
            qci.setSiteId(siteId);
            qci.setContentTypeId(ctto.getForm());
            qci.setLabel(ctto.getLabel());
            qci.setPath(ctto.getQuickCreatePath());
            Set<String> allowedPermission = securityService.getUserPermissions(siteId, ctto.getQuickCreatePath(),
                    securityService.getCurrentUser(), null);
            if (allowedPermission.contains("create content")) {
                toRet.add(qci);
            }
        }
        return toRet;
    }

    @Override
    public ContentTypeUsage getContentTypeUsage(String siteId, String contentType) throws ServiceLayerException {
        var definitionPath = getContentTypePath(contentType) + "/" + contentTypeDefinitionFilename;
        var definition = configurationService.getConfigurationAsDocument(siteId, null, definitionPath, null);
        var templateNode = definition.selectSingleNode(templateXPath);
        var usages = new ContentTypeUsage();

        if(templateNode != null && isNotEmpty(templateNode.getText())) {
            usages.setTemplates(singletonList(templateNode.getText()));
        }

        var scriptPath = contentType.replaceAll(controllerPattern, controllerFormat);

        var items = itemDao.getContentTypeUsages(siteId, contentType, scriptPath);

        usages.setContent(items.stream()
                .filter(i -> equalsAnyIgnoreCase(i.getSystemType(), CONTENT_TYPE_PAGE, CONTENT_TYPE_COMPONENT))
                .map(Item::getPath)
                .collect(toList()));

        usages.setScripts(items.stream()
                .filter(i -> equalsIgnoreCase(i.getSystemType(), (CONTENT_TYPE_SCRIPT)))
                .map(Item::getPath)
                .collect(toList()));

        return usages;
    }

    @Override
    public void deleteContentType(String siteId, String contentType) throws ServiceLayerException, AuthenticationException, DeploymentException {
        var usage = getContentTypeUsage(siteId, contentType);

        if (CollectionUtils.isNotEmpty(usage.getContent())) {
            throw new ServiceLayerException("The content-type " + contentType + " in site " + siteId + " can't be" +
                    "deleted because there is content using it");
        }

        var files = new LinkedList<String>();
        files.addAll(usage.getTemplates());
        files.addAll(usage.getScripts());
        files.add(getContentTypePath(contentType));

        if (!contentService.deleteContent(siteId, files, "Delete content-type " + contentType)) {
            throw new ServiceLayerException("Error deleting content-type " + contentType + " in site "+ siteId);
        }

    }

    protected String getContentTypePath(String contentType) {
        return normalize(contentTypeBasePathPattern.replaceFirst("\\{content-type}", contentType));
    }

}
