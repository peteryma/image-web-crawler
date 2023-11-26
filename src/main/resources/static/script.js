var resultList = document.querySelector(".output");
var urlInput = document.querySelector("input[name=url]");
var depthInput = document.querySelector("input[name=depth]");
var imgRec = document.querySelector("input[name=imgRec]");
var loaderContainer = document.querySelector(".loader-container");

const loader = document.createElement("div");
loader.classList.add("loader");

apiCallBack = function (xhr, callback) {
  if (xhr.readyState == XMLHttpRequest.DONE) {
    if (xhr.status != 200) {
      let message = xhr.status + ":" + xhr.statusText + ":" + xhr.responseText;
      alert(message);
      throw "API call returned bad code: " + xhr.status;
    }
    let response = xhr.responseText ? JSON.parse(xhr.responseText) : null;
    if (callback) {
      callback(response);
    }
  }
};

updateList = function (response) {
  resultList.innerHTML = "";
  loaderContainer.removeChild(loader);

  for (var i = 0; i < response.length; i++) {
    var img = document.createElement("img");
    img.width = 200;
    img.src = response[i];
    resultList.appendChild(img);
  }
};

makeApiCall = function (url, method, obj, callback) {
  resultList.innerHTML = "";
  // display a spinning loader while waiting for the API call to complete
  loaderContainer.appendChild(loader);

  let xhr = new XMLHttpRequest();
  xhr.open(method, url);
  xhr.onreadystatechange = apiCallBack.bind(null, xhr, callback);
  xhr.send(
    obj
      ? obj instanceof FormData || obj.constructor == String
        ? obj
        : JSON.stringify(obj)
      : null
  );
};

document
  .querySelector("#search-btn")
  .addEventListener("click", function (event) {
    event.preventDefault();

    if (depthInput.value == "") {
      depthInput.value = 0;
    }

    // POST /imagefinder/ with the given url, depth, and imgRec
    makeApiCall(
      "/imagefinder?url=" +
        urlInput.value +
        "&depth=" +
        depthInput.value +
        "&imgRec=" +
        imgRec.checked,
      "POST",
      null,
      updateList
    );
  });

document.querySelector("#results-btn").addEventListener("click", () => {
  window.location.href = "results.html";
});
