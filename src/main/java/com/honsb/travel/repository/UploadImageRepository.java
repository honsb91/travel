package com.honsb.travel.repository;

import com.honsb.travel.domain.entity.UploadImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadImageRepository extends JpaRepository<UploadImage, Long> {
}
