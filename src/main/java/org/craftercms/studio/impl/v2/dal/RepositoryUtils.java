/*
 * Copyright (C) 2007-2018 Crafter Software Corporation. All rights reserved.
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

package org.craftercms.studio.impl.v2.dal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.craftercms.studio.api.v1.log.Logger;
import org.craftercms.studio.api.v1.log.LoggerFactory;
import org.craftercms.studio.api.v1.util.StudioConfiguration;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import static org.craftercms.studio.api.v1.constant.StudioConstants.FILE_SEPARATOR;
import static org.craftercms.studio.impl.v1.repository.git.GitContentRepositoryConstants.GIT_ROOT;

public abstract class RepositoryUtils {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryUtils.class);

    public static void writeToRepo(StudioConfiguration studioConfiguration, String site, String path,
                                   InputStream content, String message) {
        try {
            Path repositoryPath = getRepositoryPath(studioConfiguration, site);
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repo = builder
                .setGitDir(repositoryPath.toFile())
                .readEnvironment()
                .findGitDir()
                .build();

            // Create basic file
            File file = new File(repo.getDirectory().getParent(), path);

            String gitPath = getGitPath(path);

            // Create parent folders
            File folder = file.getParentFile();
            if (folder != null) {
                if (!folder.exists()) {
                    folder.mkdirs();
                }
            }

            // Create the file if it doesn't exist already
            if (!file.exists()) {
                try {
                    if (!file.createNewFile()) {
                        logger.error("error creating file: site: " + site + " path: " + path);
                    }
                } catch (IOException e) {
                    logger.error("error creating file: site: " + site + " path: " + path, e);
                }
            }

            // Write the bits
            try (FileChannel outChannel = new FileOutputStream(file.getPath()).getChannel()) {
                logger.debug("created the file output channel");
                ReadableByteChannel inChannel = Channels.newChannel(content);
                logger.debug("created the file input channel");
                long amount = 1024 * 1024; // 1MB at a time
                long count;
                long offset = 0;
                while ((count = outChannel.transferFrom(inChannel, offset, amount)) > 0) {
                    logger.debug("writing the bits: offset = " + offset + " count: " + count);
                    offset += count;
                }
            }


            // Add the file to git
            try (Git git = new Git(repo)) {
                git.add().addFilepattern(gitPath).call();

                Status status = git.status().addPath(gitPath).call();

                // TODO: SJ: Below needs more thought and refactoring to detect issues with git repo and report them
                if (status.hasUncommittedChanges() || !status.isClean()) {
                    RevCommit commit;
                    commit = git.commit().setOnly(gitPath).setMessage(message).call();
                    String commitId = commit.getName();
                }

                git.close();
            } catch (GitAPIException e) {
                logger.error("error adding file to git: site: " + site + " path: " + path, e);
            }

        } catch (IOException e) {
            logger.error("error writing file: site: " + site + " path: " + path, e);
        }
    }

    private static String getGitPath(String path) {
        Path gitPath = Paths.get(path);
        gitPath = gitPath.normalize();
        try {
            gitPath = Paths.get(FILE_SEPARATOR).relativize(gitPath);
        } catch (IllegalArgumentException e) {
            logger.debug("Path: " + path + " is already relative path.");
        }
        if (StringUtils.isEmpty(gitPath.toString())) {
            return ".";
        }
        String toRet = gitPath.toString();
        toRet = FilenameUtils.separatorsToUnix(toRet);
        return toRet;
    }

    private static Path getRepositoryPath(StudioConfiguration studioConfiguration, String site) {
        if(StringUtils.isEmpty(site)) {
            return Paths.get(
                studioConfiguration.getProperty(StudioConfiguration.REPO_BASE_PATH),
                studioConfiguration.getProperty(StudioConfiguration.GLOBAL_REPO_PATH),
                GIT_ROOT
            );
        } else {
            return Paths.get(
                studioConfiguration.getProperty(StudioConfiguration.REPO_BASE_PATH),
                studioConfiguration.getProperty(StudioConfiguration.SITES_REPOS_PATH),
                site,
                studioConfiguration.getProperty(StudioConfiguration.SANDBOX_PATH),
                GIT_ROOT
            );
        }
    }

}