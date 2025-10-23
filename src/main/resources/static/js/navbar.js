const navForm = document.getElementById("navSearchForm");

navForm.addEventListener("submit", function() {

    const params = new URLSearchParams(window.location.search);

    params.delete("q");

    for (const [key, value] of params.entries()) {
        const hidden = document.createElement("input");
        hidden.type = "hidden";
        hidden.name = key;
        if(key == "page_no"){
            hidden.value = 0;
        }else{
            hidden.value = value;
        }
        navForm.appendChild(hidden);
    }
});