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
package com.acidmanic.installation;

import com.acidmanic.installation.models.DeploymentMetadata;
import com.acidmanic.installation.tasks.InstallationTask;
import com.acidmanic.installation.environment.EnvironmentalInfo;
import com.acidmanic.installation.environment.EnvironmentalInfoProvider;
import com.acidmanic.installation.utils.Os;
import com.acidmanic.installation.environment.UnixEnvironmentalInfoProvider;
import com.acidmanic.installation.environment.WindowsEnvironmentalInfoProvider;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mani Moayedi (acidmanic.moayedi@gmail.com)
 */
public abstract class InstallerBase {

    private DeploymentMetadata metadata;
    private EnvironmentalInfo environmentalInfo;

    private final List<InstallationTask> tasks;
    private final List<Boolean> results;

    protected abstract void setupMetaData(DeploymentMetadata metadata);

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public InstallerBase() {
        this.tasks = new ArrayList<>();
        this.results = new ArrayList<>();
        introduceTasks(tasks);
    }

    public void install() {
        this.metadata = new DeploymentMetadata(getExec());
        setupMetaData(metadata);
        environmentalInfo = getEnvironmentalInfoProvider().getInfo(metadata);

//        System.out.println("Jar:");
//        System.out.println(this.metadata.getExecutionJarFile().getAbsolutePath());
//        System.out.println("Applications:");
//        System.out.println(this.environmentalInfo.getApplicationsDirectory().getAbsolutePath());
//        System.out.println("Binaries:");
//        System.out.println(this.environmentalInfo.getExecutableBinariesDirectory().getAbsolutePath());
//        System.out.println("Installation:");
//        System.out.println(this.environmentalInfo.getInstallationDirectory().getAbsolutePath());
//        System.out.println("Current Directory:");
//        System.out.println(this.environmentalInfo.getCurrentDirectory().getAbsolutePath());
        runTasks();
    }

    private void runTasks() {
        Object input = null;
        for (InstallationTask task : this.tasks) {
            task.setEnvironmentalInfo(environmentalInfo);
            boolean result = task.execute(input);
            this.results.add(result);
            System.out.println(" " + (result ? "✅    Done" : "⛔    Failed") + ".");
            if (!result && !task.isIgnorable()) {
                System.out.println("ERROR: Failed. exiting the installation.");
//                break;
            }
            input = task.getResult();
        }
    }

    private EnvironmentalInfoProvider getEnvironmentalInfoProvider() {
        Os os = new Os();
        if (os.isWindows()) {
            return new WindowsEnvironmentalInfoProvider();
        } else if (os.isUnix()) {
            return new UnixEnvironmentalInfoProvider();
        }
        return (DeploymentMetadata metadata1) -> new EnvironmentalInfo(metadata1);
    }

    protected abstract void introduceTasks(List<InstallationTask> tasks);

    public List<Boolean> getResults() {
        ArrayList<Boolean> ret = new ArrayList<>();
        ret.addAll(results);
        return ret;
    }

    private File getExec() {
        try {
            return new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException ex) {
            return new File(".");
        }
    }

}
