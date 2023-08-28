# Synopsys Security Scan Plugin

This repository contains a Jenkins plugin implemented as a Gradle project. The plugin provides functionality for performing Synopsys Security Scan with Black Duck. This README.md file serves as a guide for developers and users of the plugin.

# Quick Start for the Security Scan Plugin

## Developers Guide

To work with the project locally and run it with a Jenkins server, follow these steps:

1. Run Jenkins server locally with the plugin being deployed:
```
./gradlew server
```
> Enter https://localhost:8080 in your browser

**Note:** Make sure that **port 8080** is free on your machine, and you have 
**_Bitbucket_** and **_Pipeline_** plugin installed in you Jenkins server to configure the multibranch pipeline job.

2. Building the project will generate the plugin `hpi` file:
```
./gradlew clean build
```

The generated plugin `hpi` file can be found in the `build/libs` folder of your project directory.

### Setting up dev-test environment in docker

*  Make sure a `temp-jenkins` directory exists in your home directory.


* Run the following command to export your plugin into `hpi` format:
```
./gradlew clean build
```

* Spin up the jenkins instance with the following command:

```
docker-compose up
```
or if you prefer detach mode
```
docker-compose up -d
```

3. Install the plugin `hpi` file in your Jenkins instance:
>- Go to your Jenkins instance.
>- Navigate to **Manage Jenkins** > **Manage Plugins** > **Advanced Settings**.
>- In the **Deploy Plugin** section, click **Choose File**.
>- Select the generated `hpi` file and click **Deploy**.
>- **Restart** your Jenkins instance.

**Note:** `xcode-select` may need to be installed in **Mac** if any kind of error like - `git init` failed or developer path related error is faced while running job from jenkins instance.

Command to install `xcode-select` in Mac:
```
xcode-select --install
```

## Users Guide

To use the plugin and invoke it as a pipeline step, follow these instructions:

### Synopsys Security Scan - Black Duck

1. Add the following code snippet to your `Jenkinsfile` in your project root directory that you want to scan:

```groovy
stage("Security Scan") {
    when {
        anyOf {
            environment name: 'BRANCH_IS_PRIMARY', value: 'true'
            changeRequest()
        }
    }
    steps {
        script {
            def blackDuckScanFull
            def blackDuckPrComment

            if (env.BRANCH_IS_PRIMARY) {
                blackDuckScanFull = true
            } else if (env.CHANGE_ID != null && env.CHANGE_TARGET != null) {
                blackDuckScanFull = false
                blackDuckPrComment = true
            }

            synopsys_scan scan_type: "BLACKDUCK", blackduck_url: "${env.BLACKDUCK_URL}", blackduck_api_token: "${env.BLACKDUCK_TOKEN}", blackduck_scan_full: "${blackDuckScanFull}", blackduck_automation_prcomment: "${blackDuckPrComment}"
        }
    }
}
```
Make sure to provide the required parameters such as `blackduck_url` and `blackduck_api_token` with the appropriate values.

Or the values are configure in Jenkins Global Configuration, then the below example can be used - 
```groovy
synopsys_scan blackduck_scan_full: "${blackDuckScanFull}", blackduck_automation_prcomment: "${blackDuckPrComment}"
```
Or a very basic template - 
```groovy
synopsys_scan()
```

2. Create a Multibranch Pipeline Job in your Jenkins instance
3. Add Bitbucket as the branch source in the job configuration
4. Scan Multibranch Pipeline

**Note:** Make sure you have **_Bitbucket_** and **_Pipeline_** plugin installed in you Jenkins instance to configure the multibranch pipeline job.

### Jenkins Global Configuration Inputs
- Scan Type
- Black Duck Server URL
- Black Duck API Token
- Synopsys Bridge Download URL (internal artifactory URL can be provided here)
- Bitbucket Token (for FixPr and PrComment)

If these values are configured in Jenkins Global Configuration, then it is not necessary to pass these values as pipeline input parameter.
Hence, if these values are set both from Jenkins Global Configuration and pipeline input parameter, then pipeline input values will get preference.

### Black Duck Parameters

