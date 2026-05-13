const BASE_URL = '';  // ✅ Empty = relative URLs, works on any port

window.onload = function () {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }
    loadBalance();
}

async function loadBalance() {
    const token = localStorage.getItem('token');

    try {
        const balanceResponse = await fetch(`${BASE_URL}/api/account/balance`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        // ✅ Handle unauthorized - redirect to login
        if (balanceResponse.status === 401) {
            localStorage.clear();
            window.location.href = 'login.html';
            return;
        }

        const balance = await balanceResponse.json();

        if (balanceResponse.ok) {
            // ✅ Show 0.00 if balance is null/undefined/zero
            const amount = parseFloat(balance) || 0;
            document.getElementById('balanceAmount').textContent = amount.toFixed(2);
            document.getElementById('accountNumberDisplay').textContent =
                'Account: ' + (localStorage.getItem('accountNumber') || 'N/A');
            document.getElementById('lastUpdated').textContent =
                new Date().toLocaleString('en-IN');

            // ✅ Clear any previous error
            const errEl = document.getElementById('errorMsg');
            if (errEl) errEl.style.display = 'none';

        } else {
            // ✅ Server returned error (500, 404, etc.) — show 0.00, not "Error!"
            document.getElementById('balanceAmount').textContent = '0.00';
            showError('Could not fetch balance. Please try again.');
        }

    } catch (error) {
        // ✅ Network error — show 0.00, not "Error!"
        console.error('Balance fetch error:', error);
        document.getElementById('balanceAmount').textContent = '0.00';
        showError('Network error. Make sure you are connected.');
    }
}

function refreshBalance() {
    document.getElementById('balanceAmount').textContent = 'Loading...';
    const errEl = document.getElementById('errorMsg');
    if (errEl) errEl.style.display = 'none';
    loadBalance();
}

function showError(message) {
    let errEl = document.getElementById('errorMsg');
    if (!errEl) {
        // ✅ Create error element if it doesn't exist in HTML
        errEl = document.createElement('div');
        errEl.id = 'errorMsg';
        errEl.className = 'alert alert-danger mt-3';
        // Insert it above the balance card
        const card = document.querySelector('.card') || document.body;
        card.prepend(errEl);
    }
    errEl.textContent = message;
    errEl.style.display = 'block';
}