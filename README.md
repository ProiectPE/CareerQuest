# CareerQuest - Platform for finding jobs
A place where employers from various companies can post job listings, and users can search and apply to jobs.

An employer can:
 - create jobs
 - see what applications he has received
 - select applicants for interviews

A user can:
 - create a CV on the platform
 - search for jobs
 - apply to jobs

Monetizing:
 - companies can pay a fee to be promoted and prioritised in the users' searches
 - if they don't pay the fee they have limited possibilities

Rank applicants for a job based on how much they fit

Suggest to a user what jobs he should apply to



## PROJECT SETUP 

 - Make sure Docker is running (On Windows, install Dev Container extension (for the make command) )
 - Decomment default Controllers and comment custom ones (so far, we’re using the hello-world ones)

 - **Make build** -> do this any time you change controllers
 - **./start.sh** (On Mac, you need to add /workspaces directory to File Sharing in Docker before)
 - **./stop.sh**

——————
In case **make build** fails, before retrying:
 - **docker ps -a** => get the ID of the hello-build container
 - **docker rm [id]**

To see mongo on web:

Terminal -> Ports -> 8090 (mongo), 8080 (API)  -> Forwarded address -> web icon

You will be prompted to a web page where you login:

username: unibuc

password: adobe

To test server:
