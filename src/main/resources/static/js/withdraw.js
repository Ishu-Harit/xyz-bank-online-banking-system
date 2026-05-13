const BASE_URL = 'http://localhost:8080';

window.onload = function() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
    }
}

async function withdraw() {
    const token = localStorage.getItem('token');
    const amount = document.getElementById('amount').value;
    const description = document.getElementById('description').value;
    const errorMsg = document.getElementById('errorMsg');
    const successMsg = document.getElementById('successMsg');

    errorMsg.classList.add('d-none');
    successMsg.classList.add('d-none');

    if (!amount || amount <= 0) {
        errorMsg.textContent = 'Please enter a valid amount!';
        errorMsg.classList.remove('d-none');
        return;
    }

    try {
        const response = await fetch(`${BASE_URL}/api/transaction/withdraw`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ amount: parseFloat(amount), description })
        });

        const data = await response.json();

        if (response.ok) {
            successMsg.textContent =
                `Successfully withdrew ₹${amount}!`;
            successMsg.classList.remove('d-none');
            document.getElementById('amount').value = '';
            document.getElementById('description').value = '';
        } else {
            errorMsg.textContent = data;
            errorMsg.classList.remove('d-none');
        }
    } catch (error) {
        errorMsg.textContent = 'Server error! Please try again.';
        errorMsg.classList.remove('d-none');
    }
}