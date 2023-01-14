# Grosu Gheorghe
## 324 CD

# POO TV
## Getting Started

### The main commands our app can execute:

* change page
* on page
* back
* database add
* database remove

Here we have different actions tested in our checker

### The actions for Change Page:

* **register**
* **login**
* **logout**
* **movies**
* **see details**
* **upgrades**

### The actions for On Page:

* **register**
* **login**
* **search**
* **filter**
* **buy tokens**
* **buy premium account**
* **purchase**
* **watch**
* **like**
* **rate**
* **subscribe**


## WorkFlow

My application starts from Main

Here I use an Object mapper to read from input files and write to another json files.

My platform is created here and my first page - "homepage"
Also here is created the stack of pages that I accessed with succes

To save user requests and functionalities I needed a page
Therefore, my platform have objects for :
- InputData
- Output
- CurrentPage
- List of commands (actions)
- Stack of pages

To access data from Json files I created Classes for every objects in file
All my database is localised in "**io**" package :
- Action
- Contains
- Credentials
- Filter
- Input
- Movie
- Sort
- User

I considered that my Platform has a single instance for entire execution, so here I used
the Singletone Pattern

For my main commands I tried to understand and use Command Pattern.

This objects are stored in the "**command**" package

The platform executes lists of Actions from Input and placeCommands in the commandList

Also, at the end of this actions, we have a use case when the last premium user that didn't logout receive a reccomendation

I have 4 types of commands,

so created 4 classes:
- "ChangePageAction"
- "OnPageAction"
- "DatabaseAction"
- "BackAction"

Also, the functionality for this objects are in Page Class

I have a switch with the actions as cases
For all the cases I created 5 types of services

- Movie Service
- Output Service
- User Service
- Upgrade Service
- Database Service

Next, I will explain shortly every case action

For mostly all of them we need to check if user is
capable of doing an action or send an error to Json file

# Change page

Register/ Login
--------
If action is accepted, I populate my current page with user datas

For other cases, send an error

Logout
--------
If action is accepted, I also populated page with default datas

For other cases, send an error

Movies
---------
For this action, I needed to write in both cases
For writing in Json I use the methods from OutputService

See details
------------
Firstly filtered movie List from current page or from Input data
to check if user doesn't have access to banned movies

Saved movie to current page and added POJO element to Output

Upgrades
------------
Populated current page or sended an error to ouput


# On page

Login
---------
Here I search for user in Database - Input
Saved current user and send datas to Json file

Register
---------
Here I checked for user in Database - Input
Registered new User
Saved current user and send datas to Json file

Search
----------
For search, filter, sort actions I used the Strategy pattern
Below I will explain the Implementation
Here I used the Context For Filter to search movie by name
Also changed movieList for page and Wrote to send data to Output

Filter
----------
Used movieService methods to sort or to filter contained fields in list of movie
Send datas to Json FILE

Buy premium account
--------------------
Checked if user has enogh balance and change the status from standard to premium

Purchase
----------
For purchase I used method from MovieService
* Firstly get available movies to purchase
* next, checked if current user has right to purchase movie
* we cannot purchase movie multiple times
* here we have 2 posibilities to purchase movie
* user with premium account has advantages
* if user respects the rules, we add the Movie to purchased list

Watch
-------
Also called the method from MovieService
* Checked for failed cases,
* we cannot watch a movie if we dind't buy it
* if user respects the rules, we add the Movie to watched list

Like
-------
Also called the method from MovieService
* Checked for failed cases,
* verify that first the movie was watched
* increment number of likes for every list containing him
* call the method to update lists
* if user respects the rules, we add the Movie to liked list

Rate
-------
* checked failed cases and wrote added error to JSON
* checked if the Movie exists in watched movies
* Calculated average rating and added Movie to Rated
* Updated Ratings in every

Subscribe
-----------
* checked if movie from see details contains requested subscribe genre
* checked if user has subscribed already to this genre
* save list of subscribed genres for each user

# Back Action

I explained already that I have a stack of pages in the Platform

* Next step is to verify that stack is not empty
* Then check if the page from stack is not Homepage and save that
* For all other use cases I pop the page from stack have 3 situations :
1. Register page -> write output to json (we don't have rights to access this page)
2. Login page -> write output to json (we don't have rights to access this page)
3. Other than this 2, execute change page to selected page

# Database Action

Here we have 2 basic actions : **add** and **delete**

For each of them I created fuctionalities in DatabaseService
Another important note that here also I use the notifications concept

### addToDatabase

* checked if movie is in database
* added movie to database
* notified users that have subscription on movie genres

### deleteFromDatabase

*  checked if movie is in database
*  removed movie to database
*  notified users that have subscription on movie genres
*  returned tokens or freePremiumMovies to standard or premium users


# The Strategy Implementation
I used two Generic Contexts :
- Sort
- Filter

For both of them have Intefaces with sort/filter methods
and real classes to use them
For Filter I have 2 generic methods :
- filterMovies
- filterUsers

FilterActor, FilterCountry, FilterName, FilterGenre, FilterAccpuntType implements method of IFilterStrategy

SortComplex, SortDuration, SortRating implements method of ISortStrategy

For this project I also used Builder Design Pattern in some classes that needed multiple constructors






