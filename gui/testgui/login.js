document.addEventListener("DOMContentLoaded", function() {
    var registerButton = document.querySelector('.register-button');
    var upprepaKodInput = document.querySelector('input[name="upprepa_kod"]');

    upprepaKodInput.style.display = "none"; 

    registerButton.addEventListener("click", function(event) {
        event.preventDefault();

        upprepaKodInput.style.display = "block"; 
        upprepaKodInput.focus();
    });

    var loginForm = document.querySelector('form');

    loginForm.addEventListener('submit', function(event) {
        event.preventDefault();
        
        var username = document.querySelector('input[name="avnamn"]').value;
        var password = document.querySelector('input[name="kod"]').value;

        console.log('username:', username);
        console.log('password:', password);

        var loginData = {
            username: username,
            password: password
        };

        console.log(JSON.stringify(loginData));

        fetch('http://localhost:8080/api/v1/auth/authenticate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        })
        .then(response => {
            console.log('response:', response);
            if (!response.ok) {
                throw new Error('Inloggning misslyckades');
            }
            return response.json();
        })
        .then(data => {
            console.log('data:', data);
            const token = data.token;
            localStorage.setItem('token', token);

            window.location.href = 'settings.html'; 
        })
        .catch(error => {
            console.error('Login error:', error);
        });
    });
});