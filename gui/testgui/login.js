document.addEventListener("DOMContentLoaded", function() {
    var loginButton = document.querySelector('.login-button');
    var registerButton = document.querySelector('.register-button');
    var upprepaKodInput = document.querySelector('input[name="upprepa_kod"]');
    var passwordInput = document.querySelector('input[name="kod"]');
    var repeatedPassword = false;

    upprepaKodInput.style.display = "none";

    function passwordsMatch() {
        return passwordInput.value === upprepaKodInput.value;
    }

    registerButton.addEventListener("click", function(event) {
        event.preventDefault();

        if (passwordsMatch()) {
            repeatedPassword = true;
            upprepaKodInput.style.display = "block";
            upprepaKodInput.removeAttribute('hidden'); 
            upprepaKodInput.focus();
        } else {
            alert("Lösenorden matchar inte. Var vänlig upprepa lösenordet.");
        }
    });

    loginButton.addEventListener('click', function(event) {
        event.preventDefault();

        var username = document.querySelector('input[name="avnamn"]').value;
        var password = document.querySelector('input[name="kod"]').value;

        var loginData = {
            username: username,
            password: password,
        };

        fetch('http://127.0.0.1:8080/api/v1/auth/authenticate', {
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

    registerButton.addEventListener('click', function(event) {
        event.preventDefault();

        var username = document.querySelector('input[name="avnamn"]').value;
        var password = document.querySelector('input[name="kod"]').value;

        var registerData = {
            username: username,
            password: password
        };

        fetch('http://127.0.0.1:8080/api/v1/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(registerData)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Registrering misslyckades');
            }
            return response.json();
        })
        .then(data => {
        })
        .catch(error => {
            console.error('Registration error:', error);
        });
    });
});
