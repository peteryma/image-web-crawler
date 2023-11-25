package com.example.imagefinder.webcrawler;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

    public void recognizeFaces(String[] imageUrls, List<String> faceUrls, List<String> svgUrls, List<String> restUrls) {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        AtomicInteger activeTaskCount = new AtomicInteger(0);

        List<String> synchronizedFaceUrls = Collections.synchronizedList(faceUrls);
        List<String> synchronizedSvgUrls = Collections.synchronizedList(svgUrls);
        List<String> synchronizedRestUrls = Collections.synchronizedList(restUrls);


        for (String imageUrl : imageUrls) {
            threadPool.submit(() -> {
                activeTaskCount.incrementAndGet();

                if (imageUrl.contains(".svg")) {
                    synchronizedSvgUrls.add(imageUrl);
                } else if (isFace(imageUrl)) {
                    synchronizedFaceUrls.add(imageUrl);
                } else {
                    synchronizedRestUrls.add(imageUrl);
                }

                activeTaskCount.decrementAndGet();
            });
        }

        // Wait for all tasks to complete
        try {
            while (activeTaskCount.get() > 0) {
                Thread.sleep(100);
            }

            threadPool.shutdown();
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
    }

    private boolean isFace(String imageUrl) {
        String tempFileName = "/" + java.util.UUID.randomUUID().toString() + ".jpg";

        try {
            URL url = new URL(imageUrl);

            try (InputStream is = url.openStream(); OutputStream os = new FileOutputStream(resourcesPath + tempFileName)) {
                byte[] buffer = new byte[2048];
                int bytesRead;

                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }

            Mat image = Imgcodecs.imread(resourcesPath + tempFileName);
            Mat grayImage = new Mat();
            Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

            MatOfRect faces = new MatOfRect();
            CascadeClassifier faceDetector = new CascadeClassifier(resourcesPath + "/haarcascade_frontalface_alt.xml");
            faceDetector.detectMultiScale(grayImage, faces);

            return faces.toArray().length > 0;
        } catch (Exception e) {
            System.out.println("ERORR recognizing image: " + imageUrl);
            return false;
        } finally {
            try {
                new java.io.File(resourcesPath + tempFileName).delete();
            } catch (Exception e) {
                System.out.println("ERROR deleting " + tempFileName);
            }
        }
    }
}
