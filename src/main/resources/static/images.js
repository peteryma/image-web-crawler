// When window first loads call GET /imagefinder/images/{id} and display the
// processed images for the given search
document.addEventListener("DOMContentLoaded", function () {
  const urlParams = new URLSearchParams(window.location.search);
  const imagesId = urlParams.get("imagesId");

  // load all images by default
  loadImages(imagesId, "all");

  // on change of the image categories dropdown, reload the images
  const imgCategories = document.getElementById("img-categories");
  imgCategories.addEventListener("change", function () {
    loadImages(imagesId, this.value);
  });
});

function loadImages(imagesId, category) {
  fetch(`/imagefinder/images/${imagesId}`)
    .then((response) => response.json())
    .then((imageSearchResult) => {
      document.getElementById("url-link").innerHTML = imageSearchResult.url;

      const gallery = document.getElementById("gallery");
      gallery.innerHTML = "";

      // display frontal faces images
      if (
        imageSearchResult.faceUrls.length !== 0 &&
        (category === "frontal-faces" || category === "all")
      ) {
        const faceImages = document.createElement("div");
        faceImages.classList.add("output", "frontal-faces-images");

        imageSearchResult.faceUrls.forEach((image) => {
          const img = document.createElement("img");
          img.src = image;
          faceImages.appendChild(img);
        });

        gallery.appendChild(faceImages);
      }

      // display vector images
      if (
        imageSearchResult.svgUrls.length !== 0 &&
        (category === "vectors" || category === "all")
      ) {
        const vectorImages = document.createElement("div");
        vectorImages.classList.add("output", "vectors-images");

        imageSearchResult.svgUrls.forEach((image) => {
          const img = document.createElement("img");
          img.src = image;
          vectorImages.appendChild(img);
        });

        gallery.appendChild(vectorImages);
      }

      // display uncategorized images
      if (
        imageSearchResult.uncategorizedUrls.length !== 0 &&
        (category === "uncategorized" || category === "all")
      ) {
        const uncategorizedUrls = document.createElement("div");
        uncategorizedUrls.classList.add("output", "uncategorized-images");

        imageSearchResult.uncategorizedUrls.forEach((image) => {
          const img = document.createElement("img");
          img.src = image;
          uncategorizedUrls.appendChild(img);
        });

        gallery.appendChild(uncategorizedUrls);
      }
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}

document.querySelector("#home-btn").addEventListener("click", () => {
  window.location.href = "index.html";
});

document.querySelector("#results-btn").addEventListener("click", () => {
  window.location.href = "results.html";
});
