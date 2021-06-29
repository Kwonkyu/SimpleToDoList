import {login} from '../../../serverAPI/public/login.js'


const idForm = document.getElementById("form-id");
const passwordForm = document.getElementById("form-password");

export async function onClickLoginButton() {
    if(idForm.value.length < 1) {
        idForm.focus();
        return;
    }

    if(passwordForm.value.length < 1) {
        passwordForm.focus();
        return;
    }

    await login(idForm.value, passwordForm.value);
    location.href = "/main.html";
}