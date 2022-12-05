function validateName(name) {
    let str = name.value;
    if (str.length < 3 || str.length > 16) {
        alert('Username must be between 3 and 16 characters');
        return;
    }
    let re1 = /^[a-zA-Z]/;
    if (!re1.test(str)) {
        alert('Username must start with a letter.');
        return;
    }
    let re2 = /^[a-zA-Z][a-zA-Z0-9_]{2,16}$/;
    if (!re2.test(str)) {
        alert('Username can only contain letter, digit, or underscore characters.');
        return;
    }
}
