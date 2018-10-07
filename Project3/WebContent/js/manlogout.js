

$(document).ready(function() {
	console.log("load js");
	
	$("#mlogout").click(function(event){
		console.log("logout");
		
		event.preventDefault();
		
		$.ajax({
			url:"ManagerLogout",    //absolute path
			method:"GET",
			//json? cannot use jsonobject.
			success:function(response){
				console.log(response);
	            alert("see you next time!");
	            window.location.replace("managerlogin.html");
				
				//var url="/Project2/manager/managerlogin.html";
				//location.href = url;
				
			}
		});	
		
		
	});
	console.log("done");



    console.log( "ready!" );
});