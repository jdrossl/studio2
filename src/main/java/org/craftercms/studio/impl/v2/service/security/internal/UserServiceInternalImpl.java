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

package org.craftercms.studio.impl.v2.service.security.internal;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.crypto.CryptoUtils;
import org.craftercms.studio.api.v1.exception.ServiceLayerException;
import org.craftercms.studio.api.v1.exception.security.PasswordDoesNotMatchException;
import org.craftercms.studio.api.v1.exception.security.UserAlreadyExistsException;
import org.craftercms.studio.api.v1.exception.security.UserExternallyManagedException;
import org.craftercms.studio.api.v1.exception.security.UserNotFoundException;
import org.craftercms.studio.api.v1.log.Logger;
import org.craftercms.studio.api.v1.log.LoggerFactory;
import org.craftercms.studio.api.v1.service.security.SecurityService;
import org.craftercms.studio.api.v1.service.site.SiteService;
import org.craftercms.studio.api.v2.annotation.RetryingOperation;
import org.craftercms.studio.api.v2.dal.Group;
import org.craftercms.studio.api.v2.dal.UserDAO;
import org.craftercms.studio.api.v2.dal.User;
import org.craftercms.studio.api.v2.dal.UserProperty;
import org.craftercms.studio.api.v2.exception.PasswordRequirementsFailedException;
import org.craftercms.studio.api.v2.service.security.internal.GroupServiceInternal;
import org.craftercms.studio.api.v2.service.security.internal.UserServiceInternal;
import org.craftercms.studio.api.v2.service.site.SitesService;
import org.craftercms.studio.api.v2.utils.StudioConfiguration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toMap;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.EMAIL;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.ENABLED;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.EXTERNALLY_MANAGED;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.FIRST_NAME;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.GROUP_NAME;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.GROUP_NAMES;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.ID;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.LAST_NAME;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.LIMIT;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.LOCALE;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.OFFSET;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.PASSWORD;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.SORT;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.TIMEZONE;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.USERNAME;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.USER_ID;
import static org.craftercms.studio.api.v2.dal.QueryParameterNames.USER_IDS;
import static org.craftercms.studio.api.v2.utils.StudioConfiguration.SECURITY_PASSWORD_REQUIREMENTS_VALIDATION_REGEX;
import static org.craftercms.studio.impl.v1.repository.git.GitContentRepositoryConstants.GIT_REPO_USER_USERNAME;

