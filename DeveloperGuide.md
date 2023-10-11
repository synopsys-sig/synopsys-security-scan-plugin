## Developers Guide

To work with the project locally and run it with a Jenkins server, follow these steps:

1. Run Jenkins server locally with the plugin being deployed:
```
./mvnw hpi:run
```
> Enter https://localhost:8080/jenkins in your browser

**Note:** Make sure that **port 8080** is free on your machine, and you have
**_Pipeline_** plugin installed in you Jenkins server to configure the multibranch pipeline job.

2. Building the project will generate the plugin `hpi` file:
```
./mvnw clean install -Dspotbugs.skip=true
```

The generated plugin `hpi` file can be found in the `target` folder of your project directory.

### Setting up dev-test environment in docker

*  Make sure a `temp-jenkins` directory exists in your home directory.


* Run the following command to export your plugin into `hpi` format:
```
./mvnw clean install -Dspotbugs.skip=true
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