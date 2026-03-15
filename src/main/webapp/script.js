/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


fetch ('/Zimobackend/api/stations')
        .then(response => {
            if (!response.ok){
                throw new Error("No stations found");
            }
            return response.json();
        })
        .then(stations => {
            displayMapMarkers(chargerMap, stations);
        })
        .catch(error => {
            console.error("Error occured while loading stations:", error);
        });