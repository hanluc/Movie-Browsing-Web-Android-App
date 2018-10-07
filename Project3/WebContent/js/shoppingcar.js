var allItems;

$(function () {
    $.ajax({
        dataType:"json",
        type:"get",
        url:"Cart",
        data:{
            "operation" : 4
        },
        success:function(data){
            console.log(data);
            allItems = data;
            showShoppingCart(allItems);
        }
    });
});


function showShoppingCart(allItems)
{
    var shoppingCart = $("#shopping-cart")
    shoppingCart.html("");

    var html =
        '<table class="table">'+
        '<thead>'+
        '<tr>'+
            '<th scope="col">#</th>'+
            '<th scope="col">Movie ID</th>'+
            '<th scope="col">Title</th>'+
            '<th scope="col">Number</th>'+
        '</tr>'+
        '</thead>'+
        '<tbody>';
    for(var i=0;i<allItems.length;i++)
    {
        var movieid = allItems[i].id;
        var movietitle = allItems[i].title;
        var movieNumber = allItems[i].number;

        html +=
            '<tr>'+
                '<th scope="row">'+(i+1)+'</th>'+
                '<td style="vertical-align:middle;"><a>'+movieid+'</a></td>'+
                '<td style="vertical-align:middle;"><a href="movie.html?movieid='+movieid+'">'+movietitle+'</a></td>'+
                '<td>'+
                    '<button onclick="removeItem(this)" type="button" class="btn btn-light" id="remove-'+movieid+'">-</button>'+
                    '<input onchange="changeItem(this)" value="'+movieNumber+'" style="margin: 5px;width: 33px" id="input-'+movieid+'">'+
                    '<button onclick="addItem(this)" type="button" class="btn btn-light" id="add-'+movieid+'">+</button>'+
                    '<button onclick="deleteItem(this)" style="margin-left: 15px" type="button" class="btn btn-light" id="delete-'+movieid+'">X</button>'+
                '</td>'+
            '</tr>';
    }
    html +=
        '</body>'+
            '</table>';
    shoppingCart.append(html);

}

function addItem(button)
{
    var movieid = button.id.split("-")[1];
    $.ajax({
        dataType:"json",
        type:"get",
        url:"Cart",
        data:{
            "operation" : 2,
            "movieid": movieid
        },
        success:function(data){
            allItems = data;
            showShoppingCart(allItems);
        }
    });
}

function removeItem(button) {
    var movieid = button.id.split("-")[1];
    $.ajax({
        dataType:"json",
        type:"get",
        url:"Cart",
        data:{
            "operation" : 3,
            "movieid": movieid
        },
        success:function(data){
            allItems = data;
            showShoppingCart(allItems);
        }
    });
}

function deleteItem(button) {
    var movieid = button.id.split("-")[1];
    $.ajax({
        dataType:"json",
        type:"get",
        url:"Cart",
        data:{
            "operation" : 1,
            "movieid": movieid
        },
        success:function(data){
            allItems = data;
            showShoppingCart(allItems);
        }
    });
}

function changeItem(input)
{
    var movieid = input.id.split("-")[1];
    var movieNumber = input.value;
    console.log(movieNumber);
    if(!isValidNumber(movieNumber))
    {
        alert("Please Input a Valid Number");
        showShoppingCart(allItems);
    }
    else
    {
        $.ajax({
            dataType:"json",
            type:"get",
            url:"Cart",
            data:{
                "operation" : 6,
                "movieid" : movieid,
                "number" : movieNumber
            },
            success:function(data){
                allItems = data;
                showShoppingCart(allItems);
            }
        });
    }
}


function checkout() {
    //first get all input information
    var firstName = $("#input-first-name").val();
    var lastName = $("#input-last-name").val();
    var cardNumber = $("#input-cardnumber").val();
    var year = $("#input-year").val();
    var month = $("#input-month").val();
    var day = $("#input-day").val();
    console.log(year);
    console.log(month);
    console.log(day);
    //verify year
    var fourNumber = /^\d{4}$/;
    if(!fourNumber.test(year)){
        alert("please input the valid year!");
        return false;
    }

    //verify month and day
    var twoNumber = /^\d{2}$/;
    if(!twoNumber.test(month) || month <1 || month > 12) {
        alert("please input the valid month!");
        return false;
    }
    if(!twoNumber.test(day) || day<1 || day>31) {
        alert("please input the valid month!");
        return false;
    }

    //verify card

    $.ajax({
        dataType: "json",
        type:"post",
        url:"VerifyCard",
        data:{
            "first":firstName,
            "last":lastName,
            "cardNum":cardNumber,
            "year":year,
            "month":month,
            "day":day
        },
        success:function (data) {
            var result = data.result;
            var statecode = data.statecode;
            var message = data.message;
            if(statecode != 0)
            {
                alert(message);
            }
            else
            {
                alert(message);
                //clear the cart and create sales records
                $.ajax({
                    dataType:"json",
                    type:"get",
                    url:"Cart",
                    data:{
                        "operation" : 5
                    },
                    success:function(data){
                        alert("success");
                        //show the confirmation
                        showConfirmation(data);
                    }
                });

            }
        }
    });
}

function isValidNumber(num) {
    return !isNaN(num) && num%1 === 0 && num >= 0;
}


function showConfirmation(salesRecords) {
    $("#shopping-cart-all").hide();
    var confirmation = $("#confirmation");
    confirmation.html("");

    var html =
        '<table class="table">'+
        '<thead>'+
        '<tr>'+
        '<th scope="col">#</th>'+
        '<th scope="col">Sales ID</th>'+
        '<th scope="col">Movie ID</th>'+
        '<th scope="col">Title</th>'+
        '<th scope="col">Number</th>'+
        '</tr>'+
        '</thead>'+
        '<tbody>';
    for(var i=0;i<salesRecords.length;i++)
    {
        var movieid = salesRecords[i].id;
        var movietitle = salesRecords[i].title;
        var movieNumber = salesRecords[i].number;
        var salesid = salesRecords[i].recordid;
        html +=
            '<tr>'+
            '<th scope="row">'+(i+1)+'</th>'+
            '<td style="vertical-align:middle;"><a>'+salesid+'</a></td>'+
            '<td style="vertical-align:middle;"><a>'+movieid+'</a></td>'+
            '<td style="vertical-align:middle;"><a href="movie.html?movieid='+movieid+'">'+movietitle+'</a></td>'+
            '<td>'+
            '<label style="margin:5px">'+movieNumber+'</label>'+
            '</td>'+
            '</tr>';
    }
    html +=
        '</body>'+
        '</table>';
    confirmation.append(html);

    $("#confirmation-all").removeAttr('hidden');
}
