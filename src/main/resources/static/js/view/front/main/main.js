function readJWT() {
    const jwt = localStorage.getItem("JWT");
    return jwt == null ? "" : jwt;
}

window.onload = () => {
    // load 
    if(readJWT().length == 0) {
        // redirect login page.
        location.href = "/login.html";
    } else {
        // load teams.
        alert("WELCOME USER!");
    }
}