import {onClickLoginButton} from './onClickLoginButton.js';


export function initLoginRequestButton() {
    const button = document.getElementById("button-login-request");
    button.addEventListener("click", onClickLoginButton);
}


window.onload = () => {
    initLoginRequestButton();
}