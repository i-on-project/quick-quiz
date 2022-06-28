const options = {
    enableHighAccuracy: true,
    timeout: 5000,
    maximumAge: 0
};

export const getCurrentLocation = (onSuccess, onError) => navigator.geolocation.getCurrentPosition(onSuccess, onError, options);