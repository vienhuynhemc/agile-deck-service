/*
SLACK_CHANNEL=redbull
CHECKOUT_BRANCH=develop
RELEASE_BRANCH=release

DOCKER_REGISTRY_URL=aavn-registry.axonactive.vn.local
DOCKER_CREDENTIAL_ID=ccad9d2d-0400-4a36-9238-a49a70cf98c7
PUBLISH_PORT=8091
IMAGE_NAME=agile-tools/agile-deck-service
CONTAINER_NAME=agile-deck-service-staging
NETWORK_NAME=agile-deck-network

SERVER_IP=192.168.70.91
SERVER_CREDENTIAL_ID=redbull-control-server
GIT_URI=agile-tools/agile-deck/agile-deck-service

CORS_ORIGINS=http://staging.agiledeck.axonactive.vn.local

DB_HOST=staging.agiledeck.axonactive.vn.local
DB_NAME=agile-deck-db
DB_USER=admin
DB_PASS=Aavn123
DB_PORT=5433
DB_GENERATION=update
 */


/* This method will notify when the job run fail at any stage */
def notifyFailedToSlack() {
    slackSend (channel: SLACK_CHANNEL, color: '#FF0000', message: "Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL}) build failed")
}

/* This method will notify when the job run sucessfully */
def notifySuccessToSlack(def releaseStagingBranchName) {
    slackSend (channel: SLACK_CHANNEL, color: '#32CD32', message: "Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL}) build successfully. Branch ${releaseStagingBranchName} have been created.")
}

/* This method will notify when the job started */
def notifyBeginBuildToSlack() {
    slackSend (channel: SLACK_CHANNEL, color: '#0096d6', message: "Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' started")
}

def getGitBranchName() {
    return scm.branches[0].name
}

