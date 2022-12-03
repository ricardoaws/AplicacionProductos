pipeline {
/*
El pipeline se ejecuta en la rama master
*/
  agent {
   node{label 'master'}
  }
/*
Repositorio donde se va a subir las imagenes que se creen cuando se ejecuta el pipeline.pipeline
En este caso srá DockerHub y por lo tanto, hay que indicar las credenciales de acceso
*/
  environment {
    registry = "dgm83/identityprovider"
    dockerImage = ''
    registryCredential = 'dockerhub_id'
  }
/*
A continuación, se indica cada una de las fases de pipeline
*/

  stages {

  /*
  Apikey y repositorio de Github donde se escuentra alojado al codigo a desplegar
  He modificado la Apikey para que no sea la verdadera
  */

    stage('Checkout Source') {
      steps {
        git 'https://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B@github.com/dgm83/IdentityProvider.git'
      }
    }

    /*
    Tarea de construcción de contenedor Docker añadiendo la variable BUILD_NUMBER para que cada vez que se despliegue
    se cree una nueva imagen con un nuevo numero de versión sumandole 1 al anterior número de versión.
    */

    stage('Build image') {
      steps{
        script {
          dockerImage = docker.build registry + ":$BUILD_NUMBER"
        }
      }
    }

    /*
    Se realiza un push de la imagen para subirla a DockerHub
    */

    stage('Push Image') {
      steps{
        script {
            docker.withRegistry( "", registryCredential) {
            dockerImage.push()
          }
        }
      }
    }

    /*
    Se despliega un nuevo pod en Kubernetes  con la imagen indicada en el fichero myidp.yaml y con las credenciales
    de kubernetes creadas en Jenkins con el nombre kubeconfig
    */

     stage('Deploy App') {
      steps {
        script {
          kubernetesDeploy(configs: "myidp.yaml", kubeconfigId: "kubeconfig")
        }
      }
    }
  }
}
