
/**
 * Handles the window load event to fetch user settings data.
 * Initializes event listeners for saving settings.
 * Authors: Amer & Adam
 * Contributor: Pehr
 */

// Fetch and populate user settings when loading in to the window
window.onload = fetchEvents();

/**
 * Fetches user settings from backend and populates form fields.
 */
async function fetchEvents() {
    try {
        
        // Fetch user settings from the endpoint with GET
        const response = await fetch('/api/v1/user/settings', {method: 'GET', headers: {'Authorization': 'Bearer ' + getAuthToken()}});
        const currentSettings = await response.json();
    
        // Log the current settings to console
        console.log(currentSettings.preferredTransportation);
        console.log(currentSettings.postalCode);
        console.log(currentSettings.url);
        console.log(currentSettings.address);

        // Populate the form fields with the fetched settings
        document.getElementById("adressInput").value=currentSettings.address;
        document.getElementById("postnummer").value = currentSettings.postalCode;
        document.getElementById("urlInput").value = currentSettings.url;

        // Set the preferred transportation
        if(currentSettings.preferredTransportation === "BUS"){
            document.getElementById("Buss").checked = true;
        }else if(currentSettings.preferredTransportation === "BIKE"){
            document.getElementById("Cykel").checked = true;
        }else{
            document.getElementById("Ingen").checked = true;
        }
    } catch (error) {
        console.error('Error fetching events:', error);
    }
}


/**
 * Retrieves the authentication token from cookie
 * @returns 
 */
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

// Initialize event listeners once the DOM contet has loaded
document.addEventListener("DOMContentLoaded", function() {

   // Get references to the return and save buttons 
    const returnButton = document.getElementById("return");
    returnButton.addEventListener("click", function(){
        window.location.href = "/" 
    });
    
    const saveButton = document.getElementById("saveButton");

    // Add click event listener to save button
    saveButton.addEventListener("click", function() {
        // Get form values
        const preferredTransportation = document.querySelector('input[name="option"]:checked').value;
        const url = document.getElementById("urlInput").value;
        const address = document.getElementById("adressInput").value;
        const postalCode = document.getElementById("postnummer").value;

        // Save the data
        saveData(preferredTransportation, url, address, postalCode);
        console.log(preferredTransportation + " " + url + " " + address + " " + postalCode)
    });
    /**
     * Sends the user settings data to the backend to be saved
     * @param {*} preferredTransportation - The users preferred transportation
     * @param {*} url - Schedule URL provided by user.
     * @param {*} address - Address provided by user.
     * @param {*} postalCode - Postal code provided by user.
     */
    function saveData(preferredTransportation, url, address, postalCode) {
        const data = {
            preferredTransportation: preferredTransportation,
            url: url,
            address: address,
            postalCode: postalCode
        };

        // Sends a POST request to endpoint to save the settings
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
            // Log and alert successful save
            console.log("Data saved successfully:", data);
            alert("Data saved successfully: " + JSON.stringify(data))
        })
        .catch(error => {
            // Log any errors when saving data
            console.error("Error saving data:", error);
        });
    }
});