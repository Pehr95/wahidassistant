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
        var repeatPassword = document.querySelector('input[name="upprepa_kod"]').value;

        if (password !== repeatPassword) {
            console.error('Lösenorden matchar inte.');
            return; // Avbryt registreringen om lösenorden inte matchar
        }

        console.log('username:', username);
        console.log('password:', password);

        var formData = {
            username: username,
            password: password
        };

        console.log(JSON.stringify(formData));

        // Avgör URL baserat på vilken knapp som användes för formuläret
        var url = loginForm.querySelector('button[type="submit"]').classList.contains('register-button') ? 
                  'http://localhost:8080/api/v1/auth/register' :
                  'http://localhost:8080/api/v1/auth/authenticate';

        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        })
        .then(response => {
            console.log('response:', response);
            if (!response.ok) {
                throw new Error('Autentisering/Registrering misslyckades');
            }
            return response.json();
        })
        .then(data => {
            console.log('data:', data);
            if (url.includes('register')) {
                alert('Registrering lyckades. Vänligen logga in.');
                window.location.href = 'login.html'; // Omdirigera till inloggningssidan efter framgångsrik registrering
            } else {
                const token = data.token;
                localStorage.setItem('token', token);
                window.location.href = 'settings.html'; 
            }
        })
        .catch(error => {
            console.error('Autentiserings-/Registreringsfel:', error);
        });
    });
});
