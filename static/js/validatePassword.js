function validatePassword(password) {
    let str = password.value;
    if (str.length < 8 || str.length > 20) {
        alert('Password must be between 8 and 20 characters');
        return;
    }
    let re1 = /[a-z]/;
    if (!re1.test(str)) {
        alert('Password must contain one lowercase letter');
        return;
    }
    let re2 = /[A-Z]/;
    if (!re2.test(str)) {
        alert('Password must contain one uppercase letter');
        return;
    }
    let re3 = /[0-9]/;
    if (!re3.test(str)) {
        alert('Password must contain one digit character');
        return;
    }
    let re4 = /[!@#$%&]/;
    if (!re4.test(str)) {
        alert('Password must contain one special character');
        return;
    }
}