public class UserServiceInternalImpl implements UserServiceInternal {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceInternalImpl.class);

    private UserDAO userDao;
    private GroupServiceInternal groupServiceInternal;
    private StudioConfiguration studioConfiguration;
    private SiteService siteService;

    // TODO: Remove when everything is integrated with Spring Security
    private SecurityService securityService;

    public UserServiceInternalImpl(UserDAO userDao, GroupServiceInternal groupServiceInternal,
                                   StudioConfiguration studioConfiguration, SiteService siteService,
                                   SecurityService securityService) {
        this.userDao = userDao;
        this.groupServiceInternal = groupServiceInternal;
        this.studioConfiguration = studioConfiguration;
        this.siteService = siteService;
        this.securityService = securityService;
    }

    @Override
    public User getUserByIdOrUsername(long userId, String username) throws ServiceLayerException,
                                                                           UserNotFoundException {
        Map<String, Object> params = new HashMap<>();
        params.put(USER_ID, userId);
        params.put(USERNAME, username);
        User user;

        try {
            user = userDao.getUserByIdOrUsername(params);
        } catch (Exception e) {
            throw new ServiceLayerException("Unknown database error", e);
        }

        if (user == null) {
            throw new UserNotFoundException("No user found for username '" + username + "' or id '" + userId + "'");
        }

        return user;
    }

    @Override
    public List<User> getUsersByIdOrUsername(List<Long> userIds,
                                             List<String> usernames) throws ServiceLayerException,
                                                                              UserNotFoundException {
        List<User> users = new LinkedList<>();
        for(long userId : userIds) {
            users.add(getUserByIdOrUsername(userId, StringUtils.EMPTY));
        }
        for(String username : usernames) {
            Optional<User> user = users.stream().filter(u -> u.getUsername().equals(username)).findFirst();
            if(!user.isPresent()) {
                users.add(getUserByIdOrUsername(-1, username));
            }
        }

        return users;
    }

    @Override
    public List<User> getAllUsersForSite(long orgId, List<String> groupNames, int offset, int limit,
                                         String sort) throws ServiceLayerException {
        Map<String, Object> params = new HashMap<>();
        params.put(GROUP_NAMES, groupNames);
        params.put(OFFSET, offset);
        params.put(LIMIT, limit);
        params.put(SORT, StringUtils.EMPTY);

        try {
            return userDao.getAllUsersForSite(params);
        } catch (Exception e) {
            throw new ServiceLayerException("Unknown database error", e);
        }
    }

    @Override
    public List<User> getAllUsers(int offset, int limit, String sort) throws ServiceLayerException {
        Map<String, Object> params = new HashMap<>();
        params.put(OFFSET, offset);
        params.put(LIMIT, limit);
        params.put(SORT, sort);

        try {
            return userDao.getAllUsers(params);
        } catch (Exception e) {
            throw new ServiceLayerException("Unknown database error", e);
        }
    }

    @Override
    public int getAllUsersForSiteTotal(long orgId, String siteId) throws ServiceLayerException {
        List<String> groupNames = groupServiceInternal.getSiteGroups(siteId);

        Map<String, Object> params = new HashMap<>();
        params.put(GROUP_NAMES, groupNames);

        try {
            return userDao.getAllUsersForSiteTotal(params);
        } catch (Exception e) {
            throw new ServiceLayerException("Unknown database error", e);
        }
    }

    @Override
    public int getAllUsersTotal() throws ServiceLayerException {
        try {
            return userDao.getAllUsersTotal();
        } catch (Exception e) {
            throw new ServiceLayerException("Unknown database error", e);
        }
    }

    @Override
    public User createUser(User user) throws UserAlreadyExistsException, ServiceLayerException {
        if (userExists(-1, user.getUsername())) {
            throw new UserAlreadyExistsException("User '" + user.getUsername() + "' already exists");
        }
        if (user.isExternallyManaged() || verifyPasswordRequirements(user.getPassword())) {
            Map<String, Object> params = new HashMap<>();
            params.put(USERNAME, user.getUsername());
            params.put(PASSWORD, CryptoUtils.hashPassword(user.getPassword()));
            params.put(FIRST_NAME, user.getFirstName());
            params.put(LAST_NAME, user.getLastName());
            params.put(EMAIL, user.getEmail());
            params.put(EXTERNALLY_MANAGED, user.getExternallyManagedAsInt());
            params.put(TIMEZONE, StringUtils.EMPTY);
            params.put(LOCALE, StringUtils.EMPTY);
            params.put(ENABLED, user.getEnabledAsInt());

            try {
                userDao.createUser(params);

                user.setId((Long) params.get(ID));

                return user;
            } catch (Exception e) {
                throw new ServiceLayerException("Unknown database error", e);
            }
        } else {
            throw new PasswordRequirementsFailedException();
        }
    }

    @Override
    public boolean userExists(long userId, String username) throws ServiceLayerException {
        Map<String, Object> params = new HashMap<>();
        params.put(USER_ID, userId);
        params.put(USERNAME, username);

        try {
            Integer result = userDao.userExists(params);
            return (result > 0);
        } catch (Exception e) {
            throw new ServiceLayerException("Unknown database error", e);
        }
    }

    @RetryingOperation
    @Override
    public void updateUser(User user) throws UserNotFoundException, ServiceLayerException {
        long userId = user.getId();
        String username = user.getUsername() != null ? user.getUsername() : StringUtils.EMPTY;

        User oldUser = getUserByIdOrUsername(userId, username);

        Map<String, Object> params = new HashMap<>();
        params.put(USER_ID, oldUser.getId());
        params.put(FIRST_NAME, user.getFirstName());
        params.put(LAST_NAME, user.getLastName());
        params.put(EMAIL, user.getEmail());
        params.put(TIMEZONE, StringUtils.EMPTY);
        params.put(LOCALE, StringUtils.EMPTY);

        try {
            userDao.updateUser(params);
        } catch (Exception e) {
            throw new ServiceLayerException("Unknown database error", e);
        }
    }

    @RetryingOperation
    @Override
    public void deleteUsers(List<Long> userIds,
                            List<String> usernames) throws UserNotFoundException, ServiceLayerException {
        List<User> users = getUsersByIdOrUsername(userIds, usernames);

        Map<String, Object> params = new HashMap<>();
        params.put(USER_IDS, users.stream().map(User::getId).collect(Collectors.toList()));

        try {
            userDao.deleteUsers(params);
        } catch (Exception e) {
            throw new ServiceLayerException("Unknown database error", e);
        }
    }

    @RetryingOperation
    @Override
    public List<User> enableUsers(List<Long> userIds, List<String> usernames,
                                  boolean enabled) throws ServiceLayerException, UserNotFoundException {
        List<User> users = getUsersByIdOrUsername(userIds, usernames);

        Map<String, Object> params = new HashMap<>();
        params.put(USER_IDS, users.stream().map(User::getId).collect(Collectors.toList()));
        params.put(ENABLED, enabled? 1: 0);

        try {
            userDao.enableUsers(params);

            return getUsersByIdOrUsername(userIds, usernames);
        } catch (Exception e) {
            throw new ServiceLayerException("Unknown database error", e);
        }
    }

    @Override
    public List<Group> getUserGroups(long userId, String username) throws UserNotFoundException, ServiceLayerException {
        if (!userExists(userId, username)) {
            throw new UserNotFoundException("No user found for username '" + username + "' or id '" + userId + "'");
        }

        Map<String, Object> params = new HashMap<>();
        params.put(USER_ID, userId);
        params.put(USERNAME, username);

        try {
            return userDao.getUserGroups(params);
        } catch (Exception e) {
            throw new ServiceLayerException("Unknown database error", e);
        }
    }

    @Override
    public boolean isUserMemberOfGroup(String username, String groupName) throws UserNotFoundException,
                                                                                 ServiceLayerException {
        if (!userExists(-1, username)) {
            throw new UserNotFoundException("No user found for username '" + username + "'");
        }

        Map<String, Object> params = new HashMap<>();
        params.put(GROUP_NAME, groupName);
        params.put(USERNAME, username);

        try {
            int result = userDao.isUserMemberOfGroup(params);
            return result > 0;
        } catch (Exception e) {
            throw new ServiceLayerException("Unknown database error", e);
        }
    }

    @RetryingOperation
    @Override
    public boolean changePassword(String username, String current, String newPassword)
            throws PasswordDoesNotMatchException, UserExternallyManagedException, ServiceLayerException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(USER_ID, -1);
        params.put(USERNAME, username);
        try {
            User user = userDao.getUserByIdOrUsername(params);
            if (user.isExternallyManaged()) {
                throw new UserExternallyManagedException();
            } else {
                if (CryptoUtils.matchPassword(user.getPassword(), current)) {
                    if (verifyPasswordRequirements(newPassword)) {
                        String hashedPassword = CryptoUtils.hashPassword(newPassword);
                        params = new HashMap<>();
                        params.put(USERNAME, username);
                        params.put(PASSWORD, hashedPassword);
                        userDao.setUserPassword(params);
                        return true;
                    } else {
                        throw new PasswordRequirementsFailedException();
                    }
                } else {
                    throw new PasswordDoesNotMatchException();
                }
            }
        } catch (RuntimeException e) {
            throw new ServiceLayerException("Unknown database error", e);
        }
    }

    @RetryingOperation
    @Override
    public boolean setUserPassword(String username, String newPassword) throws UserNotFoundException,
            UserExternallyManagedException, ServiceLayerException {
        if (!userExists(-1, username)) {
            throw new UserNotFoundException();
        } else {
            if (verifyPasswordRequirements(newPassword)) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put(USER_ID, -1);
                params.put(USERNAME, username);
                try {
                    User user = userDao.getUserByIdOrUsername(params);
                    if (user.isExternallyManaged()) {
                        throw new UserExternallyManagedException();
                    } else {
                        String hashedPassword = CryptoUtils.hashPassword(newPassword);
                        params = new HashMap<String, Object>();
                        params.put(USERNAME, username);
                        params.put(PASSWORD, hashedPassword);
                        userDao.setUserPassword(params);
                        return true;
                    }
                } catch (Exception e) {
                    throw new ServiceLayerException("Unknown database error", e);
                }
            } else {
                throw new PasswordRequirementsFailedException("User password does not fulfill requirements");
            }
        }
    }

    private boolean verifyPasswordRequirements(String password) {
        Pattern pattern = Pattern.compile(getPasswordRequirementValidationRegex());
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private String getPasswordRequirementValidationRegex() {
        return studioConfiguration.getProperty(SECURITY_PASSWORD_REQUIREMENTS_VALIDATION_REGEX);
    }

    @Override
    public User getUserByGitName(String gitName) throws ServiceLayerException, UserNotFoundException {
        User user =  userDao.getUserByGitName(gitName);
        if (Objects.isNull(user)) {
            logger.info("Git user " + gitName + " not found in DB.");
            user = getUserByIdOrUsername(-1, GIT_REPO_USER_USERNAME);
        }
        return user;
    }

    @Override
    public Map<String, Map<String, String>> getUserProperties(String siteId) throws ServiceLayerException {
        var actualSiteId = StringUtils.isEmpty(siteId)? "studio_root" : siteId; //TODO: Get from config
        var dbSiteId = siteService.getSite(actualSiteId).getId();
        // TODO: Refactor to get the user from Spring Security
        var username = securityService.getCurrentUser();
        try {
            var user = getUserByIdOrUsername(0, username);
            // TODO: Properly support multiple sites when needed
            // TODO: How to handle empty as a key?
            return singletonMap(siteId, userDao.getUserProperties(user.getId(), dbSiteId).stream().collect(toMap(UserProperty::getKey, UserProperty::getValue)));
        } catch (UserNotFoundException e) {
            // This should never happen if the user is logged in...
            return null;
        }
    }

    @Override
    public Map<String, String> updateUserProperties(String siteId, Map<String, String> propertiesToUpdate) throws ServiceLayerException {
        var actualSiteId = StringUtils.isEmpty(siteId)? "studio_root" : siteId; //TODO: Get from config
        var dbSiteId = siteService.getSite(actualSiteId).getId();
        // TODO: Refactor to get the user from Spring Security
        var username = securityService.getCurrentUser();
        try {
            var user = getUserByIdOrUsername(0, username);
            userDao.updateUserProperties(user.getId(), dbSiteId, propertiesToUpdate);

            return userDao.getUserProperties(user.getId(), dbSiteId).stream().collect(toMap(UserProperty::getKey, UserProperty::getValue));
        } catch (UserNotFoundException e) {
            // This should never happen if the user is logged in...
            return null;
        }
    }

    @Override
    public Map<String, String> deleteUserProperties(String siteId, List<String> propertiesToDelete) throws ServiceLayerException {
        var actualSiteId = StringUtils.isEmpty(siteId)? "studio_root" : siteId; //TODO: Get from config
        var dbSiteId = siteService.getSite(actualSiteId).getId();
        // TODO: Refactor to get the user from Spring Security
        var username = securityService.getCurrentUser();
        try {
            var user = getUserByIdOrUsername(0, username);
            userDao.deleteUserProperties(user.getId(), dbSiteId, propertiesToDelete);

            return userDao.getUserProperties(user.getId(), dbSiteId).stream().collect(toMap(UserProperty::getKey, UserProperty::getValue));
        } catch (UserNotFoundException e) {
            // This should never happen if the user is logged in...
            return null;
        }
    }

}
