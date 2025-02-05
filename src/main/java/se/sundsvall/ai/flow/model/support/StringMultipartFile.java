package se.sundsvall.ai.flow.model.support;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

public class StringMultipartFile implements MultipartFile {

	private final String prefix;
	private final String name;
	private final String value;

	public StringMultipartFile(final String prefix, final String name, final String value) {
		this.prefix = prefix;
		this.name = name;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getOriginalFilename() {
		return name;
	}

	@Override
	public String getContentType() {
		return TEXT_PLAIN_VALUE;
	}

	@Override
	public boolean isEmpty() {
		return value.isBlank();
	}

	@Override
	public long getSize() {
		return getBytes().length;
	}

	@Override
	public byte[] getBytes() {
		return "%s%s:%s".formatted(prefix, name, value).getBytes(UTF_8);
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
