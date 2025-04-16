// Global variables to store feedback data
let allFeedbacks = [];
let filteredFeedbacks = [];

// Utility functions
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

function getDecisionText(decision) {
    const decisions = {
        'L1_PASSED': 'L1 Passed',
        'L1_PASSED_WITH_COMMENT': 'L1 Passed with Comment',
        'L2_REQUIRED': 'L2 Required',
        'REJECTED': 'Rejected'
    };
    return decisions[decision] || decision || 'N/A';
}

document.addEventListener('DOMContentLoaded', function() {
    // Set username
    document.getElementById('username').textContent = sessionStorage.getItem('email') || 'User';

    // Load all feedback data when page loads
    loadAllFeedbacks();

    // Setup filter event listeners
    document.getElementById('filterRound').addEventListener('change', applyFilters);
    document.getElementById('filterDecision').addEventListener('change', applyFilters);
});

function loadAllFeedbacks() {
    fetch('/api/feedback/all')
        .then(response => {
            if (!response.ok) throw new Error('Failed to load feedback data');
            return response.json();
        })
        .then(feedbacks => {
            allFeedbacks = feedbacks;
            filteredFeedbacks = [...feedbacks];
            displayFeedbacks(filteredFeedbacks);
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('feedbackTableBody').innerHTML = `
                <tr>
                    <td colspan="8" class="text-center text-danger">
                        Failed to load feedback data. Please try again.
                    </td>
                </tr>
            `;
        });
}

function applyFilters() {
    const roundFilter = document.getElementById('filterRound').value;
    const decisionFilter = document.getElementById('filterDecision').value;

    filteredFeedbacks = allFeedbacks.filter(feedback => {
        const matchesRound = roundFilter === 'ALL' || feedback.round === roundFilter;
        const matchesDecision = decisionFilter === 'ALL' || feedback.decision === decisionFilter;
        return matchesRound && matchesDecision;
    });

    displayFeedbacks(filteredFeedbacks);
}

function displayFeedbacks(feedbacks) {
    const tableBody = document.getElementById('feedbackTableBody');
    tableBody.innerHTML = '';

    if (feedbacks.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="8" class="text-center text-muted">
                    No feedback found matching the selected filters
                </td>
            </tr>
        `;
        return;
    }

    feedbacks.forEach(feedback => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${formatDate(feedback.interviewDate)}</td>
            <td>${feedback.candidateName}</td>
            <td>${feedback.candidateEmail}</td>
            <td>${feedback.jobRole}</td>
            <td>${feedback.interviewer?.name || feedback.interviewerEmail || 'N/A'}</td>
            <td>${feedback.round}</td>
            <td>${getDecisionText(feedback.decision)}</td>
            <td>
                <button class="btn btn-sm btn-primary"
                        onclick="showFeedbackDetails(
                            '${feedback.candidateName}',
                            '${feedback.jobRole}',
                            '${feedback.round}'
                        )">
                    View
                </button>
                <button class="btn btn-sm btn-danger ms-2"
                        onclick="deleteFeedback('${feedback.id}', this)">
                    Delete
                </button>
            </td>
        `;
        tableBody.appendChild(row);
    });
}

// Global functions called from button clicks
async function showFeedbackDetails(candidateName, jobRole, round) {
    try {
        // First get the feedback ID
        const params = new URLSearchParams({
            candidateName,
            jobRole,
            round
        });

        const response = await fetch(`/api/feedback/find-id?${params}`);
        if (!response.ok) throw new Error('Feedback not found');

        // Parse the response to get the feedback ID
        const result = await response.json();
        const feedbackId = result.id || result;

        // Then get the complete feedback details
        const feedback = await fetch(`/api/feedback/details/${feedbackId}`)
            .then(response => {
                if (!response.ok) throw new Error('Could not load feedback details');
                return response.json();
            });

        // Then get the skills for this feedback
        const skills = await fetch(`/api/skills/by-feedback/${feedbackId}`)
            .then(response => {
                if (!response.ok) return [];
                return response.json();
            });

        // Display in modal with all details
        displayModal(feedback, skills);

    } catch (error) {
        alert('Failed to load feedback details: ' + error.message);
    }
}

function displayModal(feedback, skills) {
    // Candidate Information
    document.getElementById('detailCandidateName').textContent = feedback.candidateName || 'N/A';
    document.getElementById('detailCandidateEmail').textContent = feedback.candidateEmail || 'N/A';
    document.getElementById('detailJobRole').textContent = feedback.jobRole || 'N/A';

    // Interview Details
    document.getElementById('detailRound').textContent = feedback.round || 'N/A';
    document.getElementById('detailInterviewer').textContent = feedback.interviewer?.name || feedback.interviewerEmail || 'N/A';
    document.getElementById('detailInterviewDate').textContent = formatDate(feedback.interviewDate) || 'N/A';
    document.getElementById('detailDecision').textContent = getDecisionText(feedback.decision) || 'N/A';
    document.getElementById('detailOverallComments').textContent = feedback.finalComments || 'No comments';

    // Skills Evaluation
    const skillsTable = document.getElementById('detailSkillEvaluations');
    skillsTable.innerHTML = skills.length
        ? skills.map(skill => `
            <tr>
                <td>${skill.skillName || 'N/A'}</td>
                <td>${skill.rating || 'N/A'}</td>
                <td>${skill.topics || 'N/A'}</td>
                <td>${skill.comments || 'N/A'}</td>
            </tr>
        `).join('')
        : '<tr><td colspan="4">No skills evaluated</td></tr>';

    // Show modal
    new bootstrap.Modal(document.getElementById('feedbackDetailModal')).show();
}

async function deleteFeedback(feedbackId, buttonElement) {
    if (!confirm('Are you sure you want to delete this feedback? This action cannot be undone.')) {
        return;
    }

    try {
        // Disable the button during deletion
        buttonElement.disabled = true;
        buttonElement.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Deleting...';

        const response = await fetch(`/api/feedback/${feedbackId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            // Show success message
            alert('Feedback deleted successfully');
            // Refresh the feedback list
            loadAllFeedbacks();
        } else {
            throw new Error('Failed to delete feedback');
        }
    } catch (error) {
        alert('Error deleting feedback: ' + error.message);
    } finally {
        // Re-enable the button if deletion fails
        buttonElement.disabled = false;
        buttonElement.textContent = 'Delete';
    }
}