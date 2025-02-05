package se.sundsvall.ai.flow.model.session;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/*
 * "Mapping" between locally uploaded files and Intric file uploads
 */
public class Input {

	private MultipartFile file;
	private UUID intricFileId;

	public Input(final MultipartFile file) {
		this.file = file;
	}

	public MultipartFile getFile() {
		return file;
	}

	public boolean isUploadedToIntric() {
		return nonNull(intricFileId);
	}

	public UUID getIntricFileId() {
		return intricFileId;
	}

	public void setIntricFileId(UUID intricFileId) {
		this.intricFileId = intricFileId;
	}
}
