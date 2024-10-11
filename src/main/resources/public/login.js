document.addEventListener("DOMContentLoaded", function() {
    // Get references to password toggle elements and input
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
     * Checks if the token is expired and clears it if necessary.
     */
    function checkTokenExpiration() {
        const token = localStorage.getItem('auth_token'); // Assuming you're storing the JWT here
        console.log("token:" + token);
        if (token) {
            const payload = JSON.parse(atob(token.split('.')[1])); // Decode JWT to get the payload
            const expiration = payload.exp * 1000; // Convert expiration to milliseconds
            if (Date.now() > expiration) {
                localStorage.removeItem('auth_token'); // Clear the expired token
                loginError.textContent = 'Din session har gått ut. Var vänlig logga in igen.';
                //logout();
                loginErrorContainer.style.display = 'flex';
            }
        }
    }


    // Function to log out the user
    function logout() {
        // Delete the JWT cookie
        deleteAllCookies('auth_token');

        // Optionally clear other related storage if used
        localStorage.removeItem('auth_token');
        sessionStorage.removeItem('auth_token');


        // Redirect to login page or homepage after logging out
        //window.location.href = '/'; // Adjust the URL as needed
    }

    // Function to delete all cookies
    function deleteAllCookies() {
        var cookies = document.cookie.split(";");

        for (var i = 0; i < cookies.length; i++) {
            var cookie = cookies[i];
            var eqPos = cookie.indexOf("=");
            var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
            document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/;domain=" + window.location.hostname;
        }
    }

    // Call the function to check for token expiration on page load
    //checkTokenExpiration();
    //logout();

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
                // Store the JWT in local storage
                localStorage.setItem('token', data.token);
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

    // Event listener for back button
    backBtn.addEventListener("click", function() {
        registerError.textContent = '';
        registerErrorContainer.style.display = 'none';

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
