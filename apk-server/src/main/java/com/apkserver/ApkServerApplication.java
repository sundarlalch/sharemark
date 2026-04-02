package com.apkserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@SpringBootApplication
@RestController
public class ApkServerApplication {

    private static final String APK_PATH = "../app/build/outputs/apk/debug/app-debug.apk";

    public static void main(String[] args) {
        SpringApplication.run(ApkServerApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("APK Server is running!");
        System.out.println("Download URL: http://YOUR_IP:8080/download");
        System.out.println("========================================\n");
    }

    @GetMapping("/")
    public String home() {
        return """
            <html>
            <head>
                <title>Bookmark App Download</title>
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <style>
                    body { font-family: Arial, sans-serif; text-align: center; padding: 50px; background: #f5f5f5; }
                    .container { background: white; padding: 40px; border-radius: 16px; max-width: 400px; margin: 0 auto; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
                    h1 { color: #6750A4; }
                    .download-btn { display: inline-block; background: #6750A4; color: white; padding: 16px 32px; text-decoration: none; border-radius: 28px; font-size: 18px; margin-top: 20px; }
                    .download-btn:hover { background: #7c6aad; }
                    .info { color: #666; margin-top: 20px; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Bookmark App</h1>
                    <p>Save and organize your bookmarks with auto-categorization</p>
                    <a href="/download" class="download-btn">Download APK</a>
                    <p class="info">After downloading, open the APK file to install</p>
                </div>
            </body>
            </html>
            """;
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadApk() {
        File apkFile = new File(APK_PATH);

        if (!apkFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(apkFile);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"BookmarkApp.apk\"")
                .body(resource);
    }
}
