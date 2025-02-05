package se.sundsvall.ai.flow.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "flow", indexes = {
	@Index(name = "idx_flow_id", columnList = "id"),
	@Index(name = "idx_flow_id_and_version", columnList = "id, version")
})
@IdClass(FlowEntity.IdAndVersion.class)
public class FlowEntity {

	@Id
	@Column(name = "id")
	private String id;

	@Id
	@Column(name = "version")
	private Integer version;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description", columnDefinition = "text")
	private String description;

	@Column(name = "content", nullable = false, columnDefinition = "text")
	private String content;

	public String getId() {
		return id;
	}

	public FlowEntity withId(final String id) {
		this.id = id;
		return this;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public FlowEntity withVersion(final Integer version) {
		this.version = version;
		return this;
	}

	public void setVersion(final Integer version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public FlowEntity withName(final String name) {
		this.name = name;
		return this;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public FlowEntity withDescription(final String description) {
		this.description = description;
		return this;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getContent() {
		return content;
	}

	public FlowEntity withContent(final String content) {
		this.content = content;
		return this;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public record IdAndVersion(String id, Integer version) {}
}
