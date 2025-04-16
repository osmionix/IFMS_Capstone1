document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('calendar')) {
        initCalendar();
    }
});

function initCalendar() {
    const calendarEl = document.getElementById('calendar');
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        events: fetchInterviews,
        eventClick: handleEventClick,
        eventDidMount: styleEventBasedOnRound
    });
    calendar.render();
}

function getUserData() {
    try {
        // First try to parse the user object from sessionStorage
        const userJson = sessionStorage.getItem('user');
        if (userJson) {
            return JSON.parse(userJson);
        }

        // Fallback to individual sessionStorage items
        return {
            id: sessionStorage.getItem('id'),
            email: sessionStorage.getItem('email'),
            role: sessionStorage.getItem('role'),
            token: sessionStorage.getItem('token')
        };
    } catch (e) {
        console.error('Error parsing user data:', e);
        return {};
    }
}

async function fetchInterviews(fetchInfo, successCallback, failureCallback) {
    try {
        const userData = getUserData();
        const token = userData.token;
        const userId = userData.id;
        const role = userData.role;

        if (!token || !userId || !role) {
            throw new Error('Missing user session data');
        }

        let response;
        if (role === "HR") {
            response = await fetch(`http://localhost:8082/api/interviews/all`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
        } else {
            response = await fetch(`http://localhost:8082/api/interviews?interviewer=${userId}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
        }

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const interviews = await response.json();
        const events = interviews.map(interview => ({
            id: interview.id,
            title: `Interview: ${interview.candidate?.name || 'Candidate'} (${interview.round})`,
            start: interview.interviewTime,
            end: new Date(new Date(interview.interviewTime).getTime() + 60*60*1000),
            extendedProps: {
                candidateName: interview.candidate?.name,
                candidateEmail: interview.candidate?.email,
                round: interview.round,
                jobRole: interview.candidate?.jobRole,
                status: interview.status
            }
        }));
        successCallback(events);
    } catch (error) {
        console.error('Error fetching interviews:', error);
        failureCallback(error);
    }
}

function styleEventBasedOnRound(info) {
    const event = info.event;
    const round = event.extendedProps.round;
    const status = event.extendedProps.status;

    if (round === "L1") {
        info.el.style.backgroundColor = status === 'COMPLETED' ? '#90EE90' : '#808080';
    } else if (round === 'L2') {
        info.el.style.backgroundColor = status === 'COMPLETED' ? '#CCFD7F' : '#C4A484';
    }
    info.el.style.borderColor = info.el.style.backgroundColor;
    info.el.style.cursor = 'pointer';
}

function handleEventClick(info) {
    const event = info.event;

    if (new Date(event.start) < new Date()) {
        window.location.href = `/html/view-feedback.html?interviewId=${event.id}`;
    } else {
        alert('This interview is scheduled for the future');
    }
}