try{
    node('master'){

        /* Stage start, will notify to slack channel of team that run job */
        stage('Start'){
            notifyBeginBuildToSlack()
            sh "rm -r ./* || true"
            sh "rm -r ./.* || true"
        }

        /* Stage checkout, will get the source code from git server */
        stage('Checkout'){
            checkout scm
            sh "git checkout ${CHECKOUT_BRANCH}"
//            sh 'git pull'
            currentPomVersion = readMavenPom().getVersion()// Get current pom version after checkout the project

        }

        /*This stage will create a new Release Branch */
        stage('Create Release Branch'){
            currentBranchName = getGitBranchName()
            releaseStagingBranchName = "${RELEASE_BRANCH}/" + currentPomVersion.replace("-SNAPSHOT","")

            sh "git branch -D ${releaseStagingBranchName} || true"
            sh "git branch ${releaseStagingBranchName}"
            sh "git checkout ${releaseStagingBranchName}"
        }

        /* Stage build, build the project to generate war file and wildfly image */
        stage('Build'){
            withMaven( maven: 'MAVEN 3.6' ) {
                sh "mvn clean package -Pnative -Dquarkus.native.container-build=true"
            }
        }

        /* Stage check sonar, using sonar to scan the project */
        stage('Check sonar') {
            // Using sonar to scan the proejct to check coverage, bugs...
            def scannerHome = tool 'SonarQubeScanner'
            withSonarQubeEnv('SonarQube') {
                sh "${scannerHome}/bin/sonar-scanner -Dsonar.language=java \
					-Dsonar.projectName=${env.JOB_NAME} \
					-Dsonar.projectKey=${env.JOB_NAME} \
					-Dsonar.tests=src/test/java \
					-Dsonar.sources=src/main/java \
					-Dsonar.java.libraries=/var/jenkins_home/.m2/repository/org/projectlombok/lombok/1.18.2/lombok-1.18.2.jar \
					-Dsonar.java.binaries=. \
					-Dsonar.java.coveragePlugin=jacoco"
            }

            // Get report from sonar
            sleep(10)
            timeout(time:15, unit:'MINUTES'){
                qg = waitForQualityGate()
                if (qg.status != 'OK'){
                    currentBuild.result = 'FAILURE'
                    error "Pipeline aborted due to quality gate failure: ${qg.status}"
                }
            }
        }

        /* Stage build docker image, build the project to image */
        stage('Create image') {
            sh "docker build -f src/main/docker/Dockerfile.native -t ${IMAGE_NAME}:${currentPomVersion.replace("-SNAPSHOT","")} ."
        }

        /* Stage push image to aavn-registry */
        stage('Push image to docker registry') {
            docker.withRegistry("http://${DOCKER_REGISTRY_URL}", "${DOCKER_CREDENTIAL_ID}") {
                image = docker.image("${IMAGE_NAME}:${currentPomVersion.replace('-SNAPSHOT','')}")
                image.push()
            }
        }

        stage('Remove unused images') {
            sh "docker rmi ${IMAGE_NAME}:latest || true"
            sh "docker rmi ${IMAGE_NAME}:${currentPomVersion.replace("-SNAPSHOT","")} || true"
            sh "docker rmi ${DOCKER_REGISTRY_URL}/${IMAGE_NAME}:${currentPomVersion.replace("-SNAPSHOT","")} || true"
        }


        stage('Push release branch to git server') {
            releaseStagingBranchName = "${RELEASE_BRANCH}/" + currentPomVersion.replace("-SNAPSHOT","")
            //increase pom version
            withMaven( maven: 'MAVEN 3.6' ) {
                sh "mvn versions:set -DnewVersion=${currentPomVersion.replace("-SNAPSHOT","")}"
            }
            sh "git commit -am 'Create ${RELEASE_BRANCH} branch with version ${currentPomVersion.replace("-SNAPSHOT","")} - Jenkins'"
            withCredentials([usernamePassword(credentialsId: "75d76f6e-31ce-4146-9310-c75e88d86226", passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]){
                sh "git push https://${GIT_USERNAME}:${GIT_PASSWORD}@gitsource.axonactive.com/${GIT_URI}.git"
            }
        }

        /* This stage will create a new develop branch on Git and also increase version in POM file */
        stage("Update ${CHECKOUT_BRANCH} branch"){
            sh "git checkout ${CHECKOUT_BRANCH}" // switch back to current branch
            //increase pom version
            withMaven( maven: 'MAVEN 3.6' ) {
                sh "mvn build-helper:parse-version versions:set -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.nextMinorVersion}.0-SNAPSHOT versions:commit"
            }

            sh "git commit -am 'Increase minor version in pom file - Jenkins'"
            withCredentials([usernamePassword(credentialsId: "75d76f6e-31ce-4146-9310-c75e88d86226", passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]){
                sh "git push https://${GIT_USERNAME}:${GIT_PASSWORD}@gitsource.axonactive.com/${GIT_URI}.git"
            }
        }

        stage('Pull and run image on Staging server') {
            /*ssh to develop server*/
            withCredentials([usernamePassword(credentialsId: "${SERVER_CREDENTIAL_ID}", passwordVariable: 'password', usernameVariable: 'username')]) {
                def remote = [:]
                remote.user = "${username}"
                remote.password = "${password}"
                remote.name = "remote-to-agile-deck-server"
                remote.host = "${SERVER_IP}"
                remote.allowAnyHosts = true

                /*
                Pull and run image on Dev server
                Before build, must stop and remove container that already run. then remove old image in the dev server
                After remove container and image, pull new image from dockerland and rerun the container
                */

                sshCommand remote: remote, command:  """docker network create ${NETWORK_NAME} || true"""

                sshCommand remote: remote, command:  """docker stop ${CONTAINER_NAME} || true && docker rm ${CONTAINER_NAME} || true"""

                sshCommand remote: remote, command:  """docker rmi ${DOCKER_REGISTRY_URL}/${IMAGE_NAME}:${currentPomVersion.replace("-SNAPSHOT","")} -f || true"""

                withCredentials([usernamePassword(credentialsId: "${DOCKER_CREDENTIAL_ID}", passwordVariable: 'password', usernameVariable: 'username')]) {
                    sshCommand remote: remote, command:  """docker login ${DOCKER_REGISTRY_URL} -u ${username} -p ${password}"""
                    sshCommand remote: remote, command:  """docker pull ${DOCKER_REGISTRY_URL}/${IMAGE_NAME}:${currentPomVersion.replace("-SNAPSHOT","")}"""
                    sshCommand remote: remote, command:  """docker run -i -d --restart unless-stopped -p ${PUBLISH_PORT}:8080 --name ${CONTAINER_NAME} \
                            -e quarkus.http.cors.origins=${CORS_ORIGINS} \
                            -e quarkus.datasource.username=${DB_USER} \
                            -e quarkus.datasource.password=${DB_PASS} \
                            -e quarkus.datasource.jdbc.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME} \
                            -e quarkus.hibernate-orm.database.generation=${DB_GENERATION} \
							-e quarkus.file.dir=${STORAGE_DIR} \
                            ${DOCKER_REGISTRY_URL}/${IMAGE_NAME}:${currentPomVersion.replace("-SNAPSHOT","")}"""
                    sshCommand remote: remote, command:  """docker network connect ${NETWORK_NAME} ${CONTAINER_NAME}"""
                }
            }
        }


        /* Stage post build, if no error, notify success to slack channel */
        stage('Finish') {
			notifySuccessToSlack("${releaseStagingBranchName}")
        }
    }
}catch(e){
    /* if error notify error to slack channel */
    notifyFailedToSlack()
    currentBuild.result = 'FAILED'
}