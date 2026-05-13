const BASE_URL = '';  // ✅ Relative URL — works on any port

window.onload = function () {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }
}

async function deposit() {
    const token = localStorage.getItem('token');
    const amount = document.getElementById('amount').value;
    const description = document.getElementById('description').value;
    const errorMsg = document.getElementById('errorMsg');
    const successMsg = document.getElementById('successMsg');
    const depositBtn = document.getElementById('depositBtn');

    // Clear previous messages
    errorMsg.classList.add('d-none');
    successMsg.classList.add('d-none');

    // ✅ Validation
    if (!amount || parseFloat(amount) <= 0) {
        errorMsg.textContent = 'Please enter a valid amount!';
        errorMsg.classList.remove('d-none');
        return;
    }

    // ✅ Prevent double-click
    if (depositBtn) depositBtn.disabled = true;

    try {
        const response = await fetch(`${BASE_URL}/api/transaction/deposit`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                amount: parseFloat(amount),
                description: description || ''
            })
        });

        // ✅ Handle unauthorized — redirect to login
        if (response.status === 401) {
            localStorage.clear();
            window.location.href = 'login.html';
            return;
        }

        // ✅ Safe response parsing — backend may return plain text or JSON
        const contentType = response.headers.get('content-type');
        const data = contentType && contentType.includes('application/json')
            ? await response.json()
            : await response.text();

        if (response.ok) {
            successMsg.textContent = `✅ Successfully deposited ₹${parseFloat(amount).toFixed(2)}!`;
            successMsg.classList.remove('d-none');
            document.getElementById('amount').value = '';
            document.getElementById('description').value = '';
        } else {
            // ✅ Show actual backend error message if available
            const msg = typeof data === 'string' ? data : (data?.message || 'Deposit failed. Please try again.');
            errorMsg.textContent = msg;
            errorMsg.classList.remove('d-none');
        }

    } catch (error) {
        // ✅ Network/fetch error
        console.error('Deposit error:', error);
        errorMsg.textContent = 'Network error! Please check your connection and try again.';
        errorMsg.classList.remove('d-none');
    } finally {
        // ✅ Re-enable button regardless of outcome
        if (depositBtn) depositBtn.disabled = false;
    }
}