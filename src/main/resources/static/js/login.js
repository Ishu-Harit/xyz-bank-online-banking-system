const BASE_URL = 'http://localhost:8080';

async function login() {
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value.trim();
    const errorMsg = document.getElementById('errorMsg');

    errorMsg.classList.add('d-none');

    if (!email || !password) {
        errorMsg.textContent = 'Please fill in all fields!';
        errorMsg.classList.remove('d-none');
        return;
    }

    try {
        const response = await fetch(`${BASE_URL}/api/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (response.ok) {
            localStorage.setItem('token', data.token);
            localStorage.setItem('userName', data.name);
            localStorage.setItem('userEmail', data.email);
            localStorage.setItem('accountNumber', data.accountNumber);
            localStorage.setItem('accountType', data.accountType);
            window.location.href = 'dashboard.html';
        } else {
            errorMsg.textContent = data;
            errorMsg.classList.remove('d-none');
        }
    } catch (error) {
        errorMsg.textContent = 'Server error! Please try again.';
        errorMsg.classList.remove('d-none');
    }
}