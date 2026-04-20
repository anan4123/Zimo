
// --------------SEARCH FUNCTION---------------
// Code adapted from Google Maps API Documentation - Places Search Box (Google, 2026)
function user_searchBox() {
    // Create new searchbox instance for userInput
    const userInput = document.getElementById("userInput"); 
    const searchBox = new google.maps.places.SearchBox(userInput);

    // Update search box bounds when map bounds change
    chargerMap.addListener("bounds_changed", () => {
        searchBox.setBounds(chargerMap.getBounds());
    });               
            
    // Handle new search results when places change
     searchBox.addListener("places_changed", () => {
        searchResultHandler(searchBox);
                
    });
}       
   
// --------------SEARCH RESULT HANDLER---------------
// Code adapted from Google Maps API Documentation - Places Search Box (Google, 2026)
// Function used for handling search results from user
function searchResultHandler(searchBox) {
    //Get selected location
    const selectedLocation = searchBox.getPlaces();
           
    if (!selectedLocation || selectedLocation.length === 0) return;

    // Remove existing map markers on map
    searchMarkers.forEach(marker => marker.setMap(null));
    searchMarkers = [];

    const searchresult_Bounds = new google.maps.LatLngBounds();

    selectedLocation.forEach(location => {
    if (!location.geometry || !location.geometry.location) return;
                
    const lat = location.geometry.location.lat();
    const lng = location.geometry.location.lng();            

    // Check if location is within Greater London
    if (lat > 51.7 || lat < 51.3 ||
            lng > 0.3 || lng < -0.46){
        // Display error if address is outside the area
        alert ("Please search for an address within Greater London.");
        return;
    }
    
    //Create marker for new location on chargerMap
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
        
// --------------RETRIEVE STATIONS FROM ARRAYLIST---------------
// Code adapted from MDN - Make network requests with JavaScript (MDN, 2025)  
// Function loads stations from in-memory list and display map markers
function loadMapMarkers(){
    console.log("Loading stations....");

    // Code Adapted from GeeksforGeeks - How to connect Front End and Back End (GeeksforGeeks, 2025)
    fetch('/Zimo/api/stations') // GET request to the endpoint
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to load stations");
            }
            return response.json();   
        })
        .then(stations => {
            console.log("Stations has been loaded: ", stations);
            zimoStations = stations; //Store stations into a global variable
            displayMapMarkers(chargerMap, stations); //Display station markers on the map
        })

        .catch(error => { // Error if there is an issue during the fetch operation
            console.error("Error occured while loading stations", error);
            alert("No stations found");
        });
}
        

// --------------DISPLAY MAP MARKERS---------------
// Display Map Marker on the Zimo Map
// Implementation adapted from Google Maps JavaScript API - Create markers with HTML and CSS (Google, 2026)
async function displayMapMarkers(map, stations){
    // Import marker library from Google Maps API
    // Adapted from Google Maps JavaScript API - Migrate to advanced markers
    const {AdvancedMarkerElement} = await google.maps.importLibrary("marker");

   stations.forEach(station => { //Loop to fetch all stations from back-end 
        const stationPin = document.createElement('button'); // Station markers are buttons
        stationPin.className = 'station-pin';
        stationPin.textContent = '🔋'; // Marker icon

        const marker = new AdvancedMarkerElement({ //Create new markers for each station
            map: map,
            position: {lat: station.lat, lng: station.lng}, // Station coordinates
            title: station.name, // Station name shown on station name
            content: stationPin // Battery icon
        });

        marker.addListener('click', () => {
            displayStationPanel(station); // Open station panel when station marker is clicked
        });

        stationMarkers.push(marker); // Store marker in array
    });    
}    

