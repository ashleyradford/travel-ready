fetch('visit-navbar.html')
.then(res => res.text())
.then(text => {
    let oldelem = document.querySelector("script#replaceWithVisitNavbar");
    let newelem = document.createElement("div");
    newelem.innerHTML = text;
    oldelem.parentNode.replaceChild(newelem, oldelem);
})
