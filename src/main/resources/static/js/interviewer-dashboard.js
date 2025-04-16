document.addEventListener('DOMContentLoaded', function() {
    // Set username from session storage
    const userData = getUserData();
    document.getElementById('username').textContent = userData.email || 'Interviewer';

    // Load pending interviews for this interviewer
    loadPendingInterviews();
});

function getUserData() {
    try {
        // Check if user data exists as JSON string
        const userJson = sessionStorage.getItem('user');
        if (userJson) {
            return JSON.parse(userJson);
        }

        // Fallback to individual session storage items
        return {
            email: sessionStorage.getItem('email'),
            token: sessionStorage.getItem('token'),
            id: sessionStorage.getItem('id')
        };
    } catch (e) {
        console.error('Error parsing user data:', e);
        return {};
    }
}

async function loadPendingInterviews() {
    try {
        const userData = getUserData();
        console.log('User data:', userData); // Debug log

        if (!userData.token || !userData.id) {
            throw new Error('Session data missing. Please login again.');
        }

        // Get interviews assigned to this interviewer with SCHEDULED status
        const response = await fetch(
            `http://localhost:8082/api/interviews?interviewer=${userData.id}&status=SCHEDULED`,
            {
                headers: {
                    'Authorization': `Bearer ${userData.token}`,
                    'Content-Type': 'application/json'
                }
            }
        );

        if (!response.ok) {
            // Handle unauthorized (401) specifically
            if (response.status === 401) {
                sessionStorage.clear();
                window.location.href = '/html/login.html';
                return;
            }
            throw new Error(`Failed to fetch interviews: ${response.status}`);
        }

        const interviews = await response.json();
        console.log('Interviews data:', interviews); // Debug log

        displayPendingInterviews(interviews);

    } catch (error) {
        console.error('Error loading pending interviews:', error);
        showError(error.message);
    }
}

function displayPendingInterviews(interviews) {
    const container = document.getElementById('pendingInterviews');
    container.innerHTML = '';

    if (!interviews || interviews.length === 0) {
        container.innerHTML = `
            <div class="alert alert-info">
                No pending interviews scheduled for you.
            </div>
        `;
        return;
    }

    interviews.forEach(interview => {
        const item = document.createElement('a');
        item.href = `/html/feedback-form.html?interviewId=${interview.id}`;
        item.className = 'list-group-item list-group-item-action mb-2';

        // Format interview date/time
        const interviewDate = new Date(interview.interviewTime);
        const formattedDate = interviewDate.toLocaleString('en-US', {
            weekday: 'short',
            month: 'short',
            day: 'numeric',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });

        item.innerHTML = `
            <div class="d-flex justify-content-between align-items-start">
                <div>
                    <h6 class="mb-1 fw-bold">${interview.candidate?.name || 'Candidate Name Not Available'}</h6>
                    <div class="small mb-1">
                        <span class="text-muted">Role:</span> ${interview.candidate?.jobRole || 'Not specified'}
                    </div>
                    <div class="small text-muted">
                        <i class="bi bi-calendar-event"></i> ${formattedDate}
                    </div>
                </div>
                <div>
                    <span class="badge bg-primary">${interview.round || 'N/A'}</span>
                </div>
            </div>
        `;

        container.appendChild(item);
    });
}

function showError(message) {
    const container = document.getElementById('pendingInterviews');
    container.innerHTML = `
        <div class="alert alert-danger">
            ${message}
            <div class="mt-2">
                <button onclick="window.location.reload()" class="btn btn-sm btn-warning me-2">
                    Retry
                </button>
                <button onclick="window.location.href='/html/login.html'" class="btn btn-sm btn-outline-danger">
                    Login
                </button>
            </div>
        </div>
    `;
}