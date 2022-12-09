async function checkWeather(hotelid) {
    let response = await fetch('/weather?hotelid=' + hotelid, {method :'get'});
    let jo = await response.json();
    let weather = "Temperature: " + jo.temp + " °F<br>Wind speed: " + jo.wind + " km/h<br>" + jo.desc;
    document.getElementById("weather").innerHTML = weather;
}
