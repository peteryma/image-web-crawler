# Eulerity ImageFinder Hackathon Challenge: Peter Ma

My finished implementation of the ImageFinder web application is contained here. In this README, I am providing a brief overview as well as some notes about my project.

Java 8 and Maven 3.9.5 were used.

To build:

> `mvn package`

To clear:

> `mvn clean`

To run the project:

> `mvn clean test package spring-boot:run`

The server runs on `localhost:8080`.

## Features

In addition to the required features, I included some extra functionality:

Required:

- Crawls a web page as well as all its sub-pages (without duplicates) within the same domain to find all images.
- Multi-threaded to perform multiple sub-page crawls at a time.

Extra:

- Database integration and data persistence.
- Friendly crawler respects robots.txt and crawl delays.
- Image detection for frontal faces.
- Image detection for logos.
- Basic front-end development for a better user experience.

## Structure

In working on this, I divided the challenge into a few tasks as follows:

1. Setting up the framework
2. Implementing the backend
3. Creating the web crawler
4. Implemnenting the frontend

### Setting up the framework

For the framework, I decided to use Spring Boot to build the application. This allowed easier development of the backend as well as integration with databases[^1].

### Implementing the backend

The main application files are located in `src/main/java/com/example/imagefinder/`.

- `ImagefinderApplication.java`: entry point for the application.
- `ImagefinderController.java`: API layer that handles client requests.
- `ImagefinderService.java`: service layer that carries out the business logic.
- `ImagefinderRepository.java`: data access layer.
- `ImageSearch.java`/`ImageSearchResult.java`: java class objects/database entities to represent an image search.

### Creating the web crawler

The web crawler is integrated into the service layer. Upon receiving a search request, it begins its crawl.

The web crawler and logic related to it is located in the `webcrawler/` directory in `imagfinder/`.

- `WebCrawler.java`: the actual crawler that performs a web crawl using jsoup.
- `RobotsTxtHandler.java`: gets the `robots.txt` rules for a page.
- `ImageRecognizer.java`: recognizes faces from parsed images using the OpenCV library and Haar-feature-based cascade classifiers. Also separates out `.svg` vector images.

### Implemnenting the frontend

For the frontend, I created a simple interface with 3 pages[^2]. These are located in `src/main/resources/static/`.

- `index.html`/`script.js`: main landing page for searches, displays initial image results.
- `results.html`/`results.js`: list of conducted searches with processed images.
- `images.html`/`images.js`: displays processed image results for a search, displays categorized image results.
- `styles.css`: basic styling rules.

## Testing

End-to-end testing was carried out to ensure that the ImageFinder worked properly. Some simple unit tests were run as well but if more time was available, proper unit tests should have been written and run.

As noted in `test-links.txt`, the following URLs were used to extensively test the application.

http://books.toscrape.com/index.html

https://en.wikipedia.org/wiki/US_Open_(tennis)

## Notes

- [^1]: For the database, I am using H2 Database for demonstration purposes as it is an in-memory database. For an actual application. I would use a disk-based persistent database instead such as PostgreSQL.
- [^2]: For an actual web application, I would use React instead for more functionality and flexibility.

- It seems that because the user agent is randomly picked each time, the number of pages that it is able to crawl can vary drastically for the same repeated search. This is likely due to the different rules for different user agents as specified in robots.txt Therefore, inconsistencies is observed especially for crawls of depths > 0. In testing, only using one user agent yields consistent results. Websites that do not have robots.txt also are consistent. However, given that it is good practice to rotate user agents to prevent one user agent from being blocked, I kept the function of using random user agents and am noting this inconsistent behavior as expected.
