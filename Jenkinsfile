pipeline {
    agent any

    stages {

        stage('Compile') {
            steps {
                echo "Compiling..."
                sh "sbt compile"
            }
        }

        stage('Test') {
            steps {
                echo "Testing..."
                sh "sbt test"
            }
        }

        stage('Package') {
            steps {
                echo "Packaging..."
                sh "sbt package"
            }
        }

    }
    post {
        always {
            emailext body: 'A Test EMail',
            recipientProviders: [
            [$class: 'DevelopersRecipientProvider'],
            [$class: 'RequesterRecipientProvider']
            ], subject: 'Test'
        }
        
      success {
         mail to: shubham17495@.com, subject: ‘The Pipeline success :(‘
      }
    }
}
