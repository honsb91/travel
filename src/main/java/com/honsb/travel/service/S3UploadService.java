package com.honsb.travel.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.honsb.travel.domain.entity.Board;
import com.honsb.travel.domain.entity.UploadImage;
import com.honsb.travel.repository.BoardRepository;
import com.honsb.travel.repository.UploadImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final AmazonS3 amazonS3;
    private final UploadImageRepository uploadImageRepository;
    private final BoardRepository boardRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public UploadImage saveImage(MultipartFile multipartFile, Board board) throws IOException {
        if (multipartFile.isEmpty()){
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();

        // 원본 파일명 -> 서버에 저장된 파일명(중복 X)
        // 파일명이 중복되지 않도록 UUID로 설정 + 확장자 유지
        String savedFilename = UUID.randomUUID() + "." + extractExt(originalFilename);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        // S3에 파일 업로드
        amazonS3.putObject(bucket, savedFilename,multipartFile.getInputStream(), metadata);

        return uploadImageRepository.save(UploadImage.builder()
                .originalFilename(originalFilename)
                .savedFilename(savedFilename)
                .build());
    }

    // 확장자 추출
    private String extractExt(String originalFilename){
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    public ResponseEntity<UrlResource> downloadImage(Long boardId){
        //boardId에 해당하는 게시글이 없으면 null return
        Board board = boardRepository.findById(boardId).get();
        if (board == null || board.getUploadImage() == null){
            return null;
        }

        UrlResource urlResource = new UrlResource(amazonS3.getUrl(bucket,board.getUploadImage().getSavedFilename()));

        // 업로드 한 파일명이 한글인 경우 아래 작업을 안해주면 한글이 깨질 수 있음
        String encodedUpload
    }
}
