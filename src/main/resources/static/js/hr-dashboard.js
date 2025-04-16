document.addEventListener('DOMContentLoaded', function() {
    // Set username
    document.getElementById('username').textContent = sessionStorage.getItem('email') || 'HR Manager';

    // Load initial data
    loadData();

    // Setup interview assignment
    document.getElementById('assignInterviewBtn').addEventListener('click', assignInterview);
});

async function loadData() {
    await loadPendingInterviews();
    await loadInterviewers();
}

// Load interviews where current HR user is the interviewer
async function loadPendingInterviews() {
    try {
        const token = sessionStorage.getItem('token');
        const userId = sessionStorage.getItem('id');

        const response = await fetch(
            `http://localhost:8082/api/interviews?interviewer=${userId}&status=SCHEDULED`,
            { headers: { 'Authorization': `Bearer ${token}` } }
        );

        const interviews = await response.json();
        showInterviews(interviews);

    } catch (error) {
        console.error('Failed to load interviews:', error);
        document.getElementById('pendingInterviews').innerHTML = `
            <div class="alert alert-danger">Error loading interviews</div>
        `;
    }
}

// Display interviews in the list (without interviewer name)
function showInterviews(interviews) {
    const container = document.getElementById('pendingInterviews');
    container.innerHTML = '';

    if (!interviews || interviews.length === 0) {
        container.innerHTML = '<div class="text-muted">No pending interviews</div>';
        return;
    }

    interviews.forEach(interview => {
        const item = document.createElement('a');
        item.href = `/html/feedback-form.html?interviewId=${interview.id}`;
        item.className = 'list-group-item list-group-item-action';
        item.innerHTML = `
            <div class="d-flex justify-content-between">
                <div>
                    <strong>${interview.candidate?.name || 'No name'}</strong><br>
                    <small>${interview.candidate?.jobRole || ''}</small>
                </div>
                <div class="text-end">
                    <small>${new Date(interview.interviewTime).toLocaleString()}</small><br>
                    <span class="badge bg-primary">${interview.round || ''}</span>
                </div>
            </div>
        `;
        container.appendChild(item);
    });
}

// Load all interviewers for dropdown (unchanged)
async function loadInterviewers() {
    try {
        const token = sessionStorage.getItem('token');
        const response = await fetch('/api/users?role=INTERVIEWER', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        const interviewers = await response.json();
        const select = document.getElementById('interviewer');

        select.innerHTML = '<option value="">Select Interviewer</option>';
        interviewers.forEach(i => {
            select.innerHTML += `<option value="${i.id}">${i.name}</option>`;
        });

    } catch (error) {
        console.error('Failed to load interviewers:', error);
        alert('Failed to load interviewers');
    }
}

// Schedule new interview (unchanged)
async function assignInterview() {
    const form = document.getElementById('assignInterviewForm');

    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        return;
    }

    try {
        const token = sessionStorage.getItem('token');
        const interviewData = {
            candidate: {
                name: document.getElementById('candidateName').value,
                email: document.getElementById('candidateEmail').value,
                jobRole: document.getElementById('jobRole').value
            },
            interviewerId: parseInt(document.getElementById('interviewer').value),
            interviewTime: document.getElementById('interviewDate').value,
            round: document.getElementById('interviewRound').value
        };

        const response = await fetch('http://localhost:8082/api/interviews/schedule', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(interviewData)
        });

        if (!response.ok) throw new Error('Failed to schedule');

        alert('Interview scheduled!');
        bootstrap.Modal.getInstance(document.getElementById('assignInterviewModal')).hide();
        form.reset();
        loadPendingInterviews();

    } catch (error) {
        console.error('Error:', error);
        alert('Failed to schedule interview');
    }
}