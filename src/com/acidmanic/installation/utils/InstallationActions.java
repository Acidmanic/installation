/*
 * Copyright (C) 2018 Mani Moayedi (acidmanic.moayedi@gmail.com)
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
package com.acidmanic.installation.utils;

import com.acidmanic.installation.environment.EnvironmentalInfo;
import com.acidmanic.installation.models.Scription;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mani Moayedi (acidmanic.moayedi@gmail.com)
 */
public class InstallationActions {

    private final EnvironmentalInfo info;

    public InstallationActions(EnvironmentalInfo info) {
        this.info = info;
    }

    public String registerScript(Scription input, String fileExtension) {
        File file = info.getExecutableBinariesDirectory().toPath().resolve(input.getName() + fileExtension).toFile();
        try {
            if (file.exists()) {
                file.delete();
            }
            Files.write(file.toPath(),
                    input.getScript().getBytes(info.getDeploymentMetadata().getDefaultCharset()),
                    StandardOpenOption.CREATE);
            file.setExecutable(true, false);
            return file.getAbsolutePath();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

    public void installContent(String name) {
        installContent(name, new ArrayList<>());
    }

    public void installContent(String name, List<String> copiedFiles) {
        Path here = info.getDeploymentMetadata().getExecutionJarFile()
                .toPath().getParent().normalize();
        File src = here.resolve(name).toFile();
        File dstContent = info.getInstallationDirectory()
                .toPath().resolve(name).toFile();
        new Copier().tryDeleteAny(dstContent);
        new Copier().copyAny(src,
                info.getInstallationDirectory(), copiedFiles);

    }

}
