document.addEventListener("DOMContentLoaded", function() {
    // Determine which form is on the current page
    const signInForm = document.getElementById("signInForm");
    const signUpForm = document.getElementById("signUpForm");

    // Password toggle functionality
    setupPasswordToggle("password", "togglePassword");

    if (document.getElementById("confirmPassword")) {
        setupPasswordToggle("confirmPassword", "toggleConfirmPassword");
    }

     if (signInForm) {
        signInForm.addEventListener("submit", async function(e) {
            e.preventDefault();

            // Reset error messages
            clearErrors();

            const username = document.getElementById("username").value;
            const password = document.getElementById("password").value;

            // Validate fields
            let isValid = true;

            if (!username) {
                showError("username", "Username is required");
                isValid = false;
            }

            if (!password) {
                showError("password", "Password is required");
                isValid = false;
            }

            if (isValid) {
                // Gửi yêu cầu đăng nhập thực tế tới backend
                const response = await fetch("/login", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded",
                    },
                    body: `username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`,
                });

                if (response.ok) {
                    window.location.href = "/chat/index.html";
                } else {
                    showError("password", "Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.");
                }
            }
        });
    }    
    if (signUpForm) {
        signUpForm.addEventListener("submit", async function(e) {
            e.preventDefault();

            // Reset error messages
            clearErrors();

            const username = document.getElementById("username").value;
            const nickname = document.getElementById("nickname").value;
            const password = document.getElementById("password").value;
            const confirmPassword = document.getElementById("confirmPassword").value;

            // Validate fields
            let isValid = true;

            if (!username) {
                showError("username", "Username is required");
                isValid = false;
            } else if (username.length < 4) {
                showError("username", "Username must be at least 4 characters");
                isValid = false;
            }

            if (!nickname) {
                showError("nickname", "Nickname is required");
                isValid = false;
            }

            if (!password) {
                showError("password", "Password is required");
                isValid = false;
            } else if (password.length < 6) {
                showError("password", "Password must be at least 6 characters");
                isValid = false;
            }

            if (!confirmPassword) {
                showError("confirmPassword", "Please confirm your password");
                isValid = false;
            } else if (password !== confirmPassword) {
                showError("confirmPassword", "Passwords do not match");
                isValid = false;
            }

            if (isValid) {
                // Gửi yêu cầu đăng ký tới backend
                try {
                    const response = await fetch("/sign-up", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify({
                            username: username,
                            password: password,
                            nickname: nickname
                        })
                    });

                    if (response.ok) {
                        window.location.href = "sign-in.html";
                    } else {
                        showError("username", "Đăng ký thất bại. Vui lòng thử lại.");
                    }
                } catch (err) {
                    showError("username", "Lỗi kết nối máy chủ.");
                }
            }
        });
    }
    // Helper functions
    function setupPasswordToggle(inputId, toggleId) {
        const passwordInput = document.getElementById(inputId);
        const toggleButton = document.getElementById(toggleId);

        if (passwordInput && toggleButton) {
            toggleButton.addEventListener("click", function() {
                const type =
                    passwordInput.getAttribute("type") === "password"
                        ? "text"
                        : "password";
                passwordInput.setAttribute("type", type);
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
            inputElement.classList.add("error");
        }
    }

    function clearErrors() {
        const errorElements = document.querySelectorAll(".error-message");
        errorElements.forEach((element) => {
            element.textContent = "";
        });

        const inputElements = document.querySelectorAll("input");
        inputElements.forEach((element) => {
            element.classList.remove("error");
        });
    }

});

document
    .getElementById("signInForm")
    .addEventListener("submit", async function(e) {
        e.preventDefault();
        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;

        const response = await fetch("/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: `username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`,
        });

        if (response.ok) {
            window.location.href = "/chat";
        } else {
            document.getElementById("password-error").textContent =
                "Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.";
        }
    });
