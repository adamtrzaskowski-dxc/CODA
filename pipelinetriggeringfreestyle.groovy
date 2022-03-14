pipeline {
    agent any
    stages {
        stage('first') {
            steps {
                    powershell '''
                    write-output "scripts to run placeholder"
                    '''
            }
        }
    }
    post { 
        always { 
            build job: 'CODACreds'
            build job: 'Secrettest'
        }
    }
}