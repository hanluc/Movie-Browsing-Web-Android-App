$(function(){
	
	$('#back').hide();
	
	$("#submitStar").click(function(event){
		
		event.preventDefault();
		
		$.ajax({
			url:"AddStar",
			method:"GET",
			data:$("#insertStar").serialize(),
			dataType:"JSON",
			success:function(response){
				var result = response.result;  //get it as an attribute
				var message = response.message;
				
				if(result=='success'){
					$('#insertStar').html("");
					$('#form-tail').append("<h2>Congratulations! Insertion Success!</h2>");
					$("#submitStar").hide();
					$('#back').show();
					console.log("load page success");
					$('#insertStar').delay(10000);
				}
				else{
					$('#insertStar').html("");
					$('#form-tail').append("<br><h3>"+result+": "+message+"</h3>");
					$("#submitStar").hide();
					$('#back').show();
					$('#insertStar').delay(10000);
				}
				
			},
			error: function (request, status, error) {
		        //alert(request.responseText);
				$('#insertStar').html("");
		        $('#insertStar').append("<br><h3>Failed, please try again!</h3>");
		        $("#submitStar").hide();
		        $('#back').show();
		        $('#insertStar').delay(10000);
			}
		    
		});
		
	})
	
	$('#back').click(function(){
		location.href = 'DashBoard.html';
	})
	
	$('#insertStar').delay(10000);
	
	//location.href = 'DashBoard.html';
	
});