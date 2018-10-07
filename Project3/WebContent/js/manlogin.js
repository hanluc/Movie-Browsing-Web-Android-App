
$(document).ready(function() {
	console.log("load js");
	$("#login_form").submit(function(event){
		console.log("submit login form");

		event.preventDefault();

		$.ajax({
			url:"ManagerLogin",    //absolute path
			method:"POST",
			data:$("#login_form").serialize(),
			dataType:"json",                        //json? cannot use jsonobject.
			success:function(response){
				var result = response.Result;  //get it as an attribute
				var message = response.Message;

				$('#respond').append("<h2>"+result+" "+message+"</h2>"+"<p>Waiting for page's jumping</p>");
				if(result === "success"){

					var url="DashBoard.html";
					location.href = url;
				}
				else
				{
					alert(message);
					location.reload();
				}

			}
		});


	});
	console.log("done");



	console.log( "ready!" );
});