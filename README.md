# ImageFinder Web Crawler

`Java 8` and `Maven 3.9.5` were used.

To build:

> `mvn package`

To clear:

> `mvn clean`

To run the project:

> `mvn clean test package spring-boot:run`

The server runs on `localhost:8080`.

## Features
- Crawls a web page as well as all its sub-pages within the same domain without revisiting any to find all images.
- Multi-threaded to perform multiple sub-page crawls at a time.
- Database integration and data persistence.
- Friendly crawler that retrieves and respects robots.txt. Also implements crawl delays and rotates user agents.
- Image recognition for frontal faces (1).
- Image detection for logos.
- Basic front-end development for a better user experience.

## Structure
1. Setting up the framework
2. Implementing the backend
3. Creating the web crawler
4. Implementing the frontend

### Setting up the framework

Spring Boot was used to build the application for easier development of the backend as well as integration with a database(2).

### Implementing the backend

The main application files are located in `src/main/java/com/example/imagefinder/`.

- `ImageFinderApplication.java`: entry point for the application.
- `ImageFinderController.java`: API layer that handles client requests.
- `ImageFinderService.java`: service layer that carries out the business logic. The web crawler is initialized and invoked here.
- `ImageFinderRepository.java`: data access layer.
- `SearchResult.java` and `ImageSearchEntity.java`: java class objects/database entities to represent an image search and its results.

The following REST API calls are supported:

- `GET /imagefinder/results`: get a list of all processed search results.
- `GET /imagefinder/images/{id}`: get the processed images for a given search.
- `POST /imagefinder/`: search with given `url`, `depth`, and `imgRec` parameters.

### Creating the web crawler

The web crawler is integrated into the service layer. Upon receiving a search request, it begins the crawl to get all images from the provided url and its subpages for a given depth. Afterwards if requested, the images are then processed and classified.

The web crawler and logic related to it is located in the `webcrawler/` directory in `imagefinder/`.

- `WebCrawler.java`: the actual crawler that performs a web crawl on a page and its subpages using jsoup. Spawns a thread pool of 10 threads to perform multiple crawls in parallel. Each user agent checks the robots.txt rules to see if is allowed to crawl a sub-page and waits the crawl-delay period before doing so. User agents are also rotated to prevent any one from bombarding a site and getting blocked (3).
- `RobotsTxtHandler.java`: fetches the `robots.txt` rules for a page domain.
- `ImageRecognizer.java`: recognizes frontal faces from returned images using the OpenCV library and Haar-feature-based cascade classifiers (1). Also separates out `.svg` vector images. Multithreading was used as well to speed up performance.

### Implementing the frontend

The frontend includes a simple interface with 3 pages (4). These are located in `src/main/resources/static/`.

- `index.html` and `script.js`: main landing page for searches, the user specifies the url they want to search, the depth of the crawl, and whether or not they want to enable image recognition. The initial image results are returned and displayed here.
- `results.html` and `results.js`: lists completed searches for processed images.
- `images.html` and `images.js`: displays processed image results for a completed search. The view can be changed based on the categories of image results if image recognition was selected.
- `styles.css`: basic styling rules.

## Testing

End-to-end testing was carried out to ensure that the ImageFinder worked properly. Some simple unit tests were run as well, but if more time was available proper unit tests should be implemented and run.

As noted in `test-links.txt`, the following URLs were used to extensively test the application.

http://books.toscrape.com/index.html

- A crawl depth of ~50 was successfully tested, which seems to have been the depth that crawls the entire domain and returns 10259 images.
- This completed in a reasonable amount of time. Deeper crawls as well as enabling image recognition took longer, both of which is expected.
- Without image recognition enabled, this took ~10 seconds on my laptop.
- With image recognition enabled, this took under 2 minutes on my laptop.
- Frontal faces were for the most part correctly categorized. See note (1) below for additional comments.
- Also note that some images that appear to be duplicated are due to sub-pages containing the same images as each other.

https://en.wikipedia.org/wiki/US_Open_(tennis)

- Wikipedia was used as a real website to test.
- For a depth of 0, crawls were for the most part consistent, with both image recognition enabled and disabled.
- One observation I noticed was that for this domain, the number of pages crawled/the number of images returned each time, and therefore the time it takes to complete a crawl, could drastically vary between repeated searches. This was particularly noticeable for crawls with depth > 0. For example a crawl of depth = 1 returns either ~100 images or ~170,000 images. My guess for why this happens is because of the fact that the user agent is randomly rotated each time. Different rules are specified for different user agents in robots.txt, so the number of pages that each is actually able to crawl can vary. In testing, checking the user agent that performs each crawl shows a correspondence between the user agent used and the number of images it returns. Modifying the crawler to use only one user agent yielded consistent results. Crawling websites that do not have robots.txt (such as http://books.toscrape.com/index.html) also gave consistent results, all of which supports this. However, given that it is good practice to rotate user agents to prevent any one user agent from being blocked, I kept this functionality of using random user agents and am noting this inconsistent behavior as an expected tradeoff.
- Frontal faces were for the most part correctly categorized. See note (1) below for additional comments.

## Notes

1. Frontal faces was chosen to be categorized because a pretrained classifier model exists and could be easily accessed (`haarcascade_frontalface_alt.xml` file located in the `webcrawler/` directory). Additional image categories can be easily implemented by using other classifiers. For the most part, these frontal faces can be correctly identified. There seems to be cases of false negatives more so than false positives. Faces that are clear and facing forwards are for the most part correctly identified, but if a face is slightly tilted, rotated to one side, or not very clear, then it is not recognized. This makes sense if the classifier was trained on exactly straight, front facing, and clear faces. Any misalignment would not match up with the filter features and therefore lead to a false negative.
2. For the database, H2 Database was used for demonstration purposes. However, it is currently configurered as an in-memory database, so an actual application would instead use a disk-based persistent database such as PostgreSQL.
3. A few user agents were arbitrarily picked for demonstration purposes and listed in `user-agents.txt`. Additional user agents could be used by adding them to the file.
4. For an actual web application, React would be used instead for more functionality and flexibility.
