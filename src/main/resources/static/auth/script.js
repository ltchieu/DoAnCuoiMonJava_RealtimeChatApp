document.addEventListener('DOMContentLoaded', function() {
    // Determine which form is on the current page
    const signInForm = document.getElementById('signInForm');
    const signUpForm = document.getElementById('signUpForm');
    
    // Password toggle functionality
    setupPasswordToggle('password', 'togglePassword');
    
    if (document.getElementById('confirmPassword')) {
        setupPasswordToggle('confirmPassword', 'toggleConfirmPassword');
    }
    
    // Form validation
    if (signInForm) {
        signInForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            // Reset error messages
            clearErrors();
            
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            
            // Validate fields
            let isValid = true;
            
            if (!username) {
                showError('username', 'Username is required');
                isValid = false;
            }
            
            if (!password) {
                showError('password', 'Password is required');
                isValid = false;
            }
            
            if (isValid) {
                // Simulate successful login
                simulateAuth(username, true);
            }
        });
    }
    
    if (signUpForm) {
        signUpForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            // Reset error messages
            clearErrors();
            
            const username = document.getElementById('username').value;
            const nickname = document.getElementById('nickname').value;
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            // Validate fields
            let isValid = true;
            
            if (!username) {
                showError('username', 'Username is required');
                isValid = false;
            } else if (username.length < 4) {
                showError('username', 'Username must be at least 4 characters');
                isValid = false;
            }
            
            if (!nickname) {
                showError('nickname', 'Nickname is required');
                isValid = false;
            }
            
            if (!password) {
                showError('password', 'Password is required');
                isValid = false;
            } else if (password.length < 6) {
                showError('password', 'Password must be at least 6 characters');
                isValid = false;
            }
            
            if (!confirmPassword) {
                showError('confirmPassword', 'Please confirm your password');
                isValid = false;
            } else if (password !== confirmPassword) {
                showError('confirmPassword', 'Passwords do not match');
                isValid = false;
            }
            
            if (isValid) {
                // Simulate successful registration
                simulateAuth(username, false);
            }
        });
    }
    
    // Helper functions
    function setupPasswordToggle(inputId, toggleId) {
        const passwordInput = document.getElementById(inputId);
        const toggleButton = document.getElementById(toggleId);
        
        if (passwordInput && toggleButton) {
            toggleButton.addEventListener('click', function() {
                const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
                passwordInput.setAttribute('type', type);
            });
        }
    }
    
    function showError(fieldId, message) {
        const errorElement = document.getElementById(`${fieldId}-error`);
        if (errorElement) {
            errorElement.textContent = message;
        }
        
        const inputElement = document.getElementById(fieldId);
        if (inputElement) {
            inputElement.classList.add('error');
        }
    }
    
    function clearErrors() {
        const errorElements = document.querySelectorAll('.error-message');
        errorElements.forEach(element => {
            element.textContent = '';
        });
        
        const inputElements = document.querySelectorAll('input');
        inputElements.forEach(element => {
            element.classList.remove('error');
        });
    }
    
    function simulateAuth(username, isLogin) {
        
        // Store user data in localStorage (for demo purposes only)
        if (!isLogin) {
            const userData = {
                username: document.getElementById('username').value,
                nickname: document.getElementById('nickname').value
            };
            localStorage.setItem('userData', JSON.stringify(userData));
        }
        
        // Show success message
        const formContainer = document.querySelector('.form-container');
        formContainer.innerHTML = `
            <div class="success-message">
                <h2>${isLogin ? 'Login Successful!' : 'Registration Successful!'}</h2>
                <p>Welcome, ${username}!</p>
                <p class="redirect-message">Redirecting to dashboard...</p>
            </div>
        `;
        
        // Simulate redirect after 2 seconds
        setTimeout(function() {
            alert(`${isLogin ? 'Login' : 'Registration'} successful! In a real application, you would be redirected to a dashboard.`);
            window.location.href = isLogin ? 'sign-in.html' : 'sign-up.html';
        }, 2000);
    }
});
