document.addEventListener("DOMContentLoaded", function() {
    const togglePassword = document.getElementById('togglePassword');
    const password = document.getElementById('password');
    const toggleNewPassword = document.getElementById('toggleNewPassword');
    const newPassword = document.getElementById('newPassword');
    const toggleConfirmPassword = document.getElementById('toggleConfirmPassword');
    const confirmPassword = document.getElementById('confirmPassword');

    function toggleVisibility(toggleIcon, inputField) {
        toggleIcon.addEventListener('click', function() {
            const type = inputField.getAttribute('type') === 'password' ? 'text' : 'password';
            inputField.setAttribute('type', type);
            this.src = type === 'password' ? 'icon-eye.png' : 'icon-hide.png';
        });
    }

    toggleVisibility(togglePassword, password);
    toggleVisibility(toggleNewPassword, newPassword);
    toggleVisibility(toggleConfirmPassword, confirmPassword);

    const loginForm = document.getElementById("loginContainer");
    const registerForm = document.getElementById("registerForm");
    const registerBtn = document.getElementById("registerBtn");
    const backBtn = document.getElementById("backBtn");

    const loginError = document.getElementById("loginErrorMessage");
    const loginErrorContainer = document.getElementById("loginError");
    const registerError = document.getElementById("registerErrorMessage");
    const registerErrorContainer = document.getElementById("registerError");

    function login(username, password) {
        fetch('/api/v1/auth/authenticate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username: username, password: password })
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Login failed');
            }
        })
        .then(data => {
            const jwt = data.token;
            //alert('JWT: ' + jwt);
            //document.cookie = `auth_token=${jwt}; path=/`;
            //console.log("cookies: " + document.cookie);
            window.location.href = '/';
        })
        .catch(error => {
            loginError.textContent = 'Felaktigt användarnamn eller lösenord';
            loginErrorContainer.style.display = 'flex';
        });
    }

    function register(username, password) {
        fetch('/api/v1/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username: username, password: password })
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else if (response.status === 403) {
                throw new Error('Username already exists');
            } else {
                throw new Error('Registration failed');
            }
        })
        .then(data => {
            const jwt = data.token;
            //alert('JWT: ' + jwt);
            window.location.href = '/settings';
        })
        .catch(error => {
            registerError.textContent = 'Användarnamnet används redan!';
            registerErrorContainer.style.display = 'flex';
        });
    }

    registerBtn.addEventListener("click", function() {
        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;

        document.getElementById("newUsername").value = username;
        document.getElementById("newPassword").value = password;
        document.getElementById("confirmPassword").value = ''; 

        loginError.textContent = '';
        loginErrorContainer.style.display = 'none';

        loginForm.classList.add("hidden");
        registerForm.classList.remove("hidden");
    });

    backBtn.addEventListener("click", function() {
        registerError.textContent = '';
        registerErrorContainer.style.display = 'none';

        registerForm.classList.add("hidden");
        loginForm.classList.remove("hidden");
    });

    document.getElementById("login").addEventListener("submit", function(event) {
        event.preventDefault();
        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;
        login(username, password);
    });

    document.getElementById("register").addEventListener("submit", function(event) {
        event.preventDefault();
        const username = document.getElementById("newUsername").value;
        const password = document.getElementById("newPassword").value;
        const confirmPassword = document.getElementById("confirmPassword").value;
        if (password !== confirmPassword) {
            registerError.textContent = 'Lösenorden matchar inte';
            registerErrorContainer.style.display = 'flex';
            return;
        }
        register(username, password);
    });
});
