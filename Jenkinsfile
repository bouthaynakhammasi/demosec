pipeline {
    agent any
    
    environment {
        // Adapt to your Docker Hub username
        DOCKER_HUB_USER = "aziz123456789"
        IMAGE_NAME = "medicarepi-backend"
        IMAGE_TAG = "latest"
        REGISTRY_CREDENTIALS_ID = "docker-hub-credentials"
    }

    stages {
        stage('Checkout') {
            steps {
                // Code is usually retrieved from Git here
                checkout scm
            }
        }

        stage('Build with Maven') {
            steps {
                sh "mvn clean package -DskipTests"
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    docker.withRegistry('', REGISTRY_CREDENTIALS_ID) {
                        def customImage = docker.build("${DOCKER_HUB_USER}/${IMAGE_NAME}:${IMAGE_TAG}")
                        customImage.push()
                    }
                }
            }
        }

        stage('Kubernetes Deploy') {
            steps {
                script {
                    // Update image in K8s manifest or use kubectl set image
                    sh "kubectl apply -f k8s/backend.yaml"
                    sh "kubectl rollout restart deployment/medicarepi-backend-deployment"
                }
            }
        }
    }
}
