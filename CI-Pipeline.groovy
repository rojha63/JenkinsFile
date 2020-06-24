def loadProperties() {
    properties = readProperties file: 'pom_xml.properties
}	



pipeline {
    agent any
     tools {
   jdk 'jdk8'
   maven 'maven3'
     }
        stages {
          stage('SCM Checkout'){
            steps {
                git branch: 'master', credentialsId: 'github-login', poll: false, url: 'https://github.com/Shashankmishra1989/hello-world.git'            
                
            }
        }
        stage ('Compile Stage') {
            steps {
                sh 'mvn clean compile'
                echo 'Build Compile Successful'
                }
            }
         stage ('MVN-Build') {
            steps {
                sh 'mvn install'
                echo 'This is a minimal pipeline.'
            }
            }
         stage ('Build Docker image') {
             steps {
                 sh 'pwd'
                 sh 'sudo docker build -f /var/jenkins_home/workspace/Pipeline_test/Dockerfile -t rojha63/my-app:11.0.0 .'
                echo 'Docker image build.'
            }
         }
         stage ('Push Docker image to HUB') {
              steps {  
                 withCredentials([string(credentialsId: 'docker-pwd', variable: 'dockerpwd')]) {
                    sh "sudo docker login -u rojha63 -p ${dockerpwd}"
                 }   
                sh 'sudo docker push rojha63/my-app:11.0.0'
                echo 'Docker image build.'
           }
           }
           stage ('azure login'){
		     steps {
			withCredentials([azureServicePrincipal('azure_svc')]) {
            sh 'az login --service-principal -u $AZURE_CLIENT_ID -p $AZURE_CLIENT_SECRET -t $AZURE_TENANT_ID'
                    }
            echo 'Login successfull'
              }
           }   
		   stage ('aks login'){
           steps {
             sh 'az aks get-credentials --overwrite-existing --resource-group rakesh-rg --name cluster1'
              echo 'Login successfull'
              }
           } 
           stage ('Check name spaces '){
           steps {
             sh 'kubectl get ns'
                      }
           }
           stage ('Deploy in kubernetes'){
           steps {
             sh 'kubectl apply -f testapp.yaml'
                      }
           }  	   		   
    }
}
