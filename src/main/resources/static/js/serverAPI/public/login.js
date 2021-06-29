import {LOGIN_API_URL} from "../url.js";
import {fetchResult} from '../fetch.js'
import {tryRequest} from '../tryRequest.js'
import {requestOption} from '../requestOption.js'


export async function login(id, password) {
    const loginResult = await tryRequest(
        async ({url, option, message}) => {
            return await fetchResult(url, option, message);
        },
        {
            url: LOGIN_API_URL,
            option: requestOption.post({"userId":id, "password":password}),
            message: "Failed to connect login server."
        },
        async (result, validation) => {
            return (
                result != null &&
                result.status == validation.requiredStatus &&
                result.headers.has('Authorization')
            );
        },
        {
            requiredStatus: 200
        },
        (result) => {
            localStorage.setItem("JWT", result.headers.get('Authorization'));
            return result.json();
        });

    console.log(loginResult);
}