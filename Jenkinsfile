pipeline {
    agent any

    environment {
        IMAGE_NAME = 'super-skylab-app'
        REPO = 'quay.io/skylab'
        TAG = '1.0'
        QUAY_CREDENTIAL_ID = 'quay-credentials'
        
        FULL_IMAGE_NAME = "${REPO}/${IMAGE_NAME}:${TAG}"
    }

    stages {
        stage('Checkout Repository') {
            steps {
                checkout scm
            }
        }

        stage('Build Image') {
            steps {
                echo "Building Image: ${env.FULL_IMAGE_NAME}"
                sh """
                buildah build --jobs "\$(nproc)" "${env.FULL_IMAGE_NAME}" "${PWD}"
                """
            }
        }

        stage('Push Image') {
            steps {
                script {
                    echo "Sign-in to quay.io registries"
                }

                withCredentials([usernamePassword(
                    credentialsId: env.QUAY_CREDENTIAL_ID,
                    usernameVariable: 'QUAY_USER',
                    passwordVariable: 'QUAY_TOKEN'
                )]) {
                    sh """
                    buildah login \\
                        -u "\$QUAY_USER" \\
                        -p "\$QUAY_TOKEN" \\
                        ${env.REPO}
                    buildah push "${env.FULL_IMAGE_NAME}"
                    """
                }
            }
        }
    }
}

