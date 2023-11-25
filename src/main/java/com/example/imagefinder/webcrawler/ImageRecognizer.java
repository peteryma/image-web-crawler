package com.example.imagefinder.webcrawler;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import nu.pattern.OpenCV;

public class ImageRecognizer {
    
    private String resourcesPath = System.getProperty("user.dir") + "/src/main/java/com/example/imagefinder/webcrawler";

    public ImageRecognizer() {
        OpenCV.loadShared();
    }

    public void recognizeFaces(String[] imageUrls, List<String> faceUrls, List<String> nonFaceUrls, List<String> svgUrls) {

        for (String imageUrl : imageUrls) {
            if (imageUrl.contains(".svg")) {
                svgUrls.add(imageUrl);
            } else if (isFace(imageUrl)) {
                faceUrls.add(imageUrl);
            } else {
                nonFaceUrls.add(imageUrl);
            }
        }
    }

    private boolean isFace(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            try (InputStream is = url.openStream(); OutputStream os = new FileOutputStream(resourcesPath + "/temp.jpg")) {
                byte[] buffer = new byte[2048];
                int bytesRead;

                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            System.out.println("ERORR downloading image: " + imageUrl);
            return false;
        }

        Mat image = Imgcodecs.imread(resourcesPath + "/temp.jpg");
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        MatOfRect faces = new MatOfRect();
        CascadeClassifier faceDetector = new CascadeClassifier(resourcesPath + "/haarcascade_frontalface_alt.xml");
        faceDetector.detectMultiScale(grayImage, faces);

        return faces.toArray().length > 0;
    }
}
