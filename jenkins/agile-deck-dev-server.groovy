/*

SLACK_CHANNEL=redbull
CHECKOUT_BRANCH=develop

DOCKER_REGISTRY_URL=aavn-registry.axonactive.vn.local
DOCKER_CREDENTIAL_ID=ccad9d2d-0400-4a36-9238-a49a70cf98c7
PUBLISH_PORT=8090
IMAGE_NAME=agile-tools/agile-deck-service
CONTAINER_NAME=agile-deck-service-dev
NETWORK_NAME=agile-deck-network

SERVER_IP=192.168.70.91
SERVER_CREDENTIAL_ID=redbull-control-server

CORS_ORIGINS=http://dev.agiledeck.axonactive.vn.local

DB_HOST=dev.agiledeck.axonactive.vn.local
DB_NAME=agile-deck-db
DB_USER=admin
DB_PASS=Aavn123
DB_PORT=5432
DB_GENERATION=drop-and-create
 */

/* quality gate status */
def qg

/* current pom version of project */
def pomVersion

/*


/*
	This Pipelines are made up of multiple steps on multiple server. It has 2 node of 2 server: Master Server and Dev Server
	Master Server will build the image of wildfly and push to dockerland
	Dev Server will pull the image from dockerland and run it
	It also send a notification by slack to who has permission to build the job.
*/
try {
	node('master'){

		/* Stage start, will notify to slack channel of team that run job */
		stage('Start'){
			//notifyBeginBuildToSlack()
			sh "rm -r ./* || true"
			sh "rm -r ./.* || true"
		}

		/* Stage checkout, will get the source code from git server */
		stage('Checkout'){
			checkout scm
			sh "git checkout ${CHECKOUT_BRANCH}"
//			sh 'git pull'
			// Get current pom version after checkout the project
			pomVersion = readMavenPom().getVersion()
		}

		/* Stage build, build the project to generate war file and wildfly image */
		stage('Build'){
			withMaven( maven: 'MAVEN 3.6' ) {
				sh "mvn clean package -Pnative -Dquarkus.native.container-build=true"
//				sh "mvn clean package"
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
			sh "docker build -f src/main/docker/Dockerfile.native -t ${IMAGE_NAME}:${pomVersion} ."
//			sh "docker build -f src/main/docker/Dockerfile.jvm -t ${IMAGE_NAME}:${pomVersion} ."
		}

		/* Stage push image to aavn-registry */
		stage('Push image to docker registry') {
			docker.withRegistry("http://${DOCKER_REGISTRY_URL}", "${DOCKER_CREDENTIAL_ID}") {
				image = docker.image("${IMAGE_NAME}:${pomVersion}")
				image.push()
			}
		}

		stage('Remove unused images') {
			sh "docker rmi ${IMAGE_NAME}:latest || true"
			sh "docker rmi ${IMAGE_NAME}:${pomVersion} || true"
			sh "docker rmi ${DOCKER_REGISTRY_URL}/${IMAGE_NAME}:${pomVersion} || true"
		}

		stage('Pull and run image on Dev server') {
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

				sshCommand remote: remote, command:  """docker rmi ${DOCKER_REGISTRY_URL}/${IMAGE_NAME}:${pomVersion} -f || true"""

				withCredentials([usernamePassword(credentialsId: "${DOCKER_CREDENTIAL_ID}", passwordVariable: 'password', usernameVariable: 'username')]) {
					sshCommand remote: remote, command:  """docker login ${DOCKER_REGISTRY_URL} -u ${username} -p ${password}"""
					sshCommand remote: remote, command:  """docker pull ${DOCKER_REGISTRY_URL}/${IMAGE_NAME}:${pomVersion}"""
					sshCommand remote: remote, command:  """docker run -i -d --restart unless-stopped -p ${PUBLISH_PORT}:8080 --name ${CONTAINER_NAME} \
							-e quarkus.http.cors.origins=${CORS_ORIGINS} \
							-e quarkus.datasource.username=${DB_USER} \
							-e quarkus.datasource.password=${DB_PASS} \
							-e quarkus.datasource.jdbc.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME} \
							-e quarkus.hibernate-orm.database.generation=${DB_GENERATION} \
							-e quarkus.file.dir=${STORAGE_DIR} \
							${DOCKER_REGISTRY_URL}/${IMAGE_NAME}:${pomVersion}"""
					sshCommand remote: remote, command:  """docker network connect ${NETWORK_NAME} ${CONTAINER_NAME}"""
				}
			}
		}


		/* Stage post build, if no error, notify success to slack channel */
		stage('Finish') {
			//notifySuccessToSlack()
		}
	}
} catch (e) {
	/* if error notify error to slack channel */
//	notifyFailedToSlack()
	currentBuild.result = 'FAILED'
}


