// When window first loads call GET /imagefinder/results and display the
// search results
document.addEventListener("DOMContentLoaded", function () {
  fetch("/imagefinder/results")
    .then((response) => response.json())
    .then((searches) => {
      const searchesList = document.querySelector(".results");
      searchesList.innerHTML = "";

      // append each search result to the results list
      searches.forEach((search) => {
        const searchResult = document.createElement("div");
        searchResult.classList.add("search-result");
        searchResult.id = search.id;

        // on click of a search result, redirect to the specified images page
        searchResult.addEventListener("click", () => {
          window.location.href = "images.html?imagesId=" + search.id;
        });

        const searchUrl = document.createElement("div");
        searchUrl.classList.add("search-url");
        searchUrl.innerHTML = search.url;

        const searchInfo = document.createElement("div");
        searchInfo.classList.add("search-info");

        const searchDepth = document.createElement("div");
        searchDepth.classList.add("search-depth");
        searchDepth.innerHTML = "Depth: " + search.depth;

        const searchImgRec = document.createElement("div");
        searchImgRec.classList.add("search-imgRec");
        searchImgRec.innerHTML =
          "Image Recognition: " + (search.imgRec ? "Enabled" : "Disabled");

        const searchNumImages = document.createElement("div");
        searchNumImages.classList.add("search-numImages");
        searchNumImages.innerHTML = "Number of Images: " + search.numImages;

        searchInfo.appendChild(searchDepth);
        searchInfo.appendChild(searchImgRec);
        searchInfo.appendChild(searchNumImages);

        searchResult.appendChild(searchUrl);
        searchResult.appendChild(searchInfo);

        searchesList.appendChild(searchResult);
      });
    })
    .catch((error) => {
      console.error("Error:", error);
    });
});

document.querySelector("#home-btn").addEventListener("click", () => {
  window.location.href = "index.html";
});
