pipeline {
    agent any
    environment {
        CITRIXDATA = credentials('secret')
    }
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
java.net.Authenticator.setDefault (new Authenticator() {
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password.toCharArray());
    }
});


boolean fileSuccessfullyDeleted =  new File("c:\\\\temp\\\\groovydownloadtest.txt").delete() 
File destinationFile= new File("c:\\\\temp\\\\groovydownloadtest.txt")
destinationFile.withOutputStream { it << new URL("https://raw.githubusercontent.com/adamtrzaskowski-dxc/CODA/main/test.txt").newInputStream() }
            def list = []
            destinationFile.eachLine { line ->list.add(line)}
            return list
                                                    '''
                                                
                                        ]
                                    ]
                                ],
                                                                [$class: 'ChoiceParameter', 
                                    choiceType: 'PT_SINGLE_SELECT', 
                                    description: 'Select the ConnectionData from the Dropdown List', 
                                    filterLength: 1, 
                                    filterable: false, 
                                    name: 'ConnectionData', 
                                    script: [
                                        $class: 'GroovyScript', 
                                        fallbackScript: [
                                            script: 
                                                "return['Could not get connections from DB']"
                                        ], 
                                        script: [
                                            script: '''
                                                import groovy.sql.Sql

def output = []

def sql = Sql.newInstance('jdbc:sqlserver://localhost:1433;encrypt=true;trustServerCertificate=true;', 'CodaUser', 'CodaPassword', 'com.microsoft.sqlserver.jdbc.SQLServerDriver')

String sqlString = "SELECT [ConnectionData] FROM [CODA].[dbo].[Connections] where ConnectionType = 'FirstType'"
sql.eachRow(sqlString){ row -> output.push(row.'ConnectionData')
}
sql.close()
return output.sort()
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
                    powershell '''
                    $data = get-content "$env:CITRIXDATA"
                    write-output $data
                    '''
                }
            }
        }
}