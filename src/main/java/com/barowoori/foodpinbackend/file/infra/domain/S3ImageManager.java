package com.barowoori.foodpinbackend.file.infra.domain;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.barowoori.foodpinbackend.config.factory.YamlPropertySourceFactory;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Date;
import java.util.UUID;

@Component
@PropertySource(value = "classpath:secrets/aws-s3-config.yml", factory = YamlPropertySourceFactory.class)
public class S3ImageManager implements ImageManager {
    private final AmazonS3Client amazonS3;
    @Value("${s3.bucket}")
    private String bucket;

    public S3ImageManager(AmazonS3Client amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public String updateFile(MultipartFile newFile, String oldFilePath, ImageDirectory imageDirectory) {
        try {
            if (oldFilePath != null) { //기존 파일 있을 경우 삭제
                System.out.println("S3 oldFilePath: " + oldFilePath);
                deleteFile(oldFilePath);
            }
            //새 파일 업로드
            return upload(newFile, imageDirectory.getPath());
        } catch (IOException e) {
            System.out.println("이미지 업로드 실패");
        }
        return null;
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String filePath = url.getPath(); // URL의 경로 부분을 가져옴
            String decodedFilePath = URLDecoder.decode(filePath, "UTF-8"); // 디코딩하여 원본 경로 추출

            String objectKey = decodedFilePath;
            if (decodedFilePath.startsWith("/")) {
                objectKey = decodedFilePath.substring(1);
            }

            amazonS3.deleteObject(bucket, objectKey);
            System.out.println("파일이 삭제되었습니다: " + objectKey);

        } catch (Exception e) {
            System.out.println("Error while decoding or deleting the file: " + e.getMessage());
        }
    }

    private String upload(MultipartFile multipartFile, String dirName) throws IOException {
        // 파일 이름에서 공백을 제거한 새로운 파일 이름 생성
        String originalFileName = multipartFile.getOriginalFilename();

        // UUID를 파일명에 추가
        String uuid = UUID.randomUUID().toString();
        String uniqueFileName = uuid + "_" + originalFileName.replaceAll("\\s", "_");

        String fileName = dirName + "/" + uniqueFileName;
        File uploadFile = convert(multipartFile);

        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    private File convert(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String uniqueFileName = uuid + "_" + originalFileName.replaceAll("\\s", "_");

        File convertFile = new File(uniqueFileName);
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            } catch (IOException e) {
                System.out.println("파일 변환 중 오류 발생: {}" + e.getMessage());
                throw e;
            }
            return convertFile;
        }
        throw new IllegalArgumentException(String.format("파일 변환에 실패했습니다. %s", originalFileName));
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            System.out.println("파일이 삭제되었습니다.");
        } else {
            System.out.println("파일이 삭제되지 못했습니다.");
        }
    }
    @Override
    public String getPreSignUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.equals("")) {
            return null;
        }
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, getObjectKey(fileUrl))
                .withMethod(HttpMethod.GET)
                .withExpiration(getExpiration());

        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toExternalForm();
    }

    private Date getExpiration() {
        Date now = new Date();
        return new Date(now.getTime() + 1000 * 60 * 10); //10분 후 만료
    }

    private String getObjectKey(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String filePath = url.getPath(); // URL의 경로 부분을 가져옴
            String decodedFilePath = URLDecoder.decode(filePath, "UTF-8"); // 디코딩하여 원본 경로 추출

            String objectKey = decodedFilePath;
            if (decodedFilePath.startsWith("/")) {
                objectKey = decodedFilePath.substring(1);
            }

            return objectKey;

        } catch (Exception e) {
            System.out.println("change objectKey fail " + e.getMessage());
        }
        return null;
    }
}
