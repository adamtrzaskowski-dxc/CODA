function TEST{


    $credentials = get-content $env:CODACredentials | ConvertFrom-Csv
    $map=@{}
    foreach($cred in $credentials){
        $map.add($cred.Credential_Id, @($cred.LoginId,$cred.Password))
    }
Write-Output "MAP WITH SECRET DATA"
    $map
    
    $creds = New-Object System.Collections.ArrayList
    foreach($item in $map){
        $credObject = New-Object System.Management.Automation.PSCredential ($userName, (ConvertTo-SecureString $userPassword -AsPlainText -Force))
        $creds.Add($credObject) > $null
    }
Write-Output "ARRAYLIST WITH PSCREDENTIALS"
    $creds
}