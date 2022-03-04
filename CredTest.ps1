function TEST{


    $credentials = get-content $env:CODACredentials | ConvertFrom-Csv
    $map=@{}
    foreach($cred in $credentials){
        $cred.Credential_Id
        $map.add($cred.Credential_Id, @($cred.LoginId,$cred.Password))
    }
}