| Input Parameter                     | Description                                                                                                                                                                                                                               | Mandatory / Optional                                        |
|-------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------|
| `scan_type`                         | Provide the scan type that you want to execute. The scan type can also be configured in Jenkins **Global Configuration**. <br> Supported values: **BLACKDUCK**, **COVERITY** and **POLARIS** <br> Example: `scan_type: "BLACKDUCK"` </br> | Optional (Default: **BLACKDUCK**)                           |
| `blackduck_url`                     | URL for Black Duck server. The URL can also be configured in Jenkins **Global Configuration** or can be passed as **Environment Variable**. <br> Example: `blackduck_url: "${env.BLACKDUCK_URL}"` </br>                                   | Mandatory if not configured in Jenkins Global Configuration |
| `blackduck_api_token`               | API token for Black Duck. The token can also be configured in Jenkins **Global Configuration** or can be passed as **Environment Variable**. <br> Example: `blackduck_token: "${env.BLACKDUCK_TOKEN}"` </br>                              | Mandatory if not configured in Jenkins Global Configuration |
| `blackduck_install_directory`       | Directory path to install Black Duck                                                                                                                                                                                                      | Optional                                                    |
| `blackduck_scan_full`               | Specifies whether full scan is required or not. By default, pushes will initiate a full "intelligent" scan and pull requests will initiate a rapid scan. <br> Supported values: true or false </br>                                       | Optional (Default: **false**)                               |
| `blackduck_scan_failure_severities` | Scan failure severities of Black Duck. <br> Supported values: ALL, NONE, BLOCKER, CRITICAL, MAJOR, MINOR, OK, TRIVIAL, UNSPECIFIED. <br> Example: `blackduck_scan_failure_severities: "BLOCKER, TRIVIAL"` </br>                           | Optional                               |
| `blackduck_automation_prcomment`    | Flag to enable automatic pull request comment based on Black Duck scan result. <br> Supported values: true or false. <br> Example: `blackduck_automation_prcomment: true` </br>                                                           | Optional (Default: **false**)                               |
| `blackduck_automation_fixpr`        | Flag to enable automatic creation for fix pull request when Black Duck vunerabilities reported. <br> By default fix pull request creation will be disabled <br> Supported values: true or false </br>                                     | Optional (Default: **false**)                                                    |
| `bitbucket_token`                   | The token can be configured in Jenkins **Global Configuration** or can be passed as **Environment Variable**. This is required if fixpr or prcomment is set true. <br> Example: `bitbucket_token: "${env.BITBUCKET_TOKEN}"` </br>         | Optional                                                    |

### Additional Parameters
| Input Parameter           | Description                                                                                                                                                                                                                                                                                                                  |
|---------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `synopsys_bridge_path`    | Provide a path, where you want to configure or already configured Synopsys Bridge. <br> [Note - If you don't provide any path, then by default configuration path will be considered as - $HOME/synopsys-bridge]. If the configured Synopsys Bridge is not the latest one, latest Synopsys Bridge version will be downloaded |
| `bridge_download_url`     | Provide URL to bridge zip file. If provided, Synopsys Bridge will be automatically downloaded and configured in the provided bridge- or default- path. <br> [Note - As per current behavior, when this value is provided, the bridge_path or default path will be cleaned first then download and configured all the time]   |
| `bridge_download_version` | Provide bridge version. If provided, the specified version of Synopsys Bridge will be downloaded and configured.                                                                                                                                                                                                             |
| `include_diagnostics`     | It this is set **true** then the bridge diagnostics will be uploaded in Jenkins Archive Artifact.                                                                                                                                                                                                                            |

Note - If **bridge_download_version** or **bridge_download_url** is not provided, the plugin will download and configure the latest version of Bridge


# Synopsys Bridge Setup

The latest version of the Synopsys Bridge is available at: [Synopsys Bridge](https://sig-repo.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/synopsys-bridge/)

The latest version of Synopsys Bridge will be downloaded by default if user doesn't provide the specific released version in the pipeline parameter.

## Setting Up Synopsys Bridge Manually

If you are unable to download the Synopsys Bridge from our internet-hosted repository or have been directed by support or services to use a custom version of the Synopsys Bridge, you can either specify a custom URL or pre-configure your GitHub runner to include the Synopsys Bridge. In this latter case, you would specify the `synopsys_bridge_path` parameter to specify the location of the directory in which the Synopsys Bridge is pre-installed.
