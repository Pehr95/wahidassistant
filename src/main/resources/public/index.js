
window.onload = fetchEvents();


async function fetchEvents() {
    try {
        console.log("hej: " + document.cookie);
        const response = await fetch('http://192.168.1.70:8080/api/v1/schedules/all', {method: 'GET', headers: {'Authorization': 'Bearer ' + getAuthToken()}});
        const schedules = await response.json();
        console.log("ok");
        const schedule = schedules[0];
        const events = schedule.events;
        displayEvents(events);
    } catch (error) {
        console.error('Error fetching events:', error);
        window.location.href = 'templogin.html';
    }
}

function getAuthToken() {
    const cookies = document.cookie.split(';').map(cookie => cookie.trim());
    for (const cookie of cookies) {
        const [name, value] = cookie.split('=');
        if (name === 'auth_token') {
            console.log('Found auth token:', value)
            return value;
        }
    }
    console.log('No auth token found.')
    return null; // Return null if token is not found
}



function displayEvents(events) {
    const scheduleContainer = document.getElementById('scheduleContainer');

    events.forEach(event => {

        const eventDiv = document.createElement('div');
        eventDiv.classList.add('eventContainer');
        scheduleContainer.appendChild(eventDiv);


        const iconDiv = document.createElement('div');
        iconDiv.classList.add('iconContainer');
        eventDiv.appendChild(iconDiv);

        const icon = document.createElement('img');
        icon.src = 'icon-book.png';
        icon.width = 30;
        icon.height = 30;
        iconDiv.appendChild(icon);

        const infoTextDiv = document.createElement('div');
        infoTextDiv.classList.add('infoTextContainer');
        eventDiv.appendChild(infoTextDiv);


        const infoHeader = document.createElement('h5');
        infoHeader.textContent = convertToSwedishTime(event.startTime) + "-" + convertToSwedishTime(event.endTime) + ", " + event.courseName;
        infoTextDiv.appendChild(infoHeader);

        const infoRooms = document.createElement('p');
        infoRooms.textContent = "Rum: " + Object.keys(event.rooms).join(', ');
        infoTextDiv.appendChild(infoRooms);  

        const infoTeachers = document.createElement('p');
        infoTeachers.textContent = "Lärare: " + event.teachers.join(', ');
        infoTextDiv.appendChild(infoTeachers);


    });

    function convertToSwedishTime(utcTimeString) {
        const utcDate = new Date(utcTimeString);
        const swedishTime = utcDate.toLocaleString('sv-SE', { timeZone: 'Europe/Stockholm', hour12: false });
        return swedishTime.substring(11, 16);
    }

}