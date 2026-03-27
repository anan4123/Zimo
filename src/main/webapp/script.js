
// --------------SEARCH FUNCTION---------------
function user_searchBox() {
    const userInput = document.getElementById("userInput");
    const searchBox = new google.maps.places.SearchBox(userInput);

    chargerMap.addListener("bounds_changed", () => {
        searchBox.setBounds(chargerMap.getBounds());
    });               
            
     searchBox.addListener("places_changed", () => {
        searchResultHandler(searchBox);
                
    });
        
}       
                

function searchResultHandler(searchBox) {
    const selectedLocation = searchBox.getPlaces();
           
    if (!selectedLocation || selectedLocation.length === 0) return;

    searchMarkers.forEach(marker => marker.setMap(null));
    searchMarkers = [];

    const searchresult_Bounds = new google.maps.LatLngBounds();

    selectedLocation.forEach(location => {
    if (!location.geometry || !location.geometry.location) return;
                
    const lat = location.geometry.location.lat();
    const lng = location.geometry.location.lng();            

    if (lat > 51.7 || lat < 51.3 ||
            lng > 0.3 || lng < -0.46){
        alert ("Please search for an address within Greater London.");
        return;
    }

    const stationMarker = new google.maps.Marker({
        map: chargerMap,
        title: location.name,
        position: location.geometry.location
    });

    searchMarkers.push(stationMarker);

    if (location.geometry.viewport) {
        searchresult_Bounds.union(location.geometry.viewport);
    } else {
        searchresult_Bounds.extend(location.geometry.location);
    }
    });

    chargerMap.fitBounds(searchresult_Bounds);

}
        
        
function loadMapMarkers(){
    console.log("Loading stations....");

    fetch('/Zimobackend/api/stations')
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to load stations");
            }
            return response.json();   
        })
        .then(stations => {
            console.log("Stations has been loaded: ", stations);
            zimoStations = stations;
            displayMapMarkers(chargerMap, stations);
        })

        .catch(error => {
            console.error("Error occured while loading stations", error);
            alert("No stations found");
        });
}
        
        
async function displayMapMarkers(map, stations){

    const {AdvancedMarkerElement} = await google.maps.importLibrary("marker");

   stations.forEach(station => {
        const stationPin = document.createElement('button');
        stationPin.className = 'station-pin';
        stationPin.textContent = '🔋';

        const marker = new AdvancedMarkerElement({
            map: map,
            position: {lat: station.lat, lng: station.lng},
            title: station.name,
            content: stationPin
        });

        marker.addListener('click', () => {
            displayStationPanel(station);
        });

        stationMarkers.push(marker);
    });    
}        
        
function displayStationPanel(station){
    const panel = document.querySelector('.station_panel');
    const stationInformation = panel.querySelector('.information');

    fetch (`/Zimobackend/api/stations/${station.id}/chargers`)
        .then(response => response.json())
        .then(chargers => { 
            let USB_available = 0;
            let Lightning_available = 0;


            chargers.forEach(charger => {
                if (charger.status === "available" && charger.chargerType === "USB-C"){
                    USB_available++;
                }
                if (charger.status === "available" && charger.chargerType === "Lightning"){
                    Lightning_available++;
                }
            });

            stationInformation.innerHTML = `
                <div class = "station_details">
                    <h3>${station.name}</h3>
                    <p><strong>Address: </strong>${station.address}</p>
                    <p><strong>Postcode: </strong>${station.postcode}</p>

                    <br>
                    <div class = "charger_details">
                        <p><strong>Available Chargers: </strong></p>
                        <p>USB-C: ${USB_available}/2</p>
                        <p>Lightning: ${Lightning_available}/2</p>
                    </div>

                    <label><strong>Charger Type:</strong><label>

                    <select id = "charger_options">
                        <option value = "USB-C"> USB-C</option>
                        <option value = "Lightning">Lightning</option>
                    </select>           

                    <br>

                    <div class = "buttons">
                        <button id = "rent">Rent</button>
                        <button id = "return">Return</button>
                    </div>
                </div>
        
            `;
        
            document.getElementById("rent").addEventListener('click', () => {
                const type = document.getElementById('charger_options').value;
                rentCharger(station.id, type);
            });
            
            document.getElementById("return").addEventListener('click', () => {
                const type = document.getElementById('charger_options').value;
                returnCharger(station.id, type);
            });
            
            
            
    })
        .catch (error => {
            stationInformation.innerHTML = `
                <div class = "station_details">
                    <h3>${station.name}</h3>
                    <p><strong>Address: </strong>${station.address}</p>
                    <p><strong>Postcode: </strong>${station.postcode}</p>

                </div>
            `;
        });
        panel.classList.add('open');
}
        
function closeStationPanel(){
    const panel = document.querySelector('.station_panel');
    document.querySelector ('.station_panel').classList.remove('open');
}

function rentCharger(stationId, type){
    fetch(`/Zimobackend/api/stations/${stationId}/chargers`)
            .then(response => response.json())
            .then(chargers => {
                let charger_selected = null;
        
                for (let i = 0; i < chargers.length; i++){
                if (chargers[i].chargerType === type && chargers[i].status === "available"){
                    charger_selected = chargers[i];
                    break;
                }
            }

            if (charger_selected === null){
                alert(`There are no ${type} chargers available.`);
                return;
            }

            return fetch(`/Zimobackend/api/chargers/${charger_selected.id}/rent`, {method: `POST` });
        })
                .then(response => response.json())
                .then(rentResult => {
                    alert ("Charger rented successfully");
                    const station = zimoStations.find(station => station.id === stationId);
                    displayStationPanel(station);
                })
                .catch(error => {
                    alert("Failed to rent charger, Please try again.");
                });
}

function returnCharger(stationId, type){
    fetch(`/Zimobackend/api/stations/${stationId}/chargers`)
            .then(response => response.json())
            .then(chargers => {
                let charger_selected = null;
        
                for (let i = 0; i < chargers.length; i++){
                if (chargers[i].chargerType === type && chargers[i].status === "in_use"){
                    charger_selected = chargers[i];
                    break;
                }
            }

            if (charger_selected === null){
                alert(`No ${type} chargers have been borrowed.`);
                return;
            }

            return fetch(`/Zimobackend/api/chargers/${charger_selected.id}/return`, {method: `POST` });
        })
                .then(response => response.json())
                .then(returnResult => {
                    alert ("Charger returned successfully");
                    const station = zimoStations.find(station => station.id === stationId);
                    displayStationPanel(station);
                })
                .catch(error => {
                    alert("Failed to return charger, Please try again.");
                });
    
}


const USBC_option = document.getElementById('USB-C');
const Lightning_option = document.getElementById('Lightning');

function filterStations(){
    const display_USB = USB_option.checked;


}


function submitFeedback(event){
    event.preventDefault();
    const form = document.getElementById('feedback_form');
    
    
    const feedbackData = {
        topic: document.getElementById('feedback_topic').value,
        suggestion: document.getElementById('suggestion').value,
        recommendation: document.querySelector('input[name = "recommend"]:checked')?.value || ''
    };
    
    fetch ('/Zimobackend/api/feedback', {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(feedbackData)
    })
    .then (response => response.json())
    .then(data => {
        alert("Feedback submitted successfully!");
        form.reset();
    })
    .catch(error => alert("Error occured while submitting feedback")); 
}

const form = document.getElementById('feedback_form');
if (form){
    form.addEventListener('submit', submitFeedback);
}