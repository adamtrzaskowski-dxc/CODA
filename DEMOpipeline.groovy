pipeline {
    agent any
        stages {
            stage('Parameters'){
                steps {
                    script {
                    properties([
                            parameters([
                                [$class: 'ChoiceParameter', 
                                    choiceType: 'PT_SINGLE_SELECT', 
                                    description: 'Select the Environemnt from the Dropdown List', 
                                    filterLength: 1, 
                                    filterable: false, 
                                    name: 'Env', 
                                    script: [
                                        $class: 'GroovyScript', 
                                        fallbackScript: [
                                            classpath: [], 
                                            sandbox: false, 
                                            script: 
                                                "return['Could not get The environemnts']"
                                        ], 
                                        script: [
                                            classpath: [], 
                                            sandbox: false, 
                                            script: 
                                                "return['dev','stage','prod']"
                                        ]
                                    ]
                                ],
                                [$class: 'CascadeChoiceParameter', 
                                    choiceType: 'PT_SINGLE_SELECT', 
                                    description: 'Select the AMI from the Dropdown List',
                                    name: 'AMIList', 
                                    referencedParameters: 'Env', 
                                    script: 
                                        [$class: 'GroovyScript', 
                                        fallbackScript: [
                                                classpath: [], 
                                                sandbox: false, 
                                                script: "return['Could not get Environment from Env Param']"
                                                ], 
                                        script: [
                                                classpath: [], 
                                                sandbox: false, 
                                                script: '''
                                                if (Env.equals("dev")){
                                                    return["dev-123", "dev-456", "dev-789"]
                                                }
                                                else if(Env.equals("stage")){
                                                    return["stage-987", "stage-654", "stage-321"]
                                                }
                                                else if(Env.equals("prod")){
                                                    return["prod-123", "prod-456", "prod-789"]
                                                }
                                                '''
                                            ] 
                                    ]
                                ],
                                [$class: 'DynamicReferenceParameter', 
                                    choiceType: 'ET_ORDERED_LIST', 
                                    description: 'Select the  AMI based on the following infomration', 
                                    name: 'Image Information', 
                                    referencedParameters: 'Env', 
                                    script: 
                                        [$class: 'GroovyScript', 
                                        script: 'return["Could not get AMi Information"]', 
                                        script: [
                                            script: '''
                                                    if (Env.equals("dev")){
                                                        return["dev-123:  DEV with Java", "dev-456: DEV with Python", "dev-789: DEV with Groovy"]
                                                    }
                                                    else if(Env.equals("stage")){
                                                        return["stage-987:  stage with Java", "stage-654: stage with Python", "stage-321: DEV with Groovy"]
                                                    }
                                                    else if(Env.equals("prod")){
                                                        return["prod-987:  prod with Java", "prod-654: prod with Python", "prod-321: prod with Groovy"]
                                                    }
                                                    '''
                                                ]
                                        ]
                                ],
                                [$class: 'ChoiceParameter', 
                                    choiceType: 'PT_SINGLE_SELECT', 
                                    description: 'Select data from File', 
                                    filterLength: 1, 
                                    filterable: false, 
                                    name: 'FromFile', 
                                    script: [
                                        $class: 'GroovyScript', 
                                        fallbackScript: [
                                            classpath: [], 
                                            sandbox: false, 
                                            script: 
                                                "return['Could not get The environemnts']"
                                        ], 
                                        script: [
                                            classpath: [], 
                                            sandbox: false, 
                                            script: '''
                                                    def list = []
                                                    def file = "${WORKSPACE}\\\\test.txt"
                                                    File textfile= new File(file) 

                                                    textfile.eachLine { line ->list.add(line)}
                                                    return list
                                                    '''
                                                
                                        ]
                                    ]
                                ],
                            ])
                        ])
                    }
                }
            }
            stage('one'){
                steps{
                    powershell 'Write-Output "$env:Env"'
                    powershell 'Write-Output "$env:AMIList"'
                    powershell 'Write-Output "$env:FromFile"'
                }
            }
        }
        
}