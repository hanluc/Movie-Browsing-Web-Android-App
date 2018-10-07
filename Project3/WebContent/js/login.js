var g_recaptcha_response;

$(function(){
   $("#sign-in-btn").click(function(){
       var username = $("#username").val();
       var password = $("#password").val();
       $.ajax({
           dataType:"json",
           type:"POST",
           url:"Login",
           data:{"username":username,
                 "password":password,
                 "g-recaptcha-response":g_recaptcha_response},
           success:function (data) {
               var result = data.result;
               if(result === "success")
               {
                   alert("login success!");
                   location.href = "mainpage.html";
               }
               else
               {
                   alert(data.errormessage);
                   if(data.errorcode != "recaptha")
                   {
                       location.reload();
                   }
               }
           }
       });
   });
});

function response(response) {
    g_recaptcha_response = response;
}