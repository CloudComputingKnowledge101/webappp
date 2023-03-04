package com.example.demo.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.models.CloudComputingDBImage;
import com.example.demo.models.CloudComputingDBProduct;
import com.example.demo.repositories.ImageRepository;

@Service
public class CloudComputingImageService {

	@Autowired
	private ImageRepository repository;

	@Autowired
	private S3FileTransferService s3FileTransferService;
	
	@Value("${aws.s3.bucket.name}")
	private String bucketName;
	
	public CloudComputingDBImage register(MultipartFile multipartFile, CloudComputingDBProduct product) {

		ArrayList<String> imageNAMEandURL = s3FileTransferService.save(multipartFile);

		if (imageNAMEandURL == null) {

			return null;
		}

		System.out.println("IMAGE NAME: " + imageNAMEandURL.get(0) + " URL: " + imageNAMEandURL.get(1));

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

		CloudComputingDBImage image = new CloudComputingDBImage();
		image.setDate_created(timeStamp);
		image.setProduct(product);
		image.setFilename(imageNAMEandURL.get(0));
		image.setS3_bucket_path(imageNAMEandURL.get(1));

		System.out.println("Product Image metadata stored in database ....");

		return repository.saveAndFlush(image);
	}

	public CloudComputingDBImage getImage(Long id) {

		Optional<CloudComputingDBImage> dbImage = repository.findById(id);

		if (!dbImage.isPresent()) {

			return null;
		}

		return dbImage.get();
	}

	public String delete(Long id) {
		
		Optional<CloudComputingDBImage> image = repository.findById(id);
		String imageName = image.get().getFilename();
		
		s3FileTransferService.deleteObject(bucketName, imageName);
		
		repository.deleteById(id);

		return "Image deleted";
	}
}
