$headers = @{ "Content-Type" = "application/json" }

# 1. Register
Write-Host "--- Attempting Registration ---"
$regBody = Get-Content -Raw -Path "register.json"
try {
    $regResponse = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/auth/register" -Headers $headers -Body $regBody
    Write-Host "Registration Success"
}
catch {
    Write-Host "Registration Failed: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        Write-Host "Reg Response: $($reader.ReadToEnd())"
    }
}

# 2. Login
Write-Host "--- Attempting Login ---"
$loginBody = @{
    email    = "debug_admin_v2@test.com"
    password = "validPassword123!"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/auth/login" -Headers $headers -Body $loginBody
    Write-Host "Login Success"
    $token = $loginResponse.token 
    Write-Host "Got Token"
}
catch {
    Write-Host "Login Failed: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        Write-Host "Login Response: $($reader.ReadToEnd())"
    }
    exit
}

# 3. Create Trip
Write-Host "--- Attempting Create Trip ---"
$tripBody = Get-Content -Raw -Path "trip.json"
$authHeaders = @{
    "Content-Type"  = "application/json"
    "Authorization" = "Bearer $token"
}

try {
    $tripResponse = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/trips" -Headers $authHeaders -Body $tripBody
    Write-Host "Trip Created Success: $($tripResponse.id)"
}
catch {
    Write-Host "Trip Create Failed: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        Write-Host "Trip Response: $($reader.ReadToEnd())"
    }
}

# 4. Fetch Admin Bookings
Write-Host "--- Attempting Fetch Admin Bookings ---"
try {
    $bookingsResponse = Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/bookings/admin/all" -Headers $authHeaders
    Write-Host "Fetch Bookings Success"
    Write-Host "Bookings Type: $($bookingsResponse.GetType().Name)"
    Write-Host "Bookings Count: $($bookingsResponse.Count)"
    $json = $bookingsResponse | ConvertTo-Json -Depth 5
    Write-Host "Bookings Data: $json"
    $json | Set-Content "c:\Subham\Travel\SundaySoul-Server\debug_bookings.json"
}
catch {
    Write-Host "Fetch Bookings Failed: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        Write-Host "Bookings Response: $($reader.ReadToEnd())"
    }
}
