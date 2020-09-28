$(document).ready(function () {
    function hideLoading() {
        $(".loading").remove();
    }

    $.ajax({
        url: "https://project-page-with-advices.herokuapp.com/random-fact"
    }).then(function (data) {
            $(".random-fact").append(data);
            hideLoading();
        }
    );
});
