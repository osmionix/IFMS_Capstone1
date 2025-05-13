document.addEventListener('DOMContentLoaded', function () {
    const feedbackForm = document.getElementById('evaluationForm');
    loadInterviewDetails();

    if (feedbackForm) {
        feedbackForm.addEventListener('submit', submitFeedback);
    }
});

let interviewData = null;

async function loadInterviewDetails() {
    const urlParams = new URLSearchParams(window.location.search);
    const interviewId = urlParams.get('interviewId');

    if (!interviewId) {
        alert('No interview specified');
        window.location.href = '/html/interviewer-dashboard.html';
        return;
    }

    try {
        const token = localStorage.getItem('authToken');
        const response = await fetch(`/api/interviews/${interviewId}`, {
            headers: token ? { 'Authorization': `Bearer ${token}` } : {}
        });

        if (!response.ok) {
            throw new Error('Failed to load interview details');
        }

        interviewData = await response.json();

        const interviewDate = new Date(interviewData.interviewTime);
        const formattedDate = interviewDate.toLocaleDateString('en-IN', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });

        document.getElementById('interviewId').value = interviewData.id;
        document.getElementById('candidateName').value = interviewData.candidate?.name || 'N/A';
        document.getElementById('candidateEmail').value = interviewData.candidate?.email || 'N/A';
        document.getElementById('jobRole').value = interviewData.candidate?.jobRole || 'N/A';
        document.getElementById('interviewDate').value = formattedDate;
        document.getElementById('interviewRound').value = interviewData.round || 'N/A';

    } catch (err) {
        console.error("Failed to load interview details:", err);
        alert('Failed to load interview details. Please try again.');
        window.location.href = '/html/interviewer-dashboard.html';
    }
}

async function submitFeedback(e) {
    e.preventDefault();
    const form = document.getElementById('evaluationForm');

    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        return;
    }

    if (!interviewData) {
        alert('Interview details not loaded. Please try again later.');
        return;
    }

    const skillEvaluations = collectSkillEvaluations();
    const interviewDate = new Date(interviewData.interviewTime);

    const feedbackData = {
        interviewId: interviewData.id,
        candidateId: interviewData.candidate?.id,
        candidateName: interviewData.candidate?.name,
        candidateEmail: interviewData.candidate?.email,
        jobRole: interviewData.candidate?.jobRole,
        interviewDate: interviewDate.toISOString(),
        interviewerId: interviewData.interviewer?.id,
        round: interviewData.round,
        decision: document.getElementById('decision').value,
        overallComments: document.getElementById('overallComments').value,
        skillEvaluations: skillEvaluations
    };

    try {
        const token = localStorage.getItem('authToken');
        const response = await fetch('/api/feedback', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...(token && { 'Authorization': `Bearer ${token}` })
            },
            body: JSON.stringify(feedbackData)
        });

        // Check if response is OK before parsing it as JSON
        if (!response.ok) {
            const errorText = await response.text();
            console.error("Backend response:", errorText);
            throw new Error(errorText || 'Failed to submit feedback');
        }

        const responseData = await response.json();  // Parse the response JSON

        alert('Feedback submitted successfully!');
        window.location.href = '/html/interviewer-dashboard.html';

    } catch (error) {
        console.error('Error submitting feedback:', error);
        alert(`Error: ${error.message}`);
    }
}

function collectSkillEvaluations() {
    const skillEvaluations = [];
    const skillSelects = document.querySelectorAll('.skill-rating');

    skillSelects.forEach(select => {
        const skillName = select.dataset.skill;

        const evaluation = {
            skillName: skillName,
            rating: select.value,
            topicsUsed: document.querySelector(`input[data-skill="${skillName}"][placeholder="Topics evaluated"]`).value,
            comments: document.querySelector(`input[data-skill="${skillName}"][placeholder="Specific comments"]`).value
        };

        skillEvaluations.push(evaluation);
    });

    return skillEvaluations;
}
