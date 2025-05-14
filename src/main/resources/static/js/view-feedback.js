// Global variable to store interview data
let interviewData = null;

document.addEventListener('DOMContentLoaded', function() {
    // Set username from Session Storage
    document.getElementById('username').textContent = sessionStorage.getItem('email') || 'User';

    // Load interview details and feedback
    loadInterviewDetails();

    // Disable all form inputs
    disableFormInputs();
});

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

        // Fetch interview details
        const interviewResponse = await fetch(`/api/interviews/${interviewId}`, {
            headers: token ? { 'Authorization': `Bearer ${token}` } : {}
        });

        if (!interviewResponse.ok) {
            throw new Error('Failed to load interview details');
        }

        interviewData = await interviewResponse.json();

        // Format interview date
        const interviewDate = new Date(interviewData.interviewTime);
        const formattedDate = interviewDate.toLocaleDateString('en-IN', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });

        // Populate interview details
        document.getElementById('interviewId').value = interviewData.id;
        document.getElementById('candidateName').value = interviewData.candidate?.name || 'N/A';
        document.getElementById('candidateEmail').value = interviewData.candidate?.email || 'N/A';
        document.getElementById('jobRole').value = interviewData.candidate?.jobRole || 'N/A';
        document.getElementById('interviewDate').value = formattedDate;
        document.getElementById('interviewRound').value = interviewData.round || 'N/A';

        //Fetch feedback details
        const feedbackResponse = await fetch(`/api/feedback/${interviewId}`, {
            headers: token ? { 'Authorization': `Bearer ${token}` } : {}
        });

        if (!feedbackResponse.ok) {
            throw new Error('Failed to fetch feedback details');
        }

        const feedbackData = await feedbackResponse.json();

        if (feedbackData) {
            // Populate feedback details
            document.getElementById('decision').value = feedbackData.decision || '';
            document.getElementById('overallComments').value = feedbackData.overallComments || '';

            //Fetch skill evaluations
            const skillsResponse = await fetch(`/api/skills/by-feedback/${feedbackData.id}`, {
                headers: token ? { 'Authorization': `Bearer ${token}` } : {}
            });

            if (!skillsResponse.ok) {
                throw new Error('Failed to fetch skill evaluations');
            }

            const skillsData = await skillsResponse.json();

            // Populate skill evaluations
            if (skillsData && skillsData.length > 0) {
                populateSkillEvaluations(skillsData);
            }
        }
    } catch (err) {
          console.error("Failed to load data:", err);
          alert('Failed to load data. Please try again.');

          // Return to previous page
          window.history.back();
      }
}

function populateSkillEvaluations(skillsData) {

    const skillNameMap = {
        'BASIC_ALGORITHM': 'basicAlgorithm',
        'CODE_SYNTAX': 'codeAndSyntax',
        'DESIGN_PATTERNS': 'designPatterns',
        'SQL': 'sql',
        'GIT': 'git',
        'OVERALL_ATTITUDE': 'overallAttitude',
        'LEARNING_ABILITY': 'learningAbility',
        'RESUME_EXPLANATION': 'resumeExplanation',
        'COMMUNICATION': 'communication'
    };

    skillsData.forEach(skill => {
        const tableSkillName = skillNameMap[skill.skillName] || skill.skillName.toLowerCase();

        // Find the rating dropdown for this skill
        const ratingSelect = document.querySelector(`.skill-rating[data-skill="${tableSkillName}"]`);
        if (ratingSelect) {
            ratingSelect.value = skill.rating || 'NOT_EVALUATED';
        }

        // Find the topics input for this skill
        const topicsInput = document.querySelector(`input[data-skill="${tableSkillName}"][placeholder="Topics evaluated"]`);
        if (topicsInput) {
            topicsInput.value = skill.topics || '';
        }

        // Find the comments input for this skill
        const commentsInput = document.querySelector(`input[data-skill="${tableSkillName}"][placeholder="Specific comments"]`);
        if (commentsInput) {
            commentsInput.value = skill.comments || '';
        }
    });
}

function disableFormInputs() {
    // Disable all form controls
    const formControls = document.querySelectorAll('input, select, textarea');
    formControls.forEach(control => {
        control.disabled = true;
    });

    // Make disabled inputs look more "readonly"
    const inputs = document.querySelectorAll('input, textarea');
    inputs.forEach(input => {
        input.classList.add('bg-light');
    });

    const selects = document.querySelectorAll('select');
    selects.forEach(select => {
        select.classList.add('bg-light');
    });
}
