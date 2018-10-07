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
});
