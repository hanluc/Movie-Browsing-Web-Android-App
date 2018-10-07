var movieid;

$(function () {
    //get parameters
    var url = location.search;
    if(url.indexOf("?") != -1)
    {
        var str = url.substr(1);
        movieid = str.split("=")[1];
    }
    $.ajax({
        dataType:"json",
        type:"get",
        url:"MovieDetail",
        data:{
            "movieid":movieid
        },
        success: function(data){
            addMovieList(data);
        }
    });


});


function addMovieList(movieList) {
    var movies_ids = [];
    for(var i=0,l=movieList.length;i<l;i++)
    {
        var id = movieList[i].movieid;
        movies_ids.push(id);
        var title = movieList[i].title;
        $("#movie-heading").html("This is The Detail For :" +title);
        var rating = movieList[i].rating;
        var year = movieList[i].year;
        var director = movieList[i].director;
        var genreList = movieList[i].genres;
        var starList = movieList[i].starlist;
        var img_url = "";
        //get stars' names
        var starsHtml = '';
        for(var i1=0,l1=starList.length;i1<l1;i1++)
        {
            var starid = starList[i1].starId;
            var starName = starList[i1].name;
            starsHtml += '<button type="button" class="btn btn-light btn-sm" onclick=jumpToAnotherPage("star.html?starid='+starid+'")>'+starName+'</button>\n';
        }

        //get genres
        var genres  = '';
        for(var i2=0,l2=genreList.length;i2<l2;i2++)
        {
            var genreid = genreList[i2].genreid;
            var genrename = genreList[i2].name;
            var index = i2 % 5;
            switch (index)
            {
                case 0:
                    genres += '<a href="movielist.html?searchType=6&searchData='+genreid+'&start=0&end=20&sortType=1&pageNumber=1&perPageItems=20&searchName='+genrename+'" class="badge badge-primary">'+genrename+'</a>\n';
                    break;
                case 1:
                    genres += '<a href="movielist.html?searchType=6&searchData='+genreid+'&start=0&end=20&sortType=1&pageNumber=1&perPageItems=20&searchName='+genrename+'" class="badge badge-secondary">'+genrename+'</a>\n';
                    break;
                case 2:
                    genres += '<a href="movielist.html?searchType=6&searchData='+genreid+'&start=0&end=20&sortType=1&pageNumber=1&perPageItems=20&searchName='+genrename+'" class="badge badge-success">'+genrename+'</a>\n';
                    break;
                case 3:
                    genres += '<a href="movielist.html?searchType=6&searchData='+genreid+'&start=0&end=20&sortType=1&pageNumber=1&perPageItems=20&searchName='+genrename+'" class="badge badge-danger">'+genrename+'</a>\n';
                    break;
                case 4:
                    genres += '<a href="movielist.html?searchType=6&searchData='+genreid+'&start=0&end=20&sortType=1&pageNumber=1&perPageItems=20&searchName='+genrename+'" class="badge badge-warning">'+genrename+'</a>\n';
                    break;
            }
        }
        //generate the web pag
        $("#movie-list-container").append(
            '<div class="card container" style="margin:15px auto;">'+
            '<div class="my-wrapper">'+
            '<div class="my-icon">'+
            '<img id="'+id+'" '+'src="'+'">'+
            '</div>'+
            '<div class="my-content">'+
            '<div>' +
            '<h4 id="movie-'+id+'-title">'+title+'</h4>'+
            '</div>'+
            '<div>'+
            '<label>Rating: </label>'+
            '<a id="movie-rating">'+rating+'</a>'+
            '</div>'+
            '<div>'+
            '<label>Year: </label>'+
            '<a href="movielist.html?searchType=2&searchData='+year+'&start=0&end=20&sortType=1&pageNumber=1&perPageItems=20&searchName='+year+'">'+year+'</a>'+
            '</div>'+
            '<div>'+
            '<label>Director: </label>'+
            '<a href="movielist.html?searchType=3&searchData='+director+'&start=0&end=20&sortType=1&pageNumber=1&perPageItems=20&searchName='+director+'">'+director+'</a>'+
            '</div>'+
            '<div>' +
            '<label>Stars: </label>'+
            starsHtml+
            '</div>'+
            '<div>' +
            '<label>Genres: </label>'+
            genres+
            '</div>'+
            '<div>' +
            '<button type="button" class="btn btn-success btn-sm" onclick="addToShoppingCar(this)" id="btn-addToShpCar-'+id+'">Add To Cart</button>'+
            '</div>'+
            '</div>'+
            '</div>'+
            '</div>'
        )
    }

    //add poster
    addPosterToEachMovie(movies_ids);
}

function addPosterToEachMovie(movies_ids){
    for(var i=0;i<movies_ids.length;i++)
    {
        $.ajax({
            dataType:"json",
            type:"get",
            url:"GetMoviePoster",
            data:{"movieid":movies_ids[i]},
            success:function (data) {
                var posterUrl = data.poster;
                var movie_element = "#"+data.movieid;
                if(posterUrl != ""){
                    $(movie_element).attr("src",posterUrl);
                }else {
                    $(movie_element).attr("src","img/no-poster.png");
                }
            }
        });
    }
}

function addToShoppingCar(button) {
    var movieid = button.id.split("-")[2];
    $.ajax({
        dataType:"json",
        type:"get",
        url:"Cart",
        data:{
            "operation" : 0,
            "movieid": movieid
        },
        success:function(data){
            alert("add successfully!");
        }
    });
}