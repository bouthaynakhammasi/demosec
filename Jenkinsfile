pipeline {
    agent any
    
    environment {
        // Adapt to your Docker Hub username
        DOCKER_HUB_USER = "azizmelki"
        IMAGE_NAME = "medicarepi-backend"
        IMAGE_TAG = "latest"
        // L'ID doit être créé dans Jenkins !
        DOCKER_HUB_CREDS = credentials('docker-hub-credentials')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Docker Build & Push') {
            steps {
                sh "docker build -t ${DOCKER_HUB_USER}/${IMAGE_NAME}:${IMAGE_TAG} ."
                sh 'echo "$DOCKER_HUB_CREDS_PSW" | docker login -u "$DOCKER_HUB_CREDS_USR" --password-stdin'
                sh "docker push ${DOCKER_HUB_USER}/${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }
        
        stage('Kubernetes Deploy') {
            steps {
                sh "kubectl apply -f k8s/backend.yaml"
                sh "kubectl rollout restart deployment/medicarepi-backend-deployment"
            }
        }
    }
}
