pipeline {
  agent any
  //webhook z github, ale jenkins musi byc na zewnatrz, port-forwarding na routerze
  triggers {
    githubPush()  // Trigger na push do repozytorium
  }
  stages {
    stage('Clone Repository') {
      steps {
        git branch: 'main', url: 'https://github.com/DanyAAD90/snakecicd.git'
      }
    }

    stage('Build Docker Image') {
      steps {
        script {
          docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
            def appImage = docker.build("danielzdun/snakecicd:1.${env.BUILD_NUMBER}")
            appImage.push()
          }
        }
      }
    }
    
    stage('Run Docker Image for Testing') {
      steps {
        script {
          // Determine dynamic port based on BUILD_NUMBER
          def dynamicPort = 8100 + (env.BUILD_NUMBER.toInteger() % 100)

          // Stop and remove any previous container with the same name
          sh 'docker stop danielzdun_snakecicd || true && docker rm danielzdun_snakecicd || true'

          // Run the new Docker image
          sh "docker run -d -p ${dynamicPort}:80 --name danielzdun_snakecicd danielzdun/snakecicd:1.${env.BUILD_NUMBER}"

          // Verify the container is running
          sh 'docker ps | grep danielzdun_snakecicd'
        }
      }
    }
    stage('Write to File') {
            steps {
                script {
                    writeFile file: '/home/tms_master_1/snakecicd_monitor/latest_version', text: "${env.BUILD_NUMBER}"
            }
        }
    }
    stage('Test Application') {
      steps {
        script {
          // Determine dynamic port based on BUILD_NUMBER
          def dynamicPort = 8100 + (env.BUILD_NUMBER.toInteger() % 100)
          withCredentials([string(credentialsId: 'tms_master_1_ip', variable: 'TMS_MASTER_IP')]) 
          {
          retry(5) {
            sh """
            sleep 5 && curl -f http://${TMS_MASTER_IP}:${dynamicPort} || (echo 'Waiting for application to start' && exit 1)
            """
          }
          // Test if the application responds on the exposed port
          //sh "curl -f http://localhost:${dynamicPort} || (echo 'Application did not start correctly' && exit 1)"
        }
        }
      }
    }

    stage('Build') {
      steps {
        script {
          // Generate log files
          sh 'mkdir -p logs && echo "Some log" > logs/example.log'
        }
      }
    }

    stage('Archive Logs') {
      steps {
        archiveArtifacts artifacts: '**/logs/**/*', allowEmptyArchive: true
      }
    }

  }

  post {
    always {
      archiveArtifacts artifacts: '**/logs/**', fingerprint: true
      cleanWs()
    }
  }
}
