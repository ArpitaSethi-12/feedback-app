pipeline {
    agent any

    tools {
        maven 'Maven'   // name of Maven in Manage Jenkins > Tools
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/GITHUB_USER/feedback-app.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'   // use bat on Windows
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            echo 'Build successful'
        }
    }
}
