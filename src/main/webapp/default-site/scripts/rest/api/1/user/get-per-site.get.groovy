/*
 * Crafter Studio Web-content authoring solution
 * Copyright (C) 2007-2017 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */


import org.apache.commons.lang3.StringUtils
import scripts.api.SecurityServices
import org.craftercms.studio.api.v1.exception.SiteNotFoundException

def result = [:]

def site = params.site_id

/** Validate Parameters */
def invalidParams = false;
def paramsList = []

// site_id
try {
    if (StringUtils.isEmpty(site)) {
        invalidParams = true
        paramsList.add("site_id")
    }
} catch (Exception exc) {
    invalidParams = true
    paramsList.add("site_id")
}

// start
try {
    if (StringUtils.isNotEmpty(params.start)) {
        start = params.start.toInteger()
        if (start < 0) {
            invalidParams = true
            paramsList.add("start")
        }
    }
} catch (Exception exc) {
    invalidParams = true
    paramsList.add("start")
}

// number
try {
    if (StringUtils.isNotEmpty(params.number)) {
        number = params.number.toInteger()
        if (number < 0) {
            invalidParams = true
            paramsList.add("number")
        }
    }
} catch (Exception exc) {
    invalidParams = true
    paramsList.add("number")
}

if (invalidParams) {
    response.setStatus(400)
    result.message = "Invalid parameter(s): " + paramsList
} else {
    def context = SecurityServices.createContext(applicationContext, request)
    try {
        def total = SecurityServices.getUsersPerSiteTotal(context, site);
        def users = SecurityServices.getUsersPerSite(context, site, start, number);
        if (users != null) {
            def locationHeader = request.getRequestURL().toString().replace(request.getPathInfo().toString(), "") + "/api/1/services/api/1/user/get-per-site.json?site_id=" + site + "&start=" + start + "&number=" + number
            response.addHeader("Location", locationHeader)
            result.users = users
            result.total = total
            response.setStatus(200)
        } else {
            response.setStatus(500)
            result.message = "Internal server error"
        }
    } catch (SiteNotFoundException e) {
        response.setStatus(404)
        result.message = "Site not found"
    } catch (Exception e) {
        response.setStatus(500)
        result.message = "Internal server error: \n" + e
    }
}

return result