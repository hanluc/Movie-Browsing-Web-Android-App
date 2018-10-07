$(function(){
	
	$('#back').hide();
	
	$("#insertMovie").submit(function(event){
		event.preventDefault();
		
		
		$.ajax({
			url:"AddMovie",
			method:"GET",
			data:$("#insertMovie").serialize(),
			dataType:"JSON",
			success:function(response){
				
				console.log('event after this line');
				console.log(response);
				var result = response.result;  //get it as an attribute
				var message = response.message;
				console.log('the result is: ');
				console.log(result);
				
				if(result =='success'){
					console.log('after success'); 
					$('#insertMovie').remove();
					$('#show-message').append("<h2>Congratulations! Insertion Success!</h2>");
					$("#addMovieSubmit").hide();
					$('#back').show();
					console.log("load page success");
					
				}
				else{
					$('#insertMovie').html("");
					$('#form-tail').append("<br><h3>"+result+": "+message+"</h3>");
					$("#addMovieSubmit").hide();
					$('#back').show();
					
				}
				
			},
			error: function (request, status, error) {
		        //alert(request.responseText);
				$('#insertMovie').html("");
		        $('#insertMovie').append("<br><h3>Failed, please try again!</h3>");
		        $("#addMovieSubmit").hide();
		        $('#back').show();
		        
			}
		});
		
		
	})
	
	$('#back').click(function(){
		location.href = 'addMovie.html';
	})
	
	$('#insertStar').delay(10000);
	
	
});


