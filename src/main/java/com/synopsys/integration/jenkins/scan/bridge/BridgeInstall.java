package com.synopsys.integration.jenkins.scan.bridge;

import hudson.FilePath;
import hudson.model.TaskListener;

public class BridgeInstall {
    private final TaskListener listener;
    private final FilePath workspace;

    public BridgeInstall(TaskListener listener, FilePath workspace) {
        this.listener = listener;
        this.workspace = workspace;
    }

   /* public void installSynopsysBridge(FilePath bridgeZipPath, FilePath bridgeInstallationPath) {
        try {
            listener.getLogger().println("Method:installSynopsysBridge() Bridge zip path : " + bridgeZipPath);
            listener.getLogger().println("Method:installSynopsysBridge() Bridge installation path : " + bridgeInstallationPath);
            bridgeZipPath.unzip(bridgeInstallationPath);

            //Have to call delete *explicitly*.
            bridgeZipPath.delete();

            listener.getLogger().printf("Bridge zip path: %s and bridge installation path: %s %n",
                    bridgeZipPath.getRemote(), bridgeInstallationPath.getRemote());
        } catch (Exception e) {
            listener.getLogger().println("Synopsys bridge unzipping failed: " + e.getMessage());
            e.printStackTrace(listener.getLogger());
        }
    }*/

    public void installSynopsysBridge(FilePath bridgeZipPath, FilePath bridgeInstallationPath) {
        try {
            // Log the paths for debugging purposes
            listener.getLogger().println("Method: installSynopsysBridge() Bridge zip path: " + bridgeZipPath);
            listener.getLogger().println("Method: installSynopsysBridge() Bridge installation path: " + bridgeInstallationPath);

            // Create a temporary directory in the Jenkins workspace to extract the contents of the zip
//            FilePath tempDir = bridgeZipPath.sibling( "bridge_unzipped");
//            tempDir.mkdirs();

            listener.getLogger().println("Method: installSynopsysBridge() Temp unzipped Path: " + workspace.getRemote());


            // Unzip the bridge zip file to the temporary directory
            bridgeZipPath.unzip(workspace);

            // Copy the extracted contents to the bridgeInstallationPath
//            tempDir.copyRecursiveTo(bridgeInstallationPath);

            // Delete the temporary directory and the zip file
//            tempDir.deleteRecursive();
            bridgeZipPath.delete();

            // Log the paths after installation
            listener.getLogger().printf("Bridge zip path: %s and bridge installation path: %s%n",
                    bridgeZipPath.getRemote(), bridgeInstallationPath.getRemote());
        } catch (Exception e) {
            listener.getLogger().println("Synopsys bridge unzipping failed: " + e.getMessage());
            e.printStackTrace(listener.getLogger());
        }
    }

}
