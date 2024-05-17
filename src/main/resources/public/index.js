
window.onload = fetchEvents();


async function fetchEvents() {
    try {
        console.log("hej: " + document.cookie);
        const response = await fetch('/api/v1/user/schedule', {method: 'GET', headers: {'Authorization': 'Bearer ' + getAuthToken()}});
        const schedules = await response.json();
        console.log("ok");
        const schedule = schedules[0];
        const events = schedule.events;
        displayEvents(events);
        console.log(events);
    } catch (error) {
        console.error('Error fetching events:', error);
        //window.location.href = 'templogin.html';
    }
}

function getAuthToken() {
    const cookies = document.cookie.split(';').map(cookie => cookie.trim());
    console.log(cookies);
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

function redirectToSettings() {
    window.location.href = '/settings';
}

function logout() {
    // Delete the JWT cookie
    deleteCookie('auth_token');

    // Optionally clear other related storage if used
    localStorage.removeItem('auth_token');
    sessionStorage.removeItem('auth_token');

    // Redirect to login page or homepage after logging out
    window.location.href = '/'; // Adjust the URL as needed
}

function deleteCookie(name) {
    document.cookie = name + '=; Max-Age=0; path=/; domain=' + window.location.hostname + '; secure; SameSite=Strict';
}



function displayEvents(events) {
    const scheduleContainer = document.getElementById('scheduleContainer');

    let previousStartTime = "";
    let currentStartTime;

    events.forEach(event => {

        currentStartTime = convertToSwedishTime(event.startTime).substring(0, 10);

        if (currentStartTime === previousStartTime) {
            makeLessonDiv(event);
        } else {
            makeDayHeader(currentStartTime);
            makeLessonDiv(event);
        }

        previousStartTime = currentStartTime;

    });

    function makeDayHeader(date) {
        let day = getDayOfWeek(date);
        let formattedDate = formatDate(date);

        const dayDiv = document.createElement('div');
        dayDiv.classList.add('dayContainer');
        scheduleContainer.appendChild(dayDiv);

        const dayHeader = document.createElement('h3');
        dayHeader.textContent = day + " " + formattedDate;
        dayDiv.appendChild(dayHeader);
    }

    function formatDate(dateString) {
        // Create a new Date object from the dateString
        const date = new Date(dateString);
    
        // Extract day and month components
        const day = date.getDate();
        const month = date.getMonth() + 1; // Month starts from 0, so add 1
    
        // Return formatted date string without leading zeros
        return day + '/' + month;
    }

    function getDayOfWeek(date) {
        // Create a new Date object with the specified date
        var date = new Date(date);

        // Get the day of the week as int
        var dayOfWeek = date.getDay();

        // Convert the numeric representation of the day to its corresponding name
        var days = ['Söndag', 'Måndag', 'Tisdag', 'Onsdag', 'Torsdag', 'Fredag', 'Lördag'];
        var dayName = days[dayOfWeek];
        return dayName;
    }

    function makeLessonDiv(event) {
        const eventDiv = document.createElement('div');
        eventDiv.classList.add('eventContainer');
        eventDiv.addEventListener('click', function() {showPopUp(event)});
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
        infoHeader.textContent = convertToSwedishTime(event.startTime).substring(11, 16) + "-" + convertToSwedishTime(event.endTime).substring(11, 16) + ", " + event.courseName;
        infoTextDiv.appendChild(infoHeader);

        const infoRooms = document.createElement('p');
        infoRooms.textContent = "Rum: " + Object.keys(event.rooms).join(', ');
        infoTextDiv.appendChild(infoRooms);  

        const infoTeachers = document.createElement('p');
        infoTeachers.textContent = "Lärare: " + event.teachers.join(', ');
        infoTextDiv.appendChild(infoTeachers);
    }

    let popUpDiv; // Declare popUpDiv outside the function so it's accessible globally
    let popUpIsActive = false;

    function showPopUp(event) {
        if (!popUpIsActive) {
            content = document.getElementById('scheduleContainer');
            content.style.filter = "blur(3px)";
            popUpIsActive = true;
            makePopUpDiv(event);
            popUpDiv.style.display = 'block';
            // Delay adding the event listener to close the popup when clicked outside
            setTimeout(function() {
                document.addEventListener('click', closePopup);
            }, 100);
        }
    }

    // Function to close the popup when clicked outside the popup
    function closePopup(clickEvent) {
        const isClickedInsidePopup = popUpDiv.contains(clickEvent.target);
        if (!isClickedInsidePopup) {
            content = document.getElementById('scheduleContainer');
            content.style.filter = "none";
            popUpDiv.style.display = 'none';
            document.removeEventListener('click', closePopup);
            popUpIsActive = false;
        }
    }

    function makePopUpDiv(event) {

        console.log(event);
        popUpDiv = document.createElement('div');
        popUpDiv.classList.add('popUp');
        popUpDiv.id = "popUp"; // Set an id for the popup
        document.body.appendChild(popUpDiv);

        popUpInfoHeader = document.createElement('h4');
        popUpInfoHeader.textContent = event.courseName;
        popUpDiv.appendChild(popUpInfoHeader);

        timeParagraph = document.createElement('p');
        timeParagraph.innerHTML = "<strong>Tid: </strong>" + getDayOfWeek(event.startTime) + " " + formatDate(event.startTime) + " kl "+ convertToSwedishTime(event.startTime).substring(11, 16) + "-" + convertToSwedishTime(event.endTime).substring(11, 16); // Clearing previous content
        popUpDiv.appendChild(timeParagraph);
        
        teacherParagraph = document.createElement('p');
        teacherParagraph.innerHTML = "<strong>Lärare: </strong>" + event.teachers.join(', ');
        popUpDiv.appendChild(teacherParagraph);

        roomsParagraph = document.createElement('p');
        roomsParagraph.innerHTML = "<strong>Rum: </strong>"; // Clearing previous content

        // Iterate through the keys and values of event.rooms
        for (const roomName in event.rooms) {
            if (event.rooms.hasOwnProperty(roomName)) {
                const roomLink = event.rooms[roomName];
                const roomAnchor = document.createElement('a');
                roomAnchor.textContent = roomName;
                roomAnchor.href = roomLink;
                roomAnchor.target = "_blank";
                roomsParagraph.appendChild(roomAnchor);

                // Add a comma and space after each anchor except the last one
                if (Object.keys(event.rooms).indexOf(roomName) < Object.keys(event.rooms).length - 1) {
                    roomsParagraph.appendChild(document.createTextNode(', '));
                }
            }
        }
        popUpDiv.appendChild(roomsParagraph);


        descriptionParagraph = document.createElement('p');
        descriptionParagraph.innerHTML = "<strong>Beskrivning: </strong>" + event.description;
        popUpDiv.appendChild(descriptionParagraph);

    }

    function convertToSwedishTime(utcTimeString) {
        const utcDate = new Date(utcTimeString);
        const swedishTime = utcDate.toLocaleString('sv-SE', { timeZone: 'Europe/Stockholm', hour12: false });
        return swedishTime;
    }
}