// --------------STATION PANEL---------------
// Code adopted from Google Maps JavaScript API - Create interactive markers using HTML and CSS (Google, 2026)
function displayStationPanel(station){
    const panel = document.querySelector('.station_panel');
    const stationInformation = panel.querySelector('.information');

    // Code Adapted from GeeksforGeeks - How to connect Front End and Back End (GeeksforGeeks, 2025)
    fetch (`/Zimo/api/stations/${station.id}/chargers`) //Fetch all chargers at this station
        .then(response => response.json())
        .then(chargers => { 
            let USB_available = 0; //Counters for each charger type
            let Lightning_available = 0;


            chargers.forEach(charger => { //Loop through the chargers
                if (charger.status === "available" && charger.chargerType === "USB-C"){ // If charger is available and is USB-C charger
                    USB_available++; 
                }
                if (charger.status === "available" && charger.chargerType === "Lightning"){ //If charger is available and is Lightning charger
                    Lightning_available++; //Add charger counter
                }
            });

            //Content of station panel
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
            
            // Rent Button
            document.getElementById("rent").addEventListener('click', () => {
                const type = document.getElementById('charger_options').value; //Gets selected charger type
                rentCharger(station.id, type); //Call rentCharger function
            });
            
            //Return Button
            document.getElementById("return").addEventListener('click', () => {
                const type = document.getElementById('charger_options').value;
                returnCharger(station.id, type); //Calls returnCharger function
            });

    })
        .catch (error => { //Error is displayed if the API request fails
            //Station information is displayed only
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
 // --------------CLOSE STATION ---------------       
function closeStationPanel(){
    const panel = document.querySelector('.station_panel');
    document.querySelector ('.station_panel').classList.remove('open');
}

function rentCharger(stationId, type){ //Function is called when rent button is pressed
    // Code Adapted from GeeksforGeeks - How to connect Front End and Back End (GeeksforGeeks, 2025)
    fetch(`/Zimo/api/stations/${stationId}/chargers`) //Fetch all chargers from the station
            .then(response => response.json())
            .then(chargers => {
                let charger_selected = null; //charger selected by rhe system is null 
        
                for (let i = 0; i < chargers.length; i++){ //Loop through list of chargers
                if (chargers[i].chargerType === type && chargers[i].status === "available"){
                    charger_selected = chargers[i]; //If charger type matches and is available, the charger is selected
                    break;
                }
            }
 
            if (charger_selected === null){
                alert(`There are no ${type} chargers available.`); //Error message if no chargers are available to rent
                return;
            }
            // Code Adapted from GeeksforGeeks - How to connect Front End and Back End (GeeksforGeeks, 2025)
            return fetch(`/Zimo/api/chargers/${charger_selected.id}/rent`, {method: `POST` }); //POST request to back-end
        })
                .then(response => response.json())
                .then(rentResult => {
                    alert ("Charger rented successfully"); //Confirmation message
                    const station = zimoStations.find(station => station.id === stationId);
                    displayStationPanel(station); //Refresh station panel
                })
                .catch(error => {
                    alert("Failed to rent charger, Please try again.");
                });
}

function returnCharger(stationId, type){ //Function is called when return button is clicked
    // Code Adapted from GeeksforGeeks - How to connect Front End and Back End (GeeksforGeeks, 2025)
    fetch(`/Zimo/api/stations/${stationId}/chargers`) //Fetch all chargers from station
            .then(response => response.json())
            .then(chargers => {
                let charger_selected = null; 
        
                for (let i = 0; i < chargers.length; i++){ //Loop through the list of chargers
                if (chargers[i].chargerType === type && chargers[i].status === "in_use"){ //If charger is in_use
                    charger_selected = chargers[i]; //The charger_selected is charger ID
                    break; 
                }
            }

            if (charger_selected === null){
                alert(`No ${type} chargers have been borrowed.`); //Error is displayed if no chargers have been borrowed
                return;
            }

            // Code Adapted from GeeksforGeeks - How to connect Front End and Back End (GeeksforGeeks, 2025)
            return fetch(`/Zimo/api/chargers/${charger_selected.id}/return`, {method: `POST` }); //POST request to back-end
        })
                .then(response => response.json())
                .then(returnResult => {
                    alert ("Charger returned successfully"); //Conformation message
                    const station = zimoStations.find(station => station.id === stationId);
                    displayStationPanel(station); //Refresh station panel
                })
                .catch(error => {
                    alert("Failed to return charger, Please try again.");
                });
    
}

const searchFilter = document.getElementById('filter_button');

//-----------OPEN FILTER PANEL-------------
//This function is called when the filter button has been clicked
function displayFilterPanel(){
    const panel = document.querySelector('.filter_panel');
    panel.classList.add('open'); // Open filter panel
    
}

//-----------CLOSE FILTER PANEL---------------
function closeFilterPanel(){
    const panel = document.querySelector('.filter_panel');
    document.querySelector ('.filter_panel').classList.remove('open');
    
}

searchFilter.onclick = displayFilterPanel;



const USBC_option = document.getElementById('USB-C');
const Lightning_option = document.getElementById('Lightning');


// --------------FILTER PANEL---------------
// Allows users to filter the map to display map markers by the charger type
function filterStations(){ //Function is called when the checkboxis selected
    
    const display_USBC = USBC_option.checked;
    const display_Lightning = Lightning_option.checked;
    
    
    if (!display_USBC && !display_Lightning){ //If no checkbox is selected, display both chargers
        stationMarkers.forEach(marker => marker.map = chargerMap);
        return;
    }
    
    stationMarkers.forEach((marker, i) => {
        const station = zimoStations[i];
         // Code Adapted from GeeksforGeeks - How to connect Front End and Back End (GeeksforGeeks, 2025)
        fetch(`/Zimo/api/stations/${station.id}/chargers`) //Fetch chargers at a specific station
                .then(response => response.json())
                .then(chargers => {
                    let display_markers = false;

                    chargers.forEach(charger => {
                        if (charger.status === "available") { // If the charger status is available display the station
                            if (display_USBC && charger.chargerType === "USB-C") //If USB-C is selected
                                display_markers = true; //Display station marker
                            if (display_Lightning && charger.chargerType === "Lightning")
                                display_markers = true;
                        }
                    });

                    if (display_markers) {
                        marker.map = chargerMap; //Show marker on map
                    }
                    else {
                        marker.map = null; //Hide marker from map

                    }
                });
    });
}

USBC_option.addEventListener('change', filterStations);
Lightning_option.addEventListener('change', filterStations);


 // --------------FEEDBACK SUBMISSION---------------     
// Code and logic was adapted from the MDN Document: querySelector() method (MDN, 2025)
function submitFeedback(event){
    event.preventDefault(); //Prevents browser refreshing after submission
    const form = document.getElementById('feedback_form');
    
    
    const feedbackData = {
        topic: document.getElementById('feedback_topic').value, //Retrieve selected topic
        suggestion: document.getElementById('suggestion').value, // Retrieve the entered suggestion
        recommendation: document.querySelector('input[name = "recommend"]:checked')?.value || '' // Retrieve the checked recommendation option
    };
    
    fetch ('/Zimo/api/feedback', { //POST data to the back-end API
        method: "POST",
        headers: {'Content-Type': 'application/json'}, //Data is sent as JSON format
        body: JSON.stringify(feedbackData) 
    })
    .then (response => response.json())
    .then(data => {
        alert("Feedback submitted successfully!"); //Output message if the submissiion was successful
        form.reset();
    })
    .catch(error => alert("Error occured while submitting feedback")); 
}

const form = document.getElementById('feedback_form');
if (form){
    form.addEventListener('submit', submitFeedback);
}