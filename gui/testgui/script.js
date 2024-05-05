document.addEventListener('DOMContentLoaded', () => {
    function getWeekNumber(dateString) {
        const date = new Date(dateString);
        const oneJan = new Date(date.getFullYear(), 0, 1);
        const weekNum = Math.ceil((((date - oneJan) / 86400000) + oneJan.getDay() + 1) / 7);
        return weekNum;
    }

    const schemaList = document.querySelector('.schema');

    class PopupManager {
        constructor() {
            this.popupContainer = document.createElement('div');
            this.popupContainer.classList.add('popup-container');
            document.body.appendChild(this.popupContainer);
            this.activePopup = null;
        }

        createPopup(content) {
            const popup = document.createElement('div');
            popup.classList.add('popup');
            popup.innerHTML = content;
            popup.addEventListener('click', () => {
                this.hidePopup(popup);
            });
            return popup;
        }

        showPopup(popup) {
            if (this.activePopup) {
                this.hidePopup(this.activePopup);
            }
            this.activePopup = popup;
            this.popupContainer.appendChild(popup);
            setTimeout(() => popup.classList.add('show'), 10);
        }

        hidePopup(popup) {
            popup.classList.remove('show');
            this.activePopup = null;
            setTimeout(() => this.popupContainer.removeChild(popup), 300);
        }
    }

    const popupManager = new PopupManager();

    console.log('Before fetch');
    fetch('./data.json')
        .then(res => {
            console.log('Response:', res);
            return res.json();
        })
        .then(data => {
            console.log('Data:', data);
            const menu = document.querySelector('.menu');
            menu.addEventListener('click', (event) => {
                if (event.target.tagName === 'LI') {
                    const desiredWeek = parseInt(event.target.dataset.week);
                    schemaList.innerHTML = ''; 
                    const events = desiredWeek === 1 ? (data.week1 || []) : (data.week2 || []);
                    events.forEach((event, index) => {
                        console.log(`Processing event ${index + 1}:`, event);
                    
                        const startTime = new Date(event.startTime.$date).toLocaleTimeString('sv-SE', { hour: '2-digit', minute: '2-digit' });
                        const endTime = new Date(event.endTime.$date).toLocaleTimeString('sv-SE', { hour: '2-digit', minute: '2-digit' });
                    
                        let title = event.courseName || 'Okänt Ämne';
                        let teacher = event.teachers && event.teachers.join(', ') || 'Okänd Lärare';
                        let description = event.description || 'Ingen Beskrivning Tillgänglig';
                        let roomsContent = '';
                    
                        for (const room in event.rooms) {
                            if (event.rooms.hasOwnProperty(room)) {
                                console.log('Room:', room);
                                console.log('URL:', event.rooms[room]);
                                roomsContent += `
                                    <p><strong>Rum:</strong> <a href="${event.rooms[room]}" target="_blank">${room}</a></p>
                                `;
                            }
                        }
                    
                        const isLesson = !!event.courseName; 
                    
                        const popupContent = `
                            <div class="popup-content">
                                <h2>${title}</h2>
                                <p><strong>Start Tid:</strong> ${startTime}</p>
                                <p><strong>Slut Tid:</strong> ${endTime}</p>
                                <p><strong>Lärare:</strong> ${teacher}</p>
                                <p><strong>Beskrivning:</strong> ${description}</p>
                                ${roomsContent}
                            </div>
                        `;
                    
                        const schemaItem = document.createElement('div');
                        schemaItem.classList.add('schema-item');
                        schemaItem.innerHTML = `
                            <i class="${isLesson ? 'fa-solid fa-book' : 'fa-solid fa-calendar'}" style="color: ${isLesson ? 'brown' : 'black'}"></i>
                            <div class="event-info">
                                <span class="time">${startTime} - ${endTime}</span>
                                <span class="title">${title}</span>
                            </div>
                        `;
                        schemaItem.addEventListener('click', () => {
                            const popup = popupManager.createPopup(popupContent);
                            popupManager.showPopup(popup);
                        });
                        schemaList.appendChild(schemaItem);
                    });
                }
            });
        })
        .catch(error => {
            console.error('Error fetching data:', error);
        });

    console.log('After fetch');

    const weekNumber = getWeekNumber('2024-04-08T06:15:00.000Z');
    console.log('Week number:', weekNumber);
});
