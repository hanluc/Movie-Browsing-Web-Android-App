var searchType;
var searchData;
var searchName;//we search genre id but use this to present name
var start;
var end;
var sortType;
var pageNumber;
var perPageItems;
var presentItemsNumber;//this is the number of present movies on the page
$(function(){
    //first get request parameters
    var url = location.search;
    if(url.indexOf("?") != -1)
    {
        var str = url.substr(1);
        args = str.split("&");
        searchType = args[0].split("=")[1];
        searchData = decodeURI(args[1].split("=")[1]);
        start = args[2].split("=")[1];
        end = args[3].split("=")[1];
        sortType = args[4].split("=")[1];
        pageNumber = args[5].split("=")[1];
        perPageItems = args[6].split("=")[1];
        searchName = decodeURI(args[7].split("=")[1]);
    }

    $("#movie-list-heading").html('Searching Result for  "' + searchName + '"');
    $("#item-number").val(perPageItems);
    $("#btn-pagenumber").html(pageNumber);
    getMovies();
//add bing events to buttons

    //change the sort type
    $("#sort-title").click(function(){
        sortType = 1;
        pageNumber = 1;
        $("#btn-pagenumber").html(pageNumber);
        start = 0;
        end = perPageItems;
        getMovies();
    });

    $("#sort-rating").click(function(){
        sortType = 2;
        pageNumber = 1;
        $("#btn-pagenumber").html(pageNumber);
        start = 0;
        end = perPageItems;
        getMovies();
    });

    //do the sorting or page items number adjustment
    $("#item-number").change(function() {
        var newPerPageItems = $("#item-number").val();
        if(!isValidNumber(newPerPageItems))
        {
            alert("Page number is wrong!!!");
            $("#item-number").val(perPageItems);
            return false;
        }
        perPageItems = newPerPageItems;
        pageNumber = 1;
        $("#btn-pagenumber").html(pageNumber);
        start = 0;
        end = perPageItems;
        getMovies();
    });

    //got tht prex page
    $("#btn-prex").click(function(){
        if(parseInt(pageNumber) === 1)
        {
            alert("There is no pre pages");
        }
        else
        {
            perPageItems = $("#item-number").val();
            pageNumber--;
            start = parseInt(start) - parseInt(perPageItems);
            end = parseInt(end) - parseInt(perPageItems);
            var url = "movielist.html";
            url += "?searchType=" + searchType;
            url += "&searchData=" + searchData;
            url += "&start=" + start;
            url += "&end=" + end;
            url += "&sortType=" + sortType;
            url += "&pageNumber=" + pageNumber;
            url += "&perPageItems="+perPageItems;
            url += "&searchName=" + searchName;

            location.href = url;
        }
    });

    //got the next page
    $("#btn-next").click(function() {
        if(presentItemsNumber < perPageItems)
        {
            alert("This is the last page!");
        }
        else
        {
            perPageItems = $("#item-number").val();
            pageNumber++;
            start = parseInt(start) + parseInt(perPageItems);
            end = parseInt(end) + parseInt(perPageItems);
            var url = "movielist.html";
            url += "?searchType=" + searchType;
            url += "&searchData=" + searchData;
            url += "&start=" + start;
            url += "&end=" + end;
            url += "&sortType=" + sortType;
            url += "&pageNumber=" + pageNumber;
            url += "&perPageItems="+perPageItems;
            url += "&searchName=" + searchName;
            location.href = url;
        }
    });
});

function getMovies() {
    //clear present movies
    $("#movie-list-container").html("");
    var movies_ids = [];
    //request movies
    $.ajax({
        dataType:"json",
        type:"get",
        url:"Search",
        data:{
            "searchType":searchType,
            "searchData":searchData,
            "start":start,
            "end":end,
            "sortType":sortType
        },
        success:function (movieList) {
            presentItemsNumber = movieList.length;
            for(var i=0,l=movieList.length;i<l;i++)
            {
                var id = movieList[i].id;
                movies_ids.push(id);
                var title = movieList[i].title;
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
                                    '<a  href="movie.html?movieid='+id+'"><h4 id="movie-'+id+'-title">'+title+'</h4></a>'+
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

    });
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

function isValidNumber(num) {
    return !isNaN(num) && num%1 === 0 && num >= 0;
}