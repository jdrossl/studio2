/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
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
 */

import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.lang3.StringUtils
import org.craftercms.studio.api.v1.log.LoggerFactory
import scripts.api.SecurityServices
import org.apache.commons.text.StringEscapeUtils

import static org.craftercms.studio.api.v1.util.StudioConfiguration.AUTHENTICATION_HEADERS_EMAIL
import static org.craftercms.studio.api.v1.util.StudioConfiguration.AUTHENTICATION_HEADERS_ENABLED
import static org.craftercms.studio.api.v1.util.StudioConfiguration.AUTHENTICATION_HEADERS_FIRST_NAME
import static org.craftercms.studio.api.v1.util.StudioConfiguration.AUTHENTICATION_HEADERS_LAST_NAME
import static org.craftercms.studio.api.v1.util.StudioConfiguration.AUTHENTICATION_HEADERS_USERNAME

import static org.craftercms.studio.api.v1.constant.StudioConstants.SECURITY_AUTHENTICATION_TYPE_HEADERS;

def logger = LoggerFactory.getLogger(this.class)

def context = SecurityServices.createContext(applicationContext, request)

def result = [:]
def currentUser = SecurityServices.getCurrentUser(context)
def email = ""
def firstname = ""
def lastname = ""
def authenticationType = ""

if (StringUtils.isEmpty(currentUser)) {
    def studioConfigurationSB = context.applicationContext.get("studioConfiguration")
    def enabledString = studioConfigurationSB.getProperty(AUTHENTICATION_HEADERS_ENABLED)
    def enabled = Boolean.parseBoolean(enabledString)
    if (enabled) {
        currentUser = request.getHeader(studioConfigurationSB.getProperty(AUTHENTICATION_HEADERS_USERNAME))
        email = request.getHeader(studioConfigurationSB.getProperty(AUTHENTICATION_HEADERS_EMAIL))
        firstname = request.getHeader(studioConfigurationSB.getProperty(AUTHENTICATION_HEADERS_FIRST_NAME))
        lastname = request.getHeader(studioConfigurationSB.getProperty(AUTHENTICATION_HEADERS_LAST_NAME))
        authenticationType = SECURITY_AUTHENTICATION_TYPE_HEADERS
    }
}

def profile = SecurityServices.getUserProfile(context, currentUser)

if (profile == null || profile.isEmpty()) {
    profile = [:]
    profile.email = email
    profile.first_name = firstname
    profile.last_name = lastname
    profile.authentication_type = authenticationType;
}
model.username = currentUser
model.userEmail = profile.email 
model.userFirstName = profile.first_name
model.userLastName =  profile.last_name
model.authenticationType =  profile.authentication_type
model.cookieDomain = StringEscapeUtils.escapeXml10(request.getServerName())
