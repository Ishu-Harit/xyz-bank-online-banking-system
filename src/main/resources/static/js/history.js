const BASE_URL = '';  // ✅ Relative URL — works on any port

window.onload = function () {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }
    loadHistory();
}

async function loadHistory() {
    const token = localStorage.getItem('token');
    const tbody = document.getElementById('transactionTableBody');
    const errorMsg = document.getElementById('errorMsg');

    // ✅ Show loading state while fetching
    tbody.innerHTML = `
        <tr>
            <td colspan="8" class="text-center text-muted">
                <span class="spinner-border spinner-border-sm me-2"></span>
                Loading transactions...
            </td>
        </tr>`;
    errorMsg.classList.add('d-none');

    try {
        const response = await fetch(`${BASE_URL}/api/transaction/history`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        // ✅ Handle unauthorized — redirect to login
        if (response.status === 401) {
            localStorage.clear();
            window.location.href = 'login.html';
            return;
        }

        // ✅ Safe parsing — check content-type first
        const contentType = response.headers.get('content-type');
        const data = contentType && contentType.includes('application/json')
            ? await response.json()
            : await response.text();

        if (response.ok) {
            // ✅ Handle empty array
            if (!data || data.length === 0) {
                tbody.innerHTML = `
                    <tr>
                        <td colspan="8" class="text-center text-muted py-4">
                            <i class="bi bi-inbox fs-4 d-block mb-2"></i>
                            No transactions found!
                        </td>
                    </tr>`;
                return;
            }

            tbody.innerHTML = '';
            data.forEach((tx, index) => {
                // ✅ Normalize type to handle lowercase from backend
                const type = (tx.type || '').toLowerCase();
                const badgeClass = type === 'deposit'
                    ? 'badge-deposit'
                    : type === 'withdrawal'
                    ? 'badge-withdrawal'
                    : 'badge-transfer';

                // ✅ Safe date formatting — won't crash on null date
                const dateStr = tx.transactionDate
                    ? new Date(tx.transactionDate).toLocaleDateString('en-IN', {
                        day: '2-digit', month: 'short', year: 'numeric'
                      })
                    : '-';

                // ✅ Safe amount formatting — won't crash on null amount
                const amount = tx.amount != null
                    ? `₹${parseFloat(tx.amount).toFixed(2)}`
                    : '-';

                // ✅ Status badge color based on status value
                const statusClass = (tx.status || '').toLowerCase() === 'failed'
                    ? 'bg-danger'
                    : (tx.status || '').toLowerCase() === 'pending'
                    ? 'bg-warning text-dark'
                    : 'bg-success';

                tbody.innerHTML += `
                    <tr>
                        <td>${index + 1}</td>
                        <td>
                            <span class="badge ${badgeClass} px-2 py-1 rounded">
                                ${tx.type || '-'}
                            </span>
                        </td>
                        <td>${amount}</td>
                        <td>${tx.fromAccount || 'External'}</td>
                        <td>${tx.toAccount || '-'}</td>
                        <td>${tx.description || '-'}</td>
                        <td>
                            <span class="badge ${statusClass}">
                                ${tx.status || 'Unknown'}
                            </span>
                        </td>
                        <td>${dateStr}</td>
                    </tr>`;
            });

        } else {
            // ✅ Show backend error message safely
            tbody.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center text-muted py-4">
                        Could not load transactions.
                    </td>
                </tr>`;
            const msg = typeof data === 'string' ? data : (data?.message || 'Failed to load history.');
            errorMsg.textContent = msg;
            errorMsg.classList.remove('d-none');
        }

    } catch (error) {
        // ✅ Network error — clear loading, show message
        console.error('History fetch error:', error);
        tbody.innerHTML = `
            <tr>
                <td colspan="8" class="text-center text-muted py-4">
                    Could not load transactions.
                </td>
            </tr>`;
        errorMsg.textContent = 'Network error! Please check your connection and try again.';
        errorMsg.classList.remove('d-none');
    }
}