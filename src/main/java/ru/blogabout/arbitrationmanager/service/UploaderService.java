package ru.blogabout.arbitrationmanager.service;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class UploaderService {
    private final Path UPLOAD_DIR = Paths.get("frontend/public/uploads");

    @Value("${app.ftp.server}")
    private String server;

    @Value("${app.ftp.port}")
    private int port;

    @Value("${app.ftp.user}")
    private String user;

    @Value("${app.ftp.password}")
    private String password;

    public void checkDir() throws IOException {
        if (Files.notExists(UPLOAD_DIR))
            Files.createDirectory(UPLOAD_DIR);
    }

    public String save(MultipartFile file, String prefix, Long userId) {
        String fileName = "";
        try {
            checkDir();

            String extension = file.getContentType().split("/")[1];

            String uuid = String.valueOf(UUID.randomUUID());
            String encodedString = new String(Base64.getEncoder().encode(uuid.getBytes()));
            fileName = prefix + "-" + userId + "-" + encodedString + "." + extension;
            Files.copy(file.getInputStream(), UPLOAD_DIR.resolve(fileName));
        } catch (IOException e) {
            System.out.println(e);
        }

        return fileName;
    }

    public String upload(MultipartFile file, String prefix, Long userId) {
        String fileName = "";
        String directory = "/www/arbitration-manager.ru/uploads/" + prefix + "/";
        FTPClient ftpClient = new FTPClient();

        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            String extension = file.getContentType().split("/")[1];
            String uuid = String.valueOf(UUID.randomUUID());
            String encodedString = new String(Base64.getEncoder().encode(uuid.getBytes()));
            fileName = prefix + "-" + userId + "-" + encodedString + "." + extension;

            InputStream inputStream = file.getInputStream();
            boolean done = ftpClient.storeFile(directory + fileName, inputStream);
            inputStream.close();

            if (!done) {
                fileName = "";
                System.out.println("Не удалось загрузить файл по FTP.");
            }
        } catch (IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                System.out.println("Ошибка отключения от FTP сервера по причине: " + e.getMessage());
            }
        }

        return fileName;
    }
}