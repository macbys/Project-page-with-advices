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
        let answerId = $(this).attr("value");
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
                if((principal === 'anonymousUser' || principal.email !== comment.userDTO.email) && principal.authorities[0].authority !== 'ADMIN') {
                    $("#comments" + answerId).append("<hr style=\"background-color:white; border:white 1px solid;\">" +
                    "                                        <div class=\"row justify-content-between\">\n" +
                        "                                        <div class=\"col-auto pr-0\">\n" +
                        "                                            <a href=\"/user/" + comment.userDTO.id + "\">\n" +
                        "                                                <img height=\"50\" width=\"50\" \n" +
                        "                                                     src='" + image + "'>\n" +
                        "                                            </a>\n" +
                        "                                        </div>\n" +
                        "                                        <div class=\"col d-none d-sm-block font-weight-bold\">\n" +
                        "                                            <a \n" +
                        "                                               href=\"/user/" + comment.userDTO.id + "\">" + comment.userDTO.name + "</a>\n" +
                        "                                        </div>\n" +
                        "                                        <div class=\"col-5 col-sm-4 col-lg-2 text-right px-0 pr-3\"\n" +
                        "                                             >" + comment.creationDate + "</div>\n" +
                        "                                        <div class=\"col-sm-12 justify-content-center\">\n" +
                        "                                            <div class=\"col-sm-12 px-0 row justify-content-end m-0\">\n" +
                        "                                                <div class=\"col-12 px-0 font-weight-bold\">\n" +
                        "                                                    <a \n" +
                        "                                                    href=\"/user/" + comment.userDTO.id + "\">" + comment.userDTO.name + "</a>\n" +
                        "                                                </div>\n" +
                        "                                                <div class=\"col-12 white-space-pre-wrap mb-3\"\n" +
                        "                                                     >" + comment.value + "</div>\n" +
                        "                                            </div>\n" +
                        "                                        </div>\n" +
                        "                                    </div>");




                        // "                                <div \n" +
                        // "                                     class=\"row\">\n" +
                        // "                                    <div class=\"col-auto\">\n" +
                        // "                                        <a href=\"/user/" + comment.userDTO.id + "\">\n" +
                        // "                                           <img height=\"50\" width=\"50\" src='" + image + "'>\n" +
                        // "                                        </a>\n" +
                        // "                                    </div>\n" +
                        // "                                    <div class=\"col-11 row\">\n" +
                        // "                                        <div class=\"col-3 mr-auto mb-2 font-weight-bold\">\n" +
                        // "                                            <a \n" +
                        // "                                               href=\"/user/" + comment.userDTO.id + "\">" + comment.userDTO.name + "</a>\n" +
                        // "                                        </div>\n" +
                        // "                                        <div class=\"col-2\" >" + comment.creationDate + "</div>\n" +
                        // "                                        <div class=\"col-12 white-space-pre-wrap mb-3\"\n" +
                        // "                                             >" + comment.value + "</div>\n" +
                        // "                                    </div>\n" +
                        // "                                </div>");
                } else {
                    $("#comments" + answerId).append("<hr style=\"background-color:white; border:white 1px solid;\">" +
                    "                                        <div class=\"row justify-content-between\">\n" +
                        "                                        <div class=\"col-auto pr-0\">\n" +
                        "                                            <a href=\"/user/" + comment.userDTO.id + "\">\n" +
                        "                                                <img height=\"50\" width=\"50\"" +
                        "                                                     src='" + image + "'>\n" +
                        "                                            </a>\n" +
                        "                                        </div>\n" +
                        "                                        <div class=\"col d-none d-md-block font-weight-bold\">\n" +
                        "                                            <a \n" +
                        "                                               href=\"/user/" + comment.userDTO.id + "\">" + comment.userDTO.name + "</a>\n" +
                        "                                        </div>\n" +
                        "                                        <div class=\"col-5 col-sm-4 col-lg-2 text-right px-0\"\n" +
                        "                                            >" + comment.creationDate + "</div>\n" +
                        "                                        <div class=\"col-4 px-0 col-md-2 text-right\">\n" +
                        "                                            <a comment-id = "+ comment.id + "\n" +
                        "                                               class=\"btn btn-danger delete-comment-button\" role=\"button\" type=\"button\"\n" +
                        "                                               data-toggle=\"modal\" data-target=\"#deleteComment\">Delete</a>\n" +
                        "                                        </div>\n" +
                        "                                        <div class=\"col-sm-12 justify-content-center\">\n" +
                        "                                            <div class=\"col-sm-12 px-0 row justify-content-end m-0\">\n" +
                        "                                                <div class=\"col-12 px-0 d-md-none font-weight-bold\">\n" +
                        "                                                    <a \n" +
                        "                                                       href=\"/user/" + comment.userDTO.id + "\">" + comment.userDTO.name + "</a>\n" +
                        "                                                </div>\n" +
                        "                                                <div class=\"col-12 white-space-pre-wrap mb-3\"\n" +
                        "                                                       >" + comment.value + "</div>\n" +
                        "                                            </div>\n" +
                        "                                        </div>\n" +
                        "                                    </div>");


                        // "                                <div \n" +
                        // "                                     class=\"row\">\n" +
                        // "                                    <div class=\"col-auto\">\n" +
                        // "                                        <a href=\"/user/" + comment.userDTO.id + "\">\n" +
                        // "                                           <img height=\"50\" width=\"50\" src='" + image + "'>\n" +
                        // "                                        </a>\n" +
                        // "                                    </div>\n" +
                        // "                                    <div class=\"col-10 row\">\n" +
                        // "                                        <div class=\"col-3 mr-auto mb-2 font-weight-bold\">\n" +
                        // "                                            <a \n" +
                        // "                                               href=\"/user/" + comment.userDTO.id + "\">" + comment.userDTO.name + "</a>\n" +
                        // "                                        </div>\n" +
                        // "                                        <div class=\"col-2\" >" + comment.creationDate + "</div>\n" +
                        // "                                        <div class=\"col-12 white-space-pre-wrap mb-3\"\n" +
                        // "                                             >" + comment.value + "</div>\n" +
                        // "                                    </div>\n" +
                        // "                                    <div class=\"col-1\">\n" +
                        // "                                        <a comment-id = "+ comment.id + "\n" +
                        // "                                           class=\"btn btn-danger delete-comment-button\" role=\"button\" type=\"button\"\n" +
                        // "                                           data-toggle=\"modal\" data-target=\"#deleteComment\">Delete</a>\n" +
                        // "                                    </div>\n" +
                        // "                                </div>");
                }
            }
            if(data.last) {
                $("#clickToGetMoreComments" + answerId).remove();
            }
        });
    });
});
