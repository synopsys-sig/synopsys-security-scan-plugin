# synopsys-security-scan-plugin

## Introduction

TODO Describe what your plugin does here

## Getting started

To run this application, you can use either of the following commands:


```
./mvnw hpi:run
```

or

```
mvn hpi:run
```

### Setting up dev-test environment in docker

*  Make sure a `temp-jenkins` directory exists in your home directory.


* Run the following command to export your plugin into `hpi` format:
```
./mvnw hpi:hpi
```

or

```
mvn hpi:hpi
```

* Spin up the jenkins instance with the following command:

```
docker-compose up
```
or if you prefer detach mode
```
docker-compose up -d
```


**Make sure 8080 port is free**

## Synopsys Security Scan - Black Duck
Run the Black Duck Scan in jenkins pipeline - 
```
stage("synopsys-security-scan") {
    steps {
        synopsys_scan blackduck_url: "${env.BLACKDUCK_URL}", blackduck_api_token: "${env.BLACKDUCK_TOKEN}", blackduck_scan_full: true
    }
}
```

## Issues

TODO Decide where you're going to host your issues, the default is Jenkins JIRA, but you can also enable GitHub issues,
If you use GitHub issues there's no need for this section; else add the following line:

Report issues and enhancements in the [Jenkins issue tracker](https://issues.jenkins.io/).

## Contributing

TODO review the default [CONTRIBUTING](https://github.com/jenkinsci/.github/blob/master/CONTRIBUTING.md) file and make sure it is appropriate for your plugin, if not then add your own one adapted from the base file

Refer to our [contribution guidelines](https://github.com/jenkinsci/.github/blob/master/CONTRIBUTING.md)

## LICENSE

Licensed under MIT, see [LICENSE](LICENSE.md)

