document.addEventListener("DOMContentLoaded", function () {
  const urlParams = new URLSearchParams(window.location.search);
  const imagesId = urlParams.get("imagesId");
  const url = urlParams.get("url");

  document.getElementById("url").innerHTML = url;

  loadImages(imagesId, "all");

  const imgCategories = document.getElementById("img-categories");
  imgCategories.addEventListener("change", function () {
    loadImages(imagesId, this.value);
  });
});

function loadImages(imagesId, category) {
  fetch(`/imagefinder/images/${imagesId}`)
    .then((response) => response.json())
    .then((imageSearchResult) => {
      const gallery = document.getElementById("gallery");
      gallery.innerHTML = "";

      if (category === "frontal-faces" || category === "all") {
        const faceImages = document.createElement("div");
        faceImages.classList.add("frontal-faces-images");

        imageSearchResult.faceUrls.forEach((image) => {
          const img = document.createElement("img");
          img.src = image;
          faceImages.appendChild(img);
        });

        gallery.appendChild(faceImages);
      }

      if (category === "vectors" || category === "all") {
        const vectorImages = document.createElement("div");
        vectorImages.classList.add("vectors-images");

        imageSearchResult.svgUrls.forEach((image) => {
          const img = document.createElement("img");
          img.src = image;
          vectorImages.appendChild(img);
        });

        gallery.appendChild(vectorImages);
      }

      if (category === "uncategorized" || category === "all") {
        const restImages = document.createElement("div");
        restImages.classList.add("rest-images");

        imageSearchResult.restImages.forEach((image) => {
          const img = document.createElement("img");
          img.src = image;
          restImages.appendChild(img);
        });

        gallery.appendChild(restImages);
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
