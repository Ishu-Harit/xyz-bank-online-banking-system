const BASE_URL = 'http://localhost:8080';

async function register() {
    const name = document.getElementById('name').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value.trim();
    const phone = document.getElementById('phone').value.trim();
    const accountType = document.getElementById('accountType').value;
    const errorMsg = document.getElementById('errorMsg');

    errorMsg.classList.add('d-none');

    if (!name || !email || !password || !phone) {
        errorMsg.textContent = 'Please fill in all fields!';
        errorMsg.classList.remove('d-none');
        return;
    }

    try {
        const response = await fetch(`${BASE_URL}/api/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password, phone, accountType })
        });

        const data = await response.json();

        if (response.ok) {
            localStorage.setItem('token', data.token);
            localStorage.setItem('userName', data.name);
            localStorage.setItem('userEmail', data.email);
            localStorage.setItem('accountNumber', data.accountNumber);
            localStorage.setItem('accountType', data.accountType);
            window.location.href = 'account-created.html';
        } else {
            errorMsg.textContent = data;
            errorMsg.classList.remove('d-none');
        }
    } catch (error) {
        errorMsg.textContent = 'Server error! Please try again.';
        errorMsg.classList.remove('d-none');
    }
}

function goToDashboard() {
    const userName = localStorage.getItem('userName');
    const accountNumber = localStorage.getItem('accountNumber');
    const accountType = localStorage.getItem('accountType');

    document.getElementById('userName').textContent = userName;
    document.getElementById('accountNumber').textContent = accountNumber;
    document.getElementById('accountType').textContent = accountType;

    window.location.href = 'dashboard.html';
}

// Load account created details
window.onload = function() {
    if (document.getElementById('userName')) {
        document.getElementById('userName').textContent =
            localStorage.getItem('userName');
        document.getElementById('accountNumber').textContent =
            localStorage.getItem('accountNumber');
        document.getElementById('accountType').textContent =
            localStorage.getItem('accountType');
    }
}