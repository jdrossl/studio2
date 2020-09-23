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
package org.craftercms.studio.controller.rest.v2;

import org.craftercms.studio.api.v2.dal.translation.LocalizedItem;
import org.craftercms.studio.api.v2.service.translation.TranslationService;
import org.craftercms.studio.model.rest.ApiResponse;
import org.craftercms.studio.model.rest.PaginatedResultList;
import org.craftercms.studio.model.rest.ResponseBody;
import org.craftercms.studio.model.rest.Result;
import org.craftercms.studio.model.translation.MarkByIdRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author joseross
 * @since 3.2.0
 */
@RestController
@RequestMapping("/api/2/translation")
public class TranslationController {

    protected TranslationService translationService;

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @GetMapping("/list")
    public ResponseBody list(@RequestParam String siteId,
                             @RequestParam(required = false) String path,
                             @RequestParam(required = false) String locale,
                             @RequestParam(required = false, defaultValue = "true") boolean markedForTranslation,
                             @RequestParam(required = false, defaultValue = "true") boolean notTranslated,
                             @RequestParam(required = false, defaultValue = "true") boolean translationOutOfDate,
                             @RequestParam(required = false, defaultValue = "0") int offset,
                             @RequestParam(required = false, defaultValue = "10") int limit) {
        var result = new PaginatedResultList<LocalizedItem>();
        result.setResponse(ApiResponse.OK);
        result.setOffset(offset);
        result.setLimit(limit);
        result.setTotal(translationService.countItems(siteId, path, locale, markedForTranslation, notTranslated,
                translationOutOfDate));
        result.setEntities("items", translationService.getItems(siteId, path, locale, markedForTranslation,
                notTranslated, translationOutOfDate, offset, limit));

        var body = new ResponseBody();
        body.setResult(result);

        return body;
    }

    @PostMapping("/mark_for_translation_by_id")
    public ResponseBody markForTranslationById(@RequestBody @Validated MarkByIdRequest request) {
        translationService.markById(request.getSiteId(), request.getIds(), request.getLocales());

        var result = new Result();
        result.setResponse(ApiResponse.OK);

        var body = new ResponseBody();
        body.setResult(result);
        return body;
    }

}
