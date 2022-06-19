
const options = {
    enableHighAccuracy: true,
    timeout: 5000,
    maximumAge: 0
};


export const getCurrentLocation = (success, error) => navigator.geolocation.getCurrentPosition(success, error, options);
