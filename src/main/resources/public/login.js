document.addEventListener("DOMContentLoaded", function() {
    // Get references to password toggle elements and input
    const togglePassword = document.getElementById('togglePassword');
    const password = document.getElementById('password');
    const toggleNewPassword = document.getElementById('toggleNewPassword');
    const newPassword = document.getElementById('newPassword');
    const toggleConfirmPassword = document.getElementById('toggleConfirmPassword');
    const confirmPassword = document.getElementById('confirmPassword');

    /**
     * Toggles the visibility of the password input.
     * @param {*} toggleIcon
     * @param {*} inputField
     */
    function toggleVisibility(toggleIcon, inputField) {
        toggleIcon.addEventListener('click', function() {
            // Toggle the type attribute
            const type = inputField.getAttribute('type') === 'password' ? 'text' : 'password';
            inputField.setAttribute('type', type);
            // Changes the icon based on the toggle type
            this.src = type === 'password' ? 'icon-eye.png' : 'icon-hide.png';
        });
    }

    toggleVisibility(togglePassword, password);
    toggleVisibility(toggleNewPassword, newPassword);
    toggleVisibility(toggleConfirmPassword, confirmPassword);

    // Get references to form and button elements
    const loginForm = document.getElementById("loginContainer");
    const registerForm = document.getElementById("registerForm");
    const registerBtn = document.getElementById("registerBtn");
    const backBtn = document.getElementById("backBtn");

    // Get references to error message elements
    const loginError = document.getElementById("loginErrorMessage");
    const loginErrorContainer = document.getElementById("loginError");
    const registerError = document.getElementById("registerErrorMessage");
    const registerErrorContainer = document.getElementById("registerError");

    /**
     * Handles the login process by sending a POST request to the authenticate endpoint.
     * @param {*} username
     * @param {*} password
     */
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
            // Redirect to the home page
            window.location.href = '/';
        })
        .catch(error => {
            // Display login error message
            loginError.textContent = 'Felaktigt användarnamn eller lösenord';
            loginErrorContainer.style.display = 'flex';
        });
    }

    /**
     * Handles the registration process by sending a POST to the registration endpoint.
     * @param {*} username
     * @param {*} password
     */
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
            //alert('JWT: ' + jwt); // Useful to have
            // Redirect to the settings page
            window.location.href = '/settings';
        })
        .catch(error => {
            // Display registration error message
            registerError.textContent = 'Användarnamnet används redan!';
            registerErrorContainer.style.display = 'flex';
        });
    }

    // Event listener for register button click
    registerBtn.addEventListener("click", function() {
        // Populate registration form with existing username and password
        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;

        document.getElementById("newUsername").value = username;
        document.getElementById("newPassword").value = password;
        document.getElementById("confirmPassword").value = ''; 

        // Hide login error message
        loginError.textContent = '';
        loginErrorContainer.style.display = 'none';

        // Switch from login form to registration form
        loginForm.classList.add("hidden");
        registerForm.classList.remove("hidden");
    });

    // Event listener for back button
    backBtn.addEventListener("click", function() {
        // Hide registration error message
        registerError.textContent = '';
        registerErrorContainer.style.display = 'none';

        // Switch from registration form to login form
        registerForm.classList.add("hidden");
        loginForm.classList.remove("hidden");
    });

    // Event listener for registration form submission
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
        // Check if passwords match
        if (password !== confirmPassword) {
            registerError.textContent = 'Lösenorden matchar inte';
            registerErrorContainer.style.display = 'flex';
            return;
        }
        // Calls for register function
        register(username, password);
    });
});
