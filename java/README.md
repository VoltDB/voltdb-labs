# Java Samples
All samples here use one or more Java clients and may have one or more web pages.

### Expectation Example ###
VoltDB stored procedures can have "expectations" that let you define the minimum expected number of results for a given query. Expectations can greatly simplify complicated stored procedure error checking code making the code much more readable.

### Upsert Sample ###
Demonstrates a simple "upsert" stored procedure using multidimensional demographic information to create specific user profile types that can later be targeted for direct ad placements.

### Tweet Geolocation Simulator ###
An application that uses a thematic map to show the distribution of hashtags across the globe. 

### Games ###
An online gaming platform simulation that tracks user session data, player scores, and leaderboards.

### Stock Orders ###
An app that demonstrates fast ingestion of equities orders and performs realtime analytics of top symbols and accounts by volume.  
(Used in our Capital Markets webinars)

### Stored Value Cards ###
An app that simulates high velocity transaction processing of pre-authorization, purchase, and balance transfer transactions on debit or gift cards, while maintaining fully consistent balances.

### Metro ###
Bus and subway card users are getting on buses and onto trains.  Every card swipe is validated against the card account and tracked.

### Ad Tracking ###
Calculate real-time CTR and conversion rates aggregated various ways while ingesting time-series events for ad impressions, clickthroughs and conversions.

### Demographics Analytics ###
An app that demonstrates real-time analytics against a live data stream of user data. The data is broken down into demographic groups which can be viewed using a browser.

### Apache Log Analytics ###
An app that generates and stores an apache log file. The data is setup to save the raw log data and a refined version. The refined version keeps track of assets and the size for a given interval so that it can be used to track hit counts and bandwidth utilization.

### Flickr Feed Reader ###
Reads a real Flickr JSON feed and stores the results in Volt. Volt extracts all the tags associated with the image and creates a leaderboard. The application queries Flickr about once per second to avoid feed limits and displays the results of the volt query every two seconds.

## More Coming ###
Many more applications coming soon. You'll soon see all of our code from our blogs appear here too.

## Contributions Welcome ###
Do you have a sample that you used to test VoltDB? We'd love to add your sample application to this repository. Just fork the repository and send us a pull request.
