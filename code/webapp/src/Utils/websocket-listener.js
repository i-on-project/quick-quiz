import SockJS from 'sockjs-client'
import Stomp from 'stompjs'

export const register = (registrations) => {
    const socket = SockJS('/joinedsession'); // <3>
    const stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        registrations.forEach(function (registration) { // <4>
            stompClient.subscribe(registration.route, registration.callback);
        });
    });
}