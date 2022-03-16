/*
### SETUP IMAGE #####
SLACK_CHANNEL=redbull
DOCKER_REGISTRY_URL=aavn-registry.axonactive.vn.local
DOCKER_CREDENTIAL_ID=ccad9d2d-0400-4a36-9238-a49a70cf98c7

NETWORK_NAME=agile-deck-network
IMAGE_NAME=agile-tools/agile-deck-service
CONTAINER_NAME=agile-deck-service
PUBLISH_PORT=8081
RELEASE_TAG=1.0.0

### ENVIRONMENT #####
CORS_ORIGINS=https://domain.com
DB_URL=jdbc:postgresql://domain.com:5432/agile-deck-db
DB_USER=admin
DB_PASS=Aavn123
DB_GENERATION=none
 */


/* This method will notify when the job run fail at any stage */
def notifyFailedToSlack() {
    slackSend (channel: SLACK_CHANNEL, color: '#FF0000', message: "Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL}) build failed")
}

/* This method will notify when the job run sucessfully */
def notifySuccessToSlack() {
    slackSend (channel: SLACK_CHANNEL, color: '#32CD32', message: "Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL}) build successfully")
}

/* This method will notify when the job started */
def notifyBeginBuildToSlack() {
    slackSend (channel: SLACK_CHANNEL, color: '#0096d6', message: "Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' started")
}


try {
    node('master'){

        stage('Start'){
            notifyBeginBuildToSlack()
        }

        stage('Pull and run image'){
            sh "docker network create ${NETWORK_NAME} || true"
            sh "docker stop ${CONTAINER_NAME} || true && docker rm ${CONTAINER_NAME} || true"
            sh "docker rmi ${DOCKER_REGISTRY_URL}/${IMAGE_NAME}:${RELEASE_TAG} -f || true"
            withCredentials([usernamePassword(credentialsId: "${DOCKER_CREDENTIAL_ID}", passwordVariable: 'password', usernameVariable: 'username')]) {
               sh "docker login ${DOCKER_REGISTRY_URL} -u ${username} -p ${password}"
               sh "docker pull ${DOCKER_REGISTRY_URL}/${IMAGE_NAME}:${RELEASE_TAG}"
               sh "docker run --restart unless-stopped -i -d -p ${PUBLISH_PORT}:8080 --name ${CONTAINER_NAME} \
                    -e quarkus.http.cors.origins=${CORS_ORIGINS} \
                    -e quarkus.datasource.username=${DB_USER} \
                    -e quarkus.datasource.password=${DB_PASS} \
                    -e quarkus.datasource.jdbc.url=${DB_URL} \
                    -e quarkus.hibernate-orm.database.generation=${DB_GENERATION} \
                    -e quarkus.file.dir=${STORAGE_DIR} \
                    ${DOCKER_REGISTRY_URL}/${IMAGE_NAME}:${RELEASE_TAG}"
               sh "docker network connect ${NETWORK_NAME} ${CONTAINER_NAME}"
            }
        }

        stage('Finish') {
            notifySuccessToSlack()
        }
    }
} catch (e) {
    notifyFailedToSlack()
    currentBuild.result = 'FAILED'
}