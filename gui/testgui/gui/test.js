document.getElementById('saveButton').addEventListener('click', function() {
    var transport = document.getElementById('transport').value;
    var url = document.getElementById('url').value.trim();
    var address = document.getElementById('address').value.trim();

    if (!transport || transport === "none" || !url || !address) {
        alert('Please fill in all fields correctly.');
        return;
    }

    // Assuming the backend API endpoint is ready to receive data via POST request
    fetch('https://your-backend-api.com/settings', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            transport: transport,
            url: url,
            address: address
        })
    })
    .then(response => response.json())
    .then(data => console.log('Success:', data))
    .catch((error) => {
        console.error('Error:', error);
    });
});
