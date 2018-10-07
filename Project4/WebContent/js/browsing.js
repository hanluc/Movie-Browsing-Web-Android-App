
$(function(){
    //get all genres
    $.ajax({
        dataType:"json",
        type:"GET",
        url:"GetAllGenres",
        success:function (genres) {
            for(var i=0,l=genres.length;i<l;i++) {
                //get genre id and name
                var genre = genres[i];
                var id = genre.id;
                var name = genre.name;
                //add them to the html
                //<button type="button" class="btn btn-light btn-lg letters" id="btn-letter-z">Z</button>
                $("#container-genres").append(
                    '<button onclick="searchByGenre(this)" type="button" class="btn btn-light btn-lg genres" id="btn-genre-' + id + '" value='+name+'>'
                    + name + '</button>'
                );
            }
        }
    });
    
});

function searchByLetter(button){
    var letter = button.id.split("-")[2];
    var url = "movielist.html";
    url += "?searchType=" + "5";
    url += "&searchData=" + letter;
    url += "&start=0";
    url += "&end=20";
    url += "&sortType=1";
    url += "&pageNumber=1";
    url += "&perPageItems=20";
    url += "&searchName=" + letter;


    location.href = url;
}

function searchByGenre(button) {
    var genre = button.id.split("-")[2];
    var url = "movielist.html";
    url += "?searchType=" + "6";
    url += "&searchData=" + genre;
    url += "&start=0";
    url += "&end=20";
    url += "&sortType=1";
    url += "&pageNumber=1";
    url += "&perPageItems=20";
    url += "&searchName=" + button.value;



    location.href = url;
}


