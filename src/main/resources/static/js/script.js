$(document).ready(function() {

    if(errorMsg == 'Question must be at least 8 characters long') {
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
            if(answersWithComments[i].answer.id == answerId) {
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
                if(principal == 'anonymousUser' || principal.email != comment.user.email) {
                    $("#comments" + answerId).append("<hr><div>" + comment.value + "</div><div> - " + comment.user.name + " created: "
                        + comment.creationDate +"</div>");
                } else {
                    $("#comments" + answerId).append("<hr><div class='row'><div class='col-11'>" + comment.value + "</div><a comment-id='" + comment.id + "' class='btn btn-danger col-1 delete-comment-button' role='button' type='button' data-toggle='modal' data-target='#deleteComment'>Delete</a><div class='col-12'> - " + comment.user.name + " created: "
                        + comment.creationDate +"</div></div> ");
                }
            }
            if(data.last) {
                $("#clickToGetMoreComments" + answerId).remove();
            }
        });
    });
});
