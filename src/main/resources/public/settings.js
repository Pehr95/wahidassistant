
window.onload = fetchEvents();


async function fetchEvents() {
    try {
        
        const response = await fetch('http://localhost:8080/api/v1/user/settings', {method: 'GET', headers: {'Authorization': 'Bearer ' + getAuthToken()}});
        const currentSettings = await response.json();
        console.log("ok " + currentSettings.option);
        document.getElementById("addressInput").value=currentSettings.address;
        document.getElementById("postnummer").value = currentSettings.postnummer;
        document.getElementById("urlInput").value = currentSettings.url
        if(currentSettings.option === "Buss"){
            document.getElementById("Buss").checked = true;
        }else if(currentSettings.option === "Cykel"){
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
    const saveButton = document.getElementById("saveButton");

    saveButton.addEventListener("click", function() {
        const selectedOption = document.querySelector('input[name="option"]:checked').value;
        const url = document.getElementById("urlInput").value;
        const address = document.getElementById("addressInput").value;
        const postnummer = document.getElementById("postnummer").value;


        saveData(selectedOption, url, address, postnummer);
        console.log(selectedOption + " " + url + " " + address + " " + postnummer)
    });
    function saveData(option, url, address, postnummer) {
        const data = {
            option: option,
            url: url,
            address: address,
            postnummer: postnummer
        };

        fetch("http://localhost:8080/api/v1/user/settings", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + getAuthToken()
            },
            body: JSON.stringify(data)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            return response.json();
        })
        .then(data => {
            console.log("Data saved successfully:", data);
        })
        .catch(error => {
            console.error("Error saving data:", error);
        });
    }

   


});