# Interface
___

### Examples for return structure
#### success

```json
　{
    "result":"success",
    "data": 
    {
        "key": "value"
    }
  }
```
#### failure
```json
  
  {
    "result":"failure", 
    "data": 
    {
        "errorMessage": "value"
    }
  }
```
---

### 1.Get Movie Detail

give movie id and get all the information about a movie

|Servlet|request|
|---|---|
|/MovieDetail|GET

#### Request parameters
|Name|Type|
|---|---|
|movieid|String|
	
#### Return parameters
|Name|Type|
|---|---|
|title|string|
|year|string|
|director|string|
|rating|float|
|genres|list|
|stars|list|

##### each genre in geners
|Name|Type|
|---|---|
|genreid|string|
|name|string|

##### each genre in stars
|Name|Type|
|---|---|
|starid|string|
|name|string|

---
### 2.Get Star Detail
give star id and return all inforamtion about the star

|Servlet|request|
|---|---|
|/StarDetail|GET|

#### Request parameters
|Name|Type|
|---|---|
|starid|String|

#### Return parameters
|Name|Type|
|---|---|
|name|string|
|birthyear|string|
|movies|list|

##### each movie in stars
|Name|Type|
|---|---|
|movieid|string|

---
### 3. Search

__This Servlet is very important !!!!!__

The front end will request with a __code__, this code means different search types.

__All__ the return is a __*list of movies*__.

|Servlet|request|
|---|---|
|/Search|GET|

#### Request parameters
|Name|Type|desrciption|
|---|---|---|
|searchType|String|a code represent search type. like 0,1,2,3|
|searchData|String|search data, like title of the movie|
|start|int|the start index of the end of items|
|end|int|the End index of the end of items|
|sortType|String|a code represent sort type. like 0,1,2,3|



###### Searchtype list:
|searchType|description|searchdata|
|---|---|---|
|0|no specific search type|---|
|1|Search by title|title|
|2|Search by year|year|
|3|Search by director|director|
|4|Search by star's name|star's name|
|5|Search by movie title first alphanumeric letter|letter|
|6|Search by genres|genreid
|7|Search by star's id|star's id|
###### sorttype list:
|sortType|description|
|---|---|
|0|no specific sort type|
|1|sort by title|
|2|sort by rating|


#### Return parameters
|Name|Type|
|---|---|
|movies|list|