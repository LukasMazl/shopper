package com.janprach.shopper.sreality.service;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.RequestBuilder;
import org.springframework.stereotype.Component;

import com.janprach.shopper.config.SrealityFetcherConfig;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor(onConstructor = @__({ @javax.inject.Inject }))
@Component
public class ImageFetcherService {
	private final RichHttpClient richHttpClient;
	private final SrealityFetcherConfig srealityFetcherConfig;

	@PostConstruct
	void createDirectories() {
		new File(this.srealityFetcherConfig.getImagesDirectory()).mkdirs();
		new File(this.srealityFetcherConfig.getThumbnailsDirectory()).mkdirs();
	}

	public Optional<ImageMetaData> fetchAndSaveImage(final String uri) {
		log.debug("Fetching image {}", uri);
		try {
//			val sanitizedUri = uri.split("=")[0] + "?" + URLEncoder.encode(uri.split("=")[1], "UTF-8");
			val sanitizedUri = uri.replaceAll("\\|", "%7C");

			val httpRequest = RequestBuilder.get(sanitizedUri).build();
			val imageData = this.richHttpClient.getByteArray(httpRequest).get();

			val sha1 = computeSha1Digest(imageData);
			val imageFileName = sha1 + ".jpg";
			val imagePath = Paths.get(this.srealityFetcherConfig.getImagesDirectory()).resolve(imageFileName);
			FileUtils.writeByteArrayToFile(imagePath.toFile(), imageData);
			val image = ImageIO.read(new ByteArrayInputStream(imageData));

			val thumbnailPath = Paths.get(this.srealityFetcherConfig.getThumbnailsDirectory()).resolve(imageFileName);
			val maxThumbnailSize = this.srealityFetcherConfig.getMaxThumbnailSize();
			val maxImageSize = Math.max(image.getWidth(), image.getHeight());
			val scale = 1.0 * maxThumbnailSize / Math.max(maxThumbnailSize, maxImageSize);
			val thumbnail = resize(image, (int) (scale * image.getWidth()), (int) (scale * image.getHeight()));
			ImageIO.write(thumbnail, "jpg", thumbnailPath.toFile());

//			val externalFileName = Paths.get(new URI(sanitizedUri).getPath()).getFileName().toString();
			return Optional.of(new ImageMetaData(sha1, image.getWidth(), image.getHeight()));
		} catch (final IOException e) {
			log.error("Failed to fetch and save image: " + uri, e);
			return Optional.empty();
		}
	}

	private static String computeSha1Digest(final byte[] value) {
		try {
			val md = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_1);
			md.reset();
			md.update(value);
			return Hex.encodeHexString(md.digest());
		} catch (final NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

    private static BufferedImage resize(final BufferedImage image, final int width, final int height) {
		val tmp = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		val resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		val g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return resized;
    }

	@Value
	public static class ImageMetaData {
		private final String sha1;
		private final int width;
		private final int height;
	}
}
