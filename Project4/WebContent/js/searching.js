//create cache
var cache = new Map();

$(function() {
    $("#btn-search").click(function () {
        var sortType;
        var searchType;
        var searchData;
        var start;
        var end;

        //first find what kind of search type
        searchType = $("input[name='search-type']:checked").val();
        searchData = $("#search-data").val();
        start = 0;
        end = 20;
        sortType = $("input[name='sort-type']:checked").val();

        //
        var url = "movielist.html";
        url += "?searchType=" + searchType;
        url += "&searchData=" + searchData;
        url += "&start=" + start;
        url += "&end=" + end;
        url += "&sortType=" + sortType;
        url += "&pageNumber=1";
        url += "&perPageItems=20";
        url += "&searchName=" + searchData;
        location.href = url;
    });

    $("#search-data").keyup(function (event) {
       if(event.keyCode === 13){
           $("#btn-search").click();
       }
    });

    $("#search-data").autocomplete({
        lookup: function (query, doneCallback) {
            handleLookup(query,doneCallback);
        },
        onSelect: function (suggestion) {
            handleSelectSuggestion(suggestion);
        },
        // set the groupby name in the response json data field
        groupBy: "category",
        // set delay time
        deferRequestBy: 300,
        // there are some other parameters that you might want to use to satisfy all the requirements
        // TODO: add other parameters, such as minimum characters
        minChars : 3
    })
});

/*
 * This function is called by the library when it needs to lookup a query.
 *
 * The parameter query is the query string.
 * The doneCallback is a callback function provided by the library, after you get the
 *   suggestion list from AJAX, you need to call this function to let the library know.
 */
function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated");

    // TODO: if you want to check past query results first, you can do it here
    // check if we have store the json array
    if(cache.get(query) != undefined){
        console.log("Already cached: " + query);
        handleLookupAjaxSuccess(cache.get(query),query,doneCallback);
    }else {
        // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
        // with the query data
        console.log("sending AJAX request to backend Java Servlet");
        console.log("Send New Query to Server: " + query);
        $.ajax({
            dataType:"json",
            type:"GET",
            url:"AutoComplete",
            data:{"query":$("#search-data").val()},
            "success": function(data) {
                // pass the data, query, and doneCallback function into the success handler
                handleLookupAjaxSuccess(data, query, doneCallback)
            },
            "error": function(errorData) {
                console.log("lookup ajax error");
                console.log(errorData);
            }
        })
    }

}

/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
    // TODO: if you want to cache the result into a global variable you can do it here
    //add to the cache
    if(cache.get(query) === undefined){
        cache.set(query,data);
        console.log("lookup ajax successful");
    }else{
        console.log("lookup cache successful");
    }

    console.log(data);

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: data } );
}

/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion

    console.log("you select " + suggestion["value"]);
    var url = "movie.html?movieid=" + suggestion["data"]["id"];
    console.log(url);
    location.href = url;
}
