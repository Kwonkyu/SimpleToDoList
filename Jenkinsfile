pipeline {
    agent { docker { image 'gradle:6.9.2-jdk11' } }
    stages {
        stage('build') {
            steps {
                sh 'gradle --version'
            }
        }
    }
}