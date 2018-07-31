#!/usr/bin/env groovy

/*
 * This Jenkinsfile is intended to run on https://jenkins[.dev].build.msap.io and may fail anywhere else.
 * It makes assumptions about plugins being installed, labels mapping to nodes that can build what is needed, etc.
 */

pipeline {

    options {
        ansiColor('xterm')
        timeout(time: 1, unit: 'HOURS')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    environment {
        MAVEN_OPTS = '-Dmaven.repo.local=/tmp -XX:+HeapDumpOnOutOfMemoryError -Xms128m -Xmx2048m'

        // Jenkins stored resources
        MANAGED_FILE_MAVEN_SETTINGS = 'mulesoft-global-mvn-settings'
        DEVDOCKER = credentials('docker-registry')
        QUAY = credentials('docker-automation-quay.build.msap.io-write')
        SONAR = credentials('sonarqube-official')
    }

    agent {
        dockerfile {
            /**
             * Sadly we have a old jenkins declarative pipeline docker plugin so we can't customize the name of
             * the Dockerfile to Dockerfile.build, this is being worked on in CHDEVOPS-7313
             */
            /**
             * We are mounting /var/run/docker.sock in order to be able to reuse the host docker server from the
             * docker image as we need to build our own images
             */
            /**
             * Apparently we have an old docker version in our Jenkins workers due to a but in that older version
             * volumes are mounted as root (this is the case for target) so we need to run the build as root
             */
            args '-v /var/run/docker.sock:/var/run/docker.sock -v /tmp:/tmp -v $HOME/.m2:/root/.m2 -u root:root'
        }
    }

    stages {
        /**
         * Sadly we have a old jenkins declarative pipeline docker plugin this is being worked on in CHDEVOPS-7313
         * With the newer version we should be able to compile for java8 and java9 in parallel, making sure we
         * are able to shift to java9 whenever we want
         */
        stage('Build') {
            steps {
                mavenBuild "clean install -DskipTests clm:evaluate"
            }
        }

        /**
         * Sadly we have a old jenkins declarative pipeline docker plugin this is being worked on in CHDEVOPS-7313
         * With the newer version we should be able to execute test and integration tests in parallel
         */
        stage('Test') {
            steps {
                mavenBuild "verify"
                mavenBuild "sonar:sonar -Dsonar.host.url=${env.SONAR_USR} -Dsonar.login=${env.SONAR_PSW}"
            }
        }

        stage('Deploy') {
            steps {
                mavenBuild "deploy"
            }
        }
    }
}

/**
 * Executes a maven target with all the credentials and configuration files set up
 * we could do this only once but as we used a shared docker (the one from the node)
 * and there is no declarative stanza for managed files yet we keep it like this
 */
def mavenBuild(String extraArguments) {
    /**
     * withMaven behaves incorrectly in docker declarative pipeline, it doesnt seem to be fetching the servers
     * declared in the UI using the config file directly instead
     */
    configFileProvider([configFile(fileId: MANAGED_FILE_MAVEN_SETTINGS, variable: 'MAVEN_GLOBAL_SETINGS_FILE')]) {
        /**
         * withDockerRegistry behaves incorrectly in docker declarative pipeline, it seems to be fetching the home
         * directory from the nexus worker instead of the docker agent
         */
        sh "docker login -u '$DEVDOCKER_USR' -p \'$DEVDOCKER_PSW\' 'https://devdocker.mulesoft.com:18078'"
        sh "docker login -u '$QUAY_USR' -p \'$QUAY_PSW\' 'https://quay.build.msap.io'"
        sh 'mvn -s $MAVEN_GLOBAL_SETINGS_FILE -U -Dclm.applicationId=anypointmq-runtime-analytics ' +
                '-Psonar -Drelease=' + ' -Danypoint.http.default.timeout=60000 ' + extraArguments
        sh "docker logout " + 'https://devdocker.mulesoft.com:18078'
        sh "docker logout " + 'https://quay.build.msap.io'
    }
}
