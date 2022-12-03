
pipeline {
  // This pipeline requires the following plugins:
  // * Pipeline Utility Steps Plugin: https://wiki.jenkins.io/display/JENKINS/Pipeline+Utility+Steps+Plugin
  // * Git: https://plugins.jenkins.io/git/
  // * Workflow Aggregator: https://plugins.jenkins.io/workflow-aggregator/
  // * Octopus Deploy: https://plugins.jenkins.io/octopusdeploy/
  // * JUnit: https://plugins.jenkins.io/junit/
  // * Maven Integration: https://plugins.jenkins.io/maven-plugin/
  parameters {
    string(defaultValue: 'Spaces-1', description: '', name: 'SpaceId', trim: true)
    string(defaultValue: 'SampleMavenProject-SpringBoot', description: '', name: 'ProjectName', trim: true)
    string(defaultValue: 'Dev', description: '', name: 'EnvironmentName', trim: true)
    string(defaultValue: 'Octopus', description: '', name: 'ServerId', trim: true)
  }
  tools {
    jdk 'Java'
  }
  agent {
      kubernetes {
          inheritFrom 'maven'
      }
  }
  stages {
    stage('Environment') {
      steps {
          echo "PATH = ${PATH}"
      }
    }
    stage('Checkout') {
      steps {
        // If this pipeline is saved as a Jenkinsfile in a git repo, the checkout stage can be deleted as
        // Jenkins will check out the code for you.
        script {
            /*
              This is from the Jenkins "Global Variable Reference" documentation:
              SCM-specific variables such as GIT_COMMIT are not automatically defined as environment variables; rather you can use the return value of the checkout step.
            */
            def checkoutVars = checkout([$class: 'GitSCM', branches: [[name: '*/master']], userRemoteConfigs: [[url: 'https://github.com/mcasperson/SampleMavenProject-SpringBoot.git']]])
            env.GIT_URL = checkoutVars.GIT_URL
            env.GIT_COMMIT = checkoutVars.GIT_COMMIT
            env.GIT_BRANCH = checkoutVars.GIT_BRANCH
        }
      }
    }
    stage('Dependencies') {
      steps {
        // Download the dependencies and plugins before we attempt to do any further actions
        sh(script: './mvnw --batch-mode dependency:resolve-plugins dependency:go-offline')
        // Save the dependencies that went into this build into an artifact. This allows you to review any builds for vulnerabilities later on.
        sh(script: './mvnw --batch-mode dependency:tree > dependencies.txt')
        archiveArtifacts(artifacts: 'dependencies.txt', fingerprint: true)
        // List any dependency updates.
        sh(script: './mvnw --batch-mode versions:display-dependency-updates > dependencieupdates.txt')
        archiveArtifacts(artifacts: 'dependencieupdates.txt', fingerprint: true)
      }
    }
    stage('Build') {
      steps {
        // Set the build number on the generated artifact.
        sh '''
          ./mvnw --batch-mode build-helper:parse-version versions:set \
          -DnewVersion=\\${parsedVersion.majorVersion}.\\${parsedVersion.minorVersion}.\\${parsedVersion.incrementalVersion}.${BUILD_NUMBER}
        '''
        sh(script: './mvnw --batch-mode clean compile', returnStdout: true)
        script {
            env.VERSION_SEMVER = sh (script: './mvnw -q -Dexec.executable=echo -Dexec.args=\'${project.version}\' --non-recursive exec:exec', returnStdout: true)
            env.VERSION_SEMVER = env.VERSION_SEMVER.trim()
        }
      }
    }
    stage('Test') {
      steps {
        sh(script: './mvnw --batch-mode -Dmaven.test.failure.ignore=true test')
        junit(testResults: 'target/surefire-reports/*.xml', allowEmptyResults : true)
      }
    }
    stage('Package') {
      steps {
        sh(script: './mvnw --batch-mode package -DskipTests')
      }
    }
