
name = "runtime-services-analytics"
version = "1.0.0"
release = currentBuild.startTimeInMillis

@Field MANAGED_FILE_MAVEN_SETTINGS = 'mulesoft-global-mvn-settings'

node() {
    stage('Checkout') {
        sh 'sudo rm -rf ./*'
        checkout scm
    }

    stage('Integration Test') {
        steps {
            mavenBuild "verify"
            mavenBuild "verify sonar:sonar"
        }
    }

    withCredentials([
            [$class: 'UsernamePasswordMultiBinding', credentialsId: 'sonarqube-official', passwordVariable: 'SONAR_SERVER_TOKEN', usernameVariable: 'SONAR_SERVER_URL'],
    ]) {
        stage('Build') {
            timeout(time: 180, unit: 'MINUTES') {
                def maven32 = docker.build("osv2-docker", "-f Dockerfile.build .")
                configFileProvider([configFile(fileId: MANAGED_FILE_MAVEN_SETTINGS, variable: 'MAVEN_GLOBAL_SETTINGS_FILE')]) {
                    maven32.inside("-v ${MAVEN_GLOBAL_SETTINGS_FILE}:/opt/config/global/settings.xml -v ${HOME}:/root -v ${PWD}:/opt/sources -v /tmp:/tmp --privileged=true -u 0 -w /opt/sources") {
                        ansiColor('xterm') {
                            try {
                                sh 'mvn -s /opt/config/global/settings.xml -U -Pit clean install deploy sonar:sonar -Prpm -Drelease=' + release + ' -Danypoint.http.default.timeout=60000 -Dmaven.repo.local=/tmp -Dsonar.host.url=${SONAR_SERVER_URL}  -Dsonar.login=${SONAR_SERVER_TOKEN}'
                            } catch (Exception e) {
                                if (currentBuild.result == 'UNSTABLE')
                                    currentBuild.result = 'FAILURE'
                                throw e
                            }
                        }
                    }
                }
            }
        }
    }
}
