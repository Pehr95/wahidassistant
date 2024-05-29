
window.onload = fetchEvents();


async function fetchEvents() {
    try {
        
        const response = await fetch('/api/v1/user/settings', {method: 'GET', headers: {'Authorization': 'Bearer ' + getAuthToken()}});
        const currentSettings = await response.json();
    
        console.log(currentSettings.preferredTransportation);
        console.log(currentSettings.postalCode);
        console.log(currentSettings.url);
        console.log(currentSettings.address);
        document.getElementById("adressInput").value=currentSettings.address;
        document.getElementById("postnummer").value = currentSettings.postalCode;
        document.getElementById("urlInput").value = currentSettings.url;
        if(currentSettings.preferredTransportation === "BUS"){
            document.getElementById("Buss").checked = true;
        }else if(currentSettings.preferredTransportation === "BIKE"){
            document.getElementById("Cykel").checked = true;
        }else{
            document.getElementById("Ingen").checked = true;
        }
    } catch (error) {
        console.error('Error fetching events:', error);
        //window.location.href = 'templogin.html';
    }
}



function getAuthToken() {
    const cookies = document.cookie.split(';').map(cookie => cookie.trim());
    for (const cookie of cookies) {
        const [name, value] = cookie.split('=');
        if (name === 'auth_token') {
            console.log('Found auth token:', value)
            return value;
        }
    }
    console.log('No auth token found.')
    return null; // Return null if token is not found
}




document.addEventListener("DOMContentLoaded", function() {

    const returnButton = document.getElementById("return");
    returnButton.addEventListener("click", function(){
        window.location.href = "/" // funkar ens detta ? NEJ
    });
    
    const saveButton = document.getElementById("saveButton");

    saveButton.addEventListener("click", function() {
        const preferredTransportation = document.querySelector('input[name="option"]:checked').value;
        const url = document.getElementById("urlInput").value;
        const address = document.getElementById("adressInput").value;
        const postalCode = document.getElementById("postnummer").value;


        saveData(preferredTransportation, url, address, postalCode);
        console.log(preferredTransportation + " " + url + " " + address + " " + postalCode)
    });
    function saveData(preferredTransportation, url, address, postalCode) {
        const data = {
            preferredTransportation: preferredTransportation,
            url: url,
            address: address,
            postalCode: postalCode
        };

        fetch("/api/v1/user/settings", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + getAuthToken()
            },
            body: JSON.stringify(data)
        })
        .then(response => {
            if (!response.ok) {
                alert("Network response was not ok")
                throw new Error("Network response was not ok");
            }
            return response.json();
        })
        .then(data => {
            console.log("Data saved successfully:", data);
            alert("Data saved successfully!")
        })
        .catch(error => {
            console.error("Error saving data:", error);
        });
    }
});