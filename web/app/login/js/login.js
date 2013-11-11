(function($){

    $("#login-name").focus()

    $("body").on("click", "#login-form-submit", function(e){
        e.preventDefault();
        var name = $("#login-name").val();
        var pass = $("#login-pass").val();

        $.post("/api/session/", {username: name, password: pass}, function(data){
            if(data.error && data.error != "none"){
                $("#login-alert").text(data.error.name).animate({marginBottom:25},{specialEasing:{marginBottom: "linear"},complete:function(){
                    $(this).animate({height:40}, 270)
                }, duration: 270});
            }else{
                document.location.href = "/webapp/?secret=" + data.data.session.id;
            }
        }, "json");
    })
})(jQuery)