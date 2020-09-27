$(document).ready(function () {
    function hideLoading() {
        $(".loading").remove();
    }

    $.ajax({
        url: "http://localhost:8080/random-fact"
    }).then(function (data) {
            $(".random-fact").append(data);
            hideLoading();
        }
    );
});
