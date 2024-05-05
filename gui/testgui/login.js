document.addEventListener("DOMContentLoaded", function() {
    var registerButton = document.querySelector('.register-button');
    var loginButton = document.querySelector('button[type="submit"]:not(.register-button)');
    var upprepaKodInput = document.querySelector('input[name="upprepa_kod"]');

    upprepaKodInput.style.display = "none";

    registerButton.addEventListener("click", function(event) {
        event.preventDefault(); 

        upprepaKodInput.style.display = "block";
        loginButton.style.display = "none";
    });

    loginButton.addEventListener("click", function(event) {
        upprepaKodInput.style.display = "none";
    });

    var loginForm = document.querySelector('form');

    loginForm.addEventListener('submit', function(event) {
        event.preventDefault();
        
        var username = document.querySelector('input[name="avnamn"]').value;
        var password = document.querySelector('input[name="kod"]').value;

        var loginData = {
            username: username,
            password: password
        };

        fetch('backend länk', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Inloggning misslyckades');
            }
            return response.json();
        })
        .then(data => {
            const token = data.token;
            localStorage.setItem('token', token);

            window.location.href = 'settings.html'; 
        })
        .catch(error => {
            console.error('Login error:', error);
        });
    });
});