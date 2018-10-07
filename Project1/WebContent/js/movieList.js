/**
 * Created by George on 14/04/2018.
 */
$(function () {
    var movies_ids = [];
    $.ajax({
        dataType:"json",
        type:"get",
        url:"MovieList",
        success:function(movieList){
            for(var i=0,l=movieList.length;i<l;i++){
                var id = movieList[i].movieid;
                movies_ids.push(id);
                var title = movieList[i].title;
                var rating = movieList[i].rating;
                var year = movieList[i].year;
                var director = movieList[i].director;
                var genreList = movieList[i].genrelist;
                var starList = movieList[i].starlist;
                var img_url = "";
                //get stars' names
                var starsName = '';
                for(var i1=0,l1=starList.length;i1<l1;i1++)
                {
                    starsName += starList[i1].name + ',    ';
                }

                //get genres
                var genres  = '';
                for(var i2=0,l2=genreList.length;i2<l2;i2++)
                {
                    genres += '<div class="ui label">'+
                        genreList[i2].name+ '</div>';
                }
                //generate the web pag
                $("#movie-list").append(
                    '<div class="item">'+
                        '<div class="ui small image">'+
                            '<img id="'+id+'" '+'src="'+'">'+
                        '</div>'+
                        '<div class="content">'+
                            '<a class="header">'+title+'</a>'+
                            '<div class="meta">'+
                                '<a>Year: '+year+'</a>'+
                            '</div>'+
                            '<div class="description">'+
                                'Rating: '+rating+
                            '</div>'+
                            '<div class="description">'+
                                'Director: '+director+
                            '</div>'+
                            '<div class="description">'+
                                'Stars: ' + starsName+
                            '</div>'+
                            '<div class="extra">'+
                                genres+
                            '</div>'+
                        '</div>' +
                    '</div>');
            }
            
            //add poster
            addPosterToEachMovie(movies_ids);
        }
    });

});

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