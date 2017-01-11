# UdacityPopularMoviesStage2
Final Stage for Popular Movies project
This is the second and final stage for the Popular Movies project for Android Nanodegree.
In this, the students were expected to create network implementation to fetch  movie details along with reviews and trailers.
Also, there is a Favorite button addded to save movie details in SQLite database for offline access.

Following libraries are used in this project 

- http://jakewharton.github.io/butterknife ;
- https://github.com/square/picasso ;
- https://github.com/square/retrofit


In order for the project to work you'll need to get API KEY from TMDb site and replace it here

buildTypes.each {
    it.buildConfigField 'String', 'MOVIE_DATABASE_API_KEY', '"YOUR API KEY HERE"'
}


