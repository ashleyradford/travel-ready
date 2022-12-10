async function getReviews(username, hotelSearch, hotelid, offset) {
    let response = await fetch('/review-helper?hotelid=' + hotelid + "&page=" + offset, {method :'get'});
    let jsonObj = await response.json();
    let jsonArr = await jsonObj.reviews;

    let beg = "<div class=\"text-start\">";
    let info = "";
    for (let i = 0; i < jsonArr.length; i++) {
        info = info + "<p><b>" + jsonArr[i].rating + " stars ~</b> " + jsonArr[i].title + "</p>"
        + "<p><b>Written by:</b> " + jsonArr[i].username + "</p>"
        + "<p class=\"fw-light\">" + jsonArr[i].submissionDate + "</p>"
        + "<p class=\"fw-light\">" + jsonArr[i].text + "</p><br>"
        if ( jsonArr[i].username === username) {
            info = info + "<form method=\"get\" action=\"/edit-review\">"
                            + "<input type=\"hidden\" name=\"hotelSearch\" value=\"" + hotelSearch + "\">"
                            + "<input type=\"hidden\" name=\"hotelid\" value=\"" + hotelid + "\">"
                            + "<button type=\"submit\" class=\"btn btn-outline-success smoll\">Edit</button>"
                            + "</form>";
        }
        info = info + "<hr>";
    }

    let str = beg + info;
    document.getElementById("reviews").innerHTML = str;
}
