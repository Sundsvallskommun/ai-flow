package se.sundsvall.ai.flow.model.support;

import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.ai.flow.model.flowdefinition.exception.FlowException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class UploadedMultipartFile implements MultipartFile {

	private final String name;
	private final byte[] value;
	private final String originalFilename;
	private final String contentType;

	public UploadedMultipartFile(final String name, final MultipartFile original) {
		this.name = name;

		try {
			value = original.getBytes();
			originalFilename = original.getOriginalFilename();
			contentType = original.getContentType();
		} catch (IOException e) {
			throw new FlowException("Unable to handle uploaded file", e);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getOriginalFilename() {
		return originalFilename;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public boolean isEmpty() {
		return value.length == 0;
	}

	@Override
	public long getSize() {
		return getBytes().length;
	}

	@Override
	public byte[] getBytes() {
		return value;
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(getBytes());
	}

	@Override
	public void transferTo(final File dest) throws IllegalStateException {
		throw new UnsupportedOperationException("NOT IMPLEMENTED");
	}
}
