import {login} from '../../../serverAPI/public/login.js'
import { LOGIN_API_URL } from '../../../serverAPI/url.js';


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

    // await login(idForm.value, passwordForm.value);
    const result = await fetch(LOGIN_API_URL, {
        mode: "cors",
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            userId: idForm.value,
            password: passwordForm.value
        })
    });
    console.log(result);
    result.headers.forEach((B, A) => console.log(`${A}: ${B}`));
    console.log("RESPONSE BODY");
    console.log(await result.json());
    console.log("RESPONSE BODY END");

    // location.href = "/main.html";
}