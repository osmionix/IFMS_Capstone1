document.addEventListener('DOMContentLoaded', function() {
    initializeAuthForms();
    initializeLogoutButtons();
});

function initializeAuthForms() {
//    const loginForm = document.getElementById('loginForm');
//    if (loginForm) {
//        loginForm.addEventListener('submit', handleLogin);
//    }

    const signupForm = document.getElementById('signupForm');
    if (signupForm) {
        signupForm.addEventListener('submit', handleSignup);
    }
}
document.getElementById("loginForm").addEventListener("submit", handleLogin);

function initializeLogoutButtons() {
    document.querySelectorAll('.logout-btn').forEach(btn => {
        btn.addEventListener('click', handleLogout);
    });
}


async function handleLogin(e) {
    e.preventDefault(); // Prevent form from refreshing the page

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

//    console.log("Email:", email);
//    console.log("Password:", password);

    if (!email || !password) {
        alert("Please enter email and password");
        return;
    }

    const loginData = {
        email: email,
        password: password
    };

    try {
        const response = await fetch("http://localhost:8082/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(loginData)
        });

        const data = await response.json();
        console.log("Login response:", data);

        if (response.ok) {
            sessionStorage.setItem("user", JSON.stringify(data));
            sessionStorage.setItem('email', email);
            sessionStorage.setItem('id', data.id);

            if (data.role === "HR") {
                window.location.href = "/html/hr-dashboard.html";
            }
            else if (data.role === "INTERVIEWER") {
                window.location.href = "/html/interviewer-dashboard.html";
            }
            else {
                alert("Unknown role");
            }
        } else {
            throw new Error(data.message || "Login failed");
        }

    } catch (error) {
        console.error("Login error:", error.message);
        alert("Login failed: " + error.message);
    }
}


async function handleSignup(e) {
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form);

    const email = formData.get('email');
    const pattern = /^[a-zA-Z0-9._%+-]+@company\.com$/;

    if (!email || !pattern.test(email.toLowerCase())) {
      alert('Invalid email \n Suggestion: Use @company.com email id');
      return;
    }

    if (formData.get('password') !== formData.get('confirmPassword')) {
        showAlert('Passwords do not match');
        return;
    }

    try {
        const response = await fetch('/api/auth/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: formData.get('email'),
                password: formData.get('password'),
                name: formData.get('name'),
                role: formData.get('role')
            })
        });

        if (response.ok) {
            showAlert('Registration successful! Redirecting to login...', 'success');
            setTimeout(() => {
                window.location.href = '/html/login.html?signupSuccess=true';
            }, 1500);
        } else {
            const error = await response.text();
            showAlert(error || 'Registration failed');
        }
    } catch (error) {
        console.error('Signup error:', error);
        showAlert('Failed to connect to server');
    }
}

async function handleLogout() {
    try {
        const token = localStorage.getItem('authToken');
        if (token) {
            await fetch('/api/auth/logout', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
        }
        localStorage.removeItem('authToken');
        localStorage.removeItem('userRole');
        window.location.href = '/login.html';
    } catch (error) {
        console.error('Logout error:', error);
        window.location.href = '/login.html';
    }
}

function redirectBasedOnRole(role) {
    if (role === 'HR') {
        window.location.href = '/hr-dashboard.html';
    } else if (role === 'INTERVIEWER') {
        window.location.href = '/interviewer-dashboard.html';
    } else {
        window.location.href = '/';
    }
}

function showAlert(message, type = 'danger') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;

    const container = document.querySelector('.alert-container') || document.body;
    container.prepend(alertDiv);

    setTimeout(() => {
        alertDiv.remove();
    }, 5000);
}