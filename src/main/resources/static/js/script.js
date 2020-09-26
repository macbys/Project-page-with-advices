$(document).ready(function() {

    if(errorMsg == 'Question must be between 8 and 1800 characters long') {
        $("#editQuestion"). modal('show');
    }

    $(function () {
        $('[data-toggle="popover"]').popover()
    })

    $('.popover-dismiss').popover({
        trigger: 'focus'
    });

    $('#deleteComment').on('hidden.bs.modal', function () {
        let deleteCommentUri = $("#deleting-comment-form").attr("action");
        let regex = /[/](\d+)[/]delete$/;
        deleteCommentUri = deleteCommentUri.replace(regex, "");
        $("#deleting-comment-form").attr("action", deleteCommentUri);
    });

    $("body").on("click", ".delete-comment-button", function () {
        let commentId = $(this).attr("comment-id");
        $("#deleting-comment-id").val(commentId);
        let deleteCommentUri = $("#deleting-comment-form").attr("action")
        $("#deleting-comment-form").attr("action", deleteCommentUri + '/' + commentId + '/delete');
    });

    $("a[id^='clickToGetMoreComments']").click(function () {
        let answerWithComments;
        let answerId = $(this).attr("value");
        for (i = 0; i < answersWithComments.length; i++) {
            if(answersWithComments[i].answerDTO.id == answerId) {
                answerWithComments = answersWithComments[i];
                break;
            }
        }
        let pageNumber = Number($(this).attr("page"));
        $(this).attr("page", pageNumber + 1);
        $.ajax({
            url: "http://localhost:8080/answer/" + answerId + "/comments",
            data: {page: pageNumber, size: 5}
        }).then(function(data) {
            for(i = 0; i < data.content.length; i++) {
                let comment = data.content[i];
                let image;
                if(comment.userDTO.avatar == null) {
                    image = "/images/user.png"
                } else {
                    image = "data:image/png;base64,"+ comment.userDTO.avatar;
                }
                let regex = /</g;
                comment.value = comment.value.replace(regex, " &lt;");
                if(principal == 'anonymousUser' || principal.email != comment.userDTO.email) {
                    $("#comments" + answerId).append("<hr style=\"background-color:white; border:white 1px solid;\">\n" +
                        "                                <div>\n" +
                        "                                    <div class=\"row\">\n" +
                        "                                        <img class=\"col-1\" height=\"50\" width=\"50\" th:src='" + image + "'>\n" +
                        "                                        <div class=\"col-11 row\">\n" +
                        "                                            <div class=\"col-3 mr-auto mb-2 font-weight-bold\" >" + comment.userDTO.name + "</div>\n" +
                        "                                            <div class=\"col-2\" >" + comment.creationDate + "</div>\n" +
                        "                                            <div class=\"col-12 white-space-pre-wrap mb-3\" th:text=\"${commentDTO.value}\">" + comment.value +"</div>\n" +
                        "                                        </div>\n" +
                        "                                    </div>\n" +
                        "                                </div>");
                } else {
                    $("#comments" + answerId).append("<hr style=\"background-color:white; border:white 1px solid;\">\n" +
                        "<div class=\"row\">\n" +
                        "                                    <img class=\"col-1\" height=\"50\" width=\"50\" src='" + image + "'>\n" +
                        "                                    <div class=\"col-11 row\">\n" +
                        "                                        <div class=\"col-10 mb-2 font-weight-bold\" >" + comment.userDTO.name + "</div>\n" +
                        "                                        <div class=\"col-2\" >" + comment.creationDate + "</div>\n" +
                        "                                        <div class=\"col-11 white-space-pre-wrap\" >" + comment.value + "</div>\n" +
                        "                                        <div class=\"col-1\">\n" +
                        "                                            <a comment-id = '" + comment.id + "' class=\"btn btn-danger delete-comment-button\" role=\"button\" type=\"button\" data-toggle=\"modal\" data-target=\"#deleteComment\">Delete</a>\n" +
                        "                                        </div>\n" +
                        "                                    </div>\n" +
                        "                                </div>");
                }
            }
            if(data.last) {
                $("#clickToGetMoreComments" + answerId).remove();
            }
        });
    });
});
