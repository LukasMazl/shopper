package com.janprach.shopper.sreality.service;

import java.net.URI;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Hex;
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

	public Optional<ImageMetaData> fetchAndSaveImage(final String uri) {
		log.debug("Fetching image {}", uri);
		try {
//			val sanitizedUri = uri.split("=")[0] + "?" + URLEncoder.encode(uri.split("=")[1], "UTF-8");
			val sanitizedUri = uri.replaceAll("\\|", "%7C");

			val httpRequest = RequestBuilder.get(sanitizedUri).build();
			val imageData = this.richHttpClient.getByteArray(httpRequest).get();

			val md = MessageDigest.getInstance("SHA-1");
			md.reset();
			md.update(imageData);
			val sha1 = Hex.encodeHexString(md.digest());
			val imagePath = Paths.get(this.srealityFetcherConfig.getImagesDirectory(), sha1 + ".jpg");
			imagePath.toFile().getParentFile().mkdirs();
			FileUtils.writeByteArrayToFile(imagePath.toFile(), imageData);

			val externalFileName = Paths.get(new URI(sanitizedUri).getPath()).getFileName().toString();
			val imageFileName = imagePath.getFileName().toString();
			val image = ImageIO.read(imagePath.toFile());
			return Optional.of(new ImageMetaData(externalFileName, imageFileName, sha1,
					image.getWidth(), image.getHeight()));
		} catch (final Exception e) {
			log.error("Failed to fetch and save image: " + uri, e);
			return Optional.empty();
		}
	}

	@Value
	public static class ImageMetaData {
		private final String externalFileName;
		private final String fileName;
		private final String sha1;
		private final int width;
		private final int height;
	}
}
