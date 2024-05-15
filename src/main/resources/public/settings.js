document.addEventListener("DOMContentLoaded", function() {
    const saveButton = document.getElementById("saveButton");

    saveButton.addEventListener("click", function() {
        const selectedOption = document.querySelector('input[name="option"]:checked').value;
        const url = document.getElementById("urlInput").value;
        const address = document.getElementById("addressInput").value;


        saveData(selectedOption, url, address);
    });
    function saveData(option, url, address) {
        const data = {
            option: option,
            url: url,
            address: address
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


});