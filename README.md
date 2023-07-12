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

## Users Guide

To use the plugin and invoke it as a pipeline step, follow these instructions:

### Synopsys Security Scan - Black Duck

- Add the following code snippet to your `Jenkinsfile` in your project root directory that you want to scan:

```groovy
stage("Security Scan") {
    steps {
        synopsys_scan blackduck_url: "${env.BLACKDUCK_URL}", blackduck_api_token: "${env.BLACKDUCK_TOKEN}"
    }
}
```
Make sure to provide the required parameters such as `blackduck_url` and `blackduck_api_token` with the appropriate values.
- Create a Multibranch Pipeline Job in your Jenkins instance
- Add Bitbucket as the branch source in the job configuration
- Scan Multibranch Pipeline

**Note:** Make sure you have **_Bitbucket_** and **_Pipeline_** plugin installed in you Jenkins instance to configure the multibranch pipeline job.

### Black Duck Parameters

| Input Parameter                     | Description                                                                                                                                                                                                                                                | Mandatory / Optional |
|-------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------|
| `blackduck_url`                     | URL for Black Duck server. The URL can be configured in Jenkins **Global Configuration** or can be passed as **Environment Variable**. <br> Example: `blackduck_url: "${env.BLACKDUCK_URL}"` </br>                                                         | Mandatory     |
| `blackduck_apiToken`                | API token for Black Duck. The token can be configured in Jenkins **Global Configuration** or can be passed as **Environment Variable**. <br> Example: `blackduck_token: "${env.BLACKDUCK_TOKEN}"` </br>                                                    | Mandatory     |
| `blackduck_install_directory`       | Directory path to install Black Duck                                                                                                                                                                                                                       | Optional     |
| `blackduck_scan_full`               | Specifies whether full scan is required or not. By default, pushes will initiate a full "intelligent" scan and pull requests will initiate a rapid scan. <br> Supported values: true or false </br>                                                        | Optional     |
| `blackduck_scan_failure_severities` | Scan failure severities of Black Duck. <br> Supported values: ALL, NONE, BLOCKER, CRITICAL, MAJOR, MINOR, OK, TRIVIAL, UNSPECIFIED. <br> Example: `blackduck_scan_failure_severities: "BLOCKER, TRIVIAL"` </br>                                            | Optional |
| `blackduck_automation_prcomment`    | Flag to enable automatic pull request comment based on Black Duck scan result. <br> Supported values: true or false. <br> Example: `blackduck_automation_prcomment: true` </br>                                                                            | Optional    |
| `blackduck_automation_fixpr`        | Flag to enable automatic creation for fix pull request when Black Duck vunerabilities reported. <br> By default fix pull request creation will be disabled <br> Supported values: true or false </br>                                                      | Optional    |
| `bitbucket_token`                   | It is mandatory to pass bitbucket_token parameter with required permissions. The token can be configured in Jenkins **Global Configuration** or can be passed as **Environment Variable**. <br> Example: `bitbucket_token: "${env.BITBUCKET_TOKEN}"` </br> | Mandatory if blackduck_automation_fixpr or blackduck_automation_prcomment is set true |

### Additional Parameters
|Input Parameter |Description                              |
|-----------------|------------------------------------------|
|`synopsys_bridge_path`| Provide a path, where you want to configure or already configured Synopsys Bridge. [Note - If you don't provide any path, then by default configuration path will be considered as - $HOME/synopsys-bridge]. If the configured Synopsys Bridge is not the latest one, latest Synopsys Bridge version will be downloaded          |
| `bridge_download_url`      | Provide URL to bridge zip file. If provided, Synopsys Bridge will be automatically downloaded and configured in the provided bridge- or default- path. [Note - As per current behavior, when this value is provided, the bridge_path or default path will be cleaned first then download and configured all the time]               |
|`bridge_download_version`| Provide bridge version. If provided, the specified version of Synopsys Bridge will be downloaded and configured.              |

Note - If **bridge_download_version** or **bridge_download_url** is not provided, the plugin will download and configure the latest version of Bridge


# Synopsys Bridge Setup

The latest version of the Synopsys Bridge is available at: [Synopsys Bridge](https://sig-repo.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/synopsys-bridge/)

The latest version of Synopsys Bridge will be downloaded by default if user doesn't provide the specific released version in the pipeline parameter.

## Setting Up Synopsys Bridge Manually

If you are unable to download the Synopsys Bridge from our internet-hosted repository or have been directed by support or services to use a custom version of the Synopsys Bridge, you can either specify a custom URL or pre-configure your GitHub runner to include the Synopsys Bridge. In this latter case, you would specify the `synopsys_bridge_path` parameter to specify the location of the directory in which the Synopsys Bridge is pre-installed.
