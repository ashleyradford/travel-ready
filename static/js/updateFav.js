async function updateFav(hotelid) {
    let response = await fetch('/fav-helper?hotelid=' + hotelid, {method :'get'});
    let jo = await response.json();

    // check if favorited or not
    if (jo.fav === false) {
        document.getElementById("fav-heart").innerHTML = "<i class=\"bi bi-heart\"></i>";
    } else {
        document.getElementById("fav-heart").innerHTML = "<i class=\"bi bi-heart-fill\"></i>";
    }
}
