const BASE_URL = 'http://localhost:8080';

window.onload = function() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }
    loadDashboard();
}

async function loadDashboard() {
    const token = localStorage.getItem('token');

    try {
        const response = await fetch(`${BASE_URL}/api/account/details`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.status === 403 || response.status === 401) {
            localStorage.clear();
            window.location.href = 'login.html';
            return;
        }

        const data = await response.json();

        document.getElementById('navUserName').textContent =
            'Hello, ' + data.userName;
        document.getElementById('profileName').textContent =
            data.userName;
        document.getElementById('profileEmail').textContent =
            data.userEmail;
        document.getElementById('profilePhone').textContent =
            data.userPhone;
        document.getElementById('profileAccountNumber').textContent =
            'ACC: ' + data.accountNumber;
        document.getElementById('profileAccountType').textContent =
            data.accountType + ' Account';
        document.getElementById('profileBalance').textContent =
            parseFloat(data.balance).toFixed(2);

    } catch (error) {
        console.error('Dashboard load error:', error);
    }
}

function logout() {
    localStorage.clear();
    window.location.href = 'login.html